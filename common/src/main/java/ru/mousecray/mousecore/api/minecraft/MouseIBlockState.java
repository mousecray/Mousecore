/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.minecraft;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import ru.mousecray.mousecore.api.container.SoftHashMap;
import ru.mousecray.mousecore.core.Mousecore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class MouseIBlockState implements Comparable<MouseIBlockState> {
    private static final Map<BlockState, MouseIBlockState> cache = new SoftHashMap<>();

    @Nonnull private final BlockState state;

    private Boolean isAir             = null;
    private Boolean isLeaves          = null;
    private Boolean isFullCube        = null;
    private Boolean isReplaceable     = null;
    private Boolean isWood            = null;
    private Boolean isPassable        = null;
    private Boolean isFireSource      = null;
    private Boolean isFlammable       = null;
    private Boolean isBed             = null;
    private Boolean isLadder          = null;
    private Boolean hasTileEntity     = null;
    private Boolean hasContainer      = null;
    private Boolean isOpaque          = null;
    private Boolean isLiquid          = null;
    private Boolean isRandomlyTicking = null;

    private Float      hardness       = null;
    private Float      resistance     = null;
    private VoxelShape collisionShape = null;
    private VoxelShape shape          = null;
    private Integer    lightEmission  = null;

    public MouseIBlockState(@Nonnull BlockState state) {
        this.state = Objects.requireNonNull(state);
    }

    public static MouseIBlockState create(BlockState state) {
        if (state == null) return null;
        if (Mousecore.getInstance().getConfig().BLOCK_STATE_CACHE.getBooleanAnyway()) {
            return cache.computeIfAbsent(state, MouseIBlockState::new);
        } else return new MouseIBlockState(state);
    }

    public static MouseIBlockState create(Block block) { return block != null ? create(block.defaultBlockState()) : null; }

    @SuppressWarnings("deprecation")
    public static MouseIBlockState create(String prefix, String name) {
        return create(BuiltInRegistries.BLOCK.get(ResourceLocation.tryBuild(prefix, name)).defaultBlockState());
    }

    @SuppressWarnings("deprecation")
    public static MouseIBlockState create(ResourceLocation location) {
        return create(BuiltInRegistries.BLOCK.get(location).defaultBlockState());
    }

    @Nonnull public BlockState getState() { return state; }
    @Nonnull public Block getBlock()      { return state.getBlock(); }

    public boolean isAir(@Nullable BlockGetter world, @Nullable BlockPos pos) {
        return checkCacheDisabled() || isAir == null
                ? (isAir = state.isAir())
                : isAir;
    }

    public boolean isAir() { return isAir(null, null); }

    public boolean isLeaves(@Nullable BlockGetter world, @Nullable BlockPos pos) {
        return checkCacheDisabled() || isLeaves == null
                ? (isLeaves = state.is(BlockTags.LEAVES))
                : isLeaves;
    }

    public boolean isLeaves() { return isLeaves(null, null); }

    public boolean isFullCube(@Nullable BlockGetter world, @Nullable BlockPos pos, @Nullable Direction side) {
        return checkCacheDisabled() || isFullCube == null
                ? (isFullCube = state.isFaceSturdy(
                world != null ? world : EmptyBlockGetter.INSTANCE,
                pos != null ? pos : BlockPos.ZERO,
                side != null ? side : Direction.UP,
                SupportType.FULL))
                : isFullCube;
    }

    public boolean isFullCube() { return isFullCube(null, null, null); }

    public boolean isReplaceable(@Nullable BlockGetter world, @Nullable BlockPos pos) {
        return checkCacheDisabled() || isReplaceable == null
                ? (isReplaceable = state.canBeReplaced())
                : isReplaceable;
    }

    public boolean isReplaceable() { return isReplaceable(null, null); }

    public boolean isWood(@Nullable BlockGetter world, @Nullable BlockPos pos) {
        return checkCacheDisabled() || isWood == null
                ? (isWood = state.is(BlockTags.LOGS))
                : isWood;
    }

    public boolean isWood() { return isWood(null, null); }

    public boolean isPassable(@Nullable BlockGetter world, @Nullable BlockPos pos) {
        if (checkCacheDisabled()) {
            VoxelShape col = getCollisionShape(world, pos);
            return col == null || col.isEmpty();
        }
        if (isPassable == null) {
            VoxelShape col = getCollisionShape(world, pos);
            isPassable = (col == null || col.isEmpty());
        }
        return isPassable;
    }

    public boolean isPassable() { return isPassable(null, null); }

    public boolean isFireSource() {
        return checkCacheDisabled() || isFireSource == null
                ? (isFireSource = (state.is(BlockTags.INFINIBURN_OVERWORLD)
                || state.is(BlockTags.INFINIBURN_NETHER)
                || state.is(BlockTags.INFINIBURN_END)))
                : isFireSource;
    }

    public boolean isFlammable() {
        return checkCacheDisabled() || isFlammable == null
                ? (isFlammable = (state.ignitedByLava()
                || state.is(BlockTags.LOGS)
                || state.is(BlockTags.LEAVES)
                || state.is(BlockTags.PLANKS)
                || state.is(BlockTags.WOOL)))
                : isFlammable;
    }

    public boolean isBed() {
        return checkCacheDisabled() || isBed == null
                ? (isBed = state.is(BlockTags.BEDS))
                : isBed;
    }

    public boolean isLadder(@Nullable BlockGetter world, @Nullable BlockPos pos) {
        return checkCacheDisabled() || isLadder == null
                ? (isLadder = state.is(BlockTags.CLIMBABLE))
                : isLadder;
    }

    public boolean isLadder() { return isLadder(null, null); }

    public float getHardness(@Nullable BlockGetter world, @Nullable BlockPos pos) {
        return checkCacheDisabled() || hardness == null
                ? (hardness = state.getDestroySpeed(world != null ? world : EmptyBlockGetter.INSTANCE, pos != null ? pos : BlockPos.ZERO))
                : hardness;
    }

    public float getHardness() { return getHardness(null, null); }

    @SuppressWarnings("deprecation")
    public float getResistance() {
        return checkCacheDisabled() || resistance == null
                ? (resistance = state.getBlock().getExplosionResistance())
                : resistance;
    }

    public VoxelShape getCollisionShape(@Nullable BlockGetter world, @Nullable BlockPos pos) {
        return checkCacheDisabled() || collisionShape == null
                ? (collisionShape = state.getCollisionShape(
                world != null ? world : EmptyBlockGetter.INSTANCE,
                pos != null ? pos : BlockPos.ZERO,
                CollisionContext.empty()))
                : collisionShape;
    }

    public VoxelShape getCollisionShape() {
        return getCollisionShape(null, null);
    }

    public VoxelShape getShape(@Nullable BlockGetter world, @Nullable BlockPos pos) {
        return checkCacheDisabled() || shape == null
                ? (shape = state.getShape(
                world != null ? world : EmptyBlockGetter.INSTANCE,
                pos != null ? pos : BlockPos.ZERO,
                CollisionContext.empty()))
                : shape;
    }

    public VoxelShape getShape() { return getShape(null, null); }

    @SuppressWarnings("deprecation")
    public int getLightEmission() {
        return checkCacheDisabled() || lightEmission == null
                ? (lightEmission = state.getLightEmission())
                : lightEmission;
    }

    public boolean hasTileEntity() {
        return checkCacheDisabled() || hasTileEntity == null
                ? (hasTileEntity = state.hasBlockEntity())
                : hasTileEntity;
    }

    public boolean hasContainer() {
        return checkCacheDisabled() || hasContainer == null
                ? (hasContainer = (state.getBlock() instanceof EntityBlock))
                : hasContainer;
    }

    public boolean isOpaque() {
        return checkCacheDisabled() || isOpaque == null
                ? (isOpaque = state.canOcclude())
                : isOpaque;
    }

    public boolean isLiquid() {
        return checkCacheDisabled() || isLiquid == null
                ? (isLiquid = !state.getFluidState().isEmpty())
                : isLiquid;
    }

    public boolean isRandomlyTicking() {
        return checkCacheDisabled() || isRandomlyTicking == null
                ? (isRandomlyTicking = state.isRandomlyTicking())
                : isRandomlyTicking;
    }

    public MapColor getMapColor()                  { return state.getMapColor(EmptyBlockGetter.INSTANCE, BlockPos.ZERO); }
    public SoundType getSoundType()                { return state.getSoundType(); }
    public PushReaction getPistonPushReaction()    { return state.getPistonPushReaction(); }

    public boolean isEqual(MouseIBlockState other) { return other != null && (other == this || state == other.state); }

    private static boolean checkCacheDisabled()    { return !Mousecore.getInstance().getConfig().BLOCK_STATE_CACHE_DATA.getBooleanAnyway(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MouseIBlockState that)) return false;
        return Objects.equals(state, that.state);
    }

    @Override public int hashCode() { return Objects.hash(state); }

    @SuppressWarnings("deprecation") @Override
    public String toString() {
        return BuiltInRegistries.BLOCK.getKey(state.getBlock()) + "[" + state.toString() + "]";
    }

    @SuppressWarnings("deprecation") @Override
    public int compareTo(@Nonnull MouseIBlockState o) {
        return Comparator
                .comparing((MouseIBlockState t) -> BuiltInRegistries.BLOCK.getKey(t.getState().getBlock()))
                .thenComparingInt(t -> Block.getId(t.getState()))
                .compare(this, o);
    }
}