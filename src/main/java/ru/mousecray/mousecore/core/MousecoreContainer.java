package ru.mousecray.mousecore.core;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.versioning.VersionParser;
import net.minecraftforge.fml.common.versioning.VersionRange;

import java.util.Arrays;

public class MousecoreContainer extends DummyModContainer {
    public static final String VERSION = "@mod_version@";
    public static final String ID = "mousecore";
    public static final String NAME = "@mod_name@";
    public static final String ACCEPTED_MC_VERSION = "@internal_mc_version@";

    public MousecoreContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = ID;
        meta.name = NAME;
        meta.version = VERSION;
        meta.description = "Coremod for mousecray mods";
        meta.logoFile = "/assets/mousecore/textures/mod_logo.png";
        meta.authorList.addAll(Arrays.asList("@authors@"));
    }

    @Override
    public VersionRange acceptableMinecraftVersionRange() {
        return VersionParser.parseRange(ACCEPTED_MC_VERSION);
    }

    @Override
    public ModContainer getMod() {
        return this;
    }

    @Override
    public String getVersion() {
        return "@internal_version@";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {return true;}
}
