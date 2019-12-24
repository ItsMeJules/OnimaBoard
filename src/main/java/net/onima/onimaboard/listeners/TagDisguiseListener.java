package net.onima.onimaboard.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.onima.onimaapi.event.disguise.PlayerDisguiseEvent;
import net.onima.onimaapi.event.disguise.PlayerUndisguiseEvent;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaboard.players.BoardPlayer;

public class TagDisguiseListener implements Listener {
	
	@EventHandler
	public void onDisguise(PlayerDisguiseEvent event) {
		BoardPlayer.getPlayer(event.getPlayer()).getBoard().initNametag(Methods.getOnlinePlayers(null));
	}
	
	@EventHandler
	public void onUndisguise(PlayerUndisguiseEvent event) {
		BoardPlayer.getPlayer(event.getPlayer()).getBoard().initNametag(Methods.getOnlinePlayers(null));
	}
	

}
