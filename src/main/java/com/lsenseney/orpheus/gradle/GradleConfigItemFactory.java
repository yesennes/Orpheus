package com.lsenseney.orpheus.gradle;
import java.io.IOException;
import java.io.FileNotFoundException;

import com.lsenseney.orpheus.PluginManger;
import com.sun.source.util.Plugin;
import org.json.JSONObject;

import com.lsenseney.orpheus.plugin.ConfigItemFactory;
import com.lsenseney.orpheus.plugin.ConfigItem;
/**
 *
 * @author Luke Senseney
 * @version 1.0 Oct 17, 18
 **/
public class GradleConfigItemFactory implements ConfigItemFactory{
    public void init(){
    }

    public ConfigItem createItem(JSONObject obj) throws IOException{
        return new GradleConfigItem(obj);
    }

    public void destroy(){
    }
}
