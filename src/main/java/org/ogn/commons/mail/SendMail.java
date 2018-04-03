package org.ogn.commons.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {

	public static final int DEFAULT_SNMP_PORT = 587;
	final String snmpHost;
	final int snmpPort;
	final String snmpUser;
	final String snmpPass;

	public SendMail(String snmpHost, int snmpPort, String snmpUser, String snmpPass) {
		this.snmpHost = snmpHost;
		this.snmpPort = snmpPort;
		this.snmpUser = snmpUser;
		this.snmpPass = snmpPass;
	}

	public SendMail(String snmpHost, String snmpUser, String snmpPass) {
		this(snmpHost, DEFAULT_SNMP_PORT, snmpUser, snmpPass);
	}

	public void send(String[] to, String[] cc, String[] bcc, String subject, String emailBody)
			throws MessagingException {

		Properties mailServerProperties;
		Session getMailSession;
		MimeMessage generateMailMessage;

		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", String.valueOf(snmpPort));
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");

		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		generateMailMessage = new MimeMessage(getMailSession);

		for (final String to_ : to)
			generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to_));

		if (cc != null)
			for (final String cc_ : cc)
				generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(cc_));

		if (bcc != null)
			for (final String bcc_ : bcc)
				generateMailMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc_));

		generateMailMessage.setSubject(subject);

		generateMailMessage.setContent(emailBody, "text/html");

		final Transport transport = getMailSession.getTransport("smtp");

		transport.connect(snmpHost, snmpUser, snmpPass);
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();
	}

	public void send(String to, String cc, String subject, String emailBody) throws MessagingException {
		send(new String[] { to }, new String[] { cc }, null, subject, emailBody);
	}

	public void send(String to, String subject, String emailBody) throws MessagingException {
		send(new String[] { to }, null, null, subject, emailBody);
	}
}