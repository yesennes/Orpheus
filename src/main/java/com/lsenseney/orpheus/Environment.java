package com.lsenseney.orpheus;
import java.io.File;
import java.util.function.Function;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.InputStream;

import com.lsenseney.orpheus.plugin.ConfigurationFailedException;
/**
 *
 * @author Luke Senseney
 * @version 1.0 Oct 02, 18
 **/
public class Environment{
    private Set<Function<Environment, ConfigurationFailedException>> finalizers = new HashSet<>();
    private Map<Object, Object> metadata = new HashMap<>();

    public Process exec(String... command) throws IOException{
        return Runtime.getRuntime().exec(command);
    }

    public Process exec(File path, String... command) throws IOException{
        return Runtime.getRuntime().exec(command, new String[0], path);
    }

    public InputStream readFile(String filename) throws FileNotFoundException{
        return new FileInputStream(filename);
    }

    public OutputStream writeFile(String filename, boolean append) throws FileNotFoundException{
        return new FileOutputStream(filename, append);
    }

    public Map<Object, Object> getMetadata() {
        return metadata;
    }

    public void addFinalizer(Function<Environment, ConfigurationFailedException> consumer){
        finalizers.add(consumer);
    }

    public void runFinalizers() throws ConfigurationFailedException{
        for(Function<Environment, ConfigurationFailedException> finalizer : finalizers){
            ConfigurationFailedException e = finalizer.apply(this);
            if(e != null)
                throw e;
        }
    }

    public void cleanUp(){
    }
}
