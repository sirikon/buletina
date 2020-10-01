package me.sirikon.buletina.services;

import me.sirikon.buletina.configuration.Configuration;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

@Singleton
public class Mailer {

  private final Configuration configuration;

  @Inject
  public Mailer(final Configuration configuration) {
    this.configuration = configuration;
  }

  public void sendEmail(final String recipient, final String subject, final String textContent, final String htmlContent) {
    final var properties = new Properties();
    properties.put("mail.transport.protocol", "smtp");
    properties.put("mail.smtp.auth", "true");
    properties.put("mail.smtp.host", configuration.getSmtpServer());

    final var session = Session.getDefaultInstance(properties, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(
            configuration.getSmtpUsername(),
            configuration.getSmtpPassword());
      }
    });

    try {
      final var message = new MimeMessage(session);
      message.setFrom(new InternetAddress(configuration.getSmtpSender()));
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
      message.setSubject(subject);

      final var multipart = new MimeMultipart("alternative");

      final var textPart = new MimeBodyPart();
      textPart.setText(textContent, "utf-8");

      final var htmlPart = new MimeBodyPart();
      htmlPart.setContent(htmlContent, "text/html; charset=utf-8");

      multipart.addBodyPart(textPart);
      multipart.addBodyPart(htmlPart);
      message.setContent(multipart);

      Transport.send(message);
    } catch (final MessagingException e) {
      throw new RuntimeException(e);
    }
  }

}
