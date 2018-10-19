package com.lsenseney.orpheus.plugin;
import org.json.JSONObject;

/**
 *
 * @author Luke Senseney
 * @version 1.0 Sep 29, 18
 **/
public interface ConfigItemFactory {
    public void init();
    public ConfigItem createItem(JSONObject obj) throws Exception;
    public void destroy();
}
