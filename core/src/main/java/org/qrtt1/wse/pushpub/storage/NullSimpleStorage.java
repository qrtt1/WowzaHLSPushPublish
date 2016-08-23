package org.qrtt1.wse.pushpub.storage;

import java.util.HashMap;

import org.qrtt1.wse.pushpub.IPushStreamSource;

public class NullSimpleStorage implements ISimpleStorage {

    private IPushStreamSource pushSource;
    private HashMap<String, String> dataMap = new HashMap<String, String>();

    public void configure(HashMap<String, String> dataMap, IPushStreamSource pushSource) {
        this.pushSource = pushSource;
        if (dataMap != null) {
            pushSource.logInfo("configureDataMap", "" + dataMap);
            this.dataMap.putAll(dataMap);
        }
    }

    @Override
    public int deleteMediaSegment(String uri) {
        pushSource.logInfo("deleteMediaSegment", "delete " + uri);
        return 1;
    }

    @Override
    public int sendMediaSegment(String uri, byte[] data) {
        pushSource.logInfo("sendMediaSegment", "send " + uri + ", length: " + data.length);
        return 1;
    }

    @Override
    public int sendMediaPlaylist(String uri, byte[] playlist) {
        pushSource.logInfo("sendMediaPlaylist", "send-playlist: " + uri);
        return 1;
    }

    @Override
    public int sendMasterPlaylist(String uri, byte[] playlist) {
        pushSource.logInfo("sendMasterPlaylist", "send-playlist: " + uri);
        return 1;
    }

    @Override
    public int sendGroupMasterPlaylist(String groupName, String uri, byte[] playlist) {
        pushSource.logInfo("sendGroupMasterPlaylist", "send-playlist: " + uri + " for group: " + groupName);
        return 1;
    }

    @Override
    public void onDisconnect() {
        pushSource.logInfo("onDisconnection", "just a callback for disconnecting");
    }

}
