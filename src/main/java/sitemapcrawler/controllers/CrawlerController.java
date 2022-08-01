package sitemapcrawler.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Properties;

@RestController
public class CrawlerController {

    @GetMapping
    public String crawler(){
        System.out.println("\n***************************************************************\nStart scan: " + LocalDateTime.now());
        String body = "";

        body += checkUrl("https://www.hackeru.co.il/sitemap-posttype-post.2022.xml");
        body += checkUrl("https://www.hit.ac.il/sitemap.xml");
        body += checkUrl("https://www.int-college.co.il/post-sitemap.xml");
        body += checkUrl("https://rt-ed.co.il/articles-sitemap.xml");
        body += checkUrl("https://see-security.com/post-sitemap.xml");
        body += checkUrl("https://www.naya-college.co.il/post-sitemap.xml");
        body += checkUrl("https://www.ecomschool.co.il/post-sitemap.xml");
        body += checkUrl("https://gohitech.co.il/post-sitemap.xml");
        body += checkUrl("http://www.elimudim.co.il/post-sitemap.xml");
        body += checkUrl("https://studycenter.co.il/post-sitemap.xml");
        body += checkUrl("https://qamasters.co.il/post-sitemap.xml");
        body += checkUrl("https://net4uc.com/post-sitemap.xml");


        if (!body.isEmpty()) {
            sendMail(body);
            return "changes found and mail sent: \n" + body ;
        }
        return "no changes found...";
    }

    public String checkUrl(String url){
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(url);
            doc.getDocumentElement().normalize();

            NodeList urls = doc.getElementsByTagName("url");

            String body = "";
            for (int i = 0; i < urls.getLength(); i++) {
                NodeList children = urls.item(i).getChildNodes();
                Node urlName = null, lastMod = null;
                for (int j = 0; j < children.getLength(); j++) {
                    if(children.item(j).getNodeName().equalsIgnoreCase("loc")) {
                        urlName = children.item(j);
                    }
                    else if(children.item(j).getNodeName().equalsIgnoreCase("lastmod")) {
                        lastMod = children.item(j);
                        break;
                    }
                }
                if(lastMod!=null) {
                    LocalDate dateTime = LocalDate.parse(lastMod.getTextContent().substring(0, 10));
                    LocalDate now = LocalDate.now();
                    if (dateTime.isEqual(now)) {
                        String line = urlName.getTextContent() + " >> " + lastMod.getNodeName() + ":" + lastMod.getTextContent();
                        System.out.println("changed today!!");
                        body += line + "\n";
                    }
                }

            }
            return body;

        }catch(Exception e){
            System.err.println(e.getMessage());
            return "";
        }
    }

    private void sendMail(String body) {
        // Recipient's email ID needs to be mentioned.
        String to = "yakirp@johnbryce.co.il;nirg@johnbryce.co.il;";

        // Sender's email ID needs to be mentioned
        String from = "WeAreNotSpies@jbh.co.il";

        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication("wizkid1977@gmail.com", "emqqtegueqcwbkhl");

            }

        });

        // Used to debug SMTP issues
        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("HackerU new posts!");

            // Now set the actual message
            message.setText(body);

            System.out.println("sending...");
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            System.out.println("ERROR >> " + mex.getMessage());
        }

    }
}
