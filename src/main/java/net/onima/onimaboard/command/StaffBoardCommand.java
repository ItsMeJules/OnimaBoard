package net.onima.onimaboard.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.onima.onimaapi.OnimaAPI;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaboard.players.BoardPlayer;

public class StaffBoardCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!OnimaPerm.ONIMABOARD_STAFFBOARD.has(sender)) {
			sender.sendMessage(OnimaAPI.UNKNOWN_COMMAND);
			return false;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cSeulement les joueurs peuvent utiliser cette commande !");
			return false;
		}
		
		BoardPlayer boardPlayer = BoardPlayer.getPlayer((Player) sender);
		
		boardPlayer.setStaffBoard(!boardPlayer.hasStaffBoard());
		
		String msg = boardPlayer.hasStaffBoard() ? "§aactivé" : "§cdésactivé";
		
		sender.sendMessage("§eVous §7activé " + msg + " §7le staff board.");
		return false;
	}

}
