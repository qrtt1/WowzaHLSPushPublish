package org.qrtt1.wse.pushpub.storage.azure;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.qrtt1.wse.pushpub.IPushStreamSource;
import org.qrtt1.wse.pushpub.storage.ISimpleStorage;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.CopyState;
import com.microsoft.azure.storage.blob.CopyStatus;

public class AzureBlobStorage implements ISimpleStorage {

    private static final int SEGMENT_CACHE_TIME = 60 * 5;
    private IPushStreamSource pushSource;
    private HashMap<String, String> dataMap = new HashMap<String, String>();

    private CloudBlobClient client;
    private CloudBlobContainer container;
    private String prefix = "";
    private Vector<String> deleteQueue = new Vector<String>();

    @Override
    public void configure(HashMap<String, String> dataMap, IPushStreamSource pushSource) {
        pushSource.logInfo("configure", "" + dataMap);
        this.pushSource = pushSource;
        this.dataMap.putAll(dataMap);

        if (dataMap.containsKey("azure.blob.prefix")) {
            prefix = dataMap.get("azure.blob.prefix");
        }

        if (client != null) {
            return;
        }

        try {
            client = CloudStorageAccount.parse(dataMap.get("azure.blob.connection.string")).createCloudBlobClient();
            container = client.getContainerReference(dataMap.get("azure.blob.container"));
        } catch (Exception e) {
            pushSource.logError("configure", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public int deleteMediaSegment(String uri) {
        if (deleteQueue.size() < 5) {
            deleteQueue.add(uri);
            return 1;
        }
        doRemoveBlob(deleteQueue.remove(0));
        return 1;
    }

    protected void doRemoveBlob(String uri) {
        try {
            container.getBlockBlobReference(withPrefix(uri)).delete();
        } catch (Exception e) {
            pushSource.logError("deleteMediaSegment", e);
        }
    }

    @Override
    public int sendMediaSegment(String uri, byte[] data) {
        try {
            return uploadFile(withPrefix(uri), data, SEGMENT_CACHE_TIME);
        } catch (Exception e) {
            pushSource.logError("sendMediaSegment", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public int sendMediaPlaylist(String uri, byte[] playlist) {
        try {
            return uploadFile(withPrefix(uri), playlist, 0);
        } catch (Exception e) {
            e.printStackTrace();
            pushSource.logError("sendMediaPlaylist", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public int sendMasterPlaylist(String uri, byte[] playlist) {
        try {
            return uploadFile(withPrefix(uri), playlist, 0);
        } catch (Exception e) {
            pushSource.logError("sendMasterPlaylist", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public int sendGroupMasterPlaylist(String groupName, String uri, byte[] playlist) {
        try {
            return uploadFile(withPrefix(groupName + "/" + uri), playlist, 0);
        } catch (Exception e) {
            pushSource.logError("sendGroupMasterPlaylist", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public void onDisconnect() {
        // no action
    }

    protected int uploadFile(String key, byte[] data, int age)
            throws URISyntaxException, StorageException, IOException {
        pushSource.logInfo("uploadFile", "upload to " + key);
        if (StringUtils.contains(key, "m3u8")) {
            URI uri = doDirectUploadFile(key + "_", data, age);
            CloudBlockBlob blob = container.getBlockBlobReference(key);
            doUploadFileByCopy(uri, blob);
        } else {
            doDirectUploadFile(key, data, age);
        }

        return 1;
    }

    protected boolean doUploadFileByCopy(URI uri, CloudBlockBlob blob) throws StorageException {
        long startTime = System.currentTimeMillis();
        String copyId = blob.startCopy(uri);
        while (true) {

            if (System.currentTimeMillis() - startTime > 1000 * 15) {
                pushSource.logError("doUploadFileByCopy",
                        "start-copy-timeout: " + (System.currentTimeMillis() - startTime));
                return false;
            }

            CopyState copyState = blob.getCopyState();
            if (!StringUtils.equals(copyState.getCopyId(), copyId)) {
                pushSource.logError("", "invalid copyIds: " + copyState.getCopyId() + ", " + copyId);
                return false;
            }
            pushSource.logInfo("uploadFileByCopy", "status: " + copyState.getStatus());
            if (copyState.getStatus() == CopyStatus.SUCCESS) {
                return true;
            }
            if (copyState.getStatus() == CopyStatus.ABORTED) {
                break;
            }
            if (copyState.getStatus() == CopyStatus.FAILED) {
                break;
            }
            if (copyState.getStatus() == CopyStatus.PENDING) {
                sleep("wait for " + CopyStatus.PENDING);
                continue;
            }
            if (copyState.getStatus() == CopyStatus.UNSPECIFIED) {
                sleep("wait for " + CopyStatus.UNSPECIFIED);
                continue;
            }
        }

        return false;
    }

    protected void sleep(String message) {
        try {
            pushSource.logInfo("sleep", message);
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
    }

    protected URI doDirectUploadFile(String key, byte[] data, int age) throws URISyntaxException, StorageException {
        CloudBlockBlob blob = container.getBlockBlobReference(key);
        try {
            blob.upload(new ByteArrayInputStream(data), data.length);
            blob.getProperties().setCacheControl("public, max-age=" + age);
            blob.uploadProperties();

        } catch (Exception e) {
            pushSource.logError("uploadFile", e.getMessage(), e);
        }
        return blob.getUri();
    }

    private String withPrefix(String uri) {
        uri = StringUtils.replace(uri, "../", "");
        uri = StringUtils.replace(uri, "./", "");

        if (StringUtils.isEmpty(prefix)) {
            return uri;
        }
        if (StringUtils.endsWith(prefix, "/")) {
            return prefix + uri;
        }
        return prefix + "/" + uri;
    }

}
