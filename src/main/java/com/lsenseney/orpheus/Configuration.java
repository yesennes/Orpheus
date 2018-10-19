package com.lsenseney.orpheus;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.InputStream;

import com.lsenseney.orpheus.plugin.*;
/**
 *
 * @author Luke Senseney
 * @version 1.0 Sep 29, 18
 **/
public class Configuration {
    private DependencySelector selector = null;
    private Set<EnvironmentVerifier> verifiers = new HashSet<>();
    private Set<Dependency> dependencies = new HashSet<>();

    public Configuration(InputStream input) throws IllegalArgumentException, ConfigurationSetupFailedException {
        JSONObject obj = new JSONObject(new JSONTokener(input));

        ConfigurationSetupFailedException[] toThrow = new ConfigurationSetupFailedException[1];
        List<ConfigItem> configs = obj.keySet().stream().map(key -> {
            ConfigItemFactory factory = PluginManger.getConfigItemFactories().get(key);
            if(factory == null)
                throw new IllegalArgumentException(key + " is not a valid configuration option");
            try{
                return factory.createItem(obj.getJSONObject(key));
            }catch(Exception e){
                toThrow[0] =  new ConfigurationSetupFailedException("Failed to set up " + factory, e);
                return null;
            }
        }).collect(Collectors.toList());

        if(toThrow[0] != null)
            throw toThrow[0];

        EnvironmentGenerator generator = null;
        String selectorSource = null;
        String generatorSource = null;
        Set<Dependency> dependencies = new HashSet<Dependency>();

        for(ConfigItem item : configs) {
            DependencySelector newSelector = item.getDependencySelector();
            if(newSelector != null){
                if(selector == null){
                    selector = newSelector;
                    selectorSource = item.getName();
                } else if (!selector.equals(newSelector)) {
                    DependencySelector merged = selector.merge(newSelector);
                    if(merged != null){
                        selector = merged;
                        selectorSource = item.getName();
                    } else {
                        merged = newSelector.merge(selector);
                        if(merged == null)
                            throw new IllegalArgumentException(selectorSource + " incompatable with " + item.getName());
                        selector = merged;
                        selectorSource = item.getName();
                    }
                }
            }

            EnvironmentGenerator newGenerator = item.getEnvironmentGenerator();
            if(newGenerator != null){
                if(generator == null){
                    generator = newGenerator;
                    generatorSource = item.getName();
                } else if (!generator.equals(newGenerator)){
                    throw new IllegalArgumentException(generatorSource + " incompatable with " + item.getName());
                }
            }

            selector.setEnvironementGenerator(generator);
            selector.addDependencies(dependencies);
            dependencies.addAll(item.getDependencies());
            dependencies.addAll(item.getDependencies());
            verifiers.addAll(item.getEnvironmentVerifiers());
        }
    }
    public DependencySelector getSelector() {
        return selector;
    }

    public Set<EnvironmentVerifier> getVerifiers() {
        return verifiers;
    }

    public Set<Dependency> getDependencies() {
        return dependencies;
    }
}
