package undo;

import java.util.ArrayList;
import java.util.List;

public class CompositeAction implements IAction {
    private final List<IAction> actions = new ArrayList<>();

    public void add(IAction action) {
        actions.add(action);
    }

    @Override
    public void executeUndo() throws Exception {
        // undo in reverse order (important)
        for (int i = actions.size() - 1; i >= 0; i--) {
            actions.get(i).executeUndo();
        }
    }

    @Override
    public void executeRedo() throws Exception {
        // redo in normal order
        for (IAction a : actions) {
            a.executeRedo();
        }
    }
}
