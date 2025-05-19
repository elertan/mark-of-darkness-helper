package com.elertan;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.HotkeyListener;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.regex.Matcher;

@Slf4j
@PluginDescriptor(
        name = "Mark Of Darkness Helper"
)
public class MarkOfDarknessHelperPlugin extends Plugin {
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private MarkOfDarknessHelperOverlay overlay;
    @Inject
    private MarkOfDarknessHelperConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    private Client client;
    @Inject
    private Notifier notifier;

    @Provides
    MarkOfDarknessHelperConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(MarkOfDarknessHelperConfig.class);
    }

    private MarkOfDarknessHelperInfobox infobox;
    private Instant lastExpiry;
    private boolean isMarkOfDarknessClicked = false;

    private static final int SPELLBOOK_VARBIT = 4070;
    private static final int ARCEUUS_SPELLBOOK = 3;


    private final HotkeyListener hideReminderHotkeyListener = new HotkeyListener(() -> config.hideReminderHotkey()) {
        @Override
        public void hotkeyPressed() {
            overlayManager.remove(overlay);
            infoBoxManager.removeInfoBox(infobox);
            lastExpiry = null;
        }
    };


    @Override
    protected void startUp() throws Exception {
        infobox = new MarkOfDarknessHelperInfobox(this);
        keyManager.registerKeyListener(hideReminderHotkeyListener);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        infoBoxManager.removeInfoBox(infobox);
        keyManager.unregisterKeyListener(hideReminderHotkeyListener);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        // Mark of Darkness can only be cast with the Arceuus spellbook
        if (!(client.getVarbitValue(SPELLBOOK_VARBIT) == ARCEUUS_SPELLBOOK)) {
            overlayManager.remove(overlay);
            infoBoxManager.removeInfoBox(infobox);
            lastExpiry = null;

            return;
        }

        // Check recasting Mark of Darkness
//        if (isSpellClicked && (client.getLocalPlayer().getAnimation() == SUMMON_ANIMATION) || checkGraphic())
//        {
//            overlayManager.remove(overlay);
//            infoBoxManager.removeInfoBox(infobox);
//        }

        if (lastExpiry != null) {
            final Duration overlayTimeout = Duration.ofSeconds(config.markOfDarknessTimeoutSeconds());
            final Duration sinceExpiry = Duration.between(lastExpiry, Instant.now());

            if (sinceExpiry.compareTo(overlayTimeout) >= 0) {
                overlayManager.remove(overlay);
                infoBoxManager.removeInfoBox(infobox);
                lastExpiry = null;
            }
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        // Check if the clicked menu option is "Mark of Darkness"
        String menuTarget = event.getMenuTarget();
        System.out.println("Menu target clicked: " + menuTarget);
        if (!menuTarget.contains("Mark of Darkness")) {
            return;
        }

        Widget widget = event.getWidget();
        if (widget == null) {
            return;
        }

        isMarkOfDarknessClicked = true;
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.GAMEMESSAGE) {
            return;
        }

        final String markOfDarknessPlacedUponYourselfMessage = "You have placed a Mark of Darkness upon yourself.";
        final String markOfDarknessAboutToRunOutMessage = "Your Mark of Darkness is about to run out.";
        final String markOfDarknessHasFadedAwayMessage = "Your Mark of Darkness has faded away.";

        final String message = event.getMessage();
        System.out.println("Chat message: " + message);

        if (message.contains(markOfDarknessPlacedUponYourselfMessage)) {
            // Mark of Darkness has been cast
            System.out.println("Mark of Darkness has been cast!");

            overlayManager.remove(overlay);
            infoBoxManager.removeInfoBox(infobox);
            isMarkOfDarknessClicked = false;
        } else if (message.contains(markOfDarknessAboutToRunOutMessage)) {
            // Mark of Darkness is about to run out
            System.out.println("Mark of Darkness is about to run out!");
        } else if (message.contains(markOfDarknessHasFadedAwayMessage)) {
            // Mark of Darkness has faded away
            System.out.println("Mark of Darkness has faded away!");

            // If the spell has been cast there is no need to notify
            if (!isMarkOfDarknessClicked) {
                if (!infoBoxManager.getInfoBoxes().contains(infobox)) {
                    infoBoxManager.addInfoBox(infobox);
                }

                lastExpiry = Instant.now();
                if (config.shouldNotify()) {
                    notifier.notify("Cast Mark of Darkness!");
                }
            }
        }
    }


//    private boolean hasBookOfTheDead()
//    {
//        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
//        ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
//        if (inventory == null || equipment == null)
//        {
//            return false;
//        }
//        return inventory.contains(ItemID.BOOK_OF_THE_DEAD) || equipment.contains(ItemID.BOOK_OF_THE_DEAD);
//    }

    private boolean hasArceuusSpellOnAutocast() {
        return false;
    }
}
