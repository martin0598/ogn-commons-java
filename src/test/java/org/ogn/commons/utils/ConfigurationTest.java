package org.ogn.commons.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ConfigurationTest {

	static {
		System.setProperty("prop1", "1");
		System.setProperty("prop2", "blah");
		System.setProperty("prop3", "true");
		System.setProperty("prop4", "false");
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		assertEquals(1, Configuration.getIntValue("MY_ENV1", "prop1", 10));
		assertEquals(10, Configuration.getIntValue("MY_ENV2", "prop2", 10));

		assertTrue(Configuration.getBooleanValue("MY_ENV3", "prop3"));
		assertFalse(Configuration.getBooleanValue("MY_ENV4", "prop2"));
		assertFalse(Configuration.getBooleanValue("MY_ENV4", "prop4"));
	}

}
