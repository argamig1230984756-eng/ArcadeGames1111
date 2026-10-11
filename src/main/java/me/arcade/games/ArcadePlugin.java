package me.arcade.games;

import me.arcade.games.arena.ArenaManager;
import me.arcade.games.command.ArcadeCommand;
import me.arcade.games.game.GameListener;
import me.arcade.games.gui.ArenaMenu;
import me.arcade.games.util.Settings;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ArcadePlugin extends JavaPlugin {
    private ArenaManager manager;
    private Settings settings;
    private ArcadeCommand command;
    private ArenaMenu menu;

    @Override
    public void onEnable() {
        settings = new Settings(this); // creates config.yml, messages.yml and games/*.yml on the first start
        manager = new ArenaManager(this);
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        menu = new ArenaMenu(this);
        getServer().getPluginManager().registerEvents(menu, this);
        menu.restartRefresh();
        ArcadeCommand cmd = new ArcadeCommand(this);
        command = cmd;
        PluginCommand pc = getCommand("arcade");
        if (pc != null) {
            pc.setExecutor(cmd);
            pc.setTabCompleter(cmd);
        }
        // load next tick so every world (Multiverse etc.) is available
        getServer().getScheduler().runTask(this, manager::load);
    }

    @Override
    public void onDisable() {
        if (manager != null) manager.shutdown();
    }

    public ArenaManager manager() {
        return manager;
    }

    public Settings settings() {
        return settings;
    }

    public ArcadeCommand command() {
        return command;
    }

    public ArenaMenu menu() {
        return menu;
    }

    /** Re-reads config.yml, messages.yml and games/*.yml (/arcade reload). */
    public void reloadAll() {
        settings.reload();
        if (menu != null) menu.restartRefresh();
    }

    /**
     * Sends a player to the spawn (config.yml: spawn-world / spawn-command). Happens immediately, so it also
     * works while a player is quitting or the plugin is shutting down.
     */
    public void sendToSpawn(Player p) {
        String cmd = getConfig().getString("spawn-command", "");
        if (cmd != null && !cmd.trim().isEmpty()) {
            String c = cmd.trim();
            if (c.startsWith("/")) c = c.substring(1);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c.replace("%player%", p.getName()));
            return;
        }
        String worldName = getConfig().getString("spawn-world", "");
        World target = null;
        if (worldName != null && !worldName.trim().isEmpty()) {
            target = Bukkit.getWorld(worldName.trim());
            if (target == null) getLogger().warning("spawn-world '" + worldName + "' not found - using main world.");
        }
        if (target == null) {
            List<World> worlds = Bukkit.getWorlds();
            if (!worlds.isEmpty()) target = worlds.get(0);
        }
        if (target != null) p.teleport(target.getSpawnLocation());
    }
}
