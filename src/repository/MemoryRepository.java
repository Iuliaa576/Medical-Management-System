package repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MemoryRepository<ID, T> implements IRepository<ID, T> {
    protected Map<ID, T> elements = new HashMap<>();

    @Override
    public void add(ID id, T entity) throws RepositoryException {
        if (elements.containsKey(id)) {
            throw new  RepositoryException("Element with id " + id + " already exists");
        }
        elements.put(id, entity);
    }

    @Override
    public Optional<T> delete(ID id) throws RepositoryException {
        if (!elements.containsKey(id)) {
            return Optional.empty();
        }
        T removed = elements.remove(id);
        return Optional.of(removed);
    }

    @Override
    public void modify(ID id, T entity) throws RepositoryException {
        if (!elements.containsKey(id)) {
            throw new  RepositoryException("Element with id " + id + " doesn't exists");
        }
        elements.put(id, entity);
    }

    @Override
    public Optional<T> findById(ID id) throws RepositoryException {
        return Optional.ofNullable(elements.get(id));
    }

    @Override
    public Iterable<T> getAll() {
        return elements.values();
    }

}
