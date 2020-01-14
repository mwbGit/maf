package com.mwb.maf.core.logging;

import org.slf4j.event.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogConstant {

    // **********(1).logger defined**********//

    public static final String LOGGER_COM_COOHUA = "com.mwb";

    public static final String LOGGER_COM_COOHUA_MAF = "com.mwb.maf";

    public static final String LOGGER_ACCESS = "access";

    public static final String LOGGER_PERFORMANCE = "performance";

    public static final String LOGGER_ROOT = "root";

    public static final List<String> LOGGER_COMMON_LIST;

    static {
        List<String> list = new ArrayList<String>();
        list.add(LOGGER_COM_COOHUA);
        list.add(LOGGER_COM_COOHUA_MAF);
        list.add(LOGGER_ACCESS);
        list.add(LOGGER_PERFORMANCE);
        LOGGER_COMMON_LIST = list;
    }

    // **********(2).logger level defined**********//

    public static final List<String> LOGGER_LEVEL_LIST;

    static {
        List<String> list = new ArrayList<String>();
        list.add(Level.DEBUG.toString());
        list.add(Level.ERROR.toString());
        list.add(Level.INFO.toString());
        list.add(Level.TRACE.toString());
        list.add(Level.WARN.toString());

        list.add(Level.DEBUG.toString().toLowerCase());
        list.add(Level.ERROR.toString().toLowerCase());
        list.add(Level.INFO.toString().toLowerCase());
        list.add(Level.TRACE.toString().toLowerCase());
        list.add(Level.WARN.toString().toLowerCase());

        LOGGER_LEVEL_LIST = Collections.unmodifiableList(list);
    }
}
