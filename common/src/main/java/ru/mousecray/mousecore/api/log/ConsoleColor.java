/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.log;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.stream.Collectors;

public enum ConsoleColor {
    RESET("0"), BOLD("1"), ITALIC("3"), UNDERLINED("4"), STRIKETHROUGH("9"), BLACK("30"),
    RED("31"), GREEN("32"), YELLOW("33"), BLUE("34"), PURPLE("35"), CYAN("36"),
    WHITE("37"), BLACK_BG("40"), RED_BG("41"), GREEN_BG("42"), YELLOW_BG("43"),
    BLUE_BG("44"), PURPLE_BG("45"), CYAN_BG("46"), WHITE_BG("47");

    private final String code;

    ConsoleColor(String code) { this.code = code; }

    @Nonnull
    public static String getColorString(ConsoleColor... colors) {
        if (colors == null || colors.length < 1) return "";
        return "\u001B[" + Arrays.stream(colors).map(ConsoleColor::getCode).collect(Collectors.joining(";")) + "m";
    }

    public String getCode()                     { return code; }
    @Override @Nonnull public String toString() { return "\u001B[" + code + "m"; }
}