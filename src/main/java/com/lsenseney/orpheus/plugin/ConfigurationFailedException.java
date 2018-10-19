package com.lsenseney.orpheus.plugin;
/**
 *
 * @author Luke Senseney
 * @version 1.0 Oct 17, 18
 **/
public class ConfigurationFailedException extends Exception {
    public ConfigurationFailedException(String message, Throwable cause){
        super(message, cause);
    }
}
