/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.val;

import com.google.common.collect.ImmutableList;
import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.par.ConfigParBase;
import ru.mousecray.mousecore.api.config.sect.ConfigSectBase;
import ru.mousecray.mousecore.api.config.utils.ConfigLocaleType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.utils.ValueParseResult;
import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.config.val.ILocaleType;
import ru.mousecray.mousecore.api.config.val.base.ConfigNumberVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.api.utils.MouseStrings;
import ru.mousecray.mousecore.core.config.MouseConfigImpl;
import ru.mousecray.mousecore.core.config.par.ConfigParBaseImpl;
import ru.mousecray.mousecore.core.config.val.base.ConfigNumberValImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static ru.mousecray.mousecore.api.config.utils.Constraint.ConstraintConditionType.IN_RANGE;
import static ru.mousecray.mousecore.api.customtype.type.ValTypeCategory.*;

@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ConfigValImpl<V extends CustomType<?>> implements ConfigVal<V> {
    protected           boolean                                   setByConfig;
    protected final     List<PredefinedValue<V>>                  configureValues = new ArrayList<>();
    @Nullable protected ConfigParBaseImpl<?>                      owner;
    protected           V                                         value;
    protected final     V                                         disabledValue;
    protected final     V                                         defaultValue;
    protected final     Function<ConfigVal<V>, String>            specificDataType;
    /**
     * Relations with other ConfigSectBaseImpl's
     */
    protected final     List<Constraint<? extends CustomType<?>>> constraints     = new ArrayList<>();
    protected           V                                         constrainedValue;
    /**
     * Current predefined value if it specified in config
     */
    protected @Nullable PredefinedValue<V>                        currentConfigure;

    @SafeVarargs
    public ConfigValImpl(
            V defaultValue, V disabledValue, Function<ConfigVal<V>, String> specificDataType,
            @Nullable Constraint<? extends CustomType<?>>[] constraints,
            @Nullable PredefinedValue<V>... configureValues
    ) {
        this.defaultValue = Objects.requireNonNull(defaultValue);
        this.disabledValue = Objects.requireNonNull(disabledValue);
        this.specificDataType = Objects.requireNonNull(specificDataType);
        value = defaultValue;
        constrainedValue = value;

        if (constraints != null) {
            this.constraints.addAll(
                    Arrays.stream(constraints)
                            .filter(Objects::nonNull)
                            .toList()
            );
        }
        if (configureValues != null) {
            this.configureValues.addAll(
                    Arrays.stream(configureValues)
                            .filter(Objects::nonNull)
                            .toList()
            );
        }
        currentConfigure = getCurrConfigureFromValue(value);
    }

    @Override public IValType getType() { return defaultValue.getValType(); }
    @Override
    public final V getValue() { return isDisabled() ? getDisabledValue() : getConstrainedValue(); }
    @Override public final V getDisabledValue()                              { return disabledValue; }
    @Override public final V getDefaultValue()                               { return defaultValue; }
    protected final V getConstrainedValue()                                  { return constrainedValue; }

    @Nullable @Override public ConfigParBaseImpl<?> getOwner()               { return owner; }
    @Nullable protected PredefinedValue<V> getCurrentConfigure()             { return currentConfigure; }
    @Nullable protected Function<ConfigVal<V>, String> getSpecificDataType() { return specificDataType; }

    protected final ImmutableList<Constraint> getConstraints() {
        return ImmutableList.copyOf(constraints);
    }

    protected final ImmutableList<PredefinedValue<V>> getConfigureValues() {
        return ImmutableList.copyOf(configureValues);
    }

    @Override public boolean canBeConfigured()  { return !configureValues.isEmpty(); }
    @Override public boolean canBeConstrained() { return !constraints.isEmpty(); }
    @Override public boolean canBeDisabled()    { return owner != null && owner.canBeDisabled(); }
    @Override public boolean isDisabled()       { return owner != null && owner.isDisabled(); }

    protected V adaptValue(V value)             { return value; }

//    @Nonnull protected abstract VariableValue<V> invertValue(@Nullable V value);

    protected void setValue(@Nullable V value) {
        if (value == null) return;
        V val = adaptValue(value);
        if (!value.equals(val)) {
            if (hasLogger()) {
                assert owner != null;
                getLogger().atWarn()
                        .withPrefix(LogPrefixes.CONFIG)
                        .withStyle(ConsoleColor.YELLOW_BG)
                        .log("ConfigValue '{0}' got value that not valid. It was adapted", owner.getFullInternalName());
            }
        }
        currentConfigure = getCurrConfigureFromValue(this.value);
        reloadCache();
        if (owner != null) owner.markDirty();
    }

    protected abstract VariableValue<V> parseValue(@Nullable String value);


    public final ValueParseResult setValueRaw(@Nullable String value) {
        value = MouseStrings.trimWith(value, true, '\t');

        PredefinedValue<V> cVal = getCurrConfigureFromString(value);
        //Checking if the value is predefined
        if (canBeConfigured()) {
            if (cVal != null) {
                this.value = cVal.getValue();
                currentConfigure = cVal;
                return ValueParseResult.SUCCESS;
            }
        }

        //Checking if the value is present
        VariableValue<V> parsed = parseValue(value);
        Objects.requireNonNull(parsed);
        if (!parsed.isPresent()) return ValueParseResult.ERROR;

        V val        = parsed.getValue();
        V adaptedVal = adaptValue(val);
        this.value = adaptedVal;
        currentConfigure = getCurrConfigureFromValue(val);
        if (val != adaptedVal) {
            if (hasLogger()) {
                assert owner != null;
                getLogger().atWarn()
                        .withPrefix(LogPrefixes.CONFIG)
                        .withStyle(ConsoleColor.YELLOW_BG)
                        .log("ConfigValue '{0}' got value that not valid. It was adapted", owner.getFullInternalName());
            }
            return ValueParseResult.ADAPTED;
        }

        return ValueParseResult.SUCCESS;
    }

    @Nullable
    private PredefinedValue<V> getCurrConfigureFromString(@Nullable String value) {
        return value != null && canBeConfigured()
                ? MouseCollections.findAny(configureValues, val -> val.getDisplayName().equalsIgnoreCase(value))
                : null;
    }

    @Nullable
    protected PredefinedValue<V> getCurrConfigureFromValue(@Nullable V value) {
        return canBeConfigured()
                ? MouseCollections.findAny(configureValues, val -> val.getValue().equals(value))
                : null;
    }

    protected V processValueIfNotEqualConstraint(V value) { return disabledValue; }

    private void logConstraintsConstrainingDisabled() {
        if (hasLogger()) {
            assert owner != null;
            getLogger().atWarn()
                    .withPrefix(LogPrefixes.CONFIG)
                    .withStyle(ConsoleColor.YELLOW_BG)
                    .log("ConfigValue '{0}' got a ConstraintImpl but it constraining is disabled. Constraints will be skipped",
                            owner.getFullInternalName());
        }
    }

    private void logConstraintsConfigNotPresentOrNotBuilt() {
        if (hasLogger()) {
            assert owner != null;
            getLogger().atWarn()
                    .withPrefix(LogPrefixes.CONFIG)
                    .withStyle(ConsoleColor.YELLOW_BG)
                    .log(
                            "ConfigValue '{0}' got a Constraints but it config not found or not already built. Constraints will be skipped",
                            owner.getFullInternalName()
                    );
        }
    }

    private void logConstraintDisabledConditionEnd(Constraint<? extends CustomType<?>> constraint) {
        if (hasLogger()) {
            assert owner != null;
            getLogger().atInfo()
                    .withPrefix(LogPrefixes.CONFIG)
                    .log("ConfigValue '{0}' got a ConstraintImpl '{1}' with path '{2}'" +
                                    "and that ConstraintImpl was applied. All other Constraints will be skipped",
                            owner.getFullInternalName(), constraint.getConditionType(), constraint.getPath());
        }
    }

    private void logConstraintDisabledConditionNotDisabling(Constraint<? extends CustomType<?>> constraint) {
        if (hasLogger()) {
            assert owner != null;
            getLogger().atWarn()
                    .withPrefix(LogPrefixes.CONFIG)
                    .withStyle(ConsoleColor.YELLOW_BG)
                    .log("ConfigValue '{0}' got a ConstraintImpl '{1}' with path '{2}' to ConfigSectBaseImpl that cannot disabling. " +
                                    "It ConstraintImpl will be skipped",
                            owner.getFullInternalName(), constraint.getConditionType(), constraint.getPath()
                    );
        }
    }

    private void logConstraintValNull(Constraint<? extends CustomType<?>> constraint) {
        if (hasLogger()) logConstraintWarn(constraint, "but constraint val is null.");
    }

    private void logConstraintRangeNull(Constraint<? extends CustomType<?>> constraint) {
        if (hasLogger()) logConstraintWarn(constraint, "but constraint range is null.");
    }

    private void logConstraintConditionNotValid(Constraint<? extends CustomType<?>> constraint) {
        if (hasLogger()) logConstraintWarn(constraint, "but constraint condition isn't valid.");
    }

    private void logConstraintIncompatibleTargetType(Constraint<? extends CustomType<?>> constraint) {
        if (hasLogger()) logConstraintWarn(constraint, "that has incompatible type with target.");
    }

    private void logConstraintIncompatibleOwnerType(Constraint<? extends CustomType<?>> constraint) {
        if (hasLogger()) logConstraintWarn(constraint, "that has incompatible type with owner.");
    }

    private void logConstraintIncompatibleTargetValue(Constraint<? extends CustomType<?>> constraint) {
        if (hasLogger()) logConstraintWarn(constraint, "but target value is incompatible.");
    }

    private void logConstraintConditionNotSupport(Constraint<? extends CustomType<?>> constraint) {
        if (hasLogger()) logConstraintWarn(constraint, "but constraint condition type isn't support.");
    }

    private void logConstraintWarn(Constraint<? extends CustomType<?>> constraint, String uniqueMessage) {
        assert owner != null;
        getLogger().atWarn()
                .withPrefix(LogPrefixes.CONFIG)
                .withStyle(ConsoleColor.YELLOW_BG)
                .log("ConfigValue '{0}' got a {1} ConstraintImpl '{2}' with path '{3}' {4} It ConstraintImpl will be skipped",
                        owner.getFullInternalName(), constraint.getValType(), constraint.getConditionType(), constraint.getPath(), uniqueMessage
                );
    }

    private void logConstraintDisabledEnd(Constraint<? extends CustomType<?>> constraint) {
        if (hasLogger()) {
            assert owner != null;
            getLogger().atInfo()
                    .withPrefix(LogPrefixes.CONFIG)
                    .log("ConfigValue '{0}' got a {1} ConstraintImpl '{2}' of {3} type with path '{4}'" +
                                    "and that ConstraintImpl was applied. All other Constraints will be skipped",
                            owner.getFullInternalName(), constraint.getValType(), constraint.getConditionType(),
                            constraint.getType(), constraint.getPath());
        }
    }

    private void logConstraintRelationEnd(Constraint<? extends CustomType<?>> constraint) {
        if (hasLogger()) {
            assert owner != null;
            getLogger().atInfo()
                    .withPrefix(LogPrefixes.CONFIG)
                    .log("ConfigValue '{0}' got a {1} ConstraintImpl '{2}' of {3} type with path '{4}'" +
                                    "and that ConstraintImpl was applied. Current Value is '{5}'",
                            owner.getFullInternalName(), constraint.getValType(), constraint.getConditionType(),
                            constraint.getType(), constraint.getPath(), constrainedValue);
        }
    }

    public void reloadCache() {
        if (!constraints.isEmpty()) {
            if (!canBeConstrained()) {
                constrainedValue = value;
                logConstraintsConstrainingDisabled();
                return;
            } else if (!hasConfig() || !getConfig().isBuilt()) {
                constrainedValue = value;
                logConstraintsConfigNotPresentOrNotBuilt();
                return;
            }
        }

        constrainedValue = value;
        for (Constraint constraint : constraints) {
            String         path = constraint.getPath();
            ConfigSectBase sect = getConfig().getSectionBase(path);
            if (sect != null) {
                if (sect != owner) {
                    if (constraint.getConditionType() == Constraint.ConstraintConditionType.DISABLED) {
                        if (sect.canBeDisabled()) {
                            if (sect.isDisabled()) {
                                constrainedValue = disabledValue;
                                logConstraintDisabledConditionEnd(constraint);
                                return;
                            }
                        } else logConstraintDisabledConditionNotDisabling(constraint);
                    } else if (sect instanceof ConfigParBaseImpl) {
                        ConfigVal<? extends CustomType<?>> val = ((ConfigParBase<? extends CustomType<?>>) sect).getConfigVal();
                        if (constraint.getType() == Constraint.ConstraintType.DISABLED) {
                            if (constraint.hasSpecific()) {
                                switch (constraint.getValType().getCategory()) {
                                    case LOGICAL:
                                        if (val.getType().getCategory() == LOGICAL) {
                                            if (constraint.getVal() == null) {
                                                logConstraintValNull(constraint);
                                                continue;
                                            }

                                            switch (constraint.getConditionType()) {
                                                case EQUAL:
                                                    if (val.getValue().getLogicPipeline().isEqual(constraint.getVal()).asBoolean()) {
                                                        constrainedValue = disabledValue;
                                                        logConstraintDisabledEnd(constraint);
                                                        return;
                                                    }
                                                    continue;
                                                case NOT_EQUAL:
                                                    if (!val.getValue().getLogicPipeline().isEqual(constraint.getVal()).asBoolean()) {
                                                        constrainedValue = disabledValue;
                                                        logConstraintDisabledEnd(constraint);
                                                        return;
                                                    }
                                                    continue;
                                                default:
                                                    logConstraintConditionNotValid(constraint);
                                                    continue;
                                            }
                                        } else logConstraintIncompatibleTargetType(constraint);
                                        continue;
                                    case NUMBER:
                                        if (val.getType().getCategory() == NUMBER) {
                                            if (constraint.getConditionType() != IN_RANGE && constraint.getVal() == null) {
                                                logConstraintValNull(constraint);
                                                continue;
                                            }

                                            if (constraint.getConditionType() == IN_RANGE && constraint.getRange() == null) {
                                                logConstraintRangeNull(constraint);
                                                continue;
                                            }

                                            switch (constraint.getConditionType()) {
                                                case LESS:
                                                    if (val.getValue().getLogicPipeline().isLess(constraint.getVal()).asBoolean()) {
                                                        constrainedValue = disabledValue;
                                                        logConstraintDisabledEnd(constraint);
                                                        return;
                                                    }
                                                    continue;
                                                case MORE:
                                                    if (val.getValue().getLogicPipeline().isMore(constraint.getVal()).asBoolean()) {
                                                        constrainedValue = disabledValue;
                                                        logConstraintDisabledEnd(constraint);
                                                        return;
                                                    }
                                                    continue;
                                                case EQUAL:
                                                    if (val.getValue().getLogicPipeline().isEqual(constraint.getVal()).asBoolean()) {
                                                        constrainedValue = disabledValue;
                                                        logConstraintDisabledEnd(constraint);
                                                        return;
                                                    }
                                                    continue;
                                                case NOT_EQUAL:
                                                    if (!val.getValue().getLogicPipeline().isEqual(constraint.getVal()).asBoolean()) {
                                                        constrainedValue = disabledValue;
                                                        logConstraintDisabledEnd(constraint);
                                                        return;
                                                    }
                                                    continue;
                                                case LESS_OR_EQUAL:
                                                    if (val.getValue().getLogicPipeline().isLessOrEqual(constraint.getVal()).asBoolean()) {
                                                        constrainedValue = disabledValue;
                                                        logConstraintDisabledEnd(constraint);
                                                        return;
                                                    }
                                                    continue;
                                                case MORE_OR_EQUAL:
                                                    if (val.getValue().getLogicPipeline().isMoreOrEqual(constraint.getVal()).asBoolean()) {
                                                        constrainedValue = disabledValue;
                                                        logConstraintDisabledEnd(constraint);
                                                        return;
                                                    }
                                                    continue;
                                                case IN_RANGE:
                                                    if (val instanceof ConfigNumberValImpl) {
                                                        if (constraint.getRange().isInRange(((ConfigNumberVal<? extends NumberType<?>>) val).getValue())) {
                                                            constrainedValue = disabledValue;
                                                            logConstraintDisabledEnd(constraint);
                                                            return;
                                                        }
                                                    } else logConstraintIncompatibleTargetValue(constraint);
                                                    continue;
                                                default:
                                                    logConstraintConditionNotValid(constraint);
                                                    continue;
                                            }
                                        } else logConstraintIncompatibleTargetType(constraint);
                                        continue;
                                    case STRING:
                                        if (val.getType().getCategory() == STRING) {
                                            switch (constraint.getConditionType()) {
                                                case EQUAL:
                                                    if (val.getValue().getLogicPipeline().isEqual(constraint.getVal()).asBoolean()) {
                                                        constrainedValue = disabledValue;
                                                        logConstraintDisabledEnd(constraint);
                                                        return;
                                                    }
                                                    continue;
                                                case NOT_EQUAL:
                                                    if (!val.getValue().getLogicPipeline().isEqual(constraint.getVal()).asBoolean()) {
                                                        constrainedValue = disabledValue;
                                                        logConstraintDisabledEnd(constraint);
                                                        return;
                                                    }
                                                    continue;
                                                default:
                                                    logConstraintConditionNotValid(constraint);
                                                    continue;
                                            }
                                        } else logConstraintIncompatibleTargetType(constraint);
                                        continue;
                                    case OTHER:
                                        if (val.getType().getCategory() == OTHER) {
                                            switch (constraint.getConditionType()) {
                                                case EQUAL:
                                                    if (val.getValue().getLogicPipeline().isEqual(constraint.getVal()).asBoolean()) {
                                                        constrainedValue = disabledValue;
                                                        logConstraintDisabledEnd(constraint);
                                                        return;
                                                    }
                                                    continue;
                                                case NOT_EQUAL:
                                                    if (!val.getValue().getLogicPipeline().isEqual(constraint.getVal()).asBoolean()) {
                                                        constrainedValue = disabledValue;
                                                        logConstraintDisabledEnd(constraint);
                                                        return;
                                                    }
                                                    continue;
                                                default:
                                                    logConstraintConditionNotValid(constraint);
                                                    continue;
                                            }
                                        } else logConstraintIncompatibleTargetType(constraint);
                                        continue;
                                    default:
                                        logConstraintConditionNotSupport(constraint);
                                }
                            } else {
                                switch (constraint.getValType().getCategory()) {
                                    case LOGICAL:
                                        if (val.getType().getCategory() == LOGICAL) {
                                            if (getType().getCategory() == LOGICAL) {
                                                switch (constraint.getConditionType()) {
                                                    case EQUAL:
                                                        if (constrainedValue.getLogicPipeline().isEqual(val.getValue()).asBoolean()) {
                                                            constrainedValue = disabledValue;
                                                            logConstraintDisabledEnd(constraint);
                                                            return;
                                                        }
                                                        continue;
                                                    case NOT_EQUAL:
                                                        if (!constrainedValue.getLogicPipeline().isEqual(val.getValue()).asBoolean()) {
                                                            constrainedValue = disabledValue;
                                                            logConstraintDisabledEnd(constraint);
                                                            return;
                                                        }
                                                        continue;
                                                    default:
                                                        logConstraintConditionNotValid(constraint);
                                                        continue;
                                                }
                                            } else logConstraintIncompatibleOwnerType(constraint);
                                        } else logConstraintIncompatibleTargetType(constraint);
                                        continue;
                                    case NUMBER:
                                        if (val.getType().getCategory() == NUMBER) {
                                            if (getType().getCategory() == NUMBER) {
                                                switch (constraint.getConditionType()) {
                                                    case LESS:
                                                        if (constrainedValue.getLogicPipeline().isLess(val.getValue()).asBoolean()) {
                                                            constrainedValue = disabledValue;
                                                            logConstraintDisabledEnd(constraint);
                                                            return;
                                                        }
                                                        continue;
                                                    case MORE:
                                                        if (constrainedValue.getLogicPipeline().isMore(val.getValue()).asBoolean()) {
                                                            constrainedValue = disabledValue;
                                                            logConstraintDisabledEnd(constraint);
                                                            return;
                                                        }
                                                        continue;
                                                    case EQUAL:
                                                        if (constrainedValue.getLogicPipeline().isEqual(val.getValue()).asBoolean()) {
                                                            constrainedValue = disabledValue;
                                                            logConstraintDisabledEnd(constraint);
                                                            return;
                                                        }
                                                        continue;
                                                    case NOT_EQUAL:
                                                        if (!constrainedValue.getLogicPipeline().isEqual(val.getValue()).asBoolean()) {
                                                            constrainedValue = disabledValue;
                                                            logConstraintDisabledEnd(constraint);
                                                            return;
                                                        }
                                                        continue;
                                                    case LESS_OR_EQUAL:
                                                        if (constrainedValue.getLogicPipeline().isLessOrEqual(val.getValue()).asBoolean()) {
                                                            constrainedValue = disabledValue;
                                                            logConstraintDisabledEnd(constraint);
                                                            return;
                                                        }
                                                        continue;
                                                    case MORE_OR_EQUAL:
                                                        if (constrainedValue.getLogicPipeline().isMoreOrEqual(val.getValue()).asBoolean()) {
                                                            constrainedValue = disabledValue;
                                                            logConstraintDisabledEnd(constraint);
                                                            return;
                                                        }
                                                        continue;
                                                    default:
                                                        logConstraintConditionNotValid(constraint);
                                                        continue;
                                                }
                                            } else logConstraintIncompatibleOwnerType(constraint);
                                        } else logConstraintIncompatibleTargetType(constraint);
                                        continue;
                                    case OTHER:
                                        if (val.getType().getCategory() == OTHER) {
                                            if (getType().getCategory() == OTHER) {
                                                switch (constraint.getConditionType()) {
                                                    case EQUAL:
                                                        if (constrainedValue.getLogicPipeline().isEqual(val.getValue()).asBoolean()) {
                                                            constrainedValue = disabledValue;
                                                            logConstraintDisabledEnd(constraint);
                                                            return;
                                                        }
                                                        continue;
                                                    case NOT_EQUAL:
                                                        if (!constrainedValue.getLogicPipeline().isEqual(val.getValue()).asBoolean()) {
                                                            constrainedValue = disabledValue;
                                                            logConstraintDisabledEnd(constraint);
                                                            return;
                                                        }
                                                        continue;
                                                    default:
                                                        logConstraintConditionNotValid(constraint);
                                                        continue;
                                                }
                                            } else logConstraintIncompatibleOwnerType(constraint);
                                        } else logConstraintIncompatibleTargetType(constraint);
                                        continue;
                                    case STRING:
                                        if (val.getType().getCategory() == STRING) {
                                            if (getType().getCategory() == STRING) {
                                                switch (constraint.getConditionType()) {
                                                    case EQUAL:
                                                        if (constrainedValue.getLogicPipeline().isEqual(val.getValue()).asBoolean()) {
                                                            constrainedValue = disabledValue;
                                                            logConstraintDisabledEnd(constraint);
                                                            return;
                                                        }
                                                        continue;
                                                    case NOT_EQUAL:
                                                        if (!constrainedValue.getLogicPipeline().isEqual(val.getValue()).asBoolean()) {
                                                            constrainedValue = disabledValue;
                                                            logConstraintDisabledEnd(constraint);
                                                            return;
                                                        }
                                                        continue;
                                                    default:
                                                        logConstraintConditionNotValid(constraint);
                                                        continue;
                                                }
                                            } else logConstraintIncompatibleOwnerType(constraint);
                                        } else logConstraintIncompatibleTargetType(constraint);
                                        continue;
                                    default:
                                        logConstraintConditionNotSupport(constraint);
                                }
                            }
                        } else {
                            switch (constraint.getValType().getCategory()) {
                                case LOGICAL:
                                    if (val.getType().getCategory() == LOGICAL) {
                                        if (getType().getCategory() == LOGICAL) {
                                            switch (constraint.getConditionType()) {
                                                case EQUAL:
                                                    if (!constrainedValue.getLogicPipeline().isEqual(val.getValue()).asBoolean()) {
                                                        //noinspection unchecked
                                                        constrainedValue = (V) constrainedValue.asLogicalType()
                                                                .createType(val.getValue().asLogicalType().asBoolean());
                                                        logConstraintRelationEnd(constraint);
                                                    }
                                                    continue;
                                                case NOT_EQUAL:
                                                    if (constrainedValue.getLogicPipeline().isEqual(val.getValue()).asBoolean()) {
                                                        constrainedValue = processValueIfNotEqualConstraint(constrainedValue);
                                                        logConstraintRelationEnd(constraint);
                                                    }
                                                    continue;
                                                default:
                                                    logConstraintConditionNotValid(constraint);
                                                    continue;
                                            }
                                        } else logConstraintIncompatibleOwnerType(constraint);
                                    } else logConstraintIncompatibleTargetType(constraint);
                                    continue;
                                case NUMBER:
                                    if (val.getType().getCategory() == NUMBER) {
                                        if (getType().getCategory() == NUMBER) {
                                            switch (constraint.getConditionType()) {
                                                case LESS:
                                                    if (!constrainedValue.getLogicPipeline().isLess(val.getValue()).asBoolean()) {
                                                        //noinspection unchecked
                                                        constrainedValue = (V) constrainedValue.asNumberType().createType(
                                                                val.getValue().getArithmeticPipeline().decrement()
                                                                        .asNumberType().asNumber()
                                                        );
                                                        logConstraintRelationEnd(constraint);
                                                    }
                                                    continue;
                                                case MORE:
                                                    if (!constrainedValue.getLogicPipeline().isMore(val.getValue()).asBoolean()) {
                                                        //noinspection unchecked
                                                        constrainedValue = (V) constrainedValue.asNumberType().createType(
                                                                val.getValue().getArithmeticPipeline().increment()
                                                                        .asNumberType().asNumber()
                                                        );
                                                        logConstraintRelationEnd(constraint);
                                                    }
                                                    continue;
                                                case EQUAL:
                                                    if (!constrainedValue.getLogicPipeline().isEqual(val.getValue()).asBoolean()) {
                                                        //noinspection unchecked
                                                        constrainedValue = (V) constrainedValue.asNumberType()
                                                                .createType(val.getValue().asNumberType().asNumber());
                                                        logConstraintRelationEnd(constraint);
                                                    }
                                                    continue;
                                                case NOT_EQUAL:
                                                    if (constrainedValue.getLogicPipeline().isEqual(val.getValue()).asBoolean()) {
                                                        constrainedValue = processValueIfNotEqualConstraint(constrainedValue);
                                                        logConstraintRelationEnd(constraint);
                                                    }
                                                    continue;
                                                case LESS_OR_EQUAL:
                                                    if (!constrainedValue.getLogicPipeline().isLessOrEqual(val.getValue()).asBoolean()) {
                                                        //noinspection unchecked
                                                        constrainedValue = (V) constrainedValue.asNumberType().createType(
                                                                val.getValue().asNumberType().asNumber()
                                                        );
                                                        logConstraintRelationEnd(constraint);
                                                    }
                                                    continue;
                                                case MORE_OR_EQUAL:
                                                    if (!constrainedValue.getLogicPipeline().isMoreOrEqual(val.getValue()).asBoolean()) {
                                                        //noinspection unchecked
                                                        constrainedValue = (V) constrainedValue.asNumberType().createType(
                                                                val.getValue().asNumberType().asNumber()
                                                        );
                                                        logConstraintRelationEnd(constraint);
                                                    }
                                                    continue;
                                                default:
                                                    logConstraintConditionNotValid(constraint);
                                                    continue;
                                            }
                                        } else logConstraintIncompatibleOwnerType(constraint);
                                    } else logConstraintIncompatibleTargetType(constraint);
                                    continue;
                                case OTHER:
                                    if (val.getType().getCategory() == OTHER) {
                                        if (getType().getCategory() == OTHER) {
                                            switch (constraint.getConditionType()) {
                                                case EQUAL:
                                                    if (!constrainedValue.getLogicPipeline().isEqual(val.getValue()).asBoolean()) {
                                                        //noinspection unchecked
                                                        constrainedValue = (V) constrainedValue.asOtherType()
                                                                .createType(val.getValue().asOtherType().getValue());
                                                        logConstraintRelationEnd(constraint);
                                                    }
                                                    continue;
                                                case NOT_EQUAL:
                                                    if (constrainedValue.getLogicPipeline().isEqual(val.getValue()).asBoolean()) {
                                                        constrainedValue = processValueIfNotEqualConstraint(constrainedValue);
                                                        logConstraintRelationEnd(constraint);
                                                    }
                                                    continue;
                                                default:
                                                    logConstraintConditionNotValid(constraint);
                                                    continue;
                                            }
                                        } else logConstraintIncompatibleOwnerType(constraint);
                                    } else logConstraintIncompatibleTargetType(constraint);
                                    continue;
                                case STRING:
                                    if (val.getType().getCategory() == STRING) {
                                        if (getType().getCategory() == STRING) {
                                            switch (constraint.getConditionType()) {
                                                case EQUAL:
                                                    if (!constrainedValue.getLogicPipeline().isEqual(val.getValue()).asBoolean()) {
                                                        //noinspection unchecked
                                                        constrainedValue = (V) constrainedValue.asOtherType()
                                                                .createType(val.getValue().asOtherType().getValue());
                                                        logConstraintRelationEnd(constraint);
                                                    }
                                                    continue;
                                                case NOT_EQUAL:
                                                    if (constrainedValue.getLogicPipeline().isEqual(val.getValue()).asBoolean()) {
                                                        constrainedValue = processValueIfNotEqualConstraint(constrainedValue);
                                                        logConstraintRelationEnd(constraint);
                                                    }
                                                    continue;
                                                default:
                                                    logConstraintConditionNotValid(constraint);
                                                    continue;
                                            }
                                        } else logConstraintIncompatibleOwnerType(constraint);
                                    } else logConstraintIncompatibleTargetType(constraint);
                                    continue;
                                default:
                                    logConstraintConditionNotSupport(constraint);
                            }
                        }
                    } else {
                        if (hasLogger()) {
                            assert owner != null;
                            getLogger().atWarn()
                                    .withPrefix(LogPrefixes.CONFIG)
                                    .withStyle(ConsoleColor.YELLOW_BG)
                                    .log("ConfigValue '{0}' got a ConstraintImpl with path '{1}' that isn't ConfigParBaseImpl. " +
                                            "It ConstraintImpl will be skipped", owner.getFullInternalName(), path);
                        }
                    }
                } else {
                    if (hasLogger()) {
                        getLogger().atWarn()
                                .withPrefix(LogPrefixes.CONFIG)
                                .withStyle(ConsoleColor.YELLOW_BG)
                                .log("ConfigValue '{0}' got a ConstraintImpl with path '{1}' to self. " +
                                        "It ConstraintImpl will be skipped", owner.getFullInternalName(), path);
                    }
                }
            } else {
                if (hasLogger()) {
                    assert owner != null;
                    getLogger().atWarn()
                            .withPrefix(LogPrefixes.CONFIG)
                            .withStyle(ConsoleColor.YELLOW_BG)
                            .log("ConfigValue '{0}' got a ConstraintImpl with path '{1}' that does not exist. " +
                                    "It ConstraintImpl will be skipped", owner.getFullInternalName(), path
                            );
                }
            }
        }
    }

    protected boolean saveType()            { return true; }
    protected boolean saveDefaultValue()    { return true; }
    protected boolean saveDisabledValue()   { return true; }

    protected boolean saveConfigureValues() { return !configureValues.isEmpty(); }
    protected boolean saveConstraints()     { return !constraints.isEmpty(); }
    protected boolean saveRules()           { return true; }

    @Override
    public String toString() {
        return currentConfigure != null
                ? currentConfigure.getDisplayName()
                : value.toString();
    }

    @Override public boolean isLoaded()                        { return owner != null && owner.isLoaded(); }
    public void setOwner(@Nullable ConfigParBaseImpl<?> owner) { this.owner = owner; }

    protected ILocaleType getConstraintsLocaleType()           { return ConfigLocaleType.CONSTRAINTS; }
    protected ILocaleType getPredefinedLocaleType()            { return ConfigLocaleType.PREDEFINED; }
    protected ILocaleType getDefaultLocaleType()               { return ConfigLocaleType.DEFAULT; }
    protected ILocaleType getDisabledLocaleType()              { return ConfigLocaleType.DISABLED; }
    protected ILocaleType getTypeLocaleType()                  { return ConfigLocaleType.TYPE; }
    protected ILocaleType getTypeLocaleRules()                 { return ConfigLocaleType.RULES; }

    @Override
    public void reset() {
        if (value != defaultValue) {
            setValue(defaultValue);
            if (owner != null) owner.markDirty();
        }
    }

    @Override public boolean hasConfig()                                            { return owner != null && owner.getConfig() != null; }
    @SuppressWarnings("DataFlowIssue") @Override public MouseConfigImpl getConfig() { return owner.getConfig(); }
    @SuppressWarnings("DataFlowIssue") protected MouseLogger getLogger()            { return owner.getConfig().getLogger(); }
    @SuppressWarnings("DataFlowIssue") protected boolean hasLogger() {
        return hasConfig() && owner.getConfig().getLogger() != null;
    }
}