package org.qrtt1.wse.pushpub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.qrtt1.wse.pushpub.metadata.IMetadata;
import org.qrtt1.wse.pushpub.metadata.MetadataFactory;
import org.qrtt1.wse.pushpub.storage.ISimpleStorage;
import org.qrtt1.wse.pushpub.storage.SimpleStorageFactory;

import com.wowza.util.PacketFragmentList;
import com.wowza.wms.manifest.model.m3u8.MediaSegmentModel;
import com.wowza.wms.manifest.model.m3u8.PlaylistModel;
import com.wowza.wms.manifest.writer.m3u8.PlaylistWriter;
import com.wowza.wms.pushpublish.protocol.cupertino.PushPublishHTTPCupertino;
import com.wowza.wms.server.LicensingException;

public class SimpleStoragePushPublisher extends PushPublishHTTPCupertino implements IPushStreamSource {

    ISimpleStorage storage;
    IMetadata metadata;
    final String publishId = UUID.randomUUID().toString();

    public SimpleStoragePushPublisher() throws LicensingException {
        super();
    }

    @Override
    public void load(HashMap<String, String> dataMap) {
        super.load(dataMap);
        createMetadata(dataMap);
        createStorage(dataMap);
    }

    protected void createStorage(HashMap<String, String> dataMap) {
        storage = SimpleStorageFactory.createByClass(dataMap.get("storageImplementationClass"));
        storage.configure(dataMap, this);
        logInfo("createStorage", "" + storage + " for PUBLISH[" + publishId + "] => " + dataMap);
    }

    protected void createMetadata(HashMap<String, String> dataMap) {
        metadata = MetadataFactory.createByClass(dataMap.get("metadataImplementionClass"));
        metadata.configure(dataMap, this);
        logInfo("createMetadata", "" + metadata + " for PUBLISH[" + publishId + "] => " + dataMap);
    }

    @Override
    public boolean updateGroupMasterPlaylistPlaybackURI(String groupName, PlaylistModel masterPlaylist) {
        logInfo("updateGroupMasterPlaylistPlaybackURI", "by " + metadata);
        return metadata.updateGroupMasterPlaylistPlaybackURI(groupName, masterPlaylist);
    }

    @Override
    public boolean updateMasterPlaylistPlaybackURI(PlaylistModel playlist) {
        logInfo("updateMasterPlaylistPlaybackURI", "by " + metadata);
        return metadata.updateMasterPlaylistPlaybackURI(playlist);
    }

    @Override
    public boolean updateMediaPlaylistPlaybackURI(PlaylistModel playlist) {
        logInfo("updateMediaPlaylistPlaybackURI", "by " + metadata);
        return metadata.updateMediaPlaylistPlaybackURI(playlist);
    }

    @Override
    public boolean updateMediaSegmentPlaybackURI(MediaSegmentModel mediaSegment) {
        logInfo("updateMediaSegmentPlaybackURI", "by " + metadata);
        return metadata.updateMediaSegmentPlaybackURI(mediaSegment);
    }

    @Override
    public int sendGroupMasterPlaylist(String groupName, PlaylistModel playlist) {
        logInfo("sendGroupMasterPlaylist", "by " + storage);
        return storage.sendGroupMasterPlaylist(groupName, playlist.getUri().toString(), asBytes(playlist));
    }

    @Override
    public int sendMasterPlaylist(PlaylistModel playlist) {
        logInfo("sendMasterPlaylist", "by " + storage);
        return storage.sendMasterPlaylist(playlist.getUri().toString(), asBytes(playlist));
    }

    @Override
    public int sendMediaPlaylist(PlaylistModel playlist) {
        logInfo("sendMediaPlaylist", "by " + storage);
        return storage.sendMediaPlaylist(playlist.getUri().toString(), asBytes(playlist));
    }

    @Override
    public int sendMediaSegment(MediaSegmentModel mediaSegment) {
        logInfo("sendMediaSegment", "by " + storage);

        PacketFragmentList list = mediaSegment.getFragmentList();
        if (list == null) {
            return 1;
        }

        return storage.sendMediaSegment(mediaSegment.getUri().toString(), list.toByteArray());
    }

    @Override
    public int deleteMediaSegment(MediaSegmentModel mediaSegment) {
        logInfo("deleteMediaSegment", "by " + storage);
        return storage.deleteMediaSegment(mediaSegment.getUri().toString());
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

    private byte[] asBytes(PlaylistModel playlist) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PlaylistWriter writer = new PlaylistWriter(output, getContextStr());
        try {
            if (writer.write(playlist)) {
                return output.toByteArray();
            }
        } catch (IOException e) {
            logError("asBytes", e);
        }
        return output.toByteArray();
    }

    @Override
    public void disconnect(boolean hard) {
        super.disconnect(hard);
        storage.onDisconnect();
    }

}