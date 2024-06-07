package software.xdev.eclipse.store.aws.s3.express;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StopWatch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@SpringBootApplication
public class Application implements CommandLineRunner
{
	private static final Logger LOG = LoggerFactory.getLogger(Application.class);

	private BenchmarkService benchmarkService;
	private DefaultS3StorageProvider defaultS3StorageProvider;
	private ExpressS3StorageProvider expressS3StorageProvider;

	public static void main(String[] args)
	{
		SpringApplication.run(Application.class, args);
	}

	@Autowired
	public Application(
		BenchmarkService benchmarkService, DefaultS3StorageProvider defaultS3StorageProvider,
		ExpressS3StorageProvider expressS3StorageProvider)
	{
		this.benchmarkService = benchmarkService;
		this.defaultS3StorageProvider = defaultS3StorageProvider;
		this.expressS3StorageProvider = expressS3StorageProvider;
	}


	@Override public void run(String... args) throws Exception
	{
		testMethod(this.benchmarkService::defaultTest, "baseTest");
	}

	private void testMethod(Consumer<EmbeddedStorageProvider> methodToTest, String name)
	{
		LOG.info("---------TESTING METHOD %s - EXPRESS---------".formatted(name));
		testMethod(() -> methodToTest.accept(expressS3StorageProvider));
		LOG.info("---------TESTING METHOD %s - DEFAULT---------".formatted(name));
		testMethod(() -> methodToTest.accept(defaultS3StorageProvider));
	}

	private void testMethod(Runnable methodToTest)
	{
		List<Duration> durationList = new ArrayList<>();
		LOG.info("Warmup...");
		for(int warmupIndex = 0; warmupIndex < 2; warmupIndex++)
		{
			LOG.info("Warmup %d".formatted(warmupIndex));
			methodToTest.run();
		}
		LOG.info("Warmup done.");
		LOG.info("Measuring...");
		for(int i = 0; i < 10; i++)
		{
			StopWatch sw = new StopWatch();
			LOG.info("Measuring %d...".formatted(i));
			sw.start();
			methodToTest.run();
			sw.stop();
			durationList.add(Duration.ofNanos(sw.getTotalTimeNanos()));
		}
		LOG.info("Done measuring.");
		double averageDuration =
			durationList.stream().collect(Collectors.averagingInt(Duration::getNano)).doubleValue();
		LOG.info(
			String.format(
				Locale.ENGLISH,
				"Average of one run: %,.0fms.",
				averageDuration / 1_000_000.0
			)
		);
	}
}
