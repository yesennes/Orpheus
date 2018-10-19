package com.lsenseney.orpheus;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import com.lsenseney.orpheus.plugin.ConfigItemFactory;
import com.lsenseney.orpheus.plugin.ConfigItem;
/**
 *
 * @author Luke Senseney
 * @version 1.0 Sep 28, 18
 **/
public class PluginManger {
    private static Map<String, ConfigItemFactory> CONFIG_ITEM_FACTORIES = new HashMap<String, ConfigItemFactory>();
    private static boolean used = false;

    public static Map<String, ConfigItemFactory> getConfigItemFactories(){
        if(!used){
            for(ConfigItemFactory f : CONFIG_ITEM_FACTORIES.values())
                f.init();
            used = true;
        }
        return Collections.unmodifiableMap(CONFIG_ITEM_FACTORIES);
    }

    public static void registerConfigItemFactory(String name, ConfigItemFactory factory){
        if(used){
            throw new IllegalStateException("ConfigItemFactory " + factory
                    + " registered after factories are used. Try a static initilization block");
        }
        CONFIG_ITEM_FACTORIES.put(name, factory);
    }

    public static void destroyPlugins(){
        for(ConfigItemFactory f : CONFIG_ITEM_FACTORIES.values()){
            f.destroy();
        }
    }
}
