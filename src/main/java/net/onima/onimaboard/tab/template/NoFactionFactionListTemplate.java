package net.onima.onimaboard.tab.template;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimaboard.tab.Tab;
import net.onima.onimaboard.tab.utils.TabTemplate;
import net.onima.onimaboard.tab.utils.TabType;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimagames.game.Game;
import net.onima.onimagames.game.GameType;

public class NoFactionFactionListTemplate implements TabTemplate {

	@Override
	public void fill(Tab tab) {
		Player player = tab.getPlayer();
		APIPlayer apiPlayer = APIPlayer.getPlayer(player);
		Location location = player.getLocation();
		Region region = FPlayer.getPlayer(player).getRegionOn();
		Map<PlayerFaction, Integer> factions = PlayerFaction.getByMostPlayersOnline();
		
		tab.set(0, 0, "§d§lLocation :");
		tab.set(0, 1, "§7" + location.getBlockX() + ' ' + location.getBlockY() + ' ' + location.getBlockZ() + " §f(" + apiPlayer.getFacingDirection() + ')');
		tab.set(0, 2, region.getDisplayName(player));
		tab.set(0, 3, region.isDeathbannable() ? "§cDeathban §cx" + region.getDeathbanMultiplier() : "§bNon-Deathban");
		
		tab.set(0, 5, "§f[§dTIP§f] §7§oCréez votre");
		tab.set(0, 6, "§7§ofaction avec");
		tab.set(0, 7, "§7§o/f create <nom>");
		
		tab.set(0, 9, "§f[§d§lJoueur§f]");
		tab.set(0, 10, "§7Kills : " + apiPlayer.getKills());
		tab.set(0, 11, "§7Morts : " + apiPlayer.getDeaths());
		tab.set(0, 12, "§7Ping : " + apiPlayer.getPing());
		
		Game startedGame = Game.getStartedGame();
		
		if (startedGame != null) {
			GameType type = startedGame.getGameType();
			
			tab.set(0, 14, "§dEvent en cours :");
			tab.set(0, 15, type.getColor() + type.getName() + ' ' + startedGame.getName());
			
			if (startedGame.getLocation() != null) {
				Location gameLoc = startedGame.getLocation();
				tab.set(0, 16, "§7Location : ");
				tab.set(0, 17, "§7" + gameLoc.getBlockX() + ' ' + gameLoc.getBlockY() + ' ' + gameLoc.getBlockZ());
				tab.set(0, 18, "§7Commencé depuis");
				tab.set(0, 19, LongTime.setHMSFormat(System.currentTimeMillis() - startedGame.getStartedTime()));
			} else {
				tab.set(0, 16, "§7Commencé depuis");
				tab.set(0, 17, LongTime.setHMSFormat(System.currentTimeMillis() - startedGame.getStartedTime()));
			}
		} else {
			Game nextGame = Game.getNextGame();
			
			tab.set(0, 14, "§dProchain event :");
			
			if (nextGame == null) {
				tab.set(0, 15, "§7Aucun event");
				tab.set(0, 16, "§7programmé...");
			} else {
				tab.set(0, 15, "§7" + nextGame.getGameType().getName() + ' ' + nextGame.getName());
				
				if (nextGame.getLocation() != null) {
					Location gameLoc = nextGame.getLocation();
					tab.set(0, 16, "§7Location : ");
					tab.set(0, 17, "§7" + gameLoc.getBlockX() + ' ' + gameLoc.getBlockY() + ' ' + gameLoc.getBlockZ());
					tab.set(0, 18, "§7commence dans");
					tab.set(0, 19, LongTime.setDHMSFormat(nextGame.getStartTimeLeft()));
				} else {
					tab.set(0, 16, "§7commence dans");
					tab.set(0, 17, LongTime.setDHMSFormat(nextGame.getStartTimeLeft()));
				}
			}
		}
		
		tab.set(1, 0, "§7Vous jouez sur");
		tab.set(1, 1, "§d§oplay.onima.fr");
		
		tab.set(1, 3, "§7Lien du site :");
		tab.set(1, 4, "§d§ostore.onima.fr");
		
		tab.set(1, 6, "§7TeamSpeak : ");
		tab.set(1, 7, "§d§oplay.onima.fr");
		
		tab.set(1, 9, "§7Discord : ");
		tab.set(1, 10, "§d§odiscord.io/onima");
	
		if (factions.isEmpty())
			tab.set(2, 0, "§aAucune faction");
		else {
			int i = 0;
			for (Entry<PlayerFaction, Integer> entry : factions.entrySet()) {
				if (i >= 20) break;
				
				PlayerFaction faction = entry.getKey();
				
				tab.set(2, i, "§d" + (i+1) + ". " + faction.getDisplayName(player) + " §7(" + entry.getValue() + ')');
				
				i++;
			}
		}
	}

	@Override
	public TabType getTabType() {
		return TabType.NO_FACTION_FACTION_LIST;
	}

}
