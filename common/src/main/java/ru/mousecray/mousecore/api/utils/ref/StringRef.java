/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.utils.ref;

public class StringRef implements Ref<String> {
    private String val;
    public StringRef(String val)        { this.val = val; }
    @Override public void $(String val) { this.val = val; }
    public void $A(String val)          { this.val += val; }
    @Override public String $$()        { return val; }
    public String $()                   { return val; }
}
