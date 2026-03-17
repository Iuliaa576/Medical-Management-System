package undo;

import repository.IRepository;

public class ActionAdd<ID, T> implements IAction {
    private final IRepository<ID, T> repo;
    private final ID id;
    private final T addedElem;

    public ActionAdd(IRepository<ID, T> repo, ID id, T addedElem) {
        this.repo = repo;
        this.id = id;
        this.addedElem = addedElem;
    }

    @Override
    public void executeUndo() throws Exception {
        repo.delete(id);
    }

    @Override
    public void executeRedo() throws Exception {
        repo.add(id, addedElem);
    }
}
