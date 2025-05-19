package com.elertan;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

import java.awt.Color;

public class MarkOfDarknessHelperInfobox extends InfoBox {

    public MarkOfDarknessHelperInfobox(Plugin plugin) {
        super(null, plugin);
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public Color getTextColor() {
        return null;
    }

    @Override
    public String getTooltip() {
        return "Cast Mark of Darkness!";
    }
}
