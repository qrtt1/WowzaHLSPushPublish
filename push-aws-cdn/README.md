## WowzaHLSPushPublishAwsCDN

A s3-based storage implemenation (it can be the origin site for aws cloudfront)

### parameters

* storageImplementationClass: org.qrtt1.wse.pushpub.storage.aws.AwsSimpleStorage
* aws.s3.accessKey: the access key for the s3 client
* aws.s3.secretKey: the secret key for the s3 client
* aws.s3.bucket: the bucket to upload
* aws.s3.prefix: the prefix to upload

### example

```
inputStreamName={"entryName": "aws", "profile": "pushpub-bridge", "streamName": "outputStreamName", "destinationName": "cdn", "debugLog": "true", "storageImplementationClass": "org.qrtt1.wse.pushpub.storage.aws.AwsSimpleStorage", "aws.s3.prefix": "live/", "aws.s3.accessKey": "[access-key]", "aws.s3.secretKey": "[secret-key]", "aws.s3.bucket": "[bucket]"}
```

pretty printed JSON (it should be one-line in the PushPublishMap.txt)：
```json
{
  "entryName": "aws",
  "profile": "pushpub-bridge",
  "streamName": "outputStreamName",
  "destinationName": "cdn",
  "debugLog": "true",
  "storageImplementationClass": "org.qrtt1.wse.pushpub.storage.aws.AwsSimpleStorage",
  "aws.s3.prefix": "live/",
  "aws.s3.accessKey": "[access-key]",
  "aws.s3.secretKey": "[secret-key]",
  "aws.s3.bucket": "[bucket]"
}
```
