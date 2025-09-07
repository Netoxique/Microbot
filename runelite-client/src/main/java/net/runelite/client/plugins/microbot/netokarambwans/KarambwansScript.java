package net.runelite.client.plugins.microbot.netokarambwans;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.NpcID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.globval.WidgetIndices;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.netokarambwans.KarambwanInfo.botStatus;
import static net.runelite.client.plugins.microbot.netokarambwans.KarambwanInfo.states;

@Slf4j
public class KarambwansScript extends Script {
    public static double version = 1.1;
    private final WorldPoint fishingPoint = new WorldPoint(2900, 3111, 0);
    private final WorldPoint bankPoint = new WorldPoint(1483, 3648, 0);

    public boolean run(KarambwansConfig config) {
        Microbot.enableAutoRunOn = false;
        Rs2Antiban.setActivity(Activity.CATCHING_RAW_KARAMBWAN);
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                switch (botStatus) {
                    case FISHING:
                        fishingLoop();
                        Rs2Antiban.takeMicroBreakByChance();
                        botStatus = states.WALKING_TO_BANK;
                        Rs2Player.waitForAnimation();
                        break;
                    case WALKING_TO_BANK:
                        doBank();
                        botStatus = states.BANKING;
                        Rs2Random.waitEx(400, 200);
                        break;
                    case BANKING:
                        useBank();
                        botStatus = states.WALKING_TO_FISH;
                        Rs2Random.waitEx(400, 200);
                        break;
                    case WALKING_TO_FISH:
                        walkToFish();
                        botStatus = states.FISHING;
                        Rs2Random.waitEx(400, 200);
                        break;
                }
            } catch (Exception ex) {
                Microbot.logStackTrace(this.getClass().getSimpleName(), ex);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    private void fishingLoop() {
        while (!Rs2Inventory.isFull() && super.isRunning()) {
            if (!Rs2Player.isInteracting() || !Rs2Player.isAnimating()) {
                if (Rs2Inventory.contains(ItemID.TBWT_RAW_KARAMBWANJI)) {
                    interactWithFishingSpot();
                    Rs2Player.waitForAnimation();
                    sleep(2000, 4000);
                } else {
                    Microbot.showMessage("Raw karambwanji not detected. Shutting down");
                    shutdown();
                    return;
                }
            }
        }
    }

    private void doBank() {
        Rs2Walker.walkTo(bankPoint);
        sleepUntil(() -> Rs2Bank.isNearBank(10));
        Rs2Bank.openBank();
    }

    private void useBank() {
        Rs2Bank.depositAll(ItemID.TBWT_RAW_KARAMBWAN);
        Rs2Inventory.waitForInventoryChanges(2000);
        if (Rs2Inventory.contains("scroll") || Rs2Inventory.contains("Scroll")) {
            Rs2Bank.depositAll("scroll");
            Rs2Bank.depositAll("Scroll");
            Rs2Inventory.waitForInventoryChanges(2000);
        }
        if (Rs2Inventory.contains(ItemID.FISH_BARREL_OPEN) || Rs2Inventory.contains(ItemID.FISH_BARREL_CLOSED)) {
            Rs2Bank.emptyFishBarrel();
            Rs2Inventory.waitForInventoryChanges(2000);
        }
    }

    private void interactWithFishingSpot() {
        Rs2Npc.interact(NpcID._0_45_48_KARAMBWAN, "Fish");
    }

    private void refillBait() {

    }

    private void walkToFish() {
        Rs2Walker.walkTo(fishingPoint, 3);
        Rs2Player.waitForWalking();
    }
}

