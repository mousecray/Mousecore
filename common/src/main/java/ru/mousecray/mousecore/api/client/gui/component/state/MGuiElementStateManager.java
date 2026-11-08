/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.component.state;

public class MGuiElementStateManager {
    private int
            states    = 0,
            forbidden = 0;
    private boolean  forbiddenLocked = false;
    private Runnable changeListener;

    private static final int INTERACTIVE_MASK =
            MGuiElementState.HOVERED.mask |
                    MGuiElementState.PRESSED.mask |
                    MGuiElementState.FOCUSED.mask;

    public void setChangeListener(Runnable listener) { changeListener = listener; }
    private void notifyChange()                      { if (changeListener != null) changeListener.run(); }

    public void add(MGuiElementState state) {
        if ((forbidden & state.mask) != 0) return;

        if (has(MGuiElementState.DISABLED) || has(MGuiElementState.HIDDEN)) {
            if ((INTERACTIVE_MASK & state.mask) != 0) return;
        }

        int oldStates = states;

        if (state == MGuiElementState.DISABLED || state == MGuiElementState.HIDDEN) states &= ~INTERACTIVE_MASK;

        states |= state.mask;

        if (oldStates != states) notifyChange();
    }

    public void remove(MGuiElementState state) {
        int oldStates = states;
        states &= ~state.mask;
        if (oldStates != states) notifyChange();
    }

    public void clearStates() {
        int maskToClear = MGuiElementState.HOVERED.mask | MGuiElementState.PRESSED.mask |
                MGuiElementState.FOCUSED.mask | MGuiElementState.SELECTED.mask |
                MGuiElementState.FAIL.mask;
        int oldStates = states;
        states &= ~maskToClear;
        if (oldStates != states) notifyChange();
    }

    public boolean has(MGuiElementState state) { return (states & state.mask) != 0; }

    public void setForbidden(MGuiElementState state, boolean isForbidden) {
        if (forbiddenLocked) {
            throw new IllegalStateException(
                    "Forbidden states cannot be modified after " +
                            "the element has been added to the GUI tree (setParent/setScreen)!"
            );
        }
        if (isForbidden) {
            forbidden |= state.mask;
            remove(state);
        } else forbidden &= ~state.mask;
    }

    public boolean isForbidden(MGuiElementState state) { return (forbidden & state.mask) != 0; }
    public boolean satisfies(int requiredMask)         { return (states & requiredMask) == requiredMask; }

    public static int createMask(MGuiElementState... statesToCombine) {
        int mask = 0;
        for (MGuiElementState s : statesToCombine) mask |= s.mask;
        return mask;
    }

    public void lockForbidden(boolean lock) { forbiddenLocked = lock; }
}