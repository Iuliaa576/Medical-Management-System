package undo;

import repository.IRepository;

public class ActionRemove<ID, T> implements IAction {
    private final IRepository<ID, T> repo;
    private final ID id;
    private final T deletedElem;

    public ActionRemove(IRepository<ID, T> repo, ID id, T deletedElem) {
        this.repo = repo;
        this.id = id;
        this.deletedElem = deletedElem;
    }

    @Override
    public void executeUndo() throws Exception {
        repo.add(id, deletedElem);
    }

    @Override
    public void executeRedo() throws Exception {
        repo.delete(id);
    }
}
