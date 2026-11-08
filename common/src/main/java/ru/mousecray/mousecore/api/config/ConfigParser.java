/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.config.pars.ConfigPar;
import ru.mousecray.mousecore.api.config.pars.ConfigParGroup;
import ru.mousecray.mousecore.api.config.specific.ConfigLocaleType;
import ru.mousecray.mousecore.api.config.specific.ConfigValType;
import ru.mousecray.mousecore.api.config.utils.ConfigDictionary;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.values.base.list.simple.ConfigSimpleListVal;
import ru.mousecray.mousecore.api.container.ImmutableDisplayNameMap;
import ru.mousecray.mousecore.api.customtype.range.Range;
import ru.mousecray.mousecore.api.customtype.range.RangeContainer;
import ru.mousecray.mousecore.api.customtype.values.PlusMinusType;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.api.utils.MouseNumbers;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.countMatches;
import static ru.mousecray.mousecore.api.utils.MouseStrings.subAndTrimWithTabs;

@SuppressWarnings({ "SpellCheckingInspection" })
public final class ConfigParser {
    private static final String EXTENSION = ".moc";
    private static final
    String
            sectionStart      = "■", sectionDisabler = "▢", groupStart = "◉", groupDisabler = "◎", groupOpen = "⟪", groupClose = "⟫",
            parStart          = "●", parConditionStart = "◆", parListStart = "▶", parSubStart = "○", parSubConditionStart = "◇",
            parSubListStart   = "▷", parEqual = "→", sectionEqual = ":",
            sectionBorder     = "│", sectionCorner = "└", comment = "︙", delimiter = "\t", empty = "",
            constrDisabled    = "⛔", constrRelation = "⚠", constrEqual = "=", constrLess = "<", constrMore = ">",
            constrLessOrEqual = "≤", constrMoreOrEqual = "≥", constrNotEqual = "≠";
    private final MouseConfig config;
    private final MouseLogger logger;
    private final File        parentDir, file, tempFile;
    FileTime lastModTime;

    public ConfigParser(@Nonnull MouseConfig config, @Nullable MouseLogger logger) {
        this.config = config;
        parentDir = config.getPath();
        file = new File(parentDir, config.getName() + EXTENSION);
        tempFile = new File(parentDir, config.getName() + EXTENSION + "~");
        this.logger = logger;
    }

    public ConfigIOThread.IOResult readFile() {
        if (!parentDir.exists() || !file.exists()) {
            if (logger != null) {
                logger.atInfo()
                        .withPrefix("Config")
                        .withStyle(ConsoleColor.YELLOW_BG)
                        .log("Config file '{0}' is not found. It will be created on save", file.getAbsolutePath());
            }
            return ConfigIOThread.IOResult.createNotFound();
        } else {
            if (logger != null) {
                logger.atInfo()
                        .withPrefix("Config")
                        .withStyle(ConsoleColor.GREEN_BG)
                        .log("Config file '{0}' is found. It will be reading", file.getAbsolutePath());
            }
            return internalReadFile();
        }
    }

