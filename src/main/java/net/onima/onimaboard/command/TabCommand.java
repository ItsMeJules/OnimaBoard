package net.onima.onimaboard.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import net.onima.onimaapi.OnimaAPI;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaboard.players.BoardPlayer;
import net.onima.onimaboard.tab.utils.TabType;
import net.onima.onimafaction.players.FPlayer;

public class TabCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!OnimaPerm.ONIMABOARD_TAB_COMMAND.has(sender)) {
			sender.sendMessage(OnimaAPI.UNKNOWN_COMMAND);
			return false;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage("§cSeulement les joueurs peuvent utiliser cette commande !");
			return false;
		}
		
		Player player = (Player) sender;
		
		if (args.length < 1) {
			player.sendMessage("§7Utilisation : /tab <type>");
			return false;
		} else {
			TabType type = TabType.fromString(args[0]);
			
			if (type == null) {
				player.sendMessage("§cLe type de tab " + args[0] + " n'existe pas !");
				return true;
			}
			
			BoardPlayer boardPlayer = BoardPlayer.getPlayer(player);
			
			if (!boardPlayer.getFPlayer().hasFaction() && type.name().startsWith("FACTION_")) {
				player.sendMessage("§cVous avez besoin d'une faction pour afficher ce type de tab !");
				return true;
			}
			
			boardPlayer.setTab(type);
			player.sendMessage("§d§oVous §7avez changé votre type de tab par §d§o" + type.name());
		}
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> completions = new ArrayList<>();
		
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (player.hasPermission(OnimaPerm.ONIMABOARD_TAB_COMMAND.getPermission()) && args.length > 0) {
				boolean hasFaction = FPlayer.getPlayer(player).hasFaction();
				
				for (TabType type : TabType.values()) {
					if (type.name().startsWith("FACTION_") && !hasFaction) continue;
					
					if (StringUtil.startsWithIgnoreCase(type.name(), args[0]))
						completions.add(type.name());
				}
			}
		}
		
		
		return completions;
	}
	
}
