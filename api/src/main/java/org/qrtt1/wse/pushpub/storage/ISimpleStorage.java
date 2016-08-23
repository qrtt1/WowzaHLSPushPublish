package org.qrtt1.wse.pushpub.storage;

import java.util.HashMap;

import org.qrtt1.wse.pushpub.IPushStreamSource;

public interface ISimpleStorage {

    public void configure(HashMap<String, String> dataMap, IPushStreamSource pushSource);

    public int deleteMediaSegment(String uri);

    public int sendMediaSegment(String uri, byte[] data);

    public int sendMediaPlaylist(String uri, byte[] playlist);

    public int sendMasterPlaylist(String uri, byte[] playlist);

    public int sendGroupMasterPlaylist(String groupName, String uri, byte[] playlist);
    
    public void onDisconnect();

}
