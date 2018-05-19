package org.ogn.commons.igc;

import static java.util.concurrent.TimeUnit.SECONDS;
import static net.jadler.Jadler.closeJadler;
import static net.jadler.Jadler.initJadler;
import static net.jadler.Jadler.onRequest;
import static net.jadler.Jadler.port;
import static org.awaitility.Awaitility.await;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IgcUrlCacheTest {

	IgcUrlCache cache;

	@Before
	public void setUp() {
		initJadler();
	}

	@After
	public void tearDown() {
		closeJadler();
	}

	@Test
	public void test() throws Exception {

		onRequest().havingMethodEqualTo("GET").havingPathEqualTo("/igc/").respond()
				.withBody(getClass().getClassLoader().getResourceAsStream("igc_index_html/index_of_igc.html"))
				.withStatus(200);

		onRequest().havingMethodEqualTo("GET").havingPathEqualTo("/igc/2018-05-15/").respond()
				.withBody(
						getClass().getClassLoader().getResourceAsStream("igc_index_html/index_of_igc_2018-05-15.html"))
				.withStatus(200);
		onRequest().havingMethodEqualTo("GET").havingPathEqualTo("/igc/2018-05-16/").respond()
				.withBody(
						getClass().getClassLoader().getResourceAsStream("igc_index_html/index_of_igc_2018-05-16.html"))
				.withStatus(200);
		onRequest().havingMethodEqualTo("GET").havingPathEqualTo("/igc/2018-05-17/").respond()
				.withBody(
						getClass().getClassLoader().getResourceAsStream("igc_index_html/index_of_igc_2018-05-17.html"))
				.withStatus(200);

		cache = new IgcUrlCache("http://localhost:" + port() + "/igc/", 2);

		final String date = "2018-05-16";
		final String id = "4B43CD";

		await().atMost(3, SECONDS).until(() -> {
			return cache.getIgcFileUrl(date, id).isPresent();
		});
	}

}
