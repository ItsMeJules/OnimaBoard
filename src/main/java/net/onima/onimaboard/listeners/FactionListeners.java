package net.onima.onimaboard.listeners;

import java.util.Collection;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.onima.onimaapi.utils.Methods;
import net.onima.onimaboard.board.Board;
import net.onima.onimaboard.board.Nametag;
import net.onima.onimaboard.players.BoardPlayer;
import net.onima.onimaboard.tab.utils.TabType;
import net.onima.onimafaction.events.FactionCreateEvent;
import net.onima.onimafaction.events.FactionDisbandEvent;
import net.onima.onimafaction.events.FactionFocusEvent;
import net.onima.onimafaction.events.FactionPlayerJoinEvent;
import net.onima.onimafaction.events.FactionPlayerLeaveEvent;
import net.onima.onimafaction.events.FactionPlayerLeaveEvent.LeaveReason;
import net.onima.onimafaction.events.FactionUnfocusEvent;
import net.onima.onimafaction.faction.Faction;
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
			
			boardPlayer.getBoard().setNameTag(Nametag.ENEMY, Methods.getOnlinePlayers(null));
			boardPlayer.setTab(TabType.NO_FACTION_SERV_INFO);
		}
	}
	
	@EventHandler
	public void onFactionLeave(FactionPlayerLeaveEvent event) {
		OfflineFPlayer offlineFPlayer = event.getOfflineFPlayer();

		if (offlineFPlayer.getOfflineApiPlayer().isOnline() && event.getLeaveReason() != LeaveReason.DISBAND) {
			FPlayer fPlayer = (FPlayer) offlineFPlayer;
			Player player = fPlayer.getApiPlayer().toPlayer();
			BoardPlayer boardPlayer = BoardPlayer.getPlayer(player);
			PlayerFaction faction = (PlayerFaction) event.getFaction();
			Collection<Player> toUpdate = faction.getOnlineMembers(null).stream().map(fPlater -> fPlater.getApiPlayer().toPlayer()).collect(Collectors.toList());
			
			for (String ally : faction.getAllies())
				toUpdate.addAll(((PlayerFaction) Faction.getFaction(ally)).getOnlineMembers(null).stream().map(fPlater -> fPlater.getApiPlayer().toPlayer()).collect(Collectors.toList()));
			
			toUpdate.parallelStream().forEach(updater -> {
				BoardPlayer lol = BoardPlayer.getPlayer(updater.getUniqueId());
				
				lol.getTab().empty();
				lol.getBoard().setNameTag(Nametag.ENEMY, player);
			});
			boardPlayer.getBoard().setNameTag(Nametag.ENEMY, toUpdate);
			boardPlayer.setTab(TabType.NO_FACTION_SERV_INFO);
		}
	}
	
	@EventHandler
	public void onFactionJoin(FactionPlayerJoinEvent event) {
		OfflineFPlayer offlineFPlayer = event.getOfflineFPlayer();
		
		if (offlineFPlayer.getOfflineApiPlayer().isOnline()) {
			FPlayer fPlayer = (FPlayer) offlineFPlayer;
			Player player = fPlayer.getApiPlayer().toPlayer();
			BoardPlayer boardPlayer = BoardPlayer.getPlayer(player);
			Board board = boardPlayer.getBoard();
			PlayerFaction faction = (PlayerFaction) event.getFaction();
			
			faction.getOnlineMembers(null).stream().forEach(fPlater -> {
				BoardPlayer.getPlayer(fPlater.getApiPlayer().getUUID()).getBoard().setNameTag(Nametag.FACTION, player);
				board.setNameTag(Nametag.FACTION, fPlater.getApiPlayer().toPlayer());
			});
			
			for (String ally : faction.getAllies())
				((PlayerFaction) Faction.getFaction(ally)).getOnlineMembers(null).stream().forEach(fPlater -> {
					BoardPlayer.getPlayer(fPlater.getApiPlayer().getUUID()).getBoard().setNameTag(Nametag.ALLY, player);
					board.setNameTag(Nametag.FACTION, fPlater.getApiPlayer().toPlayer());
				});
			
			boardPlayer.setTab(TabType.FACTION_SERV_INFO);
		}
	}
	
	 @EventHandler
	 public void onFactionFocus(FactionFocusEvent event) {
		 OfflinePlayer offline = event.getFocused();
		 
		 if (offline.isOnline()) {
			 Player player = (Player) offline;
			 ((PlayerFaction) event.getFaction()).getOnlineMembers(null).parallelStream().forEach(updater -> BoardPlayer.getPlayer(updater.getApiPlayer().getUUID()).getBoard().setNameTag(Nametag.FOCUS, player));
		 }			
	 
	 }
	 
	 @EventHandler
	 public void onFactionUnfocus(FactionUnfocusEvent event) {
		 OfflinePlayer offline = event.getFocused();
		 
		 if (offline.isOnline())
			 BoardPlayer.getPlayer((Player) offline).getBoard().initNametag();
	 
	 }
	
}
