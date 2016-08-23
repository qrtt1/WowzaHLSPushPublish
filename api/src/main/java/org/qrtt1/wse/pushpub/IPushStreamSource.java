package org.qrtt1.wse.pushpub;

public interface IPushStreamSource {

    String getDstStreamName();

    String getRandomSessionStr();

    void logDebug(String methodName, String log);

    void logDebugManifest(String methodName, String log);

    void logError(String methodName, String log, Throwable e);

    void logError(String methodName, String log);

    void logError(String methodName, Throwable e);

    void logInfo(String methodName, String log);

    void logWarn(String methodName, String log);

}