package org.qrtt1.wse.pushpub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

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
        return storage.sendGroupMasterPlaylist(groupName, playlist.getUri().toString(), asBytes(playlist));
    }

    @Override
    public int sendMasterPlaylist(PlaylistModel playlist) {
        return storage.sendMasterPlaylist(playlist.getUri().toString(), asBytes(playlist));
    }

    @Override
    public int sendMediaPlaylist(PlaylistModel playlist) {
        return storage.sendMediaPlaylist(playlist.getUri().toString(), asBytes(playlist));
    }

    @Override
    public int sendMediaSegment(MediaSegmentModel mediaSegment) {
        PacketFragmentList list = mediaSegment.getFragmentList();
        if (list == null) {
            return 1;
        }

        return storage.sendMediaSegment(mediaSegment.getUri().toString(), list.toByteArray());
    }
    
    @Override
    public int deleteMediaSegment(MediaSegmentModel mediaSegment) {
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