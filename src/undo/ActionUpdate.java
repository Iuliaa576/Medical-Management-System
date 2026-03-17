package undo;

import repository.IRepository;

public class ActionUpdate<ID, T> implements IAction {
    private final IRepository<ID, T> repo;
    private final ID id;
    private final T oldElem;
    private final T newElem;

    public ActionUpdate(IRepository<ID, T> repo, ID id, T oldElem, T newElem) {
        this.repo = repo;
        this.id = id;
        this.oldElem = oldElem;
        this.newElem = newElem;
    }

    @Override
    public void executeUndo() throws Exception {
        repo.modify(id, oldElem);
    }

    @Override
    public void executeRedo() throws Exception {
        repo.modify(id, newElem);
    }
}
