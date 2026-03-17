package undo;

public interface IAction {
    void executeUndo() throws Exception;
    void executeRedo() throws Exception;
}
