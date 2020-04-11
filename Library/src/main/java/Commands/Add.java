package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

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
    public ServerResponse execute(Set<Route> set) {
        elementToAdd.setId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
        elementToAdd.setCreationDate(new Date());

        ServerResponse serverResponse = new ServerResponse();
        if(set.add(elementToAdd)) serverResponse.setText("Элемент был успешно добавлен в коллекию");

        else serverResponse.setText("Элемент не был доабвлен в коллекцию");

        return serverResponse;
    }
}
