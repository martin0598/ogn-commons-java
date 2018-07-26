package org.ogn.commons.igc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;

/**
 * This class implements a simple in-memory cache for keeping the URLs of IGC files stored on a remote HTTP server
 * 
 * @author buczakwk
 *
 */
public class IgcUrlCache {

	private static final Logger LOG = LoggerFactory.getLogger(IgcUrlCache.class);

	private static final String IGC_FILE_NAME_PATTERN = "^http://.*(?<date>\\d{4}-\\d{2}-\\d{2})_.*(?<id>[0-9A-F]{6})_.*?$";

	private static final Pattern pattern = Pattern.compile(IGC_FILE_NAME_PATTERN);

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();

	private final Map<String, Map<String, String>> cache = new HashMap<>();

	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	private final String url;
	private final String igcFileUrlHostPort;

	private final int refreshRate;

	// cache
	// |
	// |-date1
	// |..|
	// |..|
	// |..|->{map}
	// |.......|-id1 -> file_url
	// |.......|-id2 -> file_url
	// |
	// |-date2
	// ...|
	// ...|->{map}
	// ........|-id1 -> file_url
	// ........|-id2 -> file_url
	// ........|-idx -> file_url

	public static String replaceIgcFileUrlHostAndPort(String link, String newHostPort) {
		final Pattern patt = Pattern.compile("^http://(.+)/.+/\\d{4}-\\d{2}-\\d{2}/.*IGC$");
		final Matcher m = patt.matcher(link);

		while (m.find()) {
			final String text = m.group(1);
			return link.replace(text, newHostPort);
		}

		return link;
	}

	public IgcUrlCache(final String baseUrl) {
		this(baseUrl, null, 5 * 60); // default: 5 min
	}

	public IgcUrlCache(final String baseUrl, final String igcFileUrlHostPort) {
		this(baseUrl, igcFileUrlHostPort, 5 * 60); // default: 5 min
	}

	public IgcUrlCache(final String baseUrl, int refreshRateInSec) {
		this(baseUrl, null, refreshRateInSec); // default: 5 min
	}

	public IgcUrlCache(final String baseUrl, String igcFileUrlHostPort, int refreshRateInSec) {
		this.url = baseUrl;
		this.igcFileUrlHostPort = igcFileUrlHostPort;
		this.refreshRate = refreshRateInSec;
		executor.scheduleAtFixedRate(this::reload, 0, this.refreshRate, TimeUnit.SECONDS);
	}

	public Optional<String> getIgcFileUrl(String date, String deviceId) {
		Optional<String> result = Optional.empty();

		readLock.lock();
		if (cache.containsKey(date)) {
			result = Optional.ofNullable(cache.get(date).getOrDefault(deviceId, null));
		}
		readLock.unlock();

		return result;
	}

	public void reload() {
		LOG.debug("reloading cache, url: {}", url);
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (final Exception ex) {
			LOG.error("exception caught while trying to contact URL: " + url, ex);
			return;
		}

		final Elements links = doc.select("a[href~=(\\d{4}-\\d{2}-\\d{2})");

		writeLock.lock();
		cache.clear();

		for (final Element link : links) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("*******************");
				LOG.trace(link.absUrl("href"));
				LOG.trace("*******************");
			}

			try {
				doc = Jsoup.connect(link.absUrl("href")).get();
				final Elements links2 = doc.select("a[href$=.IGC]");
				for (final Element lnk : links2) {
					// LOG.info("before: " + lnk.absUrl("href"));

					String furl = lnk.absUrl("href");
					if (null != igcFileUrlHostPort)
						furl = replaceIgcFileUrlHostAndPort(lnk.absUrl("href"), igcFileUrlHostPort);

					if (LOG.isTraceEnabled())
						LOG.trace(furl);
					// LOG.info("after: " + lnk);

					final Matcher m = pattern.matcher(furl);
					if (m.matches()) {
						if (LOG.isTraceEnabled())
							LOG.trace("date: {} id: {}", m.group("date"), m.group("id"));
						cache.putIfAbsent(m.group("date"), new HashMap<>());
						cache.get(m.group("date")).putIfAbsent(m.group("id"), furl);
					}

				}
			} catch (final Exception ex) {
				LOG.error("exception caught while trying to contact URL: " + link.absUrl("href"), ex);
				return;
			}
		}
		writeLock.unlock();
	}

}
