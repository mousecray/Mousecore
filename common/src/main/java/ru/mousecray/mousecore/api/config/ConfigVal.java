/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config;

import com.google.common.collect.ImmutableList;
import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.anno.FieldsAreNonnullByDefault;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.config.specific.ConfigLocaleType;
import ru.mousecray.mousecore.api.config.utils.Constraint;
import ru.mousecray.mousecore.api.config.utils.Constraint.ConstraintConditionType;
import ru.mousecray.mousecore.api.config.utils.Constraint.ConstraintType;
import ru.mousecray.mousecore.api.config.utils.PredefinedValue;
import ru.mousecray.mousecore.api.config.utils.ValueParseResult;
import ru.mousecray.mousecore.api.config.values.base.ConfigNumberVal;
import ru.mousecray.mousecore.api.customtype.CustomType;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static ru.mousecray.mousecore.api.config.utils.Constraint.ConstraintConditionType.IN_RANGE;
import static ru.mousecray.mousecore.api.customtype.CustomValType.*;

@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ConfigVal<V extends CustomType<?>> {
    protected           boolean                        setByConfig;
    protected final     IValType                       type;
    protected final     List<PredefinedValue<V>>       configureValues = new ArrayList<>();
    @Nullable protected ConfigParBase<?>               owner;
    protected           V                              value;
    protected final     V                              disabledValue;
    protected final     V                              defaultValue;
    protected final     Function<ConfigVal<V>, String> specificDataType;
    /**
     * Relations with other ConfigSectBase's
     */
    protected final     List<Constraint<?>>            constraints     = new ArrayList<>();
    protected           V                              constrainedValue;
    /**
     * Current predefined value if it specified in config
     */
    protected @Nullable PredefinedValue<V>             currentConfigure;

    @SafeVarargs
    public ConfigVal(
            IValType type,
            V defaultValue, V disabledValue, Function<ConfigVal<V>, String> specificDataType,
            @Nullable Constraint<?>[] constraints,
            @Nullable PredefinedValue<V>... configureValues
    ) {
        this.type = Objects.requireNonNull(type);
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

    public IValType getType()                                                { return type; }
    public final V getValue()                                                { return isDisabled() ? getDisabledValue() : getConstrainedValue(); }
    public final V getDisabledValue()                                        { return disabledValue; }
    public final V getDefaultValue()                                         { return defaultValue; }
    protected final V getConstrainedValue()                                  { return constrainedValue; }

    @Nullable public ConfigParBase<?> getOwner()                             { return owner; }
    @Nullable protected PredefinedValue<V> getCurrentConfigure()             { return currentConfigure; }
    @Nullable protected Function<ConfigVal<V>, String> getSpecificDataType() { return specificDataType; }

    protected final ImmutableList<Constraint> getConstraints() {
        return ImmutableList.copyOf(constraints);
    }

    protected final ImmutableList<PredefinedValue<V>> getConfigureValues() {
        return ImmutableList.copyOf(configureValues);
    }

    public boolean canBeConfigured()  { return !configureValues.isEmpty(); }
    public boolean canBeConstrained() { return !constraints.isEmpty(); }
    public boolean canBeDisabled()    { return owner != null && owner.canBeDisabled(); }
    public boolean isDisabled()       { return owner != null && owner.isDisabled(); }

    protected V adaptValue(V value)   { return value; }

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


    protected final ValueParseResult setValueRaw(@Nullable String value) {
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
                    .log("ConfigValue '{0}' got a Constraint but it constraining is disabled. Constraints will be skipped",
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

    private void logConstraintDisabledConditionEnd(Constraint<?> constraint) {
        if (hasLogger()) {
            assert owner != null;
            getLogger().atInfo()
                    .withPrefix(LogPrefixes.CONFIG)
                    .log("ConfigValue '{0}' got a Constraint '{1}' with path '{2}'" +
                                    "and that Constraint was applied. All other Constraints will be skipped",
                            owner.getFullInternalName(), constraint.getConditionType(), constraint.getPath());
        }
    }

    private void logConstraintDisabledConditionNotDisabling(Constraint<?> constraint) {
        if (hasLogger()) {
            assert owner != null;
            getLogger().atWarn()
                    .withPrefix(LogPrefixes.CONFIG)
                    .withStyle(ConsoleColor.YELLOW_BG)
                    .log("ConfigValue '{0}' got a Constraint '{1}' with path '{2}' to ConfigSectBase that cannot disabling. " +
                                    "It Constraint will be skipped",
                            owner.getFullInternalName(), constraint.getConditionType(), constraint.getPath()
                    );
        }
    }

    private void logConstraintValNull(Constraint<?> constraint) {
        if (hasLogger()) logConstraintWarn(constraint, "but constraint val is null.");
    }

    private void logConstraintRangeNull(Constraint<?> constraint) {
        if (hasLogger()) logConstraintWarn(constraint, "but constraint range is null.");
    }

    private void logConstraintConditionNotValid(Constraint<?> constraint) {
        if (hasLogger()) logConstraintWarn(constraint, "but constraint condition isn't valid.");
    }

    private void logConstraintIncompatibleTargetType(Constraint<?> constraint) {
        if (hasLogger()) logConstraintWarn(constraint, "that has incompatible type with target.");
    }

    private void logConstraintIncompatibleOwnerType(Constraint<?> constraint) {
        if (hasLogger()) logConstraintWarn(constraint, "that has incompatible type with owner.");
    }

    private void logConstraintIncompatibleTargetValue(Constraint<?> constraint) {
        if (hasLogger()) logConstraintWarn(constraint, "but target value is incompatible.");
    }

    private void logConstraintConditionNotSupport(Constraint<?> constraint) {
        if (hasLogger()) logConstraintWarn(constraint, "but constraint condition type isn't support.");
    }

    private void logConstraintWarn(Constraint<?> constraint, String uniqueMessage) {
        assert owner != null;
        getLogger().atWarn()
                .withPrefix(LogPrefixes.CONFIG)
                .withStyle(ConsoleColor.YELLOW_BG)
                .log("ConfigValue '{0}' got a {1} Constraint '{2}' with path '{3}' {4} It Constraint will be skipped",
                        owner.getFullInternalName(), constraint.getValType(), constraint.getConditionType(), constraint.getPath(), uniqueMessage
                );
    }

    private void logConstraintDisabledEnd(Constraint<?> constraint) {
        if (hasLogger()) {
            assert owner != null;
            getLogger().atInfo()
                    .withPrefix(LogPrefixes.CONFIG)
                    .log("ConfigValue '{0}' got a {1} Constraint '{2}' of {3} type with path '{4}'" +
                                    "and that Constraint was applied. All other Constraints will be skipped",
                            owner.getFullInternalName(), constraint.getValType(), constraint.getConditionType(),
                            constraint.getType(), constraint.getPath());
        }
    }

    private void logConstraintRelationEnd(Constraint<?> constraint) {
        if (hasLogger()) {
            assert owner != null;
            getLogger().atInfo()
                    .withPrefix(LogPrefixes.CONFIG)
                    .log("ConfigValue '{0}' got a {1} Constraint '{2}' of {3} type with path '{4}'" +
                                    "and that Constraint was applied. Current Value is '{5}'",
                            owner.getFullInternalName(), constraint.getValType(), constraint.getConditionType(),
                            constraint.getType(), constraint.getPath(), constrainedValue);
        }
    }

    protected void reloadCache() {
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
                    if (constraint.getConditionType() == ConstraintConditionType.DISABLED) {
                        if (sect.canBeDisabled()) {
                            if (sect.isDisabled()) {
                                constrainedValue = disabledValue;
                                logConstraintDisabledConditionEnd(constraint);
                                return;
                            }
                        } else logConstraintDisabledConditionNotDisabling(constraint);
                    } else if (sect instanceof ConfigParBase) {
                        ConfigVal<?> val = ((ConfigParBase<?>) sect).getConfigVal();
                        if (constraint.getType() == ConstraintType.DISABLED) {
                            if (constraint.hasSpecific()) {
                                switch (constraint.getValType()) {
                                    case LOGICAL:
                                        if (val.getType().getValType() == LOGICAL) {
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
                                        if (val.getType().getValType() == NUMBER) {
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
                                                    if (val instanceof ConfigNumberVal) {
                                                        if (constraint.getRange().isInRange(((ConfigNumberVal<?>) val).getValue())) {
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
                                    case OTHER:
                                        if (val.getType().getValType() == OTHER) {
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
                                switch (constraint.getValType()) {
                                    case LOGICAL:
                                        if (val.getType().getValType() == LOGICAL) {
                                            if (type.getValType() == LOGICAL) {
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
                                        if (val.getType().getValType() == NUMBER) {
                                            if (type.getValType() == NUMBER) {
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
                                        if (val.getType().getValType() == OTHER) {
                                            if (type.getValType() == OTHER) {
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
                            switch (constraint.getValType()) {
                                case LOGICAL:
                                    if (val.getType().getValType() == LOGICAL) {
                                        if (type.getValType() == LOGICAL) {
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
                                    if (val.getType().getValType() == NUMBER) {
                                        if (type.getValType() == NUMBER) {
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
                                    if (val.getType().getValType() == OTHER) {
                                        if (type.getValType() == OTHER) {
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
                                    .log("ConfigValue '{0}' got a Constraint with path '{1}' that isn't ConfigParBase. " +
                                            "It Constraint will be skipped", owner.getFullInternalName(), path);
                        }
                    }
                } else {
                    if (hasLogger()) {
                        getLogger().atWarn()
                                .withPrefix(LogPrefixes.CONFIG)
                                .withStyle(ConsoleColor.YELLOW_BG)
                                .log("ConfigValue '{0}' got a Constraint with path '{1}' to self. " +
                                        "It Constraint will be skipped", owner.getFullInternalName(), path);
                    }
                }
            } else {
                if (hasLogger()) {
                    assert owner != null;
                    getLogger().atWarn()
                            .withPrefix(LogPrefixes.CONFIG)
                            .withStyle(ConsoleColor.YELLOW_BG)
                            .log("ConfigValue '{0}' got a Constraint with path '{1}' that does not exist. " +
                                    "It Constraint will be skipped", owner.getFullInternalName(), path
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

    public boolean isLoaded()                                 { return owner != null && owner.isLoaded(); }
    protected void setOwner(@Nullable ConfigParBase<?> owner) { this.owner = owner; }

    protected ILocaleType getConstraintsLocaleType()          { return ConfigLocaleType.CONSTRAINTS; }
    protected ILocaleType getPredefinedLocaleType()           { return ConfigLocaleType.PREDEFINED; }
    protected ILocaleType getDefaultLocaleType()              { return ConfigLocaleType.DEFAULT; }
    protected ILocaleType getDisabledLocaleType()             { return ConfigLocaleType.DISABLED; }
    protected ILocaleType getTypeLocaleType()                 { return ConfigLocaleType.TYPE; }
    protected ILocaleType getTypeLocaleRules()                { return ConfigLocaleType.RULES; }

    public void reset() {
        if (value != defaultValue) {
            setValue(defaultValue);
            if (owner != null) owner.markDirty();
        }
    }

    public boolean hasConfig()                                           { return owner != null && owner.getConfig() != null; }
    @SuppressWarnings("DataFlowIssue") public MouseConfig getConfig()    { return owner.getConfig(); }
    @SuppressWarnings("DataFlowIssue") protected MouseLogger getLogger() { return owner.getConfig().getLogger(); }

    @SuppressWarnings("DataFlowIssue")
    protected boolean hasLogger() {
        return hasConfig() && owner.getConfig().getLogger() != null;
    }
}