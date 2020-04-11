package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.util.Date;
import java.util.Set;

public class UpdateId implements ICommand {
    private Route elementToAdd;

    public UpdateId(long id, Route elementToAdd){
        this.elementToAdd = elementToAdd;
        elementToAdd.setId(id);
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
            set.removeIf(r->r.getId()==elementToAdd.getId());
            set.add(elementToAdd);
            serverResponse.setText("Значения полей указанного элемента успешно обновлены");
        }
        else serverResponse.setText("Элемента с таким id в коллекции не обнаружено. Коллекция не изменилась");

        return serverResponse;
    }
}
