package com.gh05typlayz.AddHearts.Listeners;

import com.gh05typlayz.AddHearts.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    Main v = new Main();
    @EventHandler
    void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (player.hasPlayedBefore()) {
            player.setMaxHealth(v.defaultHealth);
        }
    }
}
