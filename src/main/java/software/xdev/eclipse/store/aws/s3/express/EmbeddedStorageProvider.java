package software.xdev.eclipse.store.aws.s3.express;

import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;


public interface EmbeddedStorageProvider
{
	EmbeddedStorageManager getEmbeddedStorageManager(Object root);
}
