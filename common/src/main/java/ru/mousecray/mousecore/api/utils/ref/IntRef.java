/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.utils.ref;

public class IntRef implements Ref<Integer> {
    private int val;
    public IntRef(int val)               { this.val = val; }
    @Override public void $(Integer val) { this.val = val; }
    public void $(int val)               { this.val = val; }
    @Override public Integer $$()        { return val; }
    public int $()                       { return val; }
}
