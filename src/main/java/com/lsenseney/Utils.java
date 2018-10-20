package com.lsenseney;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Luke Senseney
 * @version 1.0 Oct 18, 18
 **/
public class Utils {
    private static byte[] buf = new byte[1024];
    public static String readAll(InputStream stream) throws IOException {
        StringBuilder builder = new StringBuilder();
        int len = 0;

        while((len = stream.read(buf)) != -1){
            builder.append(new String(buf, 0, len));
        }

        return builder.toString();
    }

    public static void copyAll(InputStream in, OutputStream out) throws IOException{
        int len = 0;
        while((len = in.read(buf)) != -1){
            out.write(buf, 0, len);
        }
    }
}
