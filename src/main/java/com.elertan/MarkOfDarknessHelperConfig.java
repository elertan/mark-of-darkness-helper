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
            keyName = "remindWhenAboutToExpire",
            name = "Remind when about to expire",
            description = "Reminds you when Mark of Darkness is about to expire, instead of when it actually expires.",
            position = 1
    )
    default boolean remindWhenAboutToExpire() {
        return true;
    }

    @ConfigItem(
            keyName = "remindWhenHoldingAutocastingStaff",
            name = "Remind when holding autocasting staff",
            description = "Reminds you to cast Mark of Darkness when you are holding an autocasting staff, if Mark of Darkness is currently not active.",
            position = 2
    )
    default boolean remindWhenHoldingAutocastingStaff() {
        return true;
    }

    @ConfigItem(
            keyName = "onlyRemindWhenHoldingAutocastingStaff",
            name = "Only remind when holding autocasting staff",
            description = "Only reminds you to hold Mark of Darkness when you are holding an autocasting staff, useful for e.g. Purging Staff which extends the duration when equipped.",
            position = 3
    )
    default boolean onlyRemindWhenHoldingAutocastingStaff() {
        return true;
    }

    @ConfigItem(
            keyName = "shouldNotify",
            name = "Notify when Mark of Darkness expires",
            description = "Sends a notification once Mark of Darkness needs to be cast.",
            position = 4
    )
    default boolean shouldNotify() {
        return false;
    }

    @ConfigItem(
            keyName = "markOfDarknessTimeoutSeconds",
            name = "Timeout Mark of Darkness Box",
            description = "The duration in seconds before the Mark of Darkness box disappears.",
            position = 5
    )
    @Units(Units.SECONDS)
    default int markOfDarknessTimeoutSeconds() {
        return 120;
    }

    @ConfigItem(
            keyName = "shouldFlash",
            name = "Flash the Reminder Box",
            description = "Makes the reminder box flash between the defined colors.",
            position = 6
    )
    default boolean shouldFlash() {
        return true;
    }

    @ConfigItem(
            keyName = "hideReminderHotkey",
            name = "Hide Reminder Hotkey",
            description = "Use this hotkey to hide the reminder box.",
            position = 7
    )
    default Keybind hideReminderHotkey() {
        return Keybind.NOT_SET;
    }

    @Alpha
    @ConfigItem(
            keyName = "flashColor1",
            name = "Flash Color #1",
            description = "The first color to flash between, also controls the non-flashing color.",
            position = 8
    )
    default Color flashColor1() {
        return new Color(100, 0, 220, 150);
    }

    @Alpha
    @ConfigItem(
            keyName = "flashColor2",
            name = "Flash Color #2",
            description = "The second color to flash between.",
            position = 9
    )
    default Color flashColor2() {
        return new Color(70, 61, 50, 150);
    }
}
