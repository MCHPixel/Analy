package com.mchpixel.analy;

import com.mchpixel.analy.core.ConfigManager;
import com.mchpixel.analy.tests.DebugCommand;
import com.mchpixel.analy.collectors.PlayerEventCollector;
import com.mchpixel.analy.model.MetricBuffer;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Analy extends JavaPlugin {

    // We store these as fields so onDisable can access them
    private MetricBuffer buffer;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        // Create config manager and validate before anything else
        configManager = new ConfigManager(getLogger(), this);

        if (!configManager.validate()) {
            getLogger().severe("Invalid config — shutting down!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Create the buffer with a max size of 500
        //Important to be on the top else it will not work
        buffer = new MetricBuffer(configManager.get_max_buffer_size());

        // ________________________________
        // |                              |
        // |   <Command Initialization>   |
        // |                              |
        // --------------------------------


        // Create debug command
        // Give it the Logger to use!
        DebugCommand debugCommand = new DebugCommand(getLogger());


        // Get the command from plugin.yml and validate it
        PluginCommand cmd_debuganaly = getCommand("debuganaly");

        // Check if said command is defined else kill the Plugin
        if (cmd_debuganaly == null) {
            getLogger().severe("Command 'debuganaly' is not defined in plugin.yml!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Tell Bukkit which classes handle this Command
        cmd_debuganaly.setExecutor(debugCommand);

        // _________________________________
        // |                               |
        // |   </Command Initialization>   |
        // |                               |
        // ---------------------------------




        // ______________________________
        // |                            |
        // |        <Collectors>        |
        // |                            |
        // ------------------------------


        // Register the player event collector as a listener
        PlayerEventCollector playerCollector = new PlayerEventCollector(buffer, debugCommand);
        getServer().getPluginManager().registerEvents(playerCollector, this);

        // Temp flush scheduler — just prints to console for now
        // We'll replace this with the real HTTP client later
        long flushTicks = configManager.get_flush_interval_seconds() * 20L;

        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                if (buffer.isEmpty()) return; // nothing to do

                var batch = buffer.flush();
                getLogger().info("Flushing " + batch.size() + " metrics:");
                batch.forEach(m -> getLogger().info("  " + m.toString()));
            }
        }.runTaskTimerAsynchronously(this, flushTicks, flushTicks); // 200 ticks = 10 seconds

        getLogger().info("Analy started! :3");

        // ______________________________
        // |                            |
        // |       </Collectors>        |
        // |                            |
        // ------------------------------

    }

    @Override
    public void onDisable() {
        // Flush anything still sitting in the buffer before shutdown
        if (buffer != null && !buffer.isEmpty()) {
            var remaining = buffer.flush();
            getLogger().info("Flushing " + remaining.size() + " remaining metrics on shutdown...");
            // for now just log them, later this will send them to the backend
            remaining.forEach(m -> getLogger().info("  " + m.toString()));
        }

        getLogger().info("Analy stopped!");
    }
}