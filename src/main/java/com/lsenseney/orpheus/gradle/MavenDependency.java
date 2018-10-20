package com.lsenseney.orpheus.gradle;
import java.util.regex.Matcher;
import java.io.PrintStream;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URL;
import java.util.List;

import com.lsenseney.orpheus.plugin.Dependency;
import com.lsenseney.orpheus.Environment;
import com.lsenseney.orpheus.plugin.ConfigurationFailedException;
import com.lsenseney.Utils;

/**
 *
 * @author Luke Senseney
 * @version 1.0 Oct 13, 18
 **/
public class MavenDependency implements Dependency{
    private String repoName;
    private List<String> versions;

    private String initialVersion;
    private int initialVersionIndex;

    private int maxWorkingVersion;
    private int maxUntestedVersion = -1;

    private int minWorkingVersion;
    private int minUntestedVersion = 0;

    public static final Object metadataTag = new Object(){
        public String toString(){
            return "Gradle build file:";
        }
    };

    private static final Pattern versionNumber = Pattern.compile("<version>([^<]*)</version>");

    public MavenDependency(String name, String initialVersion) throws IOException{
        repoName = name;
        this.initialVersion = initialVersion;
        versions = getVersions(repoName);

        initialVersionIndex = versions.indexOf(initialVersion);
        maxWorkingVersion = initialVersionIndex;
        minWorkingVersion = initialVersionIndex;
    }

    public String getName(){
        return repoName;
    }

    private void configureEnvironment(Environment environ, int version) throws ConfigurationFailedException{
        configureEnvironment(environ, versions.get(version));
    }

    private void configureEnvironment(Environment environ, String version) throws ConfigurationFailedException{
        System.out.println("Configuring from:" + version);
        try{
            if(!environ.getMetadata().containsKey(metadataTag)){
                InputStream stream = environ.readFile("build.gradle");
                environ.getMetadata().put(metadataTag, Utils.readAll(stream));
                environ.addFinalizer((Environment e) -> {
                    try{
                        new PrintStream(environ.writeFile("build.gradle", false)).print(e.getMetadata().get(metadataTag));
                        return null;
                    }catch(IOException ex){
                        return new ConfigurationFailedException("Could not write build.gradle", ex);
                    }
                });
            }

            environ.getMetadata().compute(metadataTag, (key, buildScript) ->
                    ((String)buildScript).replaceAll("'" + repoName + ":[^']*'", "'" + repoName + ":" + version + "'"));
        }catch(IOException e){
            throw new ConfigurationFailedException("Configuration of " + repoName + " failed, could not read build.gradle", e);
        }
    }

    public void configureEnvironment(Environment environ) throws ConfigurationFailedException{
        if(maxUntestedVersion == -1){
            configureEnvironment(environ, versions.get(versions.size() - 1));
        } else if(maxUntestedVersion > maxWorkingVersion){
            configureEnvironment(environ, (maxUntestedVersion + maxWorkingVersion + 1) / 2);
        } else if(minUntestedVersion < minWorkingVersion){
            configureEnvironment(environ, (minUntestedVersion + minWorkingVersion) / 2);
        } else
            throw new IllegalStateException("All versions have been tested");
    }

    public void markSuccess(){
        if(maxUntestedVersion == -1){
            maxWorkingVersion = versions.size() - 1;
            maxUntestedVersion = maxWorkingVersion - 1;
        } else if(maxUntestedVersion > maxWorkingVersion){
            maxWorkingVersion = (maxUntestedVersion + maxWorkingVersion + 1) / 2;
        } else if(minUntestedVersion < minWorkingVersion){
            minWorkingVersion = (minUntestedVersion + minWorkingVersion) / 2;
        } else
            throw new IllegalStateException("All versions have been tested");
    }

    public void markFailure(){
        if(maxUntestedVersion == -1){
            maxUntestedVersion = versions.size() - 2;
        } else if(maxUntestedVersion > maxWorkingVersion){
            maxUntestedVersion = ((maxUntestedVersion + maxWorkingVersion + 1) / 2) - 1;
        } else if(minUntestedVersion < minWorkingVersion){
            minUntestedVersion = ((minUntestedVersion + minWorkingVersion) / 2) + 1;
        } else
            throw new IllegalStateException("All versions have been tested");
    }

    public void configureSuccessfullVersion(Environment environ) throws ConfigurationFailedException{
        configureEnvironment(environ, versions.get(minWorkingVersion));
    }

    public boolean hasUntriedVersion(){
        return maxUntestedVersion == -1 || maxUntestedVersion > maxWorkingVersion || minUntestedVersion < minWorkingVersion;
    }

    private static List<String> getVersions(String name) throws IOException{
        URL url = new URL("https://repo.maven.apache.org/maven2/" + name.replaceAll("[:.]", "/") + "/maven-metadata.xml");
        URLConnection connect = url.openConnection();
        connect.connect();
        InputStream file = connect.getInputStream();


        List<String> toRet = new ArrayList<>();
        Matcher matcher = versionNumber.matcher(Utils.readAll(file));
        while(matcher.find()){
            toRet.add(matcher.group(1));
        }
        return toRet;
    }

}
