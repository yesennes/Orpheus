package com.lsenseney.orpheus.plugin;
import java.util.NoSuchElementException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
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
            generator.markFailure();
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

    public void setEnvironementGenerator(EnvironmentGenerator generator){
        generator = generator;
    }

    public void addDependencies(Dependency... dependencies){
        Collections.addAll(this.dependencies, dependencies);
    }

    public void addDependencies(Collection<Dependency> dependencies){
        this.dependencies.addAll(dependencies);
    }

    public Environment next() throws ConfigurationFailedException{
        if(!hasNext()){
            throw new NoSuchElementException();
        }
        try{
            if(current == null){
                current = generator.generateEnvironement();
                for(Dependency d : dependencies){
                    d.configureSuccessfullVersion(current);
                }
            } else {

                if(currentlyTesting == null){
                    currentlyTesting = dependencies.listIterator();
                }
                Dependency dependency = currentlyTesting.next();
                dependency.configureEnvironement(current);
                currentlyTesting.previous();
            }

            return current;
        }catch(ConfigurationFailedException e){
            current.cleanUp();
            throw e;
        }
    }

    public boolean hasNext(){
        if(currentlyTesting == null){
            return generator.hasUntriedVersion();
        } else {
            while(currentlyTesting.hasNext()){
                Dependency dependency = currentlyTesting.next();
                if(dependency.hasUntriedVersion()){
                    currentlyTesting.previous();
                    return true;
                }
            }
            currentlyTesting = null;
            return generator.hasUntriedVersion();
        }
    }
}
