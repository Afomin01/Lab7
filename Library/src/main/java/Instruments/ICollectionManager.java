package Instruments;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface ICollectionManager<T> {
    boolean add(T element);
    boolean removeAll(Set<T> c);
    boolean removeIf(Predicate<T> filter);
    Set<T> getSet();
    Stream<T> stream();
}
