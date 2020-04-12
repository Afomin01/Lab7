import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Predicate;

public class SpecialLinkedHashSet extends LinkedHashSet {
    @Override
    public boolean remove(Object o) {
        return super.remove(o);
    }

    @Override
    public boolean removeAll(Collection c) {
        return super.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate filter) {
        return false;
    }
}
