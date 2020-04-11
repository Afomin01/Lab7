import Storable.Route;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
    static final Logger log = Logger.getGlobal();
    private static String file;

    public static String getFile() {
        return file;
    }

    public static void main(String[] args){
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$td.%1$tm.%1$tY %1$tH:%1$tM:%1$tS %2$s%n%4$s: %5$s %n%n");
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
            Handler fh = new FileHandler("log_"+df.format(Calendar.getInstance().getTime())+".log");
            fh.setFormatter(new SimpleFormatter());
            //log.addHandler(fh);

        } catch (Exception e) {
            log.warning("Unable to create log file. Logging to a file will not be performed.");
        }
        int port = 47836;
        if (args.length == 0) {
            log.severe("The path to the xml file is not specified. Shutting down ...");
            for(Handler h : log.getHandlers())  h.close();
            System.exit(1);
        }
        if (args.length == 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                log.severe("Port value parsing error. The default port is selected: " + port);
            }
        }

        CollectionManager manager = new CollectionManager(args[0]);
        file = args[0];
        LinkedHashSet<Route> collectionLink = manager.getRoutes();
        //SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        //SSLServerSocket sslserversocket = null;
        SocketAddress address = new InetSocketAddress(port);
        ServerSocketChannel ss = null;
        try {
            ss = ServerSocketChannel.open();
            ss.bind(address);
            //sslserversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(9999);
        } catch (IOException e) {
            log.severe("Error while creating channel. Shutting down ...");
            for(Handler h : log.getHandlers())  h.close();
            System.exit(1);
        }

        log.info("The single-threaded server started successfully. Port: " + port);

        SocketChannel client;
        try {
            while (true) {
                //SSLSocket client = (SSLSocket) sslserversocket.accept();
                client = ss.accept();
                SocketHandler handler = new SocketHandler(client, collectionLink);
                handler.run();
            }
        } catch (IOException | SocketHandlerException e) {
            log.severe("Error accepting connection from client.");
        }
    }
    public static void sendMail(String mail, String text){

        String from = "sender@abc.com";       // receiver email
        String host = "127.0.0.1";            // mail server host

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);

        Session session = Session.getDefaultInstance(properties); // default session

        try {
            MimeMessage message = new MimeMessage(session); // email message

            message.setFrom(new InternetAddress(from)); // setting header fields

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(mail));

            message.setSubject("Test Mail from Java Program"); // subject line

            // actual mail body
            message.setText(text);

            // Send message
            Transport.send(message); System.out.println("Email Sent successfully....");
        } catch (MessagingException mex){ mex.printStackTrace(); }

    }
}
