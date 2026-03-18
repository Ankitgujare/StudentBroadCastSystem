package com.example.studentbroadcastsystem;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailAPI {

    private Context context;
    private Session session;
    private String subject, messageBody;
    private String[] recipients;

    // Google blocks standard passwords for SMTP. An App Password is required for this to actually send.
    private final String SENDER_EMAIL = "Admin.admin240@gmail.com";
    private final String SENDER_PASSWORD = "oaezcbnaujgsyakd";

    public JavaMailAPI(Context context, String[] recipients, String subject, String messageBody) {
        this.context = context;
        this.recipients = recipients;
        this.subject = subject;
        this.messageBody = messageBody;
    }

    public void execute() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            boolean success = sendEmail();
            handler.post(() -> {
                if (success) {
                    Toast.makeText(context, "Mails sent successfully in background!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error sending mails. Check credentials & network.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private boolean sendEmail() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(SENDER_EMAIL));
            
            InternetAddress[] recipientAddresses = new InternetAddress[recipients.length];
            for (int i = 0; i < recipients.length; i++) {
                recipientAddresses[i] = new InternetAddress(recipients[i]);
            }
            
            mimeMessage.setRecipients(Message.RecipientType.BCC, recipientAddresses);
            mimeMessage.setSubject(subject);
            mimeMessage.setText(messageBody);

            Transport.send(mimeMessage);
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
