package org.qrtt1.wse.pushpub.storage.aws;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.qrtt1.wse.pushpub.IPushStreamSource;
import org.qrtt1.wse.pushpub.storage.ISimpleStorage;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class AwsSimpleStorage implements ISimpleStorage {

    private static final int SEGMENT_CACHE_TIME = 60 * 5;
    private IPushStreamSource pushSource;
    private HashMap<String, String> dataMap = new HashMap<String, String>();

    private AmazonS3 client;
    private String bucket;
    private String prefix = "";
    private Vector<String> deleteQueue = new Vector<String>();

    @Override
    public void configure(HashMap<String, String> dataMap, IPushStreamSource pushSource) {
        pushSource.logInfo("configure", "" + dataMap + " @" + AwsSimpleStorage.class);
        this.pushSource = pushSource;
        this.dataMap.putAll(dataMap);

        if (dataMap.containsKey("aws.s3.prefix")) {
            prefix = dataMap.get("aws.s3.prefix");
        }
        pushSource.logInfo("configure", "s3-prefix: " + prefix);
        if (client != null) {
            pushSource.logInfo("configure", "returning by client: " + client);
            return;
        }

        String accessKey = null;
        String secretKey = null;
        if (hasCredentials(dataMap)) {
            accessKey = dataMap.get("aws.s3.accessKey");
            secretKey = dataMap.get("aws.s3.secretKey");
            bucket = dataMap.get("aws.s3.bucket");
        }

        try {
            pushSource.logInfo("configure", "creating s3-client");
            client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
            pushSource.logInfo("configure", "create-s3-client: " + client);
            pushSource.logInfo("configure", "using bucket: " + bucket);
        } catch (Exception e) {
            pushSource.logError("configure", e);
        }
    }

    protected boolean hasCredentials(HashMap<String, String> dataMap) {
        return dataMap.containsKey("aws.s3.accessKey") && dataMap.containsKey("aws.s3.secretKey")
                && dataMap.containsKey("aws.s3.bucket");
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
            client.deleteObject(bucket, withPrefix(uri));
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

    protected int uploadFile(String key, byte[] data, int age) throws URISyntaxException, IOException {
        pushSource.logInfo("uploadFile", "upload to " + key);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(data.length);
        metadata.setCacheControl("max-age=" + age);

        PutObjectRequest request = new PutObjectRequest(bucket, key, new ByteArrayInputStream(data), metadata);
        request.setCannedAcl(CannedAccessControlList.PublicRead);
        client.putObject(request);

        return 1;
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
