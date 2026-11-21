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
import ru.mousecray.mousecore.api.config.utils.*;
import ru.mousecray.mousecore.api.config.val.ConfigVal;
import ru.mousecray.mousecore.api.config.val.ConfigValueChange;
import ru.mousecray.mousecore.api.config.val.ConfigValueListener;
import ru.mousecray.mousecore.api.config.val.ILocaleType;
import ru.mousecray.mousecore.api.config.val.base.ConfigNumberVal;
import ru.mousecray.mousecore.api.customtype.*;
import ru.mousecray.mousecore.api.customtype.op.LogicalOperator;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValTypeCategory;
import ru.mousecray.mousecore.api.customtype.values.minecraft.MinecraftTagType;
import ru.mousecray.mousecore.api.customtype.values.other.EnumType;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.api.log.MouseLogger;
import ru.mousecray.mousecore.api.utils.MouseCollections;
import ru.mousecray.mousecore.api.utils.MouseStrings;
import ru.mousecray.mousecore.core.config.MouseConfigImpl;
import ru.mousecray.mousecore.core.config.par.ConfigParBaseImpl;
import ru.mousecray.mousecore.core.config.sync.ConfigSyncSnapshot;
import ru.mousecray.mousecore.core.config.utils.ConfigListenerDispatcher;
import ru.mousecray.mousecore.core.config.val.base.ConfigValNumberImpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;

import static ru.mousecray.mousecore.api.config.utils.Constraint.ConstraintConditionType.IN_RANGE;
import static ru.mousecray.mousecore.api.customtype.type.ValTypeCategory.*;

