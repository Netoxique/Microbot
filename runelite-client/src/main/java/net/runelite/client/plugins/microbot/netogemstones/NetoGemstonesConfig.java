package net.runelite.client.plugins.microbot.netogemstones;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("neto-gemstones")
public interface NetoGemstonesConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General settings",
            position = 0
    )
    String generalSettings = "generalSettings";

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0,
            section = generalSettings
    )
    default String GUIDE() {
        return "Start near a gem rock with a pickaxe in your inventory or equipped.";
    }
}
