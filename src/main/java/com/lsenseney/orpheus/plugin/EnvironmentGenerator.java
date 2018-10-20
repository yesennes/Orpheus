package com.lsenseney.orpheus.plugin;
import com.lsenseney.orpheus.Environment;
/**
 *
 * @author Luke Senseney
 * @version 1.0 Oct 04, 18
 **/
public abstract class EnvironmentGenerator {
    public abstract Environment generateEnvironment();

    public abstract void markSuccess();
    public abstract void markFailure();

    public abstract Environment generateSuccessfullVersion();
    public abstract boolean hasUntriedVersion();
}