    @SuppressWarnings({ "DataFlowIssue" })
    private ConfigIOThread.IOResult internalReadFile() {
        final class ReadHelper {
            private Map<Integer, List<ConfigSect>>               deepSectIndex;
            private Map<Integer, List<ConfigParGroup>>           deepGroupIndex;
            private Map<Integer, Pair<ConfigVal, StringBuilder>> openedValue;
            private Map<Integer, Pair<ConfigVal, StringBuilder>> openedGroupValue;

            private void openSection(int deep, String name) {
                closeValueIfNeeded(deep);
                closeGroupValueIfNeeded(deep);

                config.getByDeep(deep).stream()
                        .filter(s -> s instanceof ConfigSect && s.getName().getDisplayName().equalsIgnoreCase(name))
                        .findFirst().ifPresent(base -> {
                            base.setByConfig = true;
                            deepSectIndex.computeIfAbsent(deep, ArrayList::new).add((ConfigSect) base);
                        });
            }

            private void openGroup(int deep, String name) {
                closeValueIfNeeded(deep);
                closeGroupValueIfNeeded(deep);

                config.getByDeep(deep).stream()
                        .filter(s -> s instanceof ConfigParGroup && s.getName().getDisplayName().equalsIgnoreCase(name))
                        .findFirst().ifPresent(base -> {
                            base.setByConfig = true;
                            deepGroupIndex.computeIfAbsent(deep, ArrayList::new).add((ConfigParGroup) base);
                        });
            }

            private void openSectionDisabler(int deep, String name) {
                closeValueIfNeeded(deep);
                closeGroupValueIfNeeded(deep);

                List<ConfigSect> list = deepSectIndex.get(deep);
                if (list != null && !list.isEmpty()) {
                    ConfigSect sec = list.get(list.size() - 1);
                    if (sec.canBeDisabled() && sec.disablePar.getName().getDisplayName().equalsIgnoreCase(name)) {
                        sec.disablePar.setByConfig = true;
                        openedValue.put(deep, Pair.of(sec.disablePar.getConfigVal(), new StringBuilder()));
                    }
                }
            }

            private void openGroupDisabler(int deep, String name) {
                closeValueIfNeeded(deep);
                closeGroupValueIfNeeded(deep);

                List<ConfigParGroup> list = deepGroupIndex.get(deep);
                if (list != null && !list.isEmpty()) {
                    ConfigParGroup gr = list.get(list.size() - 1);
                    if (gr.canBeDisabled() && gr.disablePar.getName().getDisplayName().equalsIgnoreCase(name)) {
                        gr.disablePar.setByConfig = true;
                        openedGroupValue.put(deep, Pair.of(gr.disablePar.getConfigVal(), new StringBuilder()));
                    }
                }
            }

            private void openSectionPar(int deep, String name) {
                closeValueIfNeeded(deep);
                closeGroupValueIfNeeded(deep);

                List<ConfigSect> list = deepSectIndex.get(deep);
                if (list != null && !list.isEmpty()) {
                    ConfigSect     sec   = list.get(list.size() - 1);
                    ConfigSectBase child = sec.getChildByDisplayName(name);
                    if (child != null) {
                        if (child instanceof ConfigPar) {
                            ConfigPar cPar = (ConfigPar) child;
                            cPar.setByConfig = true;
                            openedValue.put(deep, Pair.of(cPar.getConfigVal(), new StringBuilder()));
                        }
                    }
                }
            }

            private void openGroupPar(int deep, String name) {
                closeValueIfNeeded(deep);
                closeGroupValueIfNeeded(deep);

                List<ConfigParGroup> list = deepGroupIndex.get(deep);
                if (list != null && !list.isEmpty()) {
                    ConfigParGroup gr       = list.get(list.size() - 1);
                    ConfigVal      valChild = gr.getValByDisplayName(name);
                    if (valChild != null) openedGroupValue.put(deep, Pair.of(valChild, new StringBuilder()));
                }
            }

            private void processValue(int deep, String value) {
                Pair<ConfigVal, StringBuilder> opened = openedValue.get(deep);
                if (opened != null) opened.getValue().append(value);
            }

            private void processGroupValue(int deep, String value) {
                Pair<ConfigVal, StringBuilder> opened = openedGroupValue.get(deep);
                if (opened != null) opened.getValue().append(value);
            }

            private void closeValueIfNeeded(int deep) {
                Pair<ConfigVal, StringBuilder> opened = openedValue.get(deep);
                if (opened != null) {
                    ConfigVal     key   = opened.getKey();
                    ConfigParBase owner = key.getOwner();
                    if (owner instanceof ConfigParDisabler) {
                        key.setByConfig = ((ConfigParDisabler) owner).setDisabledRaw(opened.getValue().toString()).isCanBePassed();
                        if (owner.parent != null) owner.parent.setDisabled(key.isDisabled());
                    } else key.setByConfig = key.setValueRaw(opened.getValue().toString()).isCanBePassed();
                    openedValue.put(deep, null);
                }
            }

            private void closeGroupValueIfNeeded(int deep) {
                Pair<ConfigVal, StringBuilder> opened = openedGroupValue.get(deep);
                if (opened != null) {
                    ConfigVal     key   = opened.getKey();
                    ConfigParBase owner = key.getOwner();
                    if (owner instanceof ConfigParDisabler) {
                        key.setByConfig = ((ConfigParDisabler) owner).setDisabledRaw(opened.getValue().toString()).isCanBePassed();
                        if (owner.parent != null) owner.parent.setDisabled(key.isDisabled());
                    } else key.setByConfig = key.setValueRaw(opened.getValue().toString()).isCanBePassed();
                    openedGroupValue.put(deep, null);
                }
            }

            private void closeAllValues() {
                for (Iterator<Map.Entry<Integer, Pair<ConfigVal, StringBuilder>>> iterator =
                     openedValue.entrySet().iterator();
                     iterator.hasNext();
                ) {
                    Map.Entry<Integer, Pair<ConfigVal, StringBuilder>> entry = iterator.next();
                    Pair<ConfigVal, StringBuilder>                     value = entry.getValue();
                    if (value != null) {
                        ConfigVal     key   = value.getKey();
                        ConfigParBase owner = key.getOwner();
                        if (owner instanceof ConfigParDisabler) {
                            key.setByConfig = ((ConfigParDisabler) owner).setDisabledRaw(value.getValue().toString()).isCanBePassed();
                            if (owner.parent != null) owner.parent.setDisabled(key.isDisabled());
                        } else key.setByConfig = key.setValueRaw(value.getValue().toString()).isCanBePassed();
                        iterator.remove();
                    }
                }

                for (Iterator<Map.Entry<Integer, Pair<ConfigVal, StringBuilder>>> iterator =
                     openedGroupValue.entrySet().iterator();
                     iterator.hasNext();
                ) {
                    Map.Entry<Integer, Pair<ConfigVal, StringBuilder>> entry = iterator.next();
                    Pair<ConfigVal, StringBuilder>                     value = entry.getValue();
                    if (value != null) {
                        ConfigVal     key   = value.getKey();
                        ConfigParBase owner = key.getOwner();
                        if (owner instanceof ConfigParDisabler) {
                            key.setByConfig = ((ConfigParDisabler) owner).setDisabledRaw(value.getValue().toString()).isCanBePassed();
                            if (owner.parent != null) owner.parent.setDisabled(key.isDisabled());
                        } else key.setByConfig = key.setValueRaw(value.getValue().toString()).isCanBePassed();
                        iterator.remove();
                    }
                }
            }

            private ReadHelper() {
                deepSectIndex = new HashMap<>();
                deepGroupIndex = new HashMap<>();
                openedValue = new HashMap<>();
                openedGroupValue = new HashMap<>();
            }

            private void destroy() {
                deepSectIndex.clear();
                deepSectIndex = null;
                deepGroupIndex.clear();
                deepGroupIndex = null;
                openedValue.clear();
                openedValue = null;
                openedGroupValue.clear();
                openedGroupValue = null;
            }
        }

        try (BufferedReader fileRead = Files.newBufferedReader(file.getAbsoluteFile().toPath(), Charsets.UTF_8)) {
            String     line;
            ReadHelper helper = new ReadHelper();
            while ((line = fileRead.readLine()) != null) {
                if (line.contains(sectionStart)) {
                    helper.openSection(
                            countMatches(line, sectionBorder),
                            subAndTrimWithTabs(line, sectionStart, sectionEqual)
                    );
                } else if (line.contains(groupStart)) {
                    helper.openGroup(
                            countMatches(line, sectionBorder),
                            subAndTrimWithTabs(
                                    subAndTrimWithTabs(line, groupStart, parEqual),
                                    null, groupOpen
                            )
                    );
                } else if (line.contains(sectionDisabler)) {
                    int deep = countMatches(line, sectionBorder) - 1;
                    helper.openSectionDisabler(
                            deep, subAndTrimWithTabs(line, sectionDisabler, parEqual)
                    );
                    helper.processValue(
                            deep, subAndTrimWithTabs(line, parEqual, null)
                    );
                } else if (line.contains(groupDisabler)) {
                    int deep = countMatches(line, sectionBorder);
                    helper.openGroupDisabler(
                            deep, subAndTrimWithTabs(line, groupDisabler, parEqual)
                    );
                    helper.processGroupValue(
                            deep, subAndTrimWithTabs(line, parEqual, null)
                    );
                } else if (line.contains(parStart)) {
                    int    deep     = countMatches(line, sectionBorder) - 1;
                    String par      = subAndTrimWithTabs(line, parStart, null);
                    String parName  = subAndTrimWithTabs(par, null, parEqual);
                    String parValue = subAndTrimWithTabs(par, parEqual, null);
                    helper.openSectionPar(deep, parName);
                    helper.processValue(deep, parValue);
                } else if (line.contains(parConditionStart)) {
                    int    deep     = countMatches(line, sectionBorder) - 1;
                    String par      = subAndTrimWithTabs(line, parConditionStart, null);
                    String parName  = subAndTrimWithTabs(par, null, parEqual);
                    String parValue = subAndTrimWithTabs(par, parEqual, null);
                    helper.openSectionPar(deep, parName);
                    helper.processValue(deep, parValue);
                } else if (line.contains(parListStart)) {
                    int    deep     = countMatches(line, sectionBorder) - 1;
                    String par      = subAndTrimWithTabs(line, parConditionStart, null);
                    String parName  = subAndTrimWithTabs(par, null, parEqual);
                    String parValue = subAndTrimWithTabs(par, parEqual, null);
                    helper.openSectionPar(deep, parName);
                    helper.processValue(deep, parValue);
                } else if (line.contains(parSubStart)) {
                    int    deep     = countMatches(line, sectionBorder);
                    String par      = subAndTrimWithTabs(line, parSubStart, null);
                    String parName  = subAndTrimWithTabs(par, null, parEqual);
                    String parValue = subAndTrimWithTabs(par, parEqual, null);
                    helper.openGroupPar(deep, parName);
                    helper.processGroupValue(deep, parValue);
                } else if (line.contains(parSubConditionStart)) {
                    int    deep     = countMatches(line, sectionBorder);
                    String par      = subAndTrimWithTabs(line, parSubConditionStart, null);
                    String parName  = subAndTrimWithTabs(par, null, parEqual);
                    String parValue = subAndTrimWithTabs(par, parEqual, null);
                    helper.openGroupPar(deep, parName);
                    helper.processGroupValue(deep, parValue);
                } else if (line.contains(parSubListStart)) {
                    int    deep     = countMatches(line, sectionBorder);
                    String par      = subAndTrimWithTabs(line, parSubListStart, null);
                    String parName  = subAndTrimWithTabs(par, null, parEqual);
                    String parValue = subAndTrimWithTabs(par, parEqual, null);
                    helper.openGroupPar(deep, parName);
                    helper.processGroupValue(deep, parValue);
                } else if (!line.contains(sectionCorner) && !line.contains(sectionEqual) && !line.contains(comment)) {
                    int deep = countMatches(line, sectionBorder);
                    String value = subAndTrimWithTabs(
                            line.replaceAll(groupOpen, "").replaceAll(groupClose, ""),
                            sectionBorder, null
                    );
                    helper.processValue(deep - 1, value);
                    helper.processGroupValue(deep, value);
                }
            }
            helper.closeAllValues();
            helper.destroy();
        } catch (IOException e) {
            return ConfigIOThread.IOResult.createError(e);
        } finally {
            try {
                lastModTime = Files.getLastModifiedTime(file.getAbsoluteFile().toPath());
            } catch (IOException e) { e.printStackTrace(); }
        }

        //Validate and log
        if (logger != null) {
            for (ConfigSectBase section : config.getChildrenRecursively()) {
                if (
                        section.canBeDisabled() && section instanceof ConfigSect
                                && !((ConfigSect) section).disablePar.setByConfig
                ) {
                    logger.atWarn()
                            .withPrefix("Config")
                            .withStyle(ConsoleColor.YELLOW_BG)
                            .log("SectionDisabler '{0}' is not found or corrupted", section.getFullInternalName());
                }

                if (
                        section.canBeDisabled() && section instanceof ConfigParGroup
                                && !((ConfigParGroup) section).disablePar.setByConfig
                ) {
                    logger.atWarn()
                            .withPrefix("Config")
                            .withStyle(ConsoleColor.YELLOW_BG)
                            .log("ParGroupDisabler '{0}' is not found or corrupted", section.getFullInternalName());
                }

                if (section instanceof ConfigSect) {
                    if (!section.setByConfig) {
                        logger.atWarn()
                                .withPrefix("Config")
                                .withStyle(ConsoleColor.YELLOW_BG)
                                .log("Section '{0}' is not found or corrupted", section.getFullInternalName());
                    }
                } else if (section instanceof ConfigPar) {
                    if (!section.setByConfig) {
                        logger.atWarn()
                                .withPrefix("Config")
                                .withStyle(ConsoleColor.YELLOW_BG)
                                .log("Parameter '{0}' is not found or corrupted", section.getFullInternalName());
                    }
                    if (!((ConfigPar) section).getConfigVal().setByConfig) {
                        logger.atWarn()
                                .withPrefix("Config")
                                .withStyle(ConsoleColor.YELLOW_BG)
                                .log("Parameter Value '{0}' is not found or corrupted", section.getFullInternalName());
                    }
                } else if (section instanceof ConfigParGroup) {
                    if (!section.setByConfig) {
                        logger.atWarn()
                                .withPrefix("Config")
                                .withStyle(ConsoleColor.YELLOW_BG)
                                .log("ParameterGroup '{0}' is not found or corrupted", section.getFullInternalName());
                    }
                    ((ConfigParGroup<?>) section).getValues().forEach((name, value) -> {
                        if (!value.setByConfig) {
                            logger.atWarn()
                                    .withPrefix("Config")
                                    .withStyle(ConsoleColor.YELLOW_BG)
                                    .log("Value '{0}' of ParameterGroup '{1}' is not found or corrupted",
                                            name, section.getFullInternalName());
                        }
                    });
                }
            }
        }
        return ConfigIOThread.IOResult.createSuccess();
    }

