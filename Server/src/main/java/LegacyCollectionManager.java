import Instruments.XMLParser;
import Storable.Route;

import javax.management.modelmbean.XMLParseException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashSet;
import java.util.logging.Handler;

public class LegacyCollectionManager {
    private LinkedHashSet<Route> routes;

    public LegacyCollectionManager(String path){
        try {
            if (path == null) throw new FileNotFoundException();
        }
        catch (FileNotFoundException e){
            Main.log.severe("You must specify the path to the file as a command line argument. Shutting down...");
            for(Handler h : Main.log.getHandlers())  h.close();
            System.exit(1);
        }
        File collectionFile = new File(path);
        try{
            if(!collectionFile.exists()) throw new FileNotFoundException();
        }
        catch (FileNotFoundException e){
            Main.log.severe("The file at the specified path does not exist. Shutting down...");
            for(Handler h : Main.log.getHandlers())  h.close();
            System.exit(1);
        }
        try {
            if(!collectionFile.canRead() || !collectionFile.canWrite()) throw new SecurityException();
            if (collectionFile.length() == 0 ) throw new FileNotFoundException();
            routes = XMLParser.parse(path);
            Main.log.info("File "+path+" was opened");
        }
        catch (SecurityException e) {
            Main.log.severe("File is read / write protected. Shutting down...");
            for(Handler h : Main.log.getHandlers())  h.close();
            System.exit(1);
        } catch (XMLParseException e) {
            Main.log.severe("Parsing error. Shutting down...");
            for(Handler h : Main.log.getHandlers())  h.close();
            System.exit(1);
        }  catch (FileNotFoundException e) {
            Main.log.severe("File is empty. Shutting down...");
            for(Handler h : Main.log.getHandlers())  h.close();
            System.exit(1);
        } catch ( Exception e) {
            Main.log.severe("Parsing error. Shutting down...");
            for(Handler h : Main.log.getHandlers())  h.close();
            System.exit(1);
        }

    }
    public LinkedHashSet<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(LinkedHashSet<Route> routes) {
        this.routes = routes;
    }
}
