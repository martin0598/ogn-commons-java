/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.commons.utils;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

/**
 * Helper class providing JSON serialization & de-serialization utility methods
 * 
 * @author wbuczak
 */
public class JsonUtils {

	private JsonUtils() {

	}

	private static ObjectMapper objectMapper;

	private static final Logger LOG = LoggerFactory.getLogger(JsonUtils.class);

	private static ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
			objectMapper.registerModule(new Jdk8Module());
			objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		}

		return objectMapper;
	}

	public static <T> T fromJson(final String json, Class<T> clazz) {
		try {
			return getObjectMapper().readValue(new ByteArrayInputStream(json.getBytes()), clazz);
		} catch (final Exception ex) {
			LOG.error("deserialization from JSON failed", ex);
		}
		return null;
	}

	public static String toJson(Object obj) {
		String result = null;

		final StringWriter str = new StringWriter();

		try {
			getObjectMapper().writeValue(str, obj);
			result = str.toString();
		} catch (final Exception ex) {
			LOG.error("serialization to JSON failed", ex);
		}

		return result;
	}

}