    @SuppressWarnings({ "ResultOfMethodCallIgnored", "DataFlowIssue", "unchecked" }) public ConfigIOThread.IOResult writeFile() {
        if (!parentDir.exists()) parentDir.mkdirs();

        try (BufferedWriter fileWriter = Files.newBufferedWriter(
                tempFile.getAbsoluteFile().toPath(), Charsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE)
        ) {
            config.write(
                    section -> {
                        try {
                            ConfigDictionary dictionary = config.getDictionary();
                            if (section instanceof ConfigSect) {
                                ConfigSect fullSect = (ConfigSect) section;

                                int    deep        = fullSect.getDeep();
                                String bordersOnly = StringUtils.repeat(sectionBorder, delimiter, deep);
                                String bordersTab  = bordersOnly + (deep > 0 ? delimiter : empty);

                                if (fullSect.getComment() != null) { //Comment
                                    for (String s : MouseStrings.splitWordsBaseOnLength(
                                            fullSect.getComment(), delimiter, 120
                                    )) {
                                        fileWriter.write(MouseStrings.format("{0}{1} {2}", bordersTab, comment, s));
                                        fileWriter.newLine();
                                    }
                                }

                                //Section name
                                fileWriter.write(MouseStrings.format("{0}{1} {2}{3}", bordersTab, sectionStart,
                                        section.getName().getDisplayName(), sectionEqual)
                                );
                                fileWriter.newLine();

                                if (fullSect.canBeDisabled()) { //Section disabler
                                    ConfigParDisabler disablePar = fullSect.disablePar;

                                    //Default
                                    PlusMinusType      def       = disablePar.getValue().getDefaultValue();
                                    PredefinedValue<?> configure = disablePar.getValue().getCurrConfigureFromValue(def);
                                    String defString = configure != null
                                            ? configure.getDisplayName()
                                            : MouseNumbers.formatObjectIfNumber(def.toString(), false, true);
                                    fileWriter.write(MouseStrings.format(
                                            "{0}{1}{2}{3} {4}{5} {6}", bordersTab, sectionBorder, delimiter,
                                            ConfigParser.comment, dictionary.getLocaleForLocale(disablePar.getValue().getDefaultLocaleType()),
                                            sectionEqual, configure != null ? configure.getDisplayName() : defString
                                    ));
                                    fileWriter.newLine();

                                    //ConfigureValues
                                    if (disablePar.getValue().saveConfigureValues()) {
                                        ImmutableList<PredefinedValue<PlusMinusType>> configureValues = disablePar.getValue().getConfigureValues();
                                        for (String s : groupPredefinedBaseOnLength(
                                                dictionary.getLocaleForLocale(disablePar.getValue().getPredefinedLocaleType()),
                                                parEqual, delimiter, configureValues, 120
                                        )) {
                                            fileWriter.write(MouseStrings.format(
                                                    "{0}{1}{2}{3} {4}", bordersTab, sectionBorder,
                                                    delimiter, ConfigParser.comment, s
                                            ));
                                            fileWriter.newLine();
                                        }
                                    }

                                    fileWriter.write(MouseStrings.format(
                                            "{0}{1}{2}{3} {4} {5} {6}", bordersTab, sectionBorder, delimiter, sectionDisabler,
                                            disablePar.getName().getDisplayName(), parEqual, disablePar.getValue().toString()
                                    ));
                                    fileWriter.newLine();
                                }

                                fileWriter.write(bordersOnly + (deep > 0 ? delimiter : empty) + sectionBorder); //Indent
                                fileWriter.newLine();
                            } else if (section instanceof ConfigParBase) {
                                ConfigParBase par = (ConfigParBase) section;

                                int    deep        = par.getDeep();
                                String bordersOnly = StringUtils.repeat(sectionBorder, delimiter, deep);
                                String bordersTab  = bordersOnly + (deep > 0 ? delimiter : empty);

                                boolean hasIndent = true;

                                if (par.getComment() != null) { //Comment
                                    for (String s : MouseStrings.splitWordsBaseOnLength(
                                            par.getComment(), delimiter, 120
                                    )) {
                                        fileWriter.write(MouseStrings.format(
                                                "{0}{1} {2}",
                                                bordersTab, ConfigParser.comment, s
                                        ));
                                        fileWriter.newLine();
                                    }

                                    hasIndent = false;
                                }
                                if (section instanceof ConfigPar) {
                                    boolean hasSecondGroup = false;

                                    ConfigVal configVal = par.getConfigVal();

                                    if (configVal.saveType()) { //Type
                                        if (!hasIndent) { //Comment indent
                                            fileWriter.write(bordersTab + ConfigParser.comment);
                                            fileWriter.newLine();
                                        }

                                        IValType type = configVal.getType();
                                        String specificDataType = type == ConfigValType.LIST
                                                ? dictionary.getLocaleForType(((ConfigSimpleListVal<?>) configVal).getListComponentType())
                                                : configVal.getSpecificDataType();

                                        fileWriter.write(MouseStrings.format(
                                                "{0}{1} {2}{3} {4}{5}", bordersTab, ConfigParser.comment,
                                                dictionary.getLocaleForLocale(configVal.getTypeLocaleType()), sectionEqual,
                                                dictionary.getLocaleForType(type),
                                                specificDataType != null ? " (" + StringUtils.capitalize(specificDataType) + ')' : ""
                                        ));
                                        fileWriter.newLine();

                                        hasSecondGroup = true;
                                    }

                                    if (configVal.saveRange()) { //Range
                                        if (!hasIndent && !hasSecondGroup) { //Comment indent
                                            fileWriter.write(bordersTab + ConfigParser.comment);
                                            fileWriter.newLine();
                                        }

                                        RangeContainer<?> range = configVal.getType() == ConfigValType.LIST
                                                ? ((ConfigSimpleListVal<?>) configVal).getListValueRange()
                                                : configVal.getRange();

                                        if (!range.isEmpty()) {
                                            for (String s : groupRangesBaseOnLength(
                                                    dictionary.getLocaleForLocale(configVal.getRangeLocaleType()), parEqual, delimiter,
                                                    range.getRanges(), 120
                                            )) {
                                                fileWriter.write(MouseStrings.format(
                                                        "{0}{1} {2}", bordersTab, ConfigParser.comment, s
                                                ));
                                                fileWriter.newLine();
                                            }

                                            hasSecondGroup = true;
                                        }
                                    }

                                    if (configVal.saveDefaultValue()) { //Default
                                        if (!hasIndent && !hasSecondGroup) { //Comment indent
                                            fileWriter.write(bordersTab + ConfigParser.comment);
                                            fileWriter.newLine();
                                        }

                                        VariableValue<Comparable> def = configVal.getDefaultValue();
                                        PredefinedValue<?> configure = def.isPresent()
                                                ? configVal.getCurrConfigureFromValue(def.getValue())
                                                : null;
                                        String defString = configure != null
                                                ? configure.getDisplayName()
                                                : MouseNumbers.formatObjectIfNumber(def.isPresent() ? def.getValue() : "",
                                                false, true);
                                        fileWriter.write(MouseStrings.format(
                                                "{0}{1} {2}{3} {4}", bordersTab,
                                                ConfigParser.comment, dictionary.getLocaleForLocale(configVal.getDefaultLocaleType()),
                                                sectionEqual, defString
                                        ));
                                        fileWriter.newLine();

                                        hasSecondGroup = true;
                                    }

                                    boolean hasThirdGroup = false;

                                    if (configVal.saveConfigureValues()) { //ConfigureValues
                                        List<PredefinedValue> configureValues = configVal.getConfigureValues();
                                        if (configureValues != null) {
                                            if (!hasIndent || hasSecondGroup) { //Comment indent
                                                fileWriter.write(bordersTab + ConfigParser.comment);
                                                fileWriter.newLine();
                                            }
                                            for (String s : groupPredefinedBaseOnLength(
                                                    dictionary.getLocaleForLocale(configVal.getPredefinedLocaleType()),
                                                    parEqual, delimiter, configureValues, 120)) {
                                                fileWriter.write(MouseStrings.format(
                                                        "{0}{1} {2}", bordersTab, ConfigParser.comment, s
                                                ));
                                                fileWriter.newLine();
                                            }
                                            hasThirdGroup = true;
                                        }
                                    }

                                    if (configVal.saveConstraints()) { //Constraints
                                        ImmutableMap<Constraint, ConfigSectBase> constraints = configVal.getActiveConstraints();
                                        if (constraints != null) {
                                            if ((!hasIndent || hasSecondGroup) && !hasThirdGroup) { //Comment indent
                                                fileWriter.write(bordersTab + ConfigParser.comment);
                                                fileWriter.newLine();
                                            }
                                            for (String s : groupConstraintsBaseOnLength(
                                                    dictionary.getLocaleForLocale(configVal.getConstraintsLocaleType()),
                                                    parEqual, delimiter,
                                                    config.dictionary.getLocaleForLocale(ConfigLocaleType.DISABLED_STATE),
                                                    constrDisabled, constrRelation,
                                                    constrEqual, constrLess, constrMore, constrLessOrEqual, constrMoreOrEqual, constrNotEqual,
                                                    constraints, 120
                                            )) {
                                                fileWriter.write(MouseStrings.format(
                                                        "{0}{1} {2}", bordersTab, ConfigParser.comment, s
                                                ));
                                                fileWriter.newLine();
                                            }
                                            hasThirdGroup = true;
                                        }
                                    }

                                    if (configVal.saveRules()) { //Rules
                                        String rulesForType = config.dictionary.getRulesForType(configVal.getType());
                                        if (rulesForType != null) {
                                            if ((!hasIndent || hasSecondGroup) && !hasThirdGroup) { //Comment indent
                                                fileWriter.write(bordersTab + ConfigParser.comment);
                                                fileWriter.newLine();
                                            }

                                            for (String s : MouseStrings.splitWordsBaseOnLength(
                                                    config.dictionary.getLocaleForLocale(configVal.getTypeLocaleRules())
                                                            + ": " + rulesForType, delimiter, 120)) {
                                                fileWriter.write(MouseStrings.format(
                                                        "{0}{1} {2}", bordersTab, ConfigParser.comment, s
                                                ));
                                                fileWriter.newLine();
                                            }

                                            hasThirdGroup = true;
                                        }
                                    }

                                    ICustomType type = configVal.getType();
                                    String parIcon = type == ConfigValType.LIST ? parListStart
                                            : type == ConfigValType.CONDITION ? parConditionStart : parStart;
                                    fileWriter.write(bordersTab);
                                    for (String s : MouseStrings.splitWordsBaseOnLength(MouseStrings.format(
                                            "{0} {1} {2} {3}", parIcon,
                                            par.getName().getDisplayName(), parEqual, configVal.toString()
                                    ), delimiter, 120)) {
                                        fileWriter.write(MouseStrings.format("{0}", s));
                                        fileWriter.newLine();
                                    }
                                } else if (section instanceof ConfigParGroup) {
                                    ConfigParGroup<?> parGroup = (ConfigParGroup<?>) section;

                                    fileWriter.write(bordersTab);
                                    for (String s : MouseStrings.splitWordsBaseOnLength(MouseStrings.format(
                                            "{0} {1} {2} {3}", groupStart,
                                            par.getName().getDisplayName(), parEqual, groupOpen
                                    ), delimiter, 120)) {
                                        fileWriter.write(MouseStrings.format("{0}", s));
                                        fileWriter.newLine();
                                    }

                                    boolean hasDisabler = false;

                                    if (parGroup.canBeDisabled()) { //ParGroup disabler
                                        ConfigParDisabler disablePar = parGroup.disablePar;

                                        //Default
                                        VariableValue<Boolean> def = disablePar.getValue().getDefaultValue();
                                        PredefinedValue<?> configure = def.isPresent()
                                                ? disablePar.getValue().getCurrConfigureFromValue(def.getValue())
                                                : null;
                                        String defString = configure != null
                                                ? configure.getDisplayName()
                                                : MouseNumbers.formatObjectIfNumber(def.isPresent() ? def : "",
                                                false, true);
                                        fileWriter.write(MouseStrings.format(
                                                "{0}{1}{2} {3}{4} {5}", bordersTab, delimiter,
                                                ConfigParser.comment, dictionary.getLocaleForLocale(disablePar.getValue().getDefaultLocaleType()),
                                                sectionEqual, configure != null ? configure.getDisplayName() : defString
                                        ));
                                        fileWriter.newLine();

                                        //ConfigureValues
                                        if (disablePar.getValue().saveConfigureValues()) {
                                            ImmutableList<PredefinedValue<Boolean>> configureValues = disablePar.getValue().getConfigureValues();
                                            if (configureValues != null) {
                                                for (String s : groupPredefinedBaseOnLength(
                                                        dictionary.getLocaleForLocale(disablePar.getValue().getPredefinedLocaleType()),
                                                        parEqual, delimiter, configureValues, 120
                                                )) {
                                                    fileWriter.write(MouseStrings.format(
                                                            "{0}{1}{2} {3}", bordersTab, delimiter,
                                                            ConfigParser.comment, s
                                                    ));
                                                    fileWriter.newLine();
                                                }
                                            }
                                        }

                                        fileWriter.write(MouseStrings.format(
                                                "{0}{1}{2} {3} {4} {5}", bordersTab, delimiter, groupDisabler,
                                                disablePar.getName().getDisplayName(), parEqual, disablePar.getValue().toString()
                                        ));
                                        fileWriter.newLine();
                                        hasDisabler = true;
                                    }

                                    for (ImmutableDisplayNameMap.Entry<ConfigVal<?>> entry : parGroup.values) {
                                        ConfigVal configVal      = entry.getValue();
                                        boolean   hasSecondGroup = false;

                                        if (configVal.saveType()) { //Type
                                            if (hasDisabler) {
                                                fileWriter.write(bordersTab + delimiter);
                                                fileWriter.newLine();
                                            }
                                            ICustomType type = configVal.getType();
                                            String specificDataType = type == ConfigValType.LIST
                                                    ? dictionary.getLocaleForType(((ConfigSimpleListVal<?>) configVal).getListType())
                                                    : configVal.getSpecificDataType();

                                            fileWriter.write(MouseStrings.format(
                                                    "{0}{1}{2} {3}{4} {5}{6}", bordersTab, delimiter,
                                                    ConfigParser.comment,
                                                    dictionary.getLocaleForLocale(configVal.getTypeLocaleType()), sectionEqual,
                                                    dictionary.getLocaleForType(type),
                                                    specificDataType != null ? " (" + StringUtils.capitalize(specificDataType) + ')' : ""
                                            ));
                                            fileWriter.newLine();

                                            hasSecondGroup = true;
                                        }

                                        if (configVal.saveRange()) { //Range
                                            if (hasDisabler && !hasSecondGroup) {
                                                fileWriter.write(bordersTab + delimiter);
                                                fileWriter.newLine();
                                            }
                                            RangeContainer<?> range = configVal.getType() == ConfigValType.LIST
                                                    ? ((ConfigSimpleListVal<?>) configVal).getListValueRange()
                                                    : configVal.getRange();

                                            if (!range.isEmpty()) {
                                                for (String s : groupRangesBaseOnLength(
                                                        dictionary.getLocaleForLocale(configVal.getRangeLocaleType()),
                                                        parEqual, delimiter, range.getRanges(), 120
                                                )) {
                                                    fileWriter.write(MouseStrings.format(
                                                            "{0}{1}{2} {3}", bordersTab, delimiter,
                                                            ConfigParser.comment, s
                                                    ));
                                                    fileWriter.newLine();
                                                }

                                                hasSecondGroup = true;
                                            }
                                        }

                                        if (configVal.saveDefaultValue()) { //Default
                                            if (hasDisabler && !hasSecondGroup) {
                                                fileWriter.write(bordersTab + delimiter);
                                                fileWriter.newLine();
                                            }
                                            VariableValue<Comparable> def = configVal.getDefaultValue();
                                            PredefinedValue<?> configure = def.isPresent()
                                                    ? configVal.getCurrConfigureFromValue(def.getValue())
                                                    : null;
                                            String defString = configure != null
                                                    ? configure.getDisplayName()
                                                    : MouseNumbers.formatObjectIfNumber(def.isPresent() ? def.getValue() : "",
                                                    false, true);
                                            fileWriter.write(MouseStrings.format(
                                                    "{0}{1}{2} {3}{4} {5}", bordersTab, delimiter,
                                                    ConfigParser.comment, dictionary.getLocaleForLocale(configVal.getDefaultLocaleType()),
                                                    sectionEqual, defString
                                            ));
                                            fileWriter.newLine();

                                            hasSecondGroup = true;
                                        }

                                        boolean hasThirdGroup = false;

                                        if (configVal.saveConfigureValues()) { //ConfigureValues
                                            List<PredefinedValue> configureValues = configVal.getConfigureValues();
                                            if (configureValues != null) {
                                                if (hasSecondGroup || hasDisabler) { //Comment indent
                                                    fileWriter.write(bordersTab + delimiter + ConfigParser.comment);
                                                    fileWriter.newLine();
                                                }

                                                for (String s : groupPredefinedBaseOnLength(
                                                        dictionary.getLocaleForLocale(configVal.getPredefinedLocaleType()),
                                                        parEqual, delimiter, configureValues, 120)) {
                                                    fileWriter.write(MouseStrings.format(
                                                            "{0}{1}{2} {3}", bordersTab, delimiter,
                                                            ConfigParser.comment, s
                                                    ));
                                                    fileWriter.newLine();
                                                }
                                                hasThirdGroup = true;
                                            }
                                        }

                                        if (configVal.saveConstraints()) { //Constraints
                                            ImmutableMap<Constraint, ConfigSectBase> constraints = configVal.getActiveConstraints();
                                            if (constraints != null) {
                                                if ((hasSecondGroup || hasDisabler) && !hasThirdGroup) { //Comment indent
                                                    fileWriter.write(bordersTab + delimiter + ConfigParser.comment);
                                                    fileWriter.newLine();
                                                }

                                                for (String s : groupConstraintsBaseOnLength(
                                                        dictionary.getLocaleForLocale(configVal.getConstraintsLocaleType()),
                                                        parEqual, delimiter,
                                                        config.dictionary.getLocaleForLocale(ConfigLocaleType.DISABLED_STATE),
                                                        constrDisabled, constrRelation,
                                                        constrEqual, constrLess, constrMore, constrLessOrEqual, constrMoreOrEqual, constrNotEqual,
                                                        constraints, 120
                                                )) {
                                                    fileWriter.write(MouseStrings.format(
                                                            "{0}{1}{2} {3}", bordersTab, delimiter,
                                                            ConfigParser.comment, s
                                                    ));
                                                    fileWriter.newLine();
                                                }
                                                hasThirdGroup = true;
                                            }
                                        }

                                        if (configVal.saveRules()) { //Rules
                                            String rulesForType = config.dictionary.getRulesForType(configVal.getType());
                                            if (rulesForType != null) {
                                                if ((hasSecondGroup || hasDisabler) && !hasThirdGroup) { //Comment indent
                                                    fileWriter.write(bordersTab + delimiter + ConfigParser.comment);
                                                    fileWriter.newLine();
                                                }

                                                for (String s : MouseStrings.splitWordsBaseOnLength(
                                                        config.dictionary.getLocaleForLocale(configVal.getTypeLocaleRules())
                                                                + ": " + rulesForType, delimiter, 120)) {
                                                    fileWriter.write(MouseStrings.format(
                                                            "{0}{1}{2} {3}", bordersTab, delimiter,
                                                            ConfigParser.comment, s
                                                    ));
                                                    fileWriter.newLine();
                                                }

                                                hasThirdGroup = true;
                                            }
                                        }

                                        ICustomType type = configVal.getType();
                                        String subParIcon = type == ConfigValType.LIST ? parSubListStart
                                                : type == ConfigValType.CONDITION ? parSubConditionStart : parSubStart;
                                        fileWriter.write(bordersTab + delimiter);
                                        for (String s : MouseStrings.splitWordsBaseOnLength(MouseStrings.format(
                                                "{0} {1} {2} {3}", subParIcon,
                                                entry.getKey().getDisplayName(), parEqual, configVal.toString()
                                        ), delimiter, 120)) {
                                            fileWriter.write(MouseStrings.format("{0}", s));
                                            fileWriter.newLine();
                                        }

                                    }
                                    fileWriter.write(bordersTab + groupClose);
                                    fileWriter.newLine();
                                }
                            }
                        } catch (IOException e) { e.printStackTrace(); }
                    },
                    section -> {
                        try {
                            if (section instanceof ConfigSect) {
                                int    deep        = section.getDeep();
                                String bordersOnly = StringUtils.repeat(sectionBorder, delimiter, deep);
                                String bordersTab  = bordersOnly + (deep > 0 ? delimiter : empty);

                                fileWriter.write(bordersTab + sectionCorner); //Corner

                                if (!section.equals(config.sections.getLast())) {
                                    fileWriter.newLine();
                                    if (deep <= 0) fileWriter.newLine();
                                }

                                if (deep > 0) {
                                    fileWriter.write(bordersOnly); //Indent
                                    fileWriter.newLine();
                                }
                            } else if (section instanceof ConfigParBase) {
                                int    deep        = section.getDeep();
                                String bordersOnly = StringUtils.repeat(sectionBorder, delimiter, deep);

                                if (deep > 0) {
                                    fileWriter.write(bordersOnly); //Indent
                                    fileWriter.newLine();
                                }
                            }
                        } catch (IOException e) { e.printStackTrace(); }
                    }
            );
            if (tempFile.exists()) {
                if (file.exists()) file.delete();
                tempFile.renameTo(file);
            }
            if (logger != null) {
                logger.atInfo()
                        .withPrefix("Config")
                        .withStyle(ConsoleColor.GREEN_BG)
                        .log("Config file '{0}' has been saved", file.getAbsolutePath());
            }
            return ConfigIOThread.IOResult.createSuccess();
        } catch (IOException e) { return ConfigIOThread.IOResult.createError(e); }
    }

