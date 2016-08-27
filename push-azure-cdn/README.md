## WowzaHLSPushPublishAzureCDN

A azure-blob-storage based storage implemenation (it can be the origin site for azure cdn)

### parameters

* storageImplementationClass: org.qrtt1.wse.pushpub.storage.azure.AzureBlobStorage
* azure.blob.connection.string: the connection string to create blog-storage client
* azure.blob.container: the container to upload
* azure.blob.prefix: the prefix for container

### example

```
inputStreamName={"entryName": "azure", "profile": "pushpub-bridge", "streamName": "outputStreamName", "destinationName": "cdn", "debugLog": "true", "azure.blob.connection.string": "[connection-string]", "azure.blob.container": "[container-name]", "storageImplementationClass": "org.qrtt1.wse.pushpub.storage.azure.AzureBlobStorage"}
```

pretty printed JSON (it should be one-line in the PushPublishMap.txt)：
```json
{
  "entryName": "azure",
  "profile": "pushpub-bridge",
  "streamName": "outputStreamName",
  "destinationName": "cdn",
  "debugLog": "true",
  "azure.blob.connection.string": "[connection-string]",
  "azure.blob.container": "[container-name]",
  "storageImplementationClass": "org.qrtt1.wse.pushpub.storage.azure.AzureBlobStorage"
}
```
