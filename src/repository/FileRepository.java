package repository;

import domain.Identifiable;

import java.util.Optional;

public abstract class FileRepository<ID, T extends Identifiable> extends MemoryRepository<String, T> {
    protected String FileName;

    protected abstract void readFromFile() throws RepositoryException;

    protected abstract void writeToFile();

    protected FileRepository() {};
    public FileRepository(String FileName) throws RepositoryException {
        this.FileName = FileName;
        readFromFile();
    }

    @Override
    public void add(String id, T elem) throws RepositoryException {
        super.add(id, elem);
        writeToFile();
    }

    @Override
    public Optional<T> delete(String id) throws RepositoryException{
        Optional<T> deleted = super.delete(id);
        if (deleted.isPresent()) {
            writeToFile();
        }
        return deleted;
    }

    @Override
    public void modify(String id, T elem) throws RepositoryException{
        super.modify(id, elem);
        writeToFile();
    }

}