    @SuppressWarnings("SameParameterValue")
    private static List<String> groupRangesBaseOnLength(
            @Nullable String title, @Nullable String equal, @Nullable String delimiter,
            @Nullable List<? extends Range<?>> input, int maxLength
    ) {
        if ((input == null || input.isEmpty())) return Collections.emptyList();
        if (equal == null) equal = ":";
        if (delimiter == null) delimiter = " ";

        List<String> groups = new ArrayList<>();
        for (Range<?> val : input) {
            if (val != null) {
                groups.add('['
                        + MouseNumbers.formatObjectIfNumber(val.getMinValue(), true)
                        + ' ' + equal + ' '
                        + MouseNumbers.formatObjectIfNumber(val.getMaxValue(), true)
                        + ']'
                );
            }
        }

        ArrayList<String> lines = new ArrayList<>();
        title = MouseStrings.trimWith(title, true, '\t');
        lines.add(title != null ? title + ": " : "");
        boolean first = true;
        for (String valLine : groups) {
            int    lineIndex = lines.size() - 1;
            String line      = lines.get(lineIndex);
            if (line.isEmpty()) lines.set(lineIndex, valLine);
            else if (valLine.length() + 2 <= (maxLength - line.length())) lines.set(lineIndex, line + (first ? "" : ", ") + valLine);
            else lines.add(delimiter + valLine);
            first = false;
        }

        return lines;
    }

