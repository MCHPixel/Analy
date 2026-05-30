package com.mchpixel.analy.collectors;

import com.mchpixel.analy.tests.DebugCommand;
import com.mchpixel.analy.model.Metric;
import com.mchpixel.analy.model.MetricBuffer;
import com.mchpixel.analy.model.MetricType;
import com.mchpixel.analy.model.Tags;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventCollector implements Listener {

    private final MetricBuffer buffer;
    private final DebugCommand debugCommand;

    // Passing in the Buffer from AnalyPlugin
    public PlayerEventCollector(MetricBuffer buffer, DebugCommand debugCommand) {
        this.buffer = buffer;
        this.debugCommand = debugCommand;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Metric metric = new Metric(
                "player.action.join",
                1.0,
                MetricType.COUNTER,
                System.currentTimeMillis(),
                Tags.of("player_uuid", player.getUniqueId().toString())
        );

        buffer.add(metric);
        debugCommand.log("metric added: " + metric.toString());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Metric metric = new Metric(
                "player.action.quit",
                1.0,
                MetricType.COUNTER,
                System.currentTimeMillis(),
                Tags.of("player_uuid", player.getUniqueId().toString())
        );

        buffer.add(metric);
        debugCommand.log("metric added: " + metric.toString());
    }
}
