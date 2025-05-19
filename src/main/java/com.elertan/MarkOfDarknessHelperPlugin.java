package com.elertan;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
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
    private boolean isMarkOfDarknessActive = false;

    @Provides
    MarkOfDarknessHelperConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(MarkOfDarknessHelperConfig.class);
    }

    private MarkOfDarknessHelperInfobox infobox;
    private Instant lastExpiry;
    private boolean reminderShownDueToAutocastStaff = false;
    private boolean isReminderShown = false;

    private static final int ARCEUUS_SPELLBOOK = 3;
    private static final int VARBIT_SPELLBOOK = 4070;
    private static final int VARBIT_AUTOCAST_SPELL = 276;


    private final HotkeyListener hideReminderHotkeyListener = new HotkeyListener(() -> config.hideReminderHotkey()) {
        @Override
        public void hotkeyPressed() {
            removeReminder();
        }
    };


    @Override
    protected void startUp() throws Exception {
        infobox = new MarkOfDarknessHelperInfobox(this);
        keyManager.registerKeyListener(hideReminderHotkeyListener);
    }

    @Override
    protected void shutDown() throws Exception {
        removeReminder();
        keyManager.unregisterKeyListener(hideReminderHotkeyListener);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        // Mark of Darkness can only be cast with the Arceuus spellbook
        if (!(client.getVarbitValue(VARBIT_SPELLBOOK) == ARCEUUS_SPELLBOOK)) {
            removeReminder();
            return;
        }

        // On timeout remove
        if (lastExpiry != null) {
            final Duration overlayTimeout = Duration.ofSeconds(config.markOfDarknessTimeoutSeconds());
            final Duration sinceExpiry = Duration.between(lastExpiry, Instant.now());

            if (sinceExpiry.compareTo(overlayTimeout) >= 0) {
                removeReminder();
            }
        }

        if (!isMarkOfDarknessActive) {
            if (config.remindWhenHoldingAutocastingStaff() && hasSpellOnAutocast()) {
                reminderShownDueToAutocastStaff = true;
                showReminder(false);
            } else if (reminderShownDueToAutocastStaff) {
                reminderShownDueToAutocastStaff = false;
                // If the player is not holding the autocasting staff anymore, remove the reminder
                removeReminder();
            }
        }
    }

//    @Subscribe
//    public void onMenuOptionClicked(MenuOptionClicked event) {
//        // Check if the clicked menu option is "Mark of Darkness"
//        String menuTarget = event.getMenuTarget();
//        if (!menuTarget.contains("Mark of Darkness")) {
//            return;
//        }
//        System.out.println("Mark of Darkness has been clicked...");
//
//        Widget widget = event.getWidget();
//        if (widget == null) {
//            return;
//        }
//    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.GAMEMESSAGE) {
            return;
        }

        final String markOfDarknessPlacedUponYourselfMessage = "You have placed a Mark of Darkness upon yourself.";
        final String markOfDarknessAboutToRunOutMessage = "Your Mark of Darkness is about to run out.";
        final String markOfDarknessHasFadedAwayMessage = "Your Mark of Darkness has faded away.";

        final String message = event.getMessage();

        if (message.contains(markOfDarknessPlacedUponYourselfMessage)) {
            // Mark of Darkness has been cast
//            System.out.println("Mark of Darkness has been cast!");
            isMarkOfDarknessActive = true;

            overlayManager.remove(overlay);
            infoBoxManager.removeInfoBox(infobox);
        } else if (message.contains(markOfDarknessAboutToRunOutMessage)) {
            // Mark of Darkness is about to run out
//            System.out.println("Mark of Darkness is about to run out!");

            if (config.remindWhenAboutToExpire()) {
                if (!config.onlyRemindWhenHoldingAutocastingStaff() || (config.onlyRemindWhenHoldingAutocastingStaff() && hasSpellOnAutocast())) {
                    reminderShownDueToAutocastStaff = false;
                    showReminder(true);
                }
            }
        } else if (message.contains(markOfDarknessHasFadedAwayMessage)) {
            // Mark of Darkness has faded away
//            System.out.println("Mark of Darkness has faded away!");
            isMarkOfDarknessActive = false;

            if (!config.remindWhenAboutToExpire()) {
                if (!config.onlyRemindWhenHoldingAutocastingStaff() || (config.onlyRemindWhenHoldingAutocastingStaff() && hasSpellOnAutocast())) {
                    reminderShownDueToAutocastStaff = false;
                    showReminder(true);
                }
            }
        }
    }

    private void showReminder(boolean withNotify) {
        isReminderShown = true;
        overlayManager.add(overlay);

        lastExpiry = Instant.now();
        if (config.shouldNotify() && withNotify) {
            notifier.notify("Cast Mark of Darkness!");
        }
    }

    private void removeReminder() {
        isReminderShown = false;
        overlayManager.remove(overlay);
        infoBoxManager.removeInfoBox(infobox);
        lastExpiry = null;
    }

    private int getAutocastSpellId() {
        return client.getVarbitValue(VARBIT_AUTOCAST_SPELL);
    }

    private boolean hasSpellOnAutocast() {
        int spellId = getAutocastSpellId();
        return spellId != 0;
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

