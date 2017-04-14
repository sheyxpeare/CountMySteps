package com.google.android.gms.fit.samples.common.logger;

/*
Interface for the Log Noed
 */
public interface LogNode {

    public void println(int priority, String tag, String msg, Throwable tr);

}
