package com.lsenseney.orpheus.gradle;
import java.io.PrintStream;
import java.util.concurrent.ExecutionException;
import java.io.IOException;

import com.lsenseney.Utils;
import com.lsenseney.orpheus.Environment;
import com.lsenseney.orpheus.plugin.EnvironmentVerifier;
/**
 *
 * @author Luke Senseney
 * @version 1.0 Oct 17, 18
 **/
public class GradleVerifier implements EnvironmentVerifier{
    private String task;

    public GradleVerifier(String task){
        this.task = task;
    }

    public boolean verify(Environment environ){
        Process testing = null;
        try{
            Process build = environ.exec("gradle", task);
            Utils.copyAll(build.getInputStream(), System.out);
            Utils.copyAll(build.getErrorStream(), System.out);
            build.onExit().get();
            return build.exitValue() == 0;
        }catch(IOException | InterruptedException | ExecutionException e){
            return false;
        }finally{
            if(testing != null){
                testing.destroy();
            }
        }
    }
}
