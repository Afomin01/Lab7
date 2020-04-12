package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerResponse;
import Storable.Route;

import java.util.Date;

public class UpdateId implements ICommand {
    private Route elementToAdd;
    private String user;

    public UpdateId(long id, Route elementToAdd, String user){
        this.elementToAdd = elementToAdd;
        elementToAdd.setId(id);
        this.user=user;
    }

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Update;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {

        elementToAdd.setCreationDate(new Date());
        ServerResponse serverResponse = new ServerResponse();

        if(manager.stream().anyMatch(r -> r.getId() == elementToAdd.getId())){
            if (manager.removeIf(r->(r.getId()==elementToAdd.getId() && r.getOwner().equals(user)))){
                manager.add(elementToAdd);
                serverResponse.setText("Значения полей указанного элемента успешно обновлены.");
            }
            else serverResponse.setText("Значения полей указанного элемента не были обновлены так как у Вас нет прав на его модификацию");
        }
        else serverResponse.setText("В коллекции не найдено объекта с таким id.");

        return serverResponse;
    }
}
