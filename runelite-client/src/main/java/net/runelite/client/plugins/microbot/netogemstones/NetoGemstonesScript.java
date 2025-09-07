package net.runelite.client.plugins.microbot.netogemstones;

import net.runelite.api.GameObject;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.concurrent.TimeUnit;

public class NetoGemstonesScript extends Script {

    public static String VERSION = "1.0.0";
    private static final int GEM_ROCK = 11380;
    private static final int BANK_DEPOSIT_CHEST = 10530;

    private NetoGemstonesState state = NetoGemstonesState.MINING;

    public boolean run(NetoGemstonesConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;

                switch (state) {
                    case MINING:
                        if (Rs2Inventory.isFull()) {
                            state = NetoGemstonesState.BANKING;
                            return;
                        }

                        GameObject gemRock = Rs2GameObject.getGameObject(GEM_ROCK);
                        if (gemRock != null) {
                            if (Rs2GameObject.interact(gemRock, "Mine")) {
                                Rs2Player.waitForAnimation();
                            }
                        }
                        break;
                    case BANKING:
                        if (!Rs2Inventory.isFull()) {
                            state = NetoGemstonesState.MINING;
                            return;
                        }

                        GameObject depositChest = Rs2GameObject.getGameObject(BANK_DEPOSIT_CHEST);
                        if (depositChest != null) {
                           if (Rs2GameObject.interact(depositChest, "Deposit")) {
                               if (sleepUntil(() -> Rs2Widget.hasWidget("Deposit Box"))) {
                                   if (Rs2Inventory.hasItem("Open gem bag")) {
                                        Rs2Inventory.interact("Open gem bag", "empty to bank");
                                        sleep(600, 1200);
                                   }
                                   Rs2Widget.clickWidget("Deposit inventory");
                                   sleep(600, 1200);
                                   Rs2Bank.closeBank();
                               }
                           }
                        }
                        break;
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    private enum NetoGemstonesState {
        MINING,
        BANKING
    }
}
