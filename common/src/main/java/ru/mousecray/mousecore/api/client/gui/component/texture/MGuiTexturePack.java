/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.client.gui.component.texture;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.mousecray.mouseproject.api.client.gui.component.state.MGuiElementState;
import ru.mousecray.mouseproject.api.client.gui.component.state.MGuiElementStateManager;
import ru.mousecray.mouseproject.api.client.gui.dim.GuiVector;
import ru.mousecray.mouseproject.api.client.gui.dim.IGuiVector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class MGuiTexturePack {
    public static MGuiTexturePack EMPTY() { return new MGuiTexturePack(new Int2ObjectArrayMap<>()); }

    private final Int2ObjectMap<List<MGuiTexture>> textures;

    private MGuiTexturePack(Int2ObjectMap<List<MGuiTexture>> textures) { this.textures = textures; }

    @Nullable
    public MGuiTexture getCalculatedTexture(MGuiElementStateManager stateManager) {
        List<MGuiTexture> list = getCalculatedTextures(stateManager);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<MGuiTexture> getCalculatedTextures(MGuiElementStateManager stateManager) {
        List<MGuiTexture> bestTextures = null;
        int               maxBits      = -1;
        int               bestMask     = -1;

        for (Int2ObjectMap.Entry<List<MGuiTexture>> e : textures.int2ObjectEntrySet()) {
            int mask = e.getIntKey();
            if (stateManager.satisfies(mask)) {
                int bits = Integer.bitCount(mask);
                if (bits > maxBits || (bits == maxBits && mask > bestMask)) {
                    maxBits = bits;
                    bestMask = mask;
                    bestTextures = e.getValue();
                }
            }
        }
        return bestTextures != null ? bestTextures : Collections.emptyList();
    }

    @SideOnly(Side.CLIENT)
    public static class Builder {
        private final Int2ObjectMap<List<MGuiTexture>> textures = new Int2ObjectArrayMap<>();
        private final ResourceLocation                 baseTexture;
        private final IGuiVector                       textureSize;
        private final IGuiVector                       startPos;
        private final IGuiVector                       elementSize;

        private MGuiTextureScaleRules scaleRules = new MGuiTextureScaleRules(MGuiTextureScaleType.STRETCH);

        private Builder(ResourceLocation baseTexture, IGuiVector textureSize, IGuiVector startPos, IGuiVector elementSize) {
            this.baseTexture = baseTexture;
            this.textureSize = textureSize;
            this.startPos = startPos;
            this.elementSize = elementSize;
        }

        public static Builder create(ResourceLocation baseTexture, IGuiVector textureSize, IGuiVector startPos, IGuiVector elementSize) {
            return new Builder(baseTexture, textureSize, startPos, elementSize);
        }

        public Builder setScaleRules(MGuiTextureScaleRules scaleRules) {
            this.scaleRules = scaleRules;
            return this;
        }

        public Builder addTextureLayer(IGuiVector layerOffset, IGuiVector layerSize, MGuiTextureScaleRules layerRules, int stateIndex, float opacity, MGuiElementState... states) {
            int mask = MGuiElementStateManager.createMask(states);
            IGuiVector pos = GuiVector.of(
                    startPos.x() + layerOffset.x(),
                    startPos.y() + layerOffset.y() + elementSize.y() * stateIndex
            );
            textures.computeIfAbsent(mask, k -> new ArrayList<>()).add(
                    new MGuiTexture(
                            baseTexture, textureSize, pos, layerSize, layerRules,
                            Math.max(0.0f, Math.min(1.0f, opacity))
                    )
            );
            return this;
        }

        public Builder addTextureLayer(IGuiVector layerOffset, IGuiVector layerSize, MGuiTextureScaleRules layerRules, int stateIndex, MGuiElementState... states) {
            return addTextureLayer(layerOffset, layerSize, layerRules, stateIndex, 1.0f, states);
        }

        public Builder addTexture(int index, float opacity, MGuiElementState... states) {
            int mask = MGuiElementStateManager.createMask(states);
            textures.computeIfAbsent(mask, k -> new ArrayList<>()).add(new MGuiTexture(
                    baseTexture, textureSize,
                    GuiVector.of(startPos.x(), startPos.y() + elementSize.y() * index),
                    elementSize,
                    scaleRules,
                    Math.max(0.0f, Math.min(1.0f, opacity))
            ));
            return this;
        }

        public Builder addTexture(int index, MGuiElementState... states) {
            int mask = MGuiElementStateManager.createMask(states);
            textures.computeIfAbsent(mask, k -> new ArrayList<>()).add(new MGuiTexture(
                    baseTexture, textureSize,
                    GuiVector.of(startPos.x(), startPos.y() + elementSize.y() * index),
                    elementSize,
                    scaleRules
            ));
            return this;
        }

        public MGuiTexturePack build() { return new MGuiTexturePack(textures); }
    }
}
