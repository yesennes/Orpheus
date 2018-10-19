package com.lsenseney.orpheus;
import java.util.Iterator;
import java.util.Set;
import java.io.FileNotFoundException;
import java.io.FileInputStream;

import com.lsenseney.orpheus.plugin.*;
/**
 *
 * @author Luke Senseney
 * @version 1.0 Sep 28, 18
 **/
public class Orpheus {
    public static void main(String[] args) throws ConfigurationFailedException, ConfigurationSetupFailedException{
        try{
            Configuration options = new Configuration(new FileInputStream(args[0]));

            DependencySelector dependencySelector = options.getSelector();
            Set<EnvironmentVerifier> environmentVerifiers = options.getVerifiers();

            while(dependencySelector.hasNext()){
                Environment environ = dependencySelector.next();
                boolean valid = true;
                Iterator<EnvironmentVerifier> iter = environmentVerifiers.iterator();
                while(valid && iter.hasNext()){
                    EnvironmentVerifier current = iter.next();
                    valid &= current.verify(environ);
                }
                dependencySelector.recordValid(environ, valid);
            }
        }catch(FileNotFoundException e){
            System.out.println("Cannot find file " + args[0]);
            System.exit(1);
        }
    }
}
