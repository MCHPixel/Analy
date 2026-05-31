package com.mchpixel.analy.tests;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DebugCommand implements CommandExecutor {

    private boolean debugMode = false;
    private final java.util.logging.Logger logger;

    public DebugCommand(java.util.logging.Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        debugMode = !debugMode;
        sender.sendMessage("[Analy] Debug mode: " + (debugMode ? "ON" : "OFF"));
        return true;
    }

    public void log(String message) {
        if (debugMode) {
            logger.info("[Analy Debug] " + message);
        }
    }
}