package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerResponse;
import Storable.Route;

import java.util.Date;

public class AddIfMax implements ICommand {

    private Route elementToAdd;

    public AddIfMax(Route elementToAdd){
        this.elementToAdd = elementToAdd;
    }

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Add_If_Max;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {

        elementToAdd.setCreationDate(new Date());

        ServerResponse serverResponse = new ServerResponse();
        if(elementToAdd.compareTo(manager.stream().max(Route::compareTo).get()) > 0){
            manager.add(elementToAdd);
            serverResponse.setText("Элемент был успешно добавлен в коллекцию");
        }
        else serverResponse.setText("Элемент не был добавлен в коллекцию");

        return serverResponse;
    }
}
