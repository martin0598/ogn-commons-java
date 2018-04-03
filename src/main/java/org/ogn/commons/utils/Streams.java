/**
 * Copyright (c) 2014 OGN, All Rights Reserved.
 */

package org.ogn.commons.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class Streams {

	private static final int BUFFER_SIZE = 64 * 1024;

	private Streams() {

	}

	/**
	 * Copy ALL available data from one stream into another
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void copy(InputStream in, OutputStream out) throws IOException {

		try (final ReadableByteChannel source = Channels.newChannel(in);
				final WritableByteChannel target = Channels.newChannel(out)) {
			final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
			while (source.read(buffer) != -1) {
				buffer.flip(); // Prepare the buffer to be drained
				while (buffer.hasRemaining()) {
					target.write(buffer);
				}
				buffer.clear(); // Empty buffer to get ready for filling
			}
		}

	}
}