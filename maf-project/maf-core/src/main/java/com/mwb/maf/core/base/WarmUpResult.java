package com.mwb.maf.core.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class WarmUpResult {
    private static volatile boolean warmUpFlag = false;
    private String type;
    private boolean result;
    private String description;

    public static void markAsDone() {
        warmUpFlag = true;
    }

    public static boolean isDone() {
        return warmUpFlag;
    }
}
