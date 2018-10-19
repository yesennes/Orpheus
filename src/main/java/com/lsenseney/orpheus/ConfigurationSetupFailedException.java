package com.lsenseney.orpheus;
/**
 *
 * @author Luke Senseney
 * @version 1.0 Oct 18, 18
 **/
public class ConfigurationSetupFailedException extends Exception{
    public ConfigurationSetupFailedException(String message, Exception cause){
        super(message, cause);
    }
}
