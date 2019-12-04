package net.onima.onimaboard.manager;

import net.onima.onimaboard.OnimaBoard;
import net.onima.onimaboard.command.SidebarCommand;
import net.onima.onimaboard.command.StaffBoardCommand;
import net.onima.onimaboard.command.TabCommand;

public class CommandManager {
	
	private OnimaBoard plugin;
	
	public CommandManager(OnimaBoard plugin) {
		this.plugin = plugin;
	}
	
	
	public void registerCommands() {
		plugin.getCommand("tab").setExecutor(new TabCommand());
		plugin.getCommand("sidebar").setExecutor(new SidebarCommand());
		plugin.getCommand("staffboard").setExecutor(new StaffBoardCommand());
	}
	
}
