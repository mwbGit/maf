package com.mwb.maf.core.metrics;


public class CustomProfiler {
    public static final LatencyStat CUSTOMIZED_STAT = LatencyProfiler.Builder.build()
            .tag("custom")
            .name("custom")
            .defineLabels("operation", "class")
            .buckets(
                    0.0001, 0.0005, 0.0009,
                    0.001, 0.003, 0.005, 0.007, 0.009,
                    0.01, 0.03, 0.05, 0.07, 0.09,
                    0.1, 0.3, 0.5, 0.7, 0.9,
                    1, 3, 5)
            .create();

    public static Procedure beginProcedure() {
        return new Procedure();
    }

    public static Procedure beginProcedure(String metrics, Class clz) {
        return new Procedure(metrics, clz);
    }

    public static Procedure beginProcedure(String metrics, String category) {
        return new Procedure(metrics, category);
    }

    public static class Procedure {
        private final String metrics;
        private final String category;
        private final LatencyStat.Timer timer;

        Procedure(String metrics, Class clz) {
            this(metrics, clz.getName());
        }

        Procedure(String metrics, String category) {
            this.category = category;
            this.metrics = category + "." + metrics;
            timer = CUSTOMIZED_STAT.startTimer(this.metrics, this.category);
            CUSTOMIZED_STAT.inc(this.metrics, this.category);
        }

        Procedure() {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            category = stackTraceElement.getClassName();
            metrics = category + "." + stackTraceElement.getMethodName();
            timer = CUSTOMIZED_STAT.startTimer(this.metrics, this.category);
            CUSTOMIZED_STAT.inc(this.metrics, this.category);
        }

        public String getMetrics() {
            return metrics;
        }

        public String getCategory() {
            return category;
        }

        public void exception(Throwable throwable) throws Throwable {
            CUSTOMIZED_STAT.error(metrics, category);
            throw throwable;
        }

        public void complete() {
            timer.observeDuration();
            CUSTOMIZED_STAT.dec(metrics, category);
        }
    }
}
