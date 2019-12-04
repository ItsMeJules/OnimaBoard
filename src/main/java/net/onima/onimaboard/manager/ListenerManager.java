package net.onima.onimaboard.manager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import net.onima.onimaboard.OnimaBoard;
import net.onima.onimaboard.listeners.ArmorClassListener;
import net.onima.onimaboard.listeners.FactionListeners;
import net.onima.onimaboard.listeners.GameListener;
import net.onima.onimaboard.listeners.TagDisguiseListener;

/**
 * This class handles all the bukkit's listeners.
 */
public class ListenerManager {
	
	private OnimaBoard plugin;
	private PluginManager pm;
	
	public ListenerManager(OnimaBoard plugin) {
		this.plugin = plugin;
		this.pm = Bukkit.getPluginManager();
	}
	
	public void registerListener() {
		pm.registerEvents(new FactionListeners(), plugin);
		pm.registerEvents(new ArmorClassListener(), plugin);
		pm.registerEvents(new GameListener(), plugin);
		pm.registerEvents(new TagDisguiseListener(), plugin);
	}

}
