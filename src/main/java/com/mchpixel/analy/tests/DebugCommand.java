package com.mchpixel.analy.tests;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DebugCommand implements CommandExecutor {

    private boolean debugMode = false;


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // flip the toggle
        debugMode = !debugMode;

        // tell whoever ran the command what happened
        sender.sendMessage("[Analy] Debug mode: " + (debugMode ? "ON" : "OFF"));

        return true;
    }

    // Call this from your collectors to log a metric — only prints if debug is on
    public void log(String message) {
        if (debugMode) {
            System.out.println("[Analy Debug] " + message);
        }
    }


}
