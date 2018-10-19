package com.lsenseney.orpheus.gradle;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.io.IOException;
import org.json.JSONException;
import java.util.Collections;
import java.io.FileInputStream;
import java.util.Collection;
import org.json.JSONObject;
import java.util.regex.Pattern;
import java.util.List;

import com.lsenseney.orpheus.plugin.ConfigItem;
import com.lsenseney.orpheus.plugin.Dependency;
import com.lsenseney.orpheus.plugin.DependencySelector;
import com.lsenseney.orpheus.plugin.EnvironmentGenerator;
import com.lsenseney.orpheus.plugin.EnvironmentVerifier;
import com.lsenseney.Utils;
/**
 *
 * @author Luke Senseney
 * @version 1.0 Oct 17, 18
 **/
public class GradleConfigItem implements ConfigItem{
    public GradleVerifier verifier;
    public List<Dependency> dependencies;
    private static final Pattern dependenciesBlock = Pattern.compile("dependencies\\s*{([^}]*)}");
    private static final Pattern dependenciesPattern = Pattern.compile("'(:?[^:']+):([^:'])'");

    public GradleConfigItem(JSONObject object) throws IOException{
        try{
            String verify = object.getString("verify");
            verifier = new GradleVerifier(verify);
        } catch(JSONException e){
        }
        String buildGradle = Utils.readAll(new FileInputStream("build.gradle"));
        Matcher matcher = dependenciesBlock.matcher(buildGradle);
        if(matcher.find()){
            dependencies = new ArrayList<>();
            Matcher dependenciesMatcher = dependenciesPattern.matcher(matcher.group(1));
            while(dependenciesMatcher.find()){
                dependencies.add(new MavenDependency(dependenciesMatcher.group(1), dependenciesMatcher.group(2)));
            }
        }
    }

    public String getName(){
        return "Gradle";
    }

    public DependencySelector getDependencySelector(){
        return null;
    }

    public EnvironmentGenerator getEnvironmentGenerator(){
        return null;
    }

    public Collection<EnvironmentVerifier> getEnvironmentVerifiers(){
        return Collections.singleton(verifier);
    }

    public Collection<Dependency> getDependencies(){
        return dependencies;
    }
}
