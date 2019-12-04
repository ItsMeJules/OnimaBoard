package net.onima.onimaboard.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.onima.onimaapi.OnimaAPI;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaboard.players.BoardPlayer;

public class SidebarCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cSeulement les joueurs peuvent utiliser cette commande !");
			return false;
		}
		
		if (OnimaPerm.ONIMABOARD_SIDEBAR_COMMAND.has(sender)) {
			sender.sendMessage(OnimaAPI.UNKNOWN_COMMAND);
			return false;
		}
		
		Player player = (Player) sender;
		BoardPlayer boardPlayer = BoardPlayer.getPlayer(player);
		
		if (boardPlayer.hasBoardToggled()) {
			boardPlayer.toggleBoard(false);
			player.sendMessage("§d§oVous §7avez §cdésactivé §7votre scoreboard !");
		} else {
			boardPlayer.toggleBoard(true);
			player.sendMessage("§d§oVous §7avez §aactivé §7votre scoreboard !");
		}
		
		return false;
	}

}
