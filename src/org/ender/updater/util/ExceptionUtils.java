package org.ender.updater.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {
    public static String getStackTrack(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
}
