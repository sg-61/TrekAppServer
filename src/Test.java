

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Test {

	Properties emailProperties;
	Session mailSession;
	MimeMessage emailMessage;

	public static void main(String args[]) throws AddressException,
			MessagingException {

		Test javaEmail = new Test();

		javaEmail.setMailServerProperties();
		System.out.println("set properties done");
		javaEmail.createEmailMessage();
		System.out.println("email message created");
		javaEmail.sendEmail();
		System.out.println("message sent successfully");
	}

	public void setMailServerProperties() {

		String emailPort = "25";//gmail's smtp port
		emailProperties = System.getProperties();
		
		emailProperties.put("mail.smtp.host", "true");
        emailProperties.put("mail.smtp.host", "imap.cse.iitb.ac.in");
		emailProperties.put("mail.smtp.port", emailPort);
		emailProperties.put("mail.smtp.auth", "true");
		emailProperties.put("mail.smtp.starttls.enable", "true");

	}

	public void createEmailMessage() throws AddressException,
			MessagingException {
		String[] toEmails = { "shubhamguptadeos@gmail.com" };
		String emailSubject = "Test mail";
		String emailBody = "This is an email sent to test few things.";

		mailSession = Session.getDefaultInstance(emailProperties,new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("shubham", Config.dd);
            }
        });
//		System.out.println(Config.dd);
		emailMessage = new MimeMessage(mailSession);

		for (int i = 0; i < toEmails.length; i++) {
			emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmails[i]));
		}
        emailMessage.setSentDate(new Date());
		emailMessage.setSubject(emailSubject);
		emailMessage.setContent(emailBody, "text/html");//for a html email
        emailMessage.setHeader("XPriority", "1");
		//emailMessage.setText(emailBody);// for a text email

	}

	public void sendEmail() throws AddressException, MessagingException {

		Transport.send(emailMessage);
	}

}