    @SuppressWarnings("SameParameterValue")
    private static List<String> groupPredefinedBaseOnLength(
            @Nullable String title, @Nullable String equal, @Nullable String delimiter,
            @Nullable List<? extends PredefinedValue> input, int maxLength
    ) {
        if ((input == null || input.isEmpty())) return Collections.emptyList();
        if (equal == null) equal = ":";
        if (delimiter == null) delimiter = " ";

        //Val, List of names
        HashMap<Object, List<String>> groups = new HashMap<>();
        for (PredefinedValue<?> val : input) {
            if (val != null) groups.computeIfAbsent(
                    val.getValue(),
                    (k) -> new ArrayList<>()).add(val.getDisplayName()
            );
        }

        ArrayList<String> lines = new ArrayList<>();
        title = MouseStrings.trimWith(title, true, '\t');
        lines.add(title != null ? title + ": " : "");
        boolean first = true;
        for (Map.Entry<Object, List<String>> entry : groups.entrySet()) {
            String valLine = entry.getValue().stream().map(s -> "[" + s + "]").collect(Collectors.joining(
                    ", ", "«", "»")) +
                    " " + equal + " " + MouseNumbers.formatObjectIfNumber(entry.getKey(), false, true);
            int    lineIndex = lines.size() - 1;
            String line      = lines.get(lineIndex);
            if (line.isEmpty()) lines.set(lineIndex, valLine);
            else if (valLine.length() + 2 <= (maxLength - line.length())) lines.set(lineIndex, line + (first ? "" : ", ") + valLine);
            else lines.add(delimiter + valLine);
            first = false;
        }

        return lines;
    }

