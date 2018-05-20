package org.ogn.commons.igc;

import static java.util.concurrent.TimeUnit.SECONDS;
import static net.jadler.Jadler.closeJadler;
import static net.jadler.Jadler.initJadler;
import static net.jadler.Jadler.onRequest;
import static net.jadler.Jadler.port;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.ogn.commons.igc.IgcUrlCache.replaceIgcFileUrlHostAndPort;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IgcUrlCacheTest {

	IgcUrlCache cache;

	@Before
	public void setUp() {
		initJadler();

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
	}

	@After
	public void tearDown() {
		closeJadler();
	}

	@Test
	public void test1() throws Exception {

		cache = new IgcUrlCache("http://localhost:" + port() + "/igc/", 2);

		final String date = "2018-05-16";
		final String id = "4B43CD";

		await().atMost(8, SECONDS).until(() -> {
			return cache.getIgcFileUrl(date, id).isPresent();
		});

		assertTrue(cache.getIgcFileUrl(date, id).get().matches("http://localhost:.*IGC$"));
	}

	@Test
	public void test2() throws Exception {

		cache = new IgcUrlCache("http://localhost:" + port() + "/igc/", "ognstats.ddns.net", 2);

		final String date = "2018-05-16";
		final String id = "4B43CD";

		await().atMost(3, SECONDS).until(() -> {
			return cache.getIgcFileUrl(date, id).isPresent();
		});

		assertTrue(cache.getIgcFileUrl(date, id).get().matches("http://ognstats.ddns.net/igc/2018-05-16/.*IGC$"));
	}

	@Test
	public void test3() {
		assertEquals("http://ognstats.ddns.net:8080/igc/2018-05-17/2018-05-17_PAW406D47_28_28.IGC",
				replaceIgcFileUrlHostAndPort("http://localhost:62887/igc/2018-05-17/2018-05-17_PAW406D47_28_28.IGC",
						"ognstats.ddns.net:8080"));

		assertEquals("http://ognstats.ddns.net/igc/2018-05-17/2018-05-17_PAW406D47_28_28.IGC",
				replaceIgcFileUrlHostAndPort("http://localhost:62887/igc/2018-05-17/2018-05-17_PAW406D47_28_28.IGC",
						"ognstats.ddns.net"));
	}

}
