package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

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
    public ServerResponse execute(Set<Route> set) {

        elementToAdd.setId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
        elementToAdd.setCreationDate(new Date());

        ServerResponse serverResponse = new ServerResponse();
        if(elementToAdd.compareTo(set.stream().max(Route::compareTo).get()) > 0){
            set.add(elementToAdd);
            serverResponse.setText("Элемент был успешно добавлен в коллекцию");
        }
        else serverResponse.setText("Элемент не был добавлен в коллекцию");

        return serverResponse;
    }
}
