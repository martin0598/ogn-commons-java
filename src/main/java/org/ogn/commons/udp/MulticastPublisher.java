package org.ogn.commons.udp;

import static org.apache.commons.lang.Validate.isTrue;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MulticastPublisher {

	private static final Logger LOG = LoggerFactory.getLogger(MulticastPublisher.class);

	private final int defaultTtl;

	private MulticastSocket socket;

	public MulticastPublisher(int defaultTtl) {
		isTrue(0 <= defaultTtl && defaultTtl <= 255, "TTL value expected in range [0..255]");
		this.defaultTtl = defaultTtl;
		try {
			socket = new MulticastSocket();
			socket.setTimeToLive(defaultTtl);
		} catch (final Exception ex) {
			LOG.error("failed trying to create multicast socket", ex);
		}
	}

	public void stop() {
		socket.close();
	}

	public void send(String group, int port, byte[] msg) {
		send(group, port, defaultTtl, msg);
	}

	public void send(String group, int port, int ttl, byte[] msg) {
		try {
			final DatagramPacket pack = new DatagramPacket(msg, msg.length, InetAddress.getByName(group), port);
			socket.send(pack);
		} catch (final Exception ex) {
			LOG.error("failed while trying to send packet (size: {}) to multicast group: {} on port: {}", msg.length,
					group, port, ex);
		}
	}

}
