package com.elertan;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class MarkOfDarknessHelperOverlay extends OverlayPanel {
    private final MarkOfDarknessHelperConfig config;
    private final Client client;

    @Inject
    private MarkOfDarknessHelperOverlay(MarkOfDarknessHelperConfig config, Client client) {
        this.config = config;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();

        String text = "Cast Mark of Darkness!";
        panelComponent.getChildren().add((LineComponent.builder())
                .left(text)
                .build());

        panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(text) - 20, 0));

        if (config.shouldFlash()) {
            if (client.getGameCycle() % 40 >= 20) {
                panelComponent.setBackgroundColor(config.flashColor1());
            } else {
                panelComponent.setBackgroundColor(config.flashColor2());
            }
        } else {
            panelComponent.setBackgroundColor(config.flashColor1());
        }

        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);

        return panelComponent.render(graphics);
    }
}
