package org.qrtt1.wse.pushpub;

import java.util.HashMap;

import org.qrtt1.wse.pushpub.metadata.IMetadata;
import org.qrtt1.wse.pushpub.metadata.MetadataFactory;
import org.qrtt1.wse.pushpub.storage.m3u8.IM3U8ModelStorage;
import org.qrtt1.wse.pushpub.storage.m3u8.M3U8StorageFactory;

import com.wowza.wms.manifest.model.m3u8.MediaSegmentModel;
import com.wowza.wms.manifest.model.m3u8.PlaylistModel;
import com.wowza.wms.pushpublish.protocol.cupertino.PushPublishHTTPCupertino;
import com.wowza.wms.server.LicensingException;

public class WowzaM3U8ModelPushPublisher extends PushPublishHTTPCupertino implements IPushStreamSource {

    IM3U8ModelStorage storage;
    IMetadata metadata;

    public WowzaM3U8ModelPushPublisher() throws LicensingException {
        super();
    }

    @Override
    public void load(HashMap<String, String> dataMap) {
        super.load(dataMap);

        createMetadata(dataMap);
        createStorage(dataMap);
    }

    protected void createStorage(HashMap<String, String> dataMap) {
        storage = M3U8StorageFactory.createByClass(dataMap.get("storageImplementationClass"));
        storage.configure(dataMap, this);
    }

    protected void createMetadata(HashMap<String, String> dataMap) {
        metadata = MetadataFactory.createByClass(dataMap.get("metadataImplementionClass"));
        metadata.configure(dataMap, this);
    }

    @Override
    public boolean updateGroupMasterPlaylistPlaybackURI(String groupName, PlaylistModel masterPlaylist) {
        return metadata.updateGroupMasterPlaylistPlaybackURI(groupName, masterPlaylist);
    }

    @Override
    public boolean updateMasterPlaylistPlaybackURI(PlaylistModel playlist) {
        return metadata.updateMasterPlaylistPlaybackURI(playlist);
    }

    @Override
    public boolean updateMediaPlaylistPlaybackURI(PlaylistModel playlist) {
        return metadata.updateMediaPlaylistPlaybackURI(playlist);
    }

    @Override
    public boolean updateMediaSegmentPlaybackURI(MediaSegmentModel mediaSegment) {
        return metadata.updateMediaSegmentPlaybackURI(mediaSegment);
    }

    @Override
    public int sendGroupMasterPlaylist(String groupName, PlaylistModel playlist) {
        return storage.sendGroupMasterPlaylist(groupName, playlist);
    }

    @Override
    public int sendMasterPlaylist(PlaylistModel playlist) {
        return storage.sendMasterPlaylist(playlist);
    }

    @Override
    public int sendMediaPlaylist(PlaylistModel playlist) {
        return storage.sendMediaPlaylist(playlist);
    }

    @Override
    public int sendMediaSegment(MediaSegmentModel mediaSegment) {
        return storage.sendMediaSegment(mediaSegment);
    }

    @Override
    public int deleteMediaSegment(MediaSegmentModel mediaSegment) {
        return storage.deleteMediaSegment(mediaSegment);
    }

    @Override
    public void setSendToBackupServer(boolean backup) {
    }

    @Override
    public boolean isSendToBackupServer() {
        return false;
    }

    @Override
    public boolean outputOpen() {
        return true;
    }

    @Override
    public boolean outputClose() {
        return true;
    }

    @Override
    public String getDestionationLogData() {
        return metadata.getDestionationLogData();
    }

}