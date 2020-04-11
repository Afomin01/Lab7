package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.util.Date;
import java.util.Set;

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
    public ServerResponse execute(Set<Route> set) {

        elementToAdd.setCreationDate(new Date());
        ServerResponse serverResponse = new ServerResponse();

        if(set.stream().anyMatch(r -> r.getId() == elementToAdd.getId())){
            if (set.removeIf(r->(r.getId()==elementToAdd.getId() && r.getOwner().equals(user)))){
                set.add(elementToAdd);
                serverResponse.setText("Значения полей указанного элемента успешно обновлены.");
            }
            else serverResponse.setText("Значения полей не были обновлены так как у Вас нет прав на его модификацию");
        }
        else serverResponse.setText("В коллекции не найдено объекта с таким id.");

        return serverResponse;
    }
}
