package Commands;

import Instruments.ServerResponse;
import Storable.Route;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RemoveById implements ICommand {
    public RemoveById(long id) {
        this.id = id;
    }

    private long id;

    @Override
    public EAvailableCommands getCommandEnum() {
        return EAvailableCommands.Remove_By_Id;
    }

    @Override
    public ServerResponse execute(Set<Route> set) {
        ServerResponse serverResponse = new ServerResponse();
        Stream<Route> stream = set.stream();
        if(set.removeAll(stream.filter(r -> r.getId()==id).collect(Collectors.toSet()))) serverResponse.setText("Элемент с id "+id+" был успешно удален из коллекции");
        else if(set.stream().noneMatch(r -> r.getId() == id)) serverResponse.setText("Элемента с id "+id+" в коллекции не найдено");
        else serverResponse.setText("Коллекция не была изменена");
        return serverResponse;
    }
}