    @SuppressWarnings({ "SpellCheckingInspection", "SameParameterValue" })
    private static List<String> groupConstraintsBaseOnLength(
            @Nullable String title, @Nullable String equal, @Nullable String delimiter, @Nullable String disabledState, @Nullable String constrDisabled,
            @Nullable String constrRelation, @Nullable String constrEqual, @Nullable String constrLess, @Nullable String constrMore,
            @Nullable String constrLessOrEqual, @Nullable String constrMoreOrEqual, @Nullable String constrNotEqual,
            @Nullable Map<? extends Constraint, ? extends ConfigSectBase> input, int maxLength
    ) {
        if ((input == null || input.isEmpty())) return Collections.emptyList();

        if (equal == null) equal = ":";
        if (delimiter == null) delimiter = " ";
        if (disabledState == null) disabledState = "Disabled";
        if (constrDisabled == null) constrDisabled = "DISABLED IF";
        if (constrRelation == null) constrRelation = "RELATION";
        if (constrEqual == null) constrEqual = "=";
        if (constrLess == null) constrLess = "<";
        if (constrMore == null) constrMore = ">";
        if (constrLessOrEqual == null) constrLessOrEqual = "<=";
        if (constrMoreOrEqual == null) constrMoreOrEqual = ">=";
        if (constrNotEqual == null) constrNotEqual = "!=";

        //List of constraints
        ArrayList<String> disabledGroup = new ArrayList<>();
        ArrayList<String> relationGroup = new ArrayList<>();
        for (Map.Entry<? extends Constraint, ? extends ConfigSectBase> entry : input.entrySet()) {
            Constraint     constraint = entry.getKey();
            ConfigSectBase sect       = entry.getValue();
            if (sect == null) continue;
            String displayName = sect.getName().getDisplayName();
            if (constraint.getConditionType() == Constraint.ConstraintConditionType.DISABLED) {
                disabledGroup.add(displayName + ' ' + equal + ' ' + disabledState);
            } else {
                if (constraint.getType() == Constraint.ConstraintType.DISABLED) {
                    if (constraint.hasSpecific()) {
                        switch (constraint.getConditionType()) {
                            case LESS:
                                disabledGroup.add(displayName + constrLess
                                        + MouseNumbers.formatObjectIfNumber(constraint.getVal(), false));
                                break;
                            case MORE:
                                disabledGroup.add(displayName + constrMore
                                        + MouseNumbers.formatObjectIfNumber(constraint.getVal(), false));
                                break;
                            case EQUAL:
                                disabledGroup.add(displayName + constrEqual
                                        + MouseNumbers.formatObjectIfNumber(constraint.getVal(), false));
                                break;
                            case NOT_EQUAL:
                                disabledGroup.add(displayName + constrNotEqual
                                        + MouseNumbers.formatObjectIfNumber(constraint.getVal(), false));
                                break;
                            case LESS_OR_EQUAL:
                                disabledGroup.add(displayName + constrLessOrEqual
                                        + MouseNumbers.formatObjectIfNumber(constraint.getVal(), false));
                                break;
                            case MORE_OR_EQUAL:
                                disabledGroup.add(displayName + constrMoreOrEqual
                                        + MouseNumbers.formatObjectIfNumber(constraint.getVal(), false));
                                break;
                            case IN_RANGE:
                                Range range = constraint.getRange();
                                assert range != null;
                                disabledGroup.add(
                                        MouseNumbers.formatObjectIfNumber(range.getMinValue(), false)
                                                + (range.isIncludeMin() ? constrMoreOrEqual : constrMore)
                                                + displayName
                                                + (range.isIncludeMax() ? constrLessOrEqual : constrLess)
                                                + MouseNumbers.formatObjectIfNumber(range.getMaxValue(), false)
                                );
                                break;
                        }
                    } else {
                        switch (constraint.getConditionType()) {
                            case LESS:
                                disabledGroup.add(constrLess + displayName);
                                break;
                            case MORE:
                                disabledGroup.add(constrMore + displayName);
                                break;
                            case EQUAL:
                                disabledGroup.add(constrEqual + displayName);
                                break;
                            case NOT_EQUAL:
                                disabledGroup.add(constrNotEqual + displayName);
                                break;
                            case LESS_OR_EQUAL:
                                disabledGroup.add(constrLessOrEqual + displayName);
                                break;
                            case MORE_OR_EQUAL:
                                disabledGroup.add(constrMoreOrEqual + displayName);
                                break;
                        }
                    }
                } else {
                    switch (constraint.getConditionType()) {
                        case LESS:
                            relationGroup.add(constrLess + displayName);
                            break;
                        case MORE:
                            relationGroup.add(constrMore + displayName);
                            break;
                        case EQUAL:
                            relationGroup.add(constrEqual + displayName);
                            break;
                        case LESS_OR_EQUAL:
                            relationGroup.add(constrLessOrEqual + displayName);
                            break;
                        case MORE_OR_EQUAL:
                            relationGroup.add(constrMoreOrEqual + displayName);
                            break;
                    }
                }
            }
        }

        ArrayList<String> lines = new ArrayList<>();
        title = MouseStrings.trimWith(title, true, '\t');
        if (title != null) lines.add(title + ": ");

        if (!disabledGroup.isEmpty()) {
            lines.add(delimiter + constrDisabled + ' ');
            boolean first = true;
            for (String valLine : disabledGroup) {
                int    lineIndex = lines.size() - 1;
                String line      = lines.get(lineIndex);
                if (line.isEmpty()) lines.set(lineIndex, valLine);
                else if (valLine.length() + 2 <= (maxLength - line.length()))
                    lines.set(lineIndex, line + (first ? "" : ", ") + valLine);
                else lines.add(delimiter + delimiter + valLine);
                first = false;
            }
        }

        if (!relationGroup.isEmpty()) {
            lines.add(delimiter + constrRelation + ' ');
            boolean first = true;
            for (String valLine : relationGroup) {
                int    lineIndex = lines.size() - 1;
                String line      = lines.get(lineIndex);
                if (line.isEmpty()) lines.set(lineIndex, valLine);
                else if (valLine.length() + 2 <= (maxLength - line.length()))
                    lines.set(lineIndex, line + (first ? "" : ", ") + valLine);
                else lines.add(delimiter + delimiter + valLine);
                first = false;
            }
        }

        return lines;
    }
}