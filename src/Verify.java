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
public class Verify {
	Properties emailProperties;
	Session mailSession;
	MimeMessage emailMessage;

	public static void verify(String email,String user_name, String password) throws AddressException,
			MessagingException {

		Verify javaEmail = new Verify();

		javaEmail.setMailServerProperties();
//		System.out.println("set properties done");
		javaEmail.createEmailMessage(email,user_name,password);
//		System.out.println("email message created");
		javaEmail.sendEmail();
//		System.out.println("message sent successfully");
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

	public void createEmailMessage(String email, String user_name, String password) throws AddressException,
			MessagingException {
		String[] toEmails = {email};
//		System.out.println(email);
		String emailSubject = "TrekApp Verification";
		String emailBody = "If you initiated the request to signup for trekApp, then please click the followed link or else ignore\nhttp:http://10.196.7.216:8080/TrekAppServer/Signup?user_name="+user_name+"&email="+email+"&password="+password+"&verified=1";
		mailSession = Session.getInstance(emailProperties,new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Config.user_mail, Config.dd);
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
