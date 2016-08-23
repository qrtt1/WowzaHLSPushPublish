package org.qrtt1.wse.pushpub.storage.m3u8;

import java.util.HashMap;

import org.qrtt1.wse.pushpub.IPushStreamSource;

import com.wowza.wms.manifest.model.m3u8.MediaSegmentModel;
import com.wowza.wms.manifest.model.m3u8.PlaylistModel;

public class NullM3U8Storage implements IM3U8ModelStorage {

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
    public int deleteMediaSegment(MediaSegmentModel mediaSegment) {
        pushSource.logInfo("deleteMediaSegment", "delete " + mediaSegment.getUri());
        return 1;
    }

    @Override
    public int sendMediaSegment(MediaSegmentModel mediaSegment) {
        pushSource.logInfo("sendMediaSegment", "send " + mediaSegment.getUri());
        return 1;
    }

    @Override
    public int sendMediaPlaylist(PlaylistModel playlist) {
        pushSource.logInfo("sendMediaPlaylist", "send-playlist: " + playlist.getUri());
        return 1;
    }

    @Override
    public int sendMasterPlaylist(PlaylistModel playlist) {
        pushSource.logInfo("sendMasterPlaylist", "send-playlist: " + playlist.getUri());
        return 1;
    }

    @Override
    public int sendGroupMasterPlaylist(String groupName, PlaylistModel playlist) {
        pushSource.logInfo("sendGroupMasterPlaylist",
                "send-playlist: " + playlist.getUri() + " for group: " + groupName);
        return 1;
    }

}
