import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Main {
    static final Logger log = Logger.getGlobal();
    private static Connection DBconnection;
    private static Statement statement;

    public static Connection getDBconnection() {
        return DBconnection;
    }

    public static void main(String[] args){
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$td.%1$tm.%1$tY %1$tH:%1$tM:%1$tS %2$s%n%4$s: %5$s %n%n");

        try {
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
            //Handler fh = new FileHandler("log_"+df.format(Calendar.getInstance().getTime())+".log");
            //fh.setFormatter(new SimpleFormatter());
            //log.addHandler(fh);

        } catch (Exception e) {
            log.warning("Unable to create log file. Logging to a file will not be performed.");
        }

        Properties property = new Properties();
        try {
            property.load(ClassLoader.getSystemClassLoader().getResourceAsStream("db.properties"));
        } catch (FileNotFoundException e) {
            log.severe("Database properties file not found. Shutting down...");
            for(Handler h : log.getHandlers())  h.close();
            System.exit(1);
        } catch (IOException | SecurityException e) {
            log.severe("Error reading database properties file. Shutting down...");
            for(Handler h : log.getHandlers())  h.close();
            System.exit(1);
        }

        int port = 47836;
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (Exception e) {
                log.info("Port value parsing error. The default port is selected: " + port);
            }
        }

        try {
            DBconnection = DriverManager.getConnection(property.getProperty("db.URL"), property.getProperty("db.login"), property.getProperty("db.password"));
            statement = DBconnection.createStatement();
            log.info("Connected to database: "+property.getProperty("db.URL")+ " with login "+property.getProperty("db.login")+".");
        } catch (SQLException e) {
            e.printStackTrace();
            log.severe("Error connecting to database: "+property.getProperty("db.URL")+ " with login "+property.getProperty("db.login")+". Shutting down ...");
            for(Handler h : log.getHandlers())  h.close();
            System.exit(1);
        }

        CollectionManager collectionManager = new CollectionManager(DBconnection);

        //SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        //SSLServerSocket sslserversocket = null;
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
            //sslserversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(9999);
        } catch (IOException e) {
            log.severe("Error while creating ServerSocket. Shutting down ...");
            for(Handler h : log.getHandlers())  h.close();
            System.exit(1);
        }
        System.out.println();
        log.info("The server started successfully. Port: " + port);

        Socket client;
        long clientID=0;
        SocketsHandler socketsHandler = new SocketsHandler(collectionManager);
        new Thread(socketsHandler,"Main Socket Handler Thread").start();

        try {
            while (true) {
                //SSLSocket client = (SSLSocket) sslserversocket.accept();
                client = ss.accept();
                SocketConnected socket = new SocketConnected(client,clientID);
                socketsHandler.addSocket(clientID,socket);
                clientID++;
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
            Transport.send(message);
            System.out.println("Email Sent successfully....");
        } catch (MessagingException mex){ mex.printStackTrace(); }
    }
}
