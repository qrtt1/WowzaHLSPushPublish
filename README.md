
## A Simple Brdige Module for PushPublish HLS

All implementation are inspired from the [wse-example-pushpublish-hls](https://github.com/WowzaMediaSystems/wse-example-pushpublish-hls) example project.

## How to extend it

Just implement the interface [ISimpleStorage](https://github.com/qrtt1/WowzaHLSPushPublish/blob/master/api/src/main/java/org/qrtt1/wse/pushpub/storage/ISimpleStorage.java)

```java
public interface ISimpleStorage {

    public void configure(HashMap<String, String> dataMap, IPushStreamSource pushSource);

    public int deleteMediaSegment(String uri);

    public int sendMediaSegment(String uri, byte[] data);

    public int sendMediaPlaylist(String uri, byte[] playlist);

    public int sendMasterPlaylist(String uri, byte[] playlist);

    public int sendGroupMasterPlaylist(String groupName, String uri, byte[] playlist);

    public void onDisconnect();

}
```

Any configurations can be set into dataMap (`PushPublishMap.txt`)

## Build BridgeModule

configure the `gradle.properties` to set your WowzaStreamingEngine installation path

```
WOWZA_LIBS=[point-to-your-wowza-streaming-engine]/lib
```

```
gradle bundleModule
```

>> other prerequisites are same with [wse-example-pushpublish-hls](https://github.com/WowzaMediaSystems/wse-example-pushpublish-hls) project.
