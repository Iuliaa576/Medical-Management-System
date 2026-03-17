package undo;

import java.util.ArrayDeque;
import java.util.Deque;

public class UndoRedoManager {
    private final Deque<IAction> undoStack = new ArrayDeque<>();
    private final Deque<IAction> redoStack = new ArrayDeque<>();

    public void record(IAction action) {
        undoStack.push(action);
        redoStack.clear(); // important: new action kills redo history
    }

    public void undo() throws Exception {
        if (undoStack.isEmpty()) return;
        IAction action = undoStack.pop();
        action.executeUndo();
        redoStack.push(action);
    }

    public void redo() throws Exception {
        if (redoStack.isEmpty()) return;
        IAction action = redoStack.pop();
        action.executeRedo();
        undoStack.push(action);
    }

    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }
}
