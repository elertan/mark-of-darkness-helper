package com.elertan;

import java.awt.Color;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Units;

@ConfigGroup("markofdarknesshelper")
public interface MarkOfDarknessHelperConfig extends Config {
    String GROUP = "markofdarknesshelper";

    @ConfigItem(
            keyName = "shouldNotify",
            name = "Notify when Mark of Darkness expires",
            description = "Sends a notification once Mark of Darkness needs to be cast.",
            position = 1
    )
    default boolean shouldNotify() {
        return true;
    }

    @ConfigItem(
            keyName = "markOfDarknessTimeoutSeconds",
            name = "Timeout Mark of Darkness Box",
            description = "The duration in seconds before the Mark of Darkness box disappears.",
            position = 2
    )
    @Units(Units.SECONDS)
    default int markOfDarknessTimeoutSeconds() {
        return 120;
    }

    @ConfigItem(
            keyName = "shouldFlash",
            name = "Flash the Reminder Box",
            description = "Makes the reminder box flash between the defined colors.",
            position = 3
    )
    default boolean shouldFlash() {
        return false;
    }

    @ConfigItem(
            keyName = "hideReminderHotkey",
            name = "Hide Reminder Hotkey",
            description = "Use this hotkey to hide the reminder box.",
            position = 4
    )
    default Keybind hideReminderHotkey() {
        return Keybind.NOT_SET;
    }

    @Alpha
    @ConfigItem(
            keyName = "flashColor1",
            name = "Flash Color #1",
            description = "The first color to flash between, also controls the non-flashing color.",
            position = 5
    )
    default Color flashColor1() {
        return new Color(100, 0, 220, 150);
    }

    @Alpha
    @ConfigItem(
            keyName = "flashColor2",
            name = "Flash Color #2",
            description = "The second color to flash between.",
            position = 6
    )
    default Color flashColor2() {
        return new Color(70, 61, 50, 150);
    }
}
