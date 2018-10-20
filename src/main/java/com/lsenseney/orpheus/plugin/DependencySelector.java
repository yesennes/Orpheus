package com.lsenseney.orpheus.plugin;
import java.util.NoSuchElementException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

import com.lsenseney.orpheus.Environment;
/**
 *
 * @author Luke Senseney
 * @version 1.0 Oct 01, 18
 **/
public class DependencySelector {
    protected List<Dependency> dependencies = new LinkedList<Dependency>();
    protected ListIterator<Dependency> currentlyTesting;
    protected EnvironmentGenerator generator;
    protected Environment current;

    public void recordValid(Environment environ, boolean valid){
        if(currentlyTesting == null){
            if(valid){
                generator.markSuccess();
                currentlyTesting = dependencies.listIterator();
            }else {
                current.cleanUp();
                current = null;
                generator.markFailure();
            }
        } else {
            Dependency testing = currentlyTesting.next();
            if(valid){
                testing.markSuccess();
            } else {
                testing.markFailure();
            }
            currentlyTesting.previous();
        }
    }

    public DependencySelector merge(DependencySelector other){
        return null;
    }

    public void setEnvironmentGenerator(EnvironmentGenerator generator){
        this.generator = generator;
    }

    public void addDependencies(Dependency... dependencies){
        Collections.addAll(this.dependencies, dependencies);
    }

    public void addDependencies(Collection<Dependency> dependencies){
        this.dependencies.addAll(dependencies);
    }

    public Environment next() throws ConfigurationFailedException{
        if(!hasNext()){
            return null;
        }
        try{
            if(current == null){
                current = generator.generateEnvironment();
                for(Dependency d : dependencies){
                    d.configureSuccessfullVersion(current);
                }
            } else {

                if(currentlyTesting == null){
                    currentlyTesting = dependencies.listIterator();
                }
                Dependency dependency = currentlyTesting.next();
                dependency.configureEnvironment(current);
                currentlyTesting.previous();
            }
            current.runFinalizers();
            return current;
        }catch(ConfigurationFailedException e){
            current.cleanUp();
            throw e;
        }
    }

    private boolean hasNext() throws ConfigurationFailedException{
        if(currentlyTesting == null){
            return generator.hasUntriedVersion();
        } else {
            while(currentlyTesting.hasNext()){
                Dependency dependency = currentlyTesting.next();
                if(dependency.hasUntriedVersion()) {
                    currentlyTesting.previous();
                    return true;
                }
                dependency.configureSuccessfullVersion(current);
            }
            currentlyTesting = null;
            return generator.hasUntriedVersion();
        }
    }
}
