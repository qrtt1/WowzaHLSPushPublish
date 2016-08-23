package org.qrtt1.wse.pushpub.storage.m3u8;

import java.util.HashMap;

import org.qrtt1.wse.pushpub.IPushStreamSource;

import com.wowza.wms.manifest.model.m3u8.MediaSegmentModel;
import com.wowza.wms.manifest.model.m3u8.PlaylistModel;

public interface IM3U8ModelStorage {

    public void configure(HashMap<String, String> dataMap, IPushStreamSource pushSource);

    public int deleteMediaSegment(MediaSegmentModel mediaSegment);

    public int sendMediaSegment(MediaSegmentModel mediaSegment);

    public int sendMediaPlaylist(PlaylistModel playlist);

    public int sendMasterPlaylist(PlaylistModel playlist);

    public int sendGroupMasterPlaylist(String groupName, PlaylistModel playlist);

}
