package net.onima.onimaboard.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.onima.onimaapi.event.disguise.PlayerDisguiseEvent;
import net.onima.onimaapi.event.disguise.PlayerUndisguiseEvent;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaboard.board.Nametag;
import net.onima.onimaboard.players.BoardPlayer;

public class TagDisguiseListener implements Listener {
	
	@EventHandler
	public void onDisguise(PlayerDisguiseEvent event) {
		Player disguised = event.getPlayer();
		
		for (BoardPlayer player : BoardPlayer.getOnlineBoardPlayers()) {
			if (OnimaPerm.ONIMAAPI_DISGUISE_COMMAND_LIST.has(player.getApiPlayer().toPlayer()))
				player.getBoard().setNameTag(Nametag.DISGUISED_ADMIN, disguised);
		}
	}
	
	@EventHandler
	public void onUndisguise(PlayerUndisguiseEvent event) {
		BoardPlayer.getPlayer(event.getPlayer()).getBoard().initNametag();
	}
	

}
