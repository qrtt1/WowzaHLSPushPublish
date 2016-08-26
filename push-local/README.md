## WowzaHLSPushLocalFS

A local filesystem storage implemenation

### parameters

* storageImplementationClass: org.qrtt1.wse.pushpub.storage.local.LocalStorage
* baseDir: a base path to save the playlist and segments. (default: `/tmp/LocalStorage`)

### example

```
inputStreamName={"entryName":"local", "profile":"publishb-bridge", "streamName":"outputStreamName", "destinationName":"filesystem", "debugLog":"true", "storageImplementationClass":"org.qrtt1.wse.pushpub.storage.local.LocalStorage"}
```

pretty printed JSON (it should be one-line in the PushPublishMap.txt)：
```json
{
    "entryName": "local",
    "profile": "publishb-bridge",
    "streamName": "outputStreamName",
    "destinationName": "filesystem",
    "debugLog": "true",
    "storageImplementationClass": "org.qrtt1.wse.pushpub.storage.local.LocalStorage"
}
```
