package software.xdev.eclipse.store.aws.s3.express;

import org.eclipse.store.afs.aws.s3.types.S3Connector;
import org.eclipse.store.afs.blobstore.types.BlobStoreFileSystem;
import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

@Service
public class BenchmarkService
{
	public void defaultTest(EmbeddedStorageProvider embeddedStorageProvider)
	{
		Object root = "test";
		try(EmbeddedStorageManager embeddedStorageManager = embeddedStorageProvider.getEmbeddedStorageManager(root))
		{
			embeddedStorageManager.storeRoot();
		}
	}

}
