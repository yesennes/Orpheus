package com.lsenseney.orpheus;

import com.lsenseney.orpheus.plugin.EnvironmentGenerator;

public class DefaultEnvironmentGenerator extends EnvironmentGenerator {
    boolean hasTested = false;
    @Override
    public Environment generateEnvironment() {
        return new Environment();
    }

    @Override
    public void markSuccess() {
        hasTested = true;
    }

    @Override
    public void markFailure() {
        hasTested = true;
    }

    @Override
    public Environment generateSuccessfullVersion() {
        return new Environment();
    }

    @Override
    public boolean hasUntriedVersion() {
        return !hasTested;
    }
}
