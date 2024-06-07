package software.xdev.eclipse.store.aws.s3.express;

import org.eclipse.store.afs.aws.s3.types.S3Connector;
import org.eclipse.store.afs.blobstore.types.BlobStoreFileSystem;
import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;


@Component
public class ExpressS3StorageProvider implements EmbeddedStorageProvider
{
	@Value("${eclipse.store.s3.express.region}")
	private String region;
	@Value("${eclipse.store.s3.express.access.key}")
	private String accessKey;
	@Value("${eclipse.store.s3.express.secret.access.key}")
	private String secretAccessKey;
	@Value("${eclipse.store.s3.express.bucket}")
	private String bucket;
	@Value("${eclipse.store.s3.express.endpoint}")
	private String endpoint;

	public EmbeddedStorageManager createEmbeddedStorageManager(
		Object root,
		String region, String accessKey, String secretAccessKey, String bucket)
	{
		S3ClientBuilder clientBuilder =
			S3Client
				.builder()
				.region(Region.of(region))
				.endpointOverride(URI.create(endpoint))
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

//		testS3Client(clientBuilder.build());
//		return null;

		BlobStoreFileSystem fileSystem = BlobStoreFileSystem.New(
			S3Connector.Caching(clientBuilder.build())
		);
		return EmbeddedStorage.start(
			root,
			fileSystem.ensureDirectoryPath(bucket)
		);
	}

	private void testS3Client(S3Client client)
	{
		ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucket).prefix("ksdf.").build();

		ListObjectsV2Response listBucketsResponse = client.listObjectsV2(request);

		PutObjectRequest request2 = PutObjectRequest.builder().bucket(bucket).key("test123").build();
		RequestBody body = RequestBody.empty();
		client.putObject(request2, body);
	}

	@Override public EmbeddedStorageManager getEmbeddedStorageManager(Object root)
	{
		return createEmbeddedStorageManager(
			root,
			region,
			accessKey,
			secretAccessKey,
			bucket
		);
	}
}
