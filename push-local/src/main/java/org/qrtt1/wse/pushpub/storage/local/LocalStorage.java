package org.qrtt1.wse.pushpub.storage.local;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.qrtt1.wse.pushpub.IPushStreamSource;
import org.qrtt1.wse.pushpub.storage.ISimpleStorage;

public class LocalStorage implements ISimpleStorage {

    IPushStreamSource pushSource;
    HashMap<String, String> dataMap = new HashMap<String, String>();
    String baseDir = "/tmp/LocalStorage";

    @Override
    public void configure(HashMap<String, String> dataMap, IPushStreamSource pushSource) {
        this.pushSource = pushSource;
        if (dataMap != null) {
            this.dataMap.putAll(dataMap);
        }

        if (dataMap.containsKey("baseDir")) {
            String path = dataMap.get("baseDir");
            prepareDir(pushSource, path);
        } else {
            prepareDir(pushSource, this.baseDir);
        }
    }

    protected void prepareDir(IPushStreamSource pushSource, String path) {
        File dir = new File(path);
        dir.mkdirs();
        this.baseDir = dir.getAbsolutePath();
        pushSource.logInfo("configure", "set baseDir: " + baseDir);
    }

    @Override
    public int deleteMediaSegment(String uri) {
        FileUtils.deleteQuietly(new File(baseDir, stripRelativePath(uri)));
        return 1;
    }

    @Override
    public int sendMediaSegment(String uri, byte[] data) {
        return writeFile(uri, data, "sendMediaSegment");
    }

    @Override
    public int sendMediaPlaylist(String uri, byte[] playlist) {
        return writeFile(uri, playlist, "sendMediaPlaylist");
    }

    @Override
    public int sendMasterPlaylist(String uri, byte[] playlist) {
        return writeFile(uri, playlist, "sendMasterPlaylist");
    }

    @Override
    public int sendGroupMasterPlaylist(String groupName, String uri, byte[] playlist) {
        return writeFile(groupName + "/" + uri, playlist, "sendMasterPlaylist");
    }

    protected int writeFile(String uri, byte[] playlist, String methodName) {
        try {
            File file = new File(baseDir, stripRelativePath(uri));
            pushSource.logInfo("writeFile", "output to file: " + file.getAbsolutePath());
            FileUtils.writeByteArrayToFile(file, playlist);
            return 1;
        } catch (IOException e) {
            pushSource.logError(methodName, e);
            return 0;
        }
    }

    protected String stripRelativePath(String uri) {
        uri = uri.replace("../", "");
        uri = uri.replace("./", "");
        return uri;
    }

    @Override
    public void onDisconnect() {
        // no action
    }

}
