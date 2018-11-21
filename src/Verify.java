import java.util.Properties;

import javax.mail.*;  
import javax.mail.internet.InternetAddress;  
import javax.mail.internet.MimeMessage;
public class Verify {
	public static boolean verify(String user_name,String email,String password) {
		System.out.println("came into verify");
		final String user="test.160050061@gmail.com"; //change accordingly  
		final String pass=Config.dd;
		// first 
        Properties props = new Properties();
        props.put("mail.smtp.host", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(props,  
				 new javax.mail.Authenticator() {  
				  protected PasswordAuthentication getPasswordAuthentication() {  
				   return new PasswordAuthentication(user,pass);  
				   }  
				});  
		// second
		try {  
			 MimeMessage message = new MimeMessage(session);  
			 message.setFrom(new InternetAddress(user));  
			 message.addRecipient(Message.RecipientType.TO,new InternetAddress(email));  
			 message.setSubject("Verification for TrekApp");  
			 message.setText("Please click on below link to verify :- "
			 		+ " http://192.168.0.8:8080/TrekApp/Signup?user_name="+user_name+"&email="+email+"&password="+password+"verified=1");  
			 // 	third 
			 System.out.println("going to send");
			 Transport transport = session.getTransport("smtp");
	         transport.connect("smtp.gmail.com", "test.160050061@gmail.com", Config.dd);
	         transport.sendMessage(message, message.getAllRecipients());
	         transport.close();
			 System.out.println("sent");
			 return true; 
		}
		catch(Exception e) {
			return false; 
		}
	}
}
