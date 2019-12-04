package net.onima.onimaboard.listeners;

import java.util.Collection;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
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
			boolean disguised = fPlayer.getApiPlayer().getDisguiseManager().isDisguised();
			
			for (String ally : faction.getAllies())
				toUpdate.addAll(((PlayerFaction) Faction.getFaction(ally)).getOnlineMembers(null).stream().map(fPlater -> fPlater.getApiPlayer().toPlayer()).collect(Collectors.toList()));
			
			toUpdate.parallelStream().forEach(updater -> {
				BoardPlayer lol = BoardPlayer.getPlayer(updater.getUniqueId());
				
				lol.getTab().empty();
				
				if (disguised && !OnimaPerm.ONIMAAPI_DISGUISE_COMMAND_LIST.has(updater))
					lol.getBoard().setNameTag(Nametag.ENEMY, player);
				else if (!disguised)
					lol.getBoard().setNameTag(Nametag.ENEMY, player);
			});
			
			boolean hasPerm = OnimaPerm.ONIMAAPI_DISGUISE_COMMAND_LIST.has(player);
			toUpdate.removeIf(updater -> APIPlayer.getPlayer(updater).getDisguiseManager().isDisguised() && hasPerm);
			
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
			
			boolean hasPerm = OnimaPerm.ONIMAAPI_DISGUISE_COMMAND_LIST.has(player);
			boolean disguised = fPlayer.getApiPlayer().getDisguiseManager().isDisguised();
			
			faction.getOnlineMembers(null).stream().forEach(fPlater -> {
				boolean playerDisguised = fPlater.getApiPlayer().getDisguiseManager().isDisguised();
				Player factionPlayer = fPlater.getApiPlayer().toPlayer();
				
				if (disguised && !OnimaPerm.ONIMAAPI_DISGUISE_COMMAND_LIST.has(factionPlayer))
					BoardPlayer.getPlayer(factionPlayer.getUniqueId()).getBoard().setNameTag(Nametag.FACTION, player);
				else if (!disguised)
					BoardPlayer.getPlayer(factionPlayer.getUniqueId()).getBoard().setNameTag(Nametag.FACTION, player);
				
				if (playerDisguised && hasPerm)
					board.setNameTag(Nametag.FACTION, factionPlayer);
				else if (!disguised)
					board.setNameTag(Nametag.FACTION, factionPlayer);
			});
			
			for (String ally : faction.getAllies())
				((PlayerFaction) Faction.getFaction(ally)).getOnlineMembers(null).stream().forEach(fPlater -> {
					boolean playerDisguised = fPlater.getApiPlayer().getDisguiseManager().isDisguised();
					Player factionPlayer = fPlater.getApiPlayer().toPlayer();
					
					if (disguised && !OnimaPerm.ONIMAAPI_DISGUISE_COMMAND_LIST.has(factionPlayer))
						BoardPlayer.getPlayer(factionPlayer.getUniqueId()).getBoard().setNameTag(Nametag.ALLY, player);
					else if (!disguised)
						BoardPlayer.getPlayer(factionPlayer.getUniqueId()).getBoard().setNameTag(Nametag.ALLY, player);
					
					if (playerDisguised && hasPerm)
						board.setNameTag(Nametag.ALLY, factionPlayer);
					else if (!disguised)
						board.setNameTag(Nametag.ALLY, factionPlayer);
				});
			
			boardPlayer.setTab(TabType.FACTION_SERV_INFO);
		}
	}
	
	 @EventHandler
	 public void onFactionFocus(FactionFocusEvent event) {
		 OfflinePlayer offline = event.getFocused();
		 
		 if (offline.isOnline()) {
			 Player player = (Player) offline;
			 boolean disguised = APIPlayer.getPlayer(player).getDisguiseManager().isDisguised();
			 
			 ((PlayerFaction) event.getFaction()).getOnlineMembers(null).parallelStream().forEach(updater -> {
				 Player factionPlayer = updater.getApiPlayer().toPlayer();
				 
				 if (disguised && !OnimaPerm.ONIMAAPI_DISGUISE_COMMAND_LIST.has(factionPlayer))
					BoardPlayer.getPlayer(factionPlayer.getUniqueId()).getBoard().setNameTag(Nametag.FOCUS, player);
				 else if (!disguised)
					 BoardPlayer.getPlayer(factionPlayer.getUniqueId()).getBoard().setNameTag(Nametag.FOCUS, player);
			 });
		 }			
	 
	 }
	 
	 @EventHandler
	 public void onFactionUnfocus(FactionUnfocusEvent event) {
		 OfflinePlayer offline = event.getFocused();
		 
		 if (offline.isOnline())
			 BoardPlayer.getPlayer((Player) offline).getBoard().initNametag();
	 
	 }
	
}
