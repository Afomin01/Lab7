package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerResponse;
import Storable.Route;

import java.util.Date;

public class Add implements ICommand {
    private Route elementToAdd;

    public Add(Route elementToAdd){
        this.elementToAdd = elementToAdd;
    }

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Add;
    }


    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {

        elementToAdd.setCreationDate(new Date());

        ServerResponse serverResponse = new ServerResponse();
        if(manager.add(elementToAdd)) serverResponse.setText("Элемент был успешно добавлен в коллекию");
        else serverResponse.setText("Элемент не был доабвлен в коллекцию");

        return serverResponse;
    }
}
