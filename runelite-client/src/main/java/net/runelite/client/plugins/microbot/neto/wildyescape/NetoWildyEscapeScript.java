package net.runelite.client.plugins.microbot.neto.wildyescape;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.storm.plugins.PlayerMonitor.PlayerMonitorPlugin;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.concurrent.TimeUnit;

public class NetoWildyEscapeScript extends Script {

    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!Microbot.isLoggedIn()) return;

                if (!Rs2Equipment.isWearing("Phoenix necklace")) {
                    escape();
                }
            } catch (Exception e) {
                Microbot.logStackTrace(this.getClass().getSimpleName(), e);
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    private void escape() {
        // stop wilderness agility plugin
        Microbot.stopPlugin("net.runelite.client.plugins.microbot.wildernessagility.WildernessAgilityPlugin");
        // start player monitor plugin
        Microbot.startPlugin(PlayerMonitorPlugin.class);
        // walk to gate
        Rs2Walker.walkTo(new WorldPoint(2998, 3931, 0));
        // open gate
        sleepUntilOnClientThread(() -> Rs2GameObject.getGameObject(23552) != null); // Wait for Cave
        Rs2GameObject.interact(23552, "Open");
        // walk to safe location
        Rs2Walker.walkTo(new WorldPoint(2974, 3887, 0));
        // logout
        Rs2Player.logout();
        // stop this script and plugin
        shutdown();
        Microbot.stopPlugin(NetoWildyEscapePlugin.class);
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