@MethodReturnsNonnullByDefault
@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ConfigValImpl<V extends CustomType<?>> implements ConfigVal<V> {
    protected final     List<PredefinedValue<V>>                  configureValues = new ArrayList<>();
    @Nullable protected ConfigParBaseImpl<?>                      owner;
    protected final     V                                         disabledValue;
    protected final     V                                         defaultValue;
    protected final     Function<ConfigVal<V>, String>            specificDataType;
    /**
     * Relations with other ConfigSectBaseImpl's
     */
    protected final     List<Constraint<? extends CustomType<?>>> constraints     = new ArrayList<>();

    protected volatile boolean isNewState;
    protected volatile boolean setByConfig;
    protected volatile V       value;
    protected volatile V       constrainedValue;
    protected volatile boolean constrainedDisabled;

    /**
     * Current predefined value if it specified in config
     */
    protected volatile @Nullable PredefinedValue<V> currentConfigure;

    /**
     * Network override from Server
     */
    protected volatile @Nullable V networkOverrideValue;

    private final              CopyOnWriteArrayList<ConfigListenerDispatcher.ValueListenerEntry<V>> listeners = new CopyOnWriteArrayList<>();
    @Nullable private volatile V                                                                    lastNotifiedValue;

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
            this.constraints.addAll(Arrays.stream(constraints).filter(Objects::nonNull).toList());
        }
        if (configureValues != null) {
            this.configureValues.addAll(Arrays.stream(configureValues).filter(Objects::nonNull).toList());
        }
        currentConfigure = getCurrConfigureFromValue(value);
        lastNotifiedValue = value;
    }

    public void markNew(boolean isNew)                   { isNewState = isNew; }
    @Override public ConfigSide getSide()                { return owner != null ? owner.getSide() : ConfigSide.COMMON; }
    @Override public boolean isNew()                     { return isNewState || (owner != null && owner.isNew()); }
    @Override public boolean isFrozen()                  { return constrainedDisabled || isSyncedFromServer() || (owner != null && owner.isFrozen()); }
    @Override public boolean hasState(ConfigState state) { return getStates().contains(state); }
    @Override public boolean isSyncedFromServer()        { return networkOverrideValue != null; }

    @Override
    public EnumSet<ConfigState> getStates() {
        EnumSet<ConfigState> result = owner != null ? owner.getStates() : EnumSet.noneOf(ConfigState.class);
        if (isNew()) result.add(ConfigState.NEW);
        if (isFrozen()) result.add(ConfigState.FROZEN);
        return result;
    }

    protected ILocaleType getStateLocaleType()               { return ConfigLocaleType.STATE; }
    protected ILocaleType getSideLocaleType()                { return ConfigLocaleType.SIDE; }

    @Override public boolean isThreadSafe()                  { return owner != null && owner.isThreadSafe(); }
    @Nullable protected ReentrantReadWriteLock getSyncLock() { return owner != null ? owner.getSyncLock() : null; }

    protected <R> R readLocked(Supplier<R> action) {
        ReentrantReadWriteLock lock = getSyncLock();
        if (lock == null) return action.get();
        lock.readLock().lock();
        try { return action.get(); } finally { lock.readLock().unlock(); }
    }

    protected void writeLocked(Runnable action) {
        ReentrantReadWriteLock lock = getSyncLock();
        if (lock == null) {
            action.run();
            return;
        }
        lock.writeLock().lock();
        try { action.run(); } finally { lock.writeLock().unlock(); }
    }

    protected <R> R writeLockedResult(Supplier<R> action) {
        ReentrantReadWriteLock lock = getSyncLock();
        if (lock == null) return action.get();
        lock.writeLock().lock();
        try { return action.get(); } finally { lock.writeLock().unlock(); }
    }

    @Override public IValType getType() { return defaultValue.getValType(); }

    @Override
    public final V getValue() {
        ReentrantReadWriteLock lock = getSyncLock();
        if (lock == null) return getEffectiveValueUnsafe();
        lock.readLock().lock();
        try { return getEffectiveValueUnsafe(); } finally { lock.readLock().unlock(); }
    }

    protected V getEffectiveValueUnsafe() {
        if (networkOverrideValue != null) return networkOverrideValue;
        return isValueDisabled() ? disabledValue : constrainedValue;
    }

    @Override public final V getDisabledValue()                              { return disabledValue; }
    @Override public final V getDefaultValue()                               { return defaultValue; }
    protected final V getConstrainedValue()                                  { return constrainedValue; }

    @Nullable @Override public ConfigParBaseImpl<?> getOwner()               { return owner; }
    @Nullable protected PredefinedValue<V> getCurrentConfigure()             { return currentConfigure; }
    @Nullable protected Function<ConfigVal<V>, String> getSpecificDataType() { return specificDataType; }

    protected final ImmutableList<Constraint> getConstraints()               { return ImmutableList.copyOf(constraints); }
    protected final ImmutableList<PredefinedValue<V>> getConfigureValues()   { return ImmutableList.copyOf(configureValues); }

    @Override public boolean canBeConfigured()                               { return !configureValues.isEmpty(); }
    @Override public boolean canBeConstrained()                              { return !constraints.isEmpty(); }
    @Override public boolean canBeDisabled()                                 { return (owner != null && owner.canBeDisabled()) || hasDisablingConstraints(); }
    @Override public boolean isDisabled()                                    { return isValueDisabled(); }
    public boolean isConstrainedDisabled()                                   { return constrainedDisabled; }

    public boolean hasDisablingConstraints() {
        for (Constraint<? extends CustomType<?>> c : constraints) if (c.getType() == Constraint.ConstraintType.DISABLED) return true;
        return false;
    }

    protected boolean isValueDisabled() { return (owner != null && owner.isDisabled()) || constrainedDisabled; }

    protected V adaptValue(V value)     { return value; }

    private boolean applyValueDirect(V val) {
        if (Objects.equals(value, val)) return false;
        value = val;
        constrainedValue = val;
        currentConfigure = getCurrConfigureFromValue(val);
        if (isNewState) isNewState = false;
        if (owner != null) {
            if (owner.isNew()) owner.markNew(false);
            owner.markDirty();
        }
        return true;
    }

    public void setValueDirect(@Nullable V value) {
        if (value == null) return;
        applyValueDirect(adaptValue(value));
    }

    protected void setValue(@Nullable V value) {
        if (value == null) return;
        V val = adaptValue(value);
        if (!value.equals(val) && hasLogger()) {
            assert owner != null;
            getLogger().atWarn()
                    .withPrefix(LogPrefixes.CONFIG)
                    .withStyle(ConsoleColor.YELLOW_BG)
                    .log("ConfigValue '{0}' got value that not valid. It was adapted", owner.getFullInternalName());
        }

        if (hasConfig() && getConfig().isBuilt()) {
            MouseConfigImpl                                            cfg   = getConfig();
            ReentrantReadWriteLock                                     lock  = cfg.getSyncLock();
            List<ConfigListenerDispatcher.PendingValueNotification<?>> batch = null;
            if (lock != null) lock.writeLock().lock();
            try {
                if (applyValueDirect(val)) {
                    cfg.reloadCacheDirect();
                    batch = cfg.collectChangesIfListening();
                }
            } finally {
                if (lock != null) lock.writeLock().unlock();
            }
            cfg.dispatchChanges(batch);
            cfg.broadcastSyncedIfNeeded();
        } else {
            ReentrantReadWriteLock                                     lock    = getSyncLock();
            List<ConfigListenerDispatcher.PendingValueNotification<?>> pending = null;
            if (lock != null) lock.writeLock().lock();
            try {
                if (applyValueDirect(val)) {
                    onReloadCache();
                    if (!listeners.isEmpty()) {
                        pending = new ArrayList<>(1);
                        onCollectChanges(pending);
                    }
                }
            } finally { if (lock != null) lock.writeLock().unlock(); }
            if (pending != null && !pending.isEmpty()) {
                ConfigListenerDispatcher.dispatch(null, pending, Collections.emptyList(), hasLogger() ? getLogger() : null);
            }
        }
    }

    protected abstract VariableValue<V> parseValue(@Nullable String value);

    public final ValueParseResult setValueRaw(@Nullable String value) {
        String                 trimmed = MouseStrings.trimWith(value, true, '\t');
        ReentrantReadWriteLock lock    = getSyncLock();
        if (lock != null) lock.writeLock().lock();
        try {
            PredefinedValue<V> cVal = getCurrConfigureFromString(trimmed);
            if (canBeConfigured() && cVal != null) {
                this.value = cVal.getValue();
                constrainedValue = this.value;
                currentConfigure = cVal;
                return ValueParseResult.SUCCESS;
            }

            VariableValue<V> parsed = parseValue(trimmed);
            Objects.requireNonNull(parsed);
            if (!parsed.isPresent()) return ValueParseResult.ERROR;

            V val        = parsed.getValue();
            V adaptedVal = adaptValue(val);
            this.value = adaptedVal;
            constrainedValue = adaptedVal;
            currentConfigure = getCurrConfigureFromValue(adaptedVal);
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
        } finally {
            if (lock != null) lock.writeLock().unlock();
        }
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

    protected V processValueIfNotEqualConstraint(V value)         { return disabledValue; }
    @Nullable public String getRules(ConfigDictionary dictionary) { return dictionary.getRulesForType(getType()); }

    public ConfigSyncSnapshot.SyncEntry createSyncEntry() {
        return new ConfigSyncSnapshot.SyncEntry(isDisabled(), isDisabled() ? null : value.toString());
    }

    public void applySyncEntry(ConfigSyncSnapshot.SyncEntry entry) {
        if (entry.disabled()) networkOverrideValue = disabledValue;
        else if (entry.rawValue() != null) {
            PredefinedValue<V> cVal = getCurrConfigureFromString(entry.rawValue());
            if (canBeConfigured() && cVal != null) networkOverrideValue = cVal.getValue();
            else {
                VariableValue<V> parsed = parseValue(entry.rawValue());
                if (parsed != null && parsed.isPresent()) networkOverrideValue = adaptValue(parsed.getValue());
            }
        } else networkOverrideValue = null;
    }

    public void onClearNetworkOverride() { networkOverrideValue = null; }


    @Override
    public void reset() {
        if (!Objects.equals(value, defaultValue)) setValue(defaultValue);
    }

    public final void reloadCache() {
        ReentrantReadWriteLock lock = getSyncLock();
        if (lock != null) lock.writeLock().lock();
        try { onReloadCache(); } finally { if (lock != null) lock.writeLock().unlock(); }
    }

    public void onReset()         { applyValueDirect(defaultValue); }

    public void onSyncListeners() { lastNotifiedValue = getEffectiveValueUnsafe(); }

    public void onCollectChanges(List<ConfigListenerDispatcher.PendingValueNotification<?>> out) {
        V current = getEffectiveValueUnsafe();
        V prev    = lastNotifiedValue;
        if (prev == null) {
            lastNotifiedValue = current;
            return;
        }
        if (!Objects.equals(prev, current)) {
            lastNotifiedValue = current;
            boolean hasGlobal = hasConfig() && getConfig().hasGlobalListeners();
            if (!listeners.isEmpty() || hasGlobal) {
                List<ConfigListenerDispatcher.ValueListenerEntry<V>> snapshot = listeners.isEmpty()
                        ? Collections.emptyList()
                        : List.copyOf(listeners);
                out.add(new ConfigListenerDispatcher.PendingValueNotification<>(
                        new ConfigValueChange<>(this, prev, current),
                        snapshot
                ));
            }
        }
    }

    @SuppressWarnings({ "rawtypes" })
    public void onReloadCache() {
        constrainedDisabled = false;
        constrainedValue = value;
        if (constraints.isEmpty()) return;

        if (!canBeConstrained()) {
            logConstraintsConstrainingDisabled();
            return;
        } else if (!hasConfig() || !getConfig().isBuilt()) {
            logConstraintsConfigNotPresentOrNotBuilt();
            return;
        }

        for (Constraint constraint : constraints) {
            String         path = constraint.getPath();
            ConfigSectBase sect = getConfig().getSectionBase(path);
            if (sect == null) {
                if (hasLogger()) {
                    assert owner != null;
                    getLogger().atWarn()
                            .withPrefix(LogPrefixes.CONFIG)
                            .withStyle(ConsoleColor.YELLOW_BG)
                            .log("ConfigValue '{0}' got a ConstraintImpl with path '{1}' that does not exist. " +
                                    "It ConstraintImpl will be skipped", owner.getFullInternalName(), path);
                }
                continue;
            }
            if (sect == owner) {
                if (hasLogger()) {
                    getLogger().atWarn()
                            .withPrefix(LogPrefixes.CONFIG)
                            .withStyle(ConsoleColor.YELLOW_BG)
                            .log("ConfigValue '{0}' got a ConstraintImpl with path '{1}' to self. " +
                                    "It ConstraintImpl will be skipped", owner.getFullInternalName(), path);
                }
                continue;
            }

            if (constraint.getConditionType() == Constraint.ConstraintConditionType.DISABLED) {
                if (sect.canBeDisabled()) {
                    if (sect.isDisabled()) {
                        constrainedValue = disabledValue;
                        constrainedDisabled = true;
                        logConstraintDisabledConditionEnd(constraint);
                        return;
                    }
                } else logConstraintDisabledConditionNotDisabling(constraint);
                continue;
            }

            if (!(sect instanceof ConfigParBaseImpl)) {
                if (hasLogger()) {
                    assert owner != null;
                    getLogger().atWarn()
                            .withPrefix(LogPrefixes.CONFIG)
                            .withStyle(ConsoleColor.YELLOW_BG)
                            .log("ConfigValue '{0}' got a ConstraintImpl with path '{1}' that isn't ConfigParBaseImpl. " +
                                    "It ConstraintImpl will be skipped", owner.getFullInternalName(), path);
                }
                continue;
            }

            ConfigVal<? extends CustomType<?>> targetVal = ((ConfigParBase<? extends CustomType<?>>) sect).getConfigVal();
            ValTypeCategory                    constrCat = constraint.getValTypeCategory();
            ValTypeCategory                    targetCat = targetVal.getType().getCategory();
            ValTypeCategory                    ownerCat  = getType().getCategory();

            if (constrCat == UNDEFINED) {
                logConstraintConditionNotSupport(constraint);
                continue;
            }
            if (targetCat != constrCat) {
                logConstraintIncompatibleTargetType(constraint);
                continue;
            }

            if (constraint.getType() == Constraint.ConstraintType.DISABLED) {
                if (constraint.hasSpecific()) {
                    if (applyDisabledSpecific(constraint, targetVal, constrCat)) return;
                } else {
                    if (ownerCat != constrCat) {
                        logConstraintIncompatibleOwnerType(constraint);
                        continue;
                    }
                    if (applyDisabledRelative(constraint, targetVal, constrCat)) return;
                }
            } else {
                if (ownerCat != constrCat) {
                    logConstraintIncompatibleOwnerType(constraint);
                    continue;
                }
                applyRelation(constraint, targetVal, constrCat);
            }
        }
    }

    @SuppressWarnings({ "rawtypes" })
    private boolean applyDisabledSpecific(Constraint constraint, ConfigVal<? extends CustomType<?>> targetVal, ValTypeCategory category) {
        Constraint.ConstraintConditionType cond = constraint.getConditionType();
        if (category == NUMBER) {
            if (cond == IN_RANGE) {
                if (constraint.getRange() == null) {
                    logConstraintRangeNull(constraint);
                    return false;
                }
                if (targetVal instanceof ConfigValNumberImpl) {
                    if (constraint.getRange().isInRange(((ConfigNumberVal<? extends NumberType<?>>) targetVal).getValue())) {
                        constrainedValue = disabledValue;
                        constrainedDisabled = true;
                        logConstraintDisabledEnd(constraint);
                        return true;
                    }
                } else logConstraintIncompatibleTargetValue(constraint);
                return false;
            }

            if (constraint.getVal() == null) {
                logConstraintValNull(constraint);
                return false;
            }
            CustomType<?> tv = targetVal.getValue();
            CustomType<?> cv = constraint.getVal();
            boolean triggered = switch (cond) {
                case LESS -> tv.getLogicPipeline().isLess(cv).asBoolean();
                case MORE -> tv.getLogicPipeline().isMore(cv).asBoolean();
                case EQUAL -> areEqualValues(tv, cv);
                case NOT_EQUAL -> !areEqualValues(tv, cv);
                case LESS_OR_EQUAL -> tv.getLogicPipeline().isLessOrEqual(cv).asBoolean();
                case MORE_OR_EQUAL -> tv.getLogicPipeline().isMoreOrEqual(cv).asBoolean();
                default -> {
                    logConstraintConditionNotValid(constraint);
                    yield false;
                }
            };
            if (triggered) {
                constrainedValue = disabledValue;
                constrainedDisabled = true;
                logConstraintDisabledEnd(constraint);
                return true;
            }
            return false;
        }

        if (constraint.getVal() == null) {
            logConstraintValNull(constraint);
            return false;
        }
        boolean triggered = switch (cond) {
            case EQUAL -> areEqualValues(targetVal.getValue(), constraint.getVal());
            case NOT_EQUAL -> !areEqualValues(targetVal.getValue(), constraint.getVal());
            default -> {
                logConstraintConditionNotValid(constraint);
                yield false;
            }
        };
        if (triggered) {
            constrainedValue = disabledValue;
            constrainedDisabled = true;
            logConstraintDisabledEnd(constraint);
            return true;
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    private boolean applyDisabledRelative(Constraint constraint, ConfigVal<? extends CustomType<?>> targetVal, ValTypeCategory category) {
        Constraint.ConstraintConditionType cond = constraint.getConditionType();
        CustomType<?>                      tv   = targetVal.getValue();
        boolean                            triggered;

        if (category == NUMBER) {
            triggered = switch (cond) {
                case LESS -> constrainedValue.getLogicPipeline().isLess(tv).asBoolean();
                case MORE -> constrainedValue.getLogicPipeline().isMore(tv).asBoolean();
                case EQUAL -> areEqualValues(constrainedValue, tv);
                case NOT_EQUAL -> !areEqualValues(constrainedValue, tv);
                case LESS_OR_EQUAL -> constrainedValue.getLogicPipeline().isLessOrEqual(tv).asBoolean();
                case MORE_OR_EQUAL -> constrainedValue.getLogicPipeline().isMoreOrEqual(tv).asBoolean();
                default -> {
                    logConstraintConditionNotValid(constraint);
                    yield false;
                }
            };
        } else {
            triggered = switch (cond) {
                case EQUAL -> areEqualValues(constrainedValue, tv);
                case NOT_EQUAL -> !areEqualValues(constrainedValue, tv);
                default -> {
                    logConstraintConditionNotValid(constraint);
                    yield false;
                }
            };
        }

        if (triggered) {
            constrainedValue = disabledValue;
            constrainedDisabled = true;
            logConstraintDisabledEnd(constraint);
            return true;
        }
        return false;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void applyRelation(Constraint constraint, ConfigVal<? extends CustomType<?>> targetVal, ValTypeCategory category) {
        Constraint.ConstraintConditionType cond = constraint.getConditionType();
        CustomType<?>                      tv   = targetVal.getValue();

        if (category == NUMBER) {
            switch (cond) {
                case LESS -> {
                    if (!constrainedValue.getLogicPipeline().isLess(tv).asBoolean()) {
                        constrainedValue = adaptValue((V) constrainedValue.asNumberType().createType(
                                tv.getArithmeticPipeline().decrement().asNumberType().asNumber()));
                        logConstraintRelationEnd(constraint);
                    }
                }
                case MORE -> {
                    if (!constrainedValue.getLogicPipeline().isMore(tv).asBoolean()) {
                        constrainedValue = adaptValue((V) constrainedValue.asNumberType().createType(
                                tv.getArithmeticPipeline().increment().asNumberType().asNumber()));
                        logConstraintRelationEnd(constraint);
                    }
                }
                case EQUAL -> {
                    if (!areEqualValues(constrainedValue, tv)) {
                        constrainedValue = adaptValue((V) constrainedValue.asNumberType().createType(tv.asNumberType().asNumber()));
                        logConstraintRelationEnd(constraint);
                    }
                }
                case NOT_EQUAL -> {
                    if (areEqualValues(constrainedValue, tv)) {
                        constrainedValue = adaptValue(processValueIfNotEqualConstraint(constrainedValue));
                        logConstraintRelationEnd(constraint);
                    }
                }
                case LESS_OR_EQUAL -> {
                    if (!constrainedValue.getLogicPipeline().isLessOrEqual(tv).asBoolean()) {
                        constrainedValue = adaptValue((V) constrainedValue.asNumberType().createType(tv.asNumberType().asNumber()));
                        logConstraintRelationEnd(constraint);
                    }
                }
                case MORE_OR_EQUAL -> {
                    if (!constrainedValue.getLogicPipeline().isMoreOrEqual(tv).asBoolean()) {
                        constrainedValue = adaptValue((V) constrainedValue.asNumberType().createType(tv.asNumberType().asNumber()));
                        logConstraintRelationEnd(constraint);
                    }
                }
                default -> logConstraintConditionNotValid(constraint);
            }
            return;
        }

        if (category == LOGICAL) {
            switch (cond) {
                case EQUAL -> {
                    if (!areEqualValues(constrainedValue, tv)) {
                        constrainedValue = (V) constrainedValue.asLogicalType().createType(tv.asLogicalType().asBoolean());
                        logConstraintRelationEnd(constraint);
                    }
                }
                case NOT_EQUAL -> {
                    if (areEqualValues(constrainedValue, tv)) {
                        constrainedValue = processValueIfNotEqualConstraint(constrainedValue);
                        logConstraintRelationEnd(constraint);
                    }
                }
                default -> logConstraintConditionNotValid(constraint);
            }
            return;
        }

        switch (cond) {
            case EQUAL -> {
                if (!areEqualValues(constrainedValue, tv)) {
                    V converted = tryCopyOrCastValue(tv);
                    if (converted != null) {
                        constrainedValue = converted;
                        logConstraintRelationEnd(constraint);
                    } else {
                        logConstraintIncompatibleTargetValue(constraint);
                    }
                }
            }
            case NOT_EQUAL -> {
                if (areEqualValues(constrainedValue, tv)) {
                    constrainedValue = adaptValue(processValueIfNotEqualConstraint(constrainedValue));
                    logConstraintRelationEnd(constraint);
                }
            }
            default -> logConstraintConditionNotValid(constraint);
        }
    }

    private static boolean areEqualValues(CustomType<?> left, CustomType<?> right) {
        if (left.getLogicPipeline().isSupport(LogicalOperator.Binary.EQUAL, right)) {
            return left.getLogicPipeline().isEqual(right).asBoolean();
        }
        return Objects.equals(left, right);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private V tryCopyOrCastValue(CustomType<?> source) {
        Class<? extends CustomType<?>> targetClass = (Class<? extends CustomType<?>>) constrainedValue.getClass();
        if (targetClass.isInstance(source)) {
            return areStructuresCompatible(constrainedValue, source) ? adaptValue((V) source) : null;
        }
        if (source.getCastPipeline().isSupport(targetClass)) {
            CustomType<?> casted = source.getCastPipeline().asValue(targetClass);
            return areStructuresCompatible(constrainedValue, casted) ? adaptValue((V) casted) : null;
        }
        return null;
    }

    private static boolean areStructuresCompatible(CustomType<?> target, CustomType<?> candidate) {
        if (target instanceof EnumType<?> te && candidate instanceof EnumType<?> ce) return te.getEnumClass() == ce.getEnumClass();
        if (target instanceof ListType<?, ?> tl && candidate instanceof ListType<?, ?> cl)
            return tl.getListComponentValType().equals(cl.getListComponentValType());
        if (target instanceof MapType<?, ?, ?> tm && candidate instanceof MapType<?, ?, ?> cm) {
            return tm.getKeyType().equals(cm.getKeyType()) && tm.getValueType().equals(cm.getValueType());
        }
        if (target instanceof TableType<?> tt && candidate instanceof TableType<?> ct) return tt.getSchema().equals(ct.getSchema());
        if (target instanceof TupleType<?> ttu && candidate instanceof TupleType<?> ctu) return ttu.getSchema().equals(ctu.getSchema());
        if (target instanceof MinecraftTagType ttag && candidate instanceof MinecraftTagType ctag)
            return ttag.getTargetValType().equals(ctag.getTargetValType());
        return true;
    }

    @Override
    public void addListener(ConfigThreadPolicy policy, ConfigValueListener<V> listener) {
        Objects.requireNonNull(policy);
        Objects.requireNonNull(listener);
        synchronized (listeners) {
            listeners.add(new ConfigListenerDispatcher.ValueListenerEntry<>(policy, listener));
            if (hasConfig()) getConfig().adjustListenerCount(1);
        }
    }

    @Override
    public boolean removeListener(ConfigValueListener<V> listener) {
        Objects.requireNonNull(listener);
        synchronized (listeners) {
            int     before  = listeners.size();
            boolean removed = listeners.removeIf(entry -> entry.listener().equals(listener));
            int     delta   = listeners.size() - before;
            if (delta != 0 && hasConfig()) getConfig().adjustListenerCount(delta);
            return removed;
        }
    }

    public void clearListeners() {
        synchronized (listeners) {
            int count = listeners.size();
            if (count > 0) {
                listeners.clear();
                if (hasConfig()) getConfig().adjustListenerCount(-count);
            }
        }
    }

    public void onConfigAttached(MouseConfigImpl config) {
        synchronized (listeners) {
            int count = listeners.size();
            if (count > 0) config.adjustListenerCount(count);
        }
    }

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
                    .log("ConfigValue '{0}' got a Constraints but it config not found or not already built. Constraints will be skipped",
                            owner.getFullInternalName());
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
                            owner.getFullInternalName(), constraint.getConditionType(), constraint.getPath());
        }
    }

    private void logConstraintValNull(Constraint<? extends CustomType<?>> constraint) {
        if (hasLogger())
            logConstraintWarn(constraint, "but constraint val is null.");
    }
    private void logConstraintRangeNull(Constraint<? extends CustomType<?>> constraint) {
        if (hasLogger())
            logConstraintWarn(constraint, "but constraint range is null.");
    }
    private void logConstraintConditionNotValid(Constraint<? extends CustomType<?>> constraint) {
        if (hasLogger())
            logConstraintWarn(constraint, "but constraint conditional isn't valid.");
    }
    private void logConstraintIncompatibleTargetType(Constraint<? extends CustomType<?>> constraint) {
        if (hasLogger())
            logConstraintWarn(constraint, "that has incompatible type with target.");
    }
    private void logConstraintIncompatibleOwnerType(Constraint<? extends CustomType<?>> constraint) {
        if (hasLogger())
            logConstraintWarn(constraint, "that has incompatible type with owner.");
    }
    private void logConstraintIncompatibleTargetValue(Constraint<? extends CustomType<?>> constraint) {
        if (hasLogger())
            logConstraintWarn(constraint, "but target value is incompatible.");
    }
    private void logConstraintConditionNotSupport(Constraint<? extends CustomType<?>> constraint) {
        if (hasLogger())
            logConstraintWarn(constraint, "but constraint conditional type isn't support.");
    }

    private void logConstraintWarn(Constraint<? extends CustomType<?>> constraint, String uniqueMessage) {
        assert owner != null;
        getLogger().atWarn()
                .withPrefix(LogPrefixes.CONFIG)
                .withStyle(ConsoleColor.YELLOW_BG)
                .log("ConfigValue '{0}' got a {1} ConstraintImpl '{2}' with path '{3}' {4} It ConstraintImpl will be skipped",
                        owner.getFullInternalName(), constraint.getValTypeCategory(), constraint.getConditionType(), constraint.getPath(), uniqueMessage);
    }

    private void logConstraintDisabledEnd(Constraint<? extends CustomType<?>> constraint) {
        if (hasLogger()) {
            assert owner != null;
            getLogger().atInfo()
                    .withPrefix(LogPrefixes.CONFIG)
                    .log("ConfigValue '{0}' got a {1} ConstraintImpl '{2}' of {3} type with path '{4}'" +
                                    "and that ConstraintImpl was applied. All other Constraints will be skipped",
                            owner.getFullInternalName(), constraint.getValTypeCategory(), constraint.getConditionType(),
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
                            owner.getFullInternalName(), constraint.getValTypeCategory(), constraint.getConditionType(),
                            constraint.getType(), constraint.getPath(), constrainedValue);
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
        ReentrantReadWriteLock lock = getSyncLock();
        if (lock == null) {
            PredefinedValue<V> cfg = currentConfigure;
            return cfg != null ? cfg.getDisplayName() : value.toString();
        }
        lock.readLock().lock();
        try {
            return currentConfigure != null ? currentConfigure.getDisplayName() : value.toString();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override public boolean isLoaded()                                             { return owner != null && owner.isLoaded(); }
    public void setOwner(@Nullable ConfigParBaseImpl<?> owner)                      { this.owner = owner; }

    protected ILocaleType getConstraintsLocaleType()                                { return ConfigLocaleType.CONSTRAINTS; }
    protected ILocaleType getPredefinedLocaleType()                                 { return ConfigLocaleType.PREDEFINED; }
    protected ILocaleType getDefaultLocaleType()                                    { return ConfigLocaleType.DEFAULT; }
    protected ILocaleType getDisabledLocaleType()                                   { return ConfigLocaleType.DISABLED; }
    protected ILocaleType getTypeLocaleType()                                       { return ConfigLocaleType.TYPE; }
    protected ILocaleType getTypeLocaleRules()                                      { return ConfigLocaleType.RULES; }

    @Override public boolean hasConfig()                                            { return owner != null && owner.getConfig() != null; }
    @SuppressWarnings("DataFlowIssue") @Override public MouseConfigImpl getConfig() { return owner.getConfig(); }
    @SuppressWarnings("DataFlowIssue") protected MouseLogger getLogger()            { return owner.getConfig().getLogger(); }
    @SuppressWarnings("DataFlowIssue") protected boolean hasLogger()                { return hasConfig() && owner.getConfig().getLogger() != null; }
}