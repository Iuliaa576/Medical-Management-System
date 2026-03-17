package repository;

import filter.AbstractFilter;

import java.util.ArrayList;
import java.util.List;

public class FilteredRepository<ID, T> extends MemoryRepository<ID, T> {

    private AbstractFilter<T> filter;

    public FilteredRepository(AbstractFilter<T> filter) {
        this.filter = filter;
    }

    @Override
    public Iterable<T> getAll() {
        List<T> filteredList = new ArrayList<>();

        elements.values().forEach(element -> {
            if (filter.accept(element)) {
                filteredList.add(element);
            }
        });

        return filteredList;
    }

    public void setFilter(AbstractFilter<T> filter) {
        this.filter = filter;
    }
}
