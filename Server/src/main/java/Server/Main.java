package Server;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Properties;
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
            property.load(ClassLoader.getSystemClassLoader().getResourceAsStream("app.properties"));
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
        ServerSocketHandler socketsHandler = new ServerSocketHandler(collectionManager, port);
        new Thread(socketsHandler).start();
    }
}
