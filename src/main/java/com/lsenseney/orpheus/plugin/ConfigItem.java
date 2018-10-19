package com.lsenseney.orpheus.plugin;
import java.util.Collection;
import java.util.Set;
/**
 *
 * @author Luke Senseney
 * @version 1.0 Sep 29, 18
 **/
public interface ConfigItem {
    String getName();
    DependencySelector getDependencySelector();
    EnvironmentGenerator getEnvironmentGenerator();
    Collection<EnvironmentVerifier> getEnvironmentVerifiers();
    Collection<Dependency> getDependencies();
}
