/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api;

import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nonnull;
import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
public final class DisplayName {
    @Nonnull private final String internalName, displayName;

    @SuppressWarnings("DataFlowIssue")
    public DisplayName(@Nonnull String internalName, @Nonnull String displayName) {
        Objects.requireNonNull(internalName);
        Objects.requireNonNull(displayName);
        internalName = MouseStrings.trimWith(internalName, true, '\t');
        displayName = MouseStrings.trimWith(displayName, true, '\t');
        if (internalName.isEmpty()) throw new IllegalArgumentException("internalName cannot be empty");
        if (displayName.isEmpty()) throw new IllegalArgumentException("displayName cannot be empty");
        if (internalName.contains(".") || internalName.contains(" ") || internalName.contains("\t")) {
            throw new IllegalArgumentException("internalName cannot contains spaces, tabs or points");
        }
        if (displayName.contains("\t")) throw new IllegalArgumentException("displayName cannot contains tabs");
        this.internalName = internalName;
        this.displayName = displayName;
    }
    @Override @Nonnull
    public String toString() { return "DisplayName{internalName='" + internalName + '\'' + ", dispayName='" + displayName + '\'' + '}'; }
    @Override public int hashCode() { return Objects.hash(internalName, displayName); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DisplayName that = (DisplayName) o;
        return internalName.equals(that.internalName) && displayName.equals(that.displayName);
    }

    @Nonnull public String getInternalName() { return internalName; }
    @Nonnull public String getDisplayName()  { return displayName; }
}