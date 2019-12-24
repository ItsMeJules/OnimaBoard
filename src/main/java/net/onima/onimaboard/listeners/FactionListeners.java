package net.onima.onimaboard.listeners;

import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.onima.onimaapi.utils.Methods;
import net.onima.onimaboard.players.BoardPlayer;
import net.onima.onimaboard.tab.utils.TabType;
import net.onima.onimafaction.events.FactionCreateEvent;
import net.onima.onimafaction.events.FactionDisbandEvent;
import net.onima.onimafaction.events.FactionFocusEvent;
import net.onima.onimafaction.events.FactionPlayerJoinedEvent;
import net.onima.onimafaction.events.FactionPlayerLeaveEvent.LeaveReason;
import net.onima.onimafaction.events.FactionPlayerLeftEvent;
import net.onima.onimafaction.events.FactionUnfocusEvent;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimafaction.players.OfflineFPlayer;

public class FactionListeners implements Listener {
	
	@EventHandler
	public void onFactionCreate(FactionCreateEvent event) {
		BoardPlayer.getPlayer(event.getPlayer()).setTab(TabType.FACTION_SERV_INFO);
	}
	
	@EventHandler
	public void onFactionDisband(FactionDisbandEvent event) {
		for (FPlayer fPlayer : event.getFaction().getOnlineMembers(null)) {
			BoardPlayer boardPlayer = BoardPlayer.getPlayer(fPlayer.getApiPlayer().getUUID());
			
			boardPlayer.getBoard().initNametag(Methods.getOnlinePlayers(null));
			boardPlayer.setTab(TabType.NO_FACTION_SERV_INFO);
		}
	}
	
	@EventHandler
	public void onFactionLeave(FactionPlayerLeftEvent event) {
		BoardPlayer boardPlayer = BoardPlayer.getPlayer(event.getOfflineFPlayer().getOfflineApiPlayer().getUUID());
			
		boardPlayer.getBoard().initNametag(Methods.getOnlinePlayers(null));
		boardPlayer.setTab(TabType.NO_FACTION_SERV_INFO);
		
		if (boardPlayer.getOfflineApiPlayer().isOnline() && event.getLeaveReason() != LeaveReason.DISBAND) {
			for (FPlayer fPlayer : ((PlayerFaction) event.getFaction()).getOnlineMembers(null)) {
				BoardPlayer boardFPlayer = BoardPlayer.getPlayer(fPlayer.getApiPlayer().getUUID());
				
				for (int y = 3; y < 20; y++)
					boardFPlayer.getTab().set(1, y, "");
			}
		}
	}
	
	@EventHandler
	public void onFactionJoin(FactionPlayerJoinedEvent event) {
		OfflineFPlayer offlineFPlayer = event.getOfflineFPlayer();
		
		if (offlineFPlayer.getOfflineApiPlayer().isOnline()) {
			BoardPlayer boardPlayer = BoardPlayer.getPlayer(offlineFPlayer.getOfflineApiPlayer().getUUID());
			
			boardPlayer.getBoard().initNametag(Methods.getOnlinePlayers(null));
			boardPlayer.setTab(TabType.FACTION_SERV_INFO);
		}
	}
	
	 @EventHandler
	 public void onFactionFocus(FactionFocusEvent event) {
		 OfflinePlayer offline = event.getFocused();
		 
		 if (offline.isOnline() && FPlayer.getPlayer(offline.getUniqueId()).getArcherTag() <= 0) {
			 BoardPlayer.getPlayer(offline.getUniqueId()).getBoard().initNametag(
					 ((PlayerFaction) event.getFaction()).getOnlineMembers(null).stream()
					 .map(fPlayer -> fPlayer.getApiPlayer().toPlayer())
					 .collect(Collectors.toList()));
		 }
	 }
	 
	 @EventHandler
	 public void onFactionUnfocus(FactionUnfocusEvent event) {
		 OfflinePlayer offline = event.getFocused();
		 
		 if (offline.isOnline() && FPlayer.getPlayer(offline.getUniqueId()).getArcherTag() <= 0) {
			 BoardPlayer.getPlayer(offline.getUniqueId()).getBoard().initNametag(
					 ((PlayerFaction) event.getFaction()).getOnlineMembers(null).stream()
					 .map(fPlayer -> fPlayer.getApiPlayer().toPlayer())
					 .collect(Collectors.toList()));
		 }
	 }
	 
}
