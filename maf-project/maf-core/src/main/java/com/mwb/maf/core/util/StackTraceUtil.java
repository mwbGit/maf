package com.mwb.maf.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 描述:
 *
 * @author mengweibo@kanzhun.com
 * @create 2020/1/16
 */
public class StackTraceUtil {

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        String var3;
        try {
            throwable.printStackTrace(pw);
            var3 = sw.toString();
        } finally {
            pw.close();
        }

        return var3;
    }
}

