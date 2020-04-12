package Commands;

import Instruments.ICollectionManager;
import Instruments.ServerResponse;
import Storable.Route;

import java.util.stream.Stream;

public class RemoveById implements ICommand {
    private String user;

    public RemoveById(long id, String user) {
        this.id = id;
        this.user=user;
    }

    private long id;

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Remove_By_Id;
    }

    @Override
    public ServerResponse execute(ICollectionManager<Route> manager) {
        ServerResponse serverResponse = new ServerResponse();
        Stream<Route> stream = manager.stream();

        if(manager.stream().anyMatch(r -> r.getId()==id)){
            if(manager.removeIf(r -> (r.getId()==id && r.getOwner().equals(user)))) serverResponse.setText("Элемент с id "+id+" был успешно удален из коллекции");
            else serverResponse.setText("Элемент не был удален так как у Вас нет прав на его модификацию");
        }else serverResponse.setText("Элемента с id "+id+" в коллекции не найдено");

        return serverResponse;
    }
}
