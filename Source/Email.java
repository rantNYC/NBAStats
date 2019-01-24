import java.util.Properties;
import java.util.logging.Level;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

//Example from https://www.tutorialspoint.com/javamail_api/javamail_api_sending_simple_email.htm
public class Email {
	
	private String to;
	private final String from = "enviromenttesting5@gmail.com";
	private final String username = "enviromenttesting5@gmail.com";
	private final String password = "Testing.123";
	
	//Uses a static value to build the text message accross the application
	public static StringBuilder textMessage = new StringBuilder("");
	
	public Email(String email) {
		if(email.isEmpty()) {
			to = null;
			Log.log(Level.WARNING, "No email specified, no email will be sent");
		}else {
			to = email;
		}
	}
	
   public void sendEmail() {
      
	   if(to == null || to.isEmpty()) {
		   throw new IllegalArgumentException("Email wasn't specified");
	   }

	   Properties props = new Properties();
	   props.put("mail.smtp.starttls.enable", "true");
	   props.put("mail.smtp.host", "smtp.gmail.com");
	   props.put("mail.smtp.user", from);
	   props.put("mail.smtp.password", password);
	   props.put("mail.smtp.port", "587");
	   props.put("mail.smtp.auth", "true");
	   props.put("mail.debug", "false");
	   props.put("mail.smtp.ssl.trust", "*");

	   Session session = Session.getInstance(props,
			   new javax.mail.Authenticator() {
            	protected PasswordAuthentication getPasswordAuthentication() {
            		return new PasswordAuthentication(username, password);
            		}
	   			});

	   try {
		   Message message = new MimeMessage(session);
	
		   message.setFrom(new InternetAddress(from));
	
		   message.setRecipients(Message.RecipientType.TO,
				   InternetAddress.parse(to));
	
		   message.setSubject("Stats NBA Report");
	
		   message.setText(textMessage.toString());

		   Transport.send(message);

		   System.out.println("Sent message successfully....");

	   } catch (MessagingException e) {
		   throw new RuntimeException(e);
	   }
   	}
}
