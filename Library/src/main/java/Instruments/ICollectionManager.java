package Instruments;

import Storable.Route;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface ICollectionManager<T> {
    ManagerResponseCodes add(T element);
    ManagerResponseCodes removeAll(Set<T> c, String user);
    ManagerResponseCodes removeIf(Predicate<T> filter, String user);
    ManagerResponseCodes update(long id, Route r, String user);
    Stream<T> stream();
}
