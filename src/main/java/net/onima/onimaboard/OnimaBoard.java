package net.onima.onimaboard;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import net.onima.onimaapi.OnimaAPI;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaboard.manager.CommandManager;
import net.onima.onimaboard.manager.ListenerManager;
import net.onima.onimaboard.task.ScoreboardEntryTask;
import net.onima.onimaboard.task.TabEntryTask;
import net.onima.onimaboard.utils.InvisibilityHandler;

public class OnimaBoard extends JavaPlugin {
	
	private static OnimaBoard instance;
	
	@Override
	public void onEnable() {
		if (!Bukkit.getOnlineMode()) {
			getPluginLoader().disablePlugin(this);
			return;
		}
		
		long started = System.currentTimeMillis();
		OnimaAPI.sendConsoleMessage("====================§6[§3ACTIVATION§6]§r====================", ConfigurationService.ONIMABOARD_PREFIX);
		instance = this;
		registerManager();
		OnimaAPI.sendConsoleMessage("====================§6[§3ACTIVE EN (" + (System.currentTimeMillis() - started) + "ms)§6]§r====================", ConfigurationService.ONIMABOARD_PREFIX);
	}
	
	public void registerManager() {
		ScoreboardEntryTask.init(this);
		TabEntryTask.init(this);
		
		InvisibilityHandler.hook(this);
		
		new ListenerManager(this).registerListener();
		new CommandManager(this).registerCommands();
	}

	public void onDisable() {
		OnimaAPI.sendConsoleMessage("====================§6[§cDESACTIVATION§6]§r====================", ConfigurationService.ONIMABOARD_PREFIX);
	}
	
	public static OnimaBoard getInstance() {
		return instance;
	}

}
