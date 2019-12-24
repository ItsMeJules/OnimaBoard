package net.onima.onimaboard.listeners;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.event.CooldownEndEvent;
import net.onima.onimaapi.event.CooldownStopEvent;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaboard.players.BoardPlayer;
import net.onima.onimafaction.cooldowns.ArcherMarkCooldown;
import net.onima.onimafaction.events.armorclass.archer.ArcherTagPlayerEvent;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;

public class ArmorClassListener implements Listener {
	
	@EventHandler
	public void onArcherMark(ArcherTagPlayerEvent event) {
		if (event.getMark() > 0) {
			FPlayer fPlayer = event.getFPlayerTagged();
			Collection<Player> players = Methods.getOnlinePlayers(null);
			
			if (fPlayer.hasFaction()) {
				Map<UUID, Role> members = fPlayer.getFaction().getMembers();
				
				players.parallelStream().filter(updater -> !members.containsKey(updater.getUniqueId())).collect(Collectors.toList());
			}
			
			BoardPlayer.getPlayer(fPlayer.getApiPlayer().getUUID()).getBoard().initNametag(players);
		}
	}
	
	 @EventHandler
	 public void onArcherMarkEnd(CooldownEndEvent event) {
		 if (event.getCooldown().getId() != Cooldown.getCooldown(ArcherMarkCooldown.class).getId())
			 return;
		 
		 OfflineAPIPlayer offline = event.getOfflineAPIPlayer();
		 
		 if (offline.isOnline()) {
			 FPlayer fPlayer = FPlayer.getPlayer(offline.getUUID());
			 Collection<Player> players = Methods.getOnlinePlayers(null);
				
			 if (fPlayer.hasFaction()) {
				 Map<UUID, Role> members = fPlayer.getFaction().getMembers();
						
				 players.parallelStream().filter(updater -> !members.containsKey(updater.getUniqueId())).collect(Collectors.toList());
			 }
			 
			 BoardPlayer.getPlayer(fPlayer.getApiPlayer().getUUID()).getBoard().initNametag(players);
		 }
	 }
	 
	 @EventHandler
	 public void onArcherMarkStop(CooldownStopEvent event) {
		 if (event.getCooldown().getId() != Cooldown.getCooldown(ArcherMarkCooldown.class).getId())
			 return;
		 
		 onArcherMarkEnd(event);
	 }

}
