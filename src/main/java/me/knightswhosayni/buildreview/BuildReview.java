package me.knightswhosayni.buildreview;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public final class BuildReview extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        //config
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        
        //commands
        Objects.requireNonNull(getCommand("review")).setExecutor(new BuildReviewCommand(this));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
