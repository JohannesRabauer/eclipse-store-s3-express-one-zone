package software.xdev.eclipse.store.aws.s3.express;

import org.eclipse.store.afs.aws.s3.types.S3Connector;
import org.eclipse.store.afs.blobstore.types.BlobStoreFileSystem;
import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

@Component
public class DefaultS3StorageProvider implements EmbeddedStorageProvider
{
	@Value("${eclipse.store.s3.default.region}")
	private String defaultRegion;
	@Value("${eclipse.store.s3.default.access.key}")
	private String defaultAccessKey;
	@Value("${eclipse.store.s3.default.secret.access.key}")
	private String defaultSecretAccessKey;
	@Value("${eclipse.store.s3.default.bucket}")
	private String defaultBucket;

	public EmbeddedStorageManager createEmbeddedStorageManager(
		Object root,
		String region, String accessKey, String secretAccessKey, String bucket)
	{

		S3ClientBuilder clientBuilder =
			S3Client
				.builder()
				.region(Region.of(region))
				.credentialsProvider(
					StaticCredentialsProvider.create(new AwsCredentials()
					{
						@Override public String accessKeyId()
						{
							return accessKey;
						}

						@Override public String secretAccessKey()
						{
							return secretAccessKey;
						}
					})
				);

		BlobStoreFileSystem fileSystem = BlobStoreFileSystem.New(
			S3Connector.Caching(clientBuilder.build())
		);
		return EmbeddedStorage.start(
			root,
			fileSystem.ensureDirectoryPath(bucket)
		);
	}
	@Override public EmbeddedStorageManager getEmbeddedStorageManager(Object root)
	{
		return createEmbeddedStorageManager(
			root,
			defaultRegion,
			defaultAccessKey,
			defaultSecretAccessKey,
			defaultBucket
		);
	}
}
