package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Info implements ICommand {
    //private String fileName;
    
    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Info;
    }

    @Override
    public ServerResponse execute(Set<Route> set) {
        ServerResponse serverResponse = new ServerResponse();

        String out="";
        out = out + "Тип коллекции: "+set.getClass().getTypeName();
        out = out + "\nКоличесвто элементов коллекции: "+set.size();
/*      SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try{
            BasicFileAttributes attributes = Files.readAttributes((new File(fileName)).toPath(), BasicFileAttributes.class);
            out = out + "\nДата создания файла коллекции: "+ df.format(attributes.creationTime().to(TimeUnit.MILLISECONDS));
            out = out + "\nДата последнего доступа файла коллекции: "+ df.format(attributes.lastAccessTime().to(TimeUnit.MILLISECONDS));
            out = out + "\nДата последней модификации файла коллекции: "+ df.format(attributes.lastModifiedTime().to(TimeUnit.MILLISECONDS));
            out = out + "\nРазмер файла коллекции: "+ attributes.size()+ " байт";
        }
        catch (IOException e){
            serverResponse.setText("Ошибка чтения артибутов файла коллекции");
        }*/
        serverResponse.setText(out);
        return serverResponse;
    }
}
