package net.onima.onimaboard.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.onima.onimaapi.event.CooldownEndEvent;
import net.onima.onimaapi.event.CooldownStopEvent;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaboard.board.Nametag;
import net.onima.onimaboard.players.BoardPlayer;
import net.onima.onimafaction.events.armorclass.archer.ArcherTagPlayerEvent;
import net.onima.onimafaction.players.FPlayer;

public class ArmorClassListener implements Listener {
	
	@EventHandler
	public void onArcherMark(ArcherTagPlayerEvent event) {
		FPlayer fPlayer = event.getFPlayerTagged();
		Player player = fPlayer.getApiPlayer().toPlayer();
		boolean disguised = fPlayer.getApiPlayer().getDisguiseManager().isDisguised();

		if (event.getMark() > 1) {
			Methods.getOnlinePlayers(null).parallelStream().forEach(updater -> {
				if (fPlayer.hasFaction() && fPlayer.getFaction().getMembers().containsKey(updater.getUniqueId()))
					return;
				
				if (disguised && !OnimaPerm.ONIMAAPI_DISGUISE_COMMAND_LIST.has(updater))
					BoardPlayer.getPlayer(updater).getBoard().setNameTag(Nametag.ARCHER_TAG_2, player);
				else if (!disguised)
					BoardPlayer.getPlayer(updater).getBoard().setNameTag(Nametag.ARCHER_TAG_2, player);
			});
		} else {
			Methods.getOnlinePlayers(null).parallelStream().forEach(updater -> {
				if (fPlayer.hasFaction() && fPlayer.getFaction().getMembers().containsKey(updater.getUniqueId()))
					return;
				
				if (disguised && !OnimaPerm.ONIMAAPI_DISGUISE_COMMAND_LIST.has(updater))
					BoardPlayer.getPlayer(updater).getBoard().setNameTag(Nametag.ARCHER_TAG_1, player);
				else if (!disguised)
					BoardPlayer.getPlayer(updater).getBoard().setNameTag(Nametag.ARCHER_TAG_1, player);
			});
		}
	}
	
	 @EventHandler
	 public void onArcherMarkEnd(CooldownEndEvent event) {
		 OfflineAPIPlayer offline = event.getOfflineAPIPlayer();
		 
		 if (offline.isOnline()) {
			 FPlayer fPlayer = FPlayer.getPlayer((Player) offline);
			 Player player = fPlayer.getApiPlayer().toPlayer();
			 boolean disguised = fPlayer.getApiPlayer().getDisguiseManager().isDisguised();
			 
			 Methods.getOnlinePlayers(null).parallelStream().forEach(updater -> {
				if (fPlayer.hasFaction() && fPlayer.getFaction().getMembers().containsKey(updater.getUniqueId()))
					return;
				
				if (disguised && !OnimaPerm.ONIMAAPI_DISGUISE_COMMAND_LIST.has(updater))
					BoardPlayer.getPlayer(updater).getBoard().setNameTag(Nametag.ENEMY, player);
				else if (!disguised)
					BoardPlayer.getPlayer(updater).getBoard().setNameTag(Nametag.ENEMY, player);
			});
			 
		 }
	 }
	 
	 @EventHandler
	 public void onArcherMarkStop(CooldownStopEvent event) {
		 onArcherMarkEnd(event);
	 }

}
