package me.nikita.MurderSpree;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.v1_8_R1.*;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin implements Listener {

    private Map<String, Long> killTimes = new HashMap<>();
    private Map<String, Integer> killCounts = new HashMap<>();
    private static final Map<Integer, String> killStrings = ImmutableMap.of(
            2, "§6Double Kill",
            3, "§3Triple Kill",
            4, "§2Ultra Kill",
            5, "§4RAMPAGE!!!"
    );


    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {

            Player killer = event.getEntity().getKiller();
            World world = killer.getWorld();

            if (killTimes.containsKey(killer.getName())) {
                // if time between kills is less than 4 seconds
                if (System.currentTimeMillis() - killTimes.get(killer.getName()) < 4 * 1000) {

                    if (killCounts.get(killer.getName()) == 5) {
                        setPlayerState(killer, 1);
                        // TODO: add key for lucky chest as a gift to player
                        return;
                    } else {
                        setPlayerState(killer, killCounts.get(killer.getName()) + 1);
                    }

                    actionOnKillingSeries(killer, world);
                    return;
                }
            }
            setPlayerState(killer, 1);
        }
    }

    private void setPlayerState(Player player, Integer count) {
        killCounts.put(player.getName(), count);
        killTimes.put(player.getName(), System.currentTimeMillis());
    }

    private void sendSubTitlePacket(Player player, String message) {
        PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE,
                ChatSerializer.a("{\"text\":\"§e \"}"), 10, 20, 10);
        PacketPlayOutTitle packetSubTitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE,
                ChatSerializer.a("{\"text\":\"" + message + "\"}"), 10, 20, 10);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetTitle);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetSubTitle);
    }

    private void actionOnKillingSeries(Player killer, World world) {
        world.playSound(killer.getLocation(), Sound.FIREWORK_BLAST, 10, 1);
        String subTitle = killStrings.get(killCounts.get(killer.getName()));
        sendSubTitlePacket(killer, subTitle);
    }
}

