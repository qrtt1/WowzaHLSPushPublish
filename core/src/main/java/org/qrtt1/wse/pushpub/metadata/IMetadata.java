package org.qrtt1.wse.pushpub.metadata;

import java.util.HashMap;

import org.qrtt1.wse.pushpub.IPushStreamSource;

import com.wowza.wms.manifest.model.m3u8.MediaSegmentModel;
import com.wowza.wms.manifest.model.m3u8.PlaylistModel;

public interface IMetadata {
    
    public void configure(HashMap<String, String> dataMap, IPushStreamSource pushSource);

    public boolean updateGroupMasterPlaylistPlaybackURI(String groupName, PlaylistModel masterPlaylist);

    public boolean updateMasterPlaylistPlaybackURI(PlaylistModel playlist);

    public boolean updateMediaPlaylistPlaybackURI(PlaylistModel playlist);

    public boolean updateMediaSegmentPlaybackURI(MediaSegmentModel mediaSegment);

    public String getDestionationLogData();
}
