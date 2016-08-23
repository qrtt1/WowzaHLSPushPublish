package org.qrtt1.wse.pushpub.metadata;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.qrtt1.wse.pushpub.IPushStreamSource;

import com.wowza.wms.manifest.model.m3u8.MediaSegmentModel;
import com.wowza.wms.manifest.model.m3u8.PlaylistModel;

public class DefaultMetadata implements IMetadata {

    private HashMap<String, String> dataMap = new HashMap<String, String>();
    private IPushStreamSource pushSource;

    @Override
    public void configure(HashMap<String, String> dataMap, IPushStreamSource pushSource) {
        this.pushSource = pushSource;
        if (dataMap != null) {
            pushSource.logInfo("configureDataMap", "" + dataMap);
            this.dataMap.putAll(dataMap);
        }
    }

    @Override
    public boolean updateGroupMasterPlaylistPlaybackURI(String groupName, PlaylistModel masterPlaylist) {
        boolean retVal = true;
        String newPath = basePath() + groupName + "/" + masterPlaylist.getUri().getPath();
        try {
            masterPlaylist.setUri(new URI(newPath));
        } catch (Exception e) {
            pushSource.logError("updateGroupMasterPlaylistPlaybackURI", "Invalid path " + newPath, e);
            retVal = false;
        }
        return retVal;
    }

    @Override
    public boolean updateMasterPlaylistPlaybackURI(PlaylistModel playlist) {
        boolean retVal = true;
        String path = basePath() + pushSource.getDstStreamName() + "/" + playlist.getUri().toString();
        try {
            playlist.setUri(new URI(path));
        } catch (URISyntaxException e) {
            pushSource.logError("updateMasterPlaylistPlaybackURI", "Failed to update master playlist to " + path);
            retVal = false;
        }
        return retVal;
    }

    @Override
    public boolean updateMediaPlaylistPlaybackURI(PlaylistModel playlist) {
        boolean retVal = true;

        String path = basePath() + pushSource.getDstStreamName() + "/" + playlist.getUri().toString();
        try {
            playlist.setUri(new URI(path));
        } catch (URISyntaxException e) {
            pushSource.logError("updateMediaPlaylistPlaybackURI", "Failed to update media playlist to " + path);
            retVal = false;
        }
        return retVal;
    }

    @Override
    public boolean updateMediaSegmentPlaybackURI(MediaSegmentModel mediaSegment) {
        boolean retVal = true;
        String newPath = mediaSegment.getUri().getPath();

        // to prevent overriding prior segments if the stream were to reset,
        // we'll use the sessionStr to create a sub directory to keep the
        // media segments in.

        try {
            String temp = pushSource.getRandomSessionStr() + "/" + newPath;
            mediaSegment.setUri(new URI(temp));
        } catch (Exception e) {
            retVal = false;
            pushSource.logError("updateMediaSegmentPlaybackURI", "Invalid path " + newPath, e);
        }
        return retVal;
    }

    @Override
    public String getDestionationLogData() {
        return "defaultMetadata";
    }

    protected String basePath() {
        if (dataMap.containsKey("storage.basePath")) {
            return dataMap.get("storage.basePath");
        }
        return "../";
    }

}
