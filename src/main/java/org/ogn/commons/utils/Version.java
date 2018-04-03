package org.ogn.commons.utils;

/**
 * This toolkit class provides conversion of version (in X.Y.Z format) to int and from int to X.Y.Z string Expected
 * string version format: X.Y.Z
 * 
 * @author wbuczak
 */
public class Version {

	private static final int W1 = 100000;
	private static final int W2 = 1000;

	private Version() {

	}

	public static final String fromInt(final int version) {
		final int p1 = version / W1;
		final int p2 = ((version - p1 * W1)) / W2;
		final int p3 = version - (p1 * W1 + p2 * W2);

		return p1 + "." + p2 + "." + p3;
	}

	public static final int fromString(final String ver) {
		if (ver == null || ver.split("\\.").length != 3) {
			throw new IllegalArgumentException("incorrect version");
		}
		final String[] parts = ver.split("\\.");

		final int v1 = Integer.parseInt(parts[0]);
		final int v2 = Integer.parseInt(parts[1]);
		final int v3 = Integer.parseInt(parts[2]);

		if (v1 < 0 || v2 < 0 || v3 < 0)
			throw new IllegalArgumentException("incorrect version");

		return W1 * v1 + W2 * v2 + v3;
	}
}