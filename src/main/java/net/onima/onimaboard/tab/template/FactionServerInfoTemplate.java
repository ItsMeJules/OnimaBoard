package net.onima.onimaboard.tab.template;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.onima.onimaapi.OnimaAPI;
import net.onima.onimaapi.manager.ChatManager;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.WorldBorder;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimaboard.players.BoardPlayer;
import net.onima.onimaboard.tab.Tab;
import net.onima.onimaboard.tab.utils.TabTemplate;
import net.onima.onimaboard.tab.utils.TabType;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimafaction.timed.event.BattleRoyale;
import net.onima.onimafaction.timed.event.BattleRoyale.Phase;
import net.onima.onimagames.game.Game;
import net.onima.onimagames.game.GameType;

public class FactionServerInfoTemplate implements TabTemplate {
	
	private BattleRoyale br;
	private int flashText;
	
	{
		br = OnimaFaction.getInstance().getBattleRoyale();
		flashText = 0;
	}

	@Override
	public void fill(Tab tab) {
		Player player = tab.getPlayer();
		FPlayer fPlayer = FPlayer.getPlayer(player);
		APIPlayer apiPlayer = fPlayer.getApiPlayer();
		Location location = player.getLocation();
		Region region = fPlayer.getRegionOn();
		String border = Methods.round("0.0", WorldBorder.getBorders().get(location.getWorld().getName()));
		PlayerFaction faction = fPlayer.getFaction();
		
		if (faction == null) {
			BoardPlayer.getPlayer(apiPlayer.getUUID()).setTab(TabType.NO_FACTION_SERV_INFO);
			return;
		}
		
		Location factionHome = faction.getHome();
		List<FPlayer> members = faction.getOnlineMembers(player).stream().sorted((a, b) -> -Integer.compare(a.getRole().getValue(), b.getRole().getValue())).collect(Collectors.toCollection(() -> new ArrayList<>(17)));
		ChatManager chatManager = OnimaAPI.getInstance().getChatManager();
		
		tab.set(0, 0, "§d§lLocation :");
		tab.set(0, 1, "§7" + location.getBlockX() + ' ' + location.getBlockY() + ' ' + location.getBlockZ() + " §f(" + apiPlayer.getFacingDirection() + ')');
		tab.set(0, 2, region.getDisplayName(player));
		tab.set(0, 3, region.isDeathbannable() ? "§cDeathban §cx" + region.getDeathbanMultiplier() : "§bNon-Deathban");
		
		tab.set(0, 5, "§f[§d§lFaction§f]");
		tab.set(0, 6, "§7Home : " + (factionHome != null ? factionHome.getBlockX() + ", " + factionHome.getBlockY() + ", " + factionHome.getBlockZ() : "§7Aucun"));
		tab.set(0, 7, "§7DTR : " + faction.getDTRColour() + faction.getDTR() + faction.getDTRStatut().getSymbol());
		
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
		
		tab.set(1, 0, "     §dOnima");
		
		tab.set(1, 2, "§d" + faction.getName());
		for (int i = 0; i < members.size(); i++) {
			if ((i + 3) >= 20)
				break;
			
			FPlayer member = members.get(i);
			
			tab.set(1, i + 3, "§a" + member.getRole().getRole() + member.getApiPlayer().getName());
		}
		
		tab.set(2, 0, "§dMap kit : ");
		tab.set(2, 1, "§7Sharpness 3");
		tab.set(2, 2, "§7Protection 3");
		
		if (!br.isStarted()) {
			tab.set(2, 4, "§dBordure du monde");
			tab.set(2, 5, "§d" + ConfigurationService.ENVIRONMENT_NAME.get(location.getWorld().getEnvironment()));
			tab.set(2, 6, "§7" + border + " x " + border);
			
			tab.set(2, 8, "§dJoueurs en ligne :");
			tab.set(2, 9, "§7" + Bukkit.getOnlinePlayers().size() + '/' + Bukkit.getMaxPlayers());
			
			tab.set(2, 11, "§dSpawn :");
			tab.set(2, 12, "§70, 0");
			
			tab.set(2, 13, "§dEtat du chat :");
			tab.set(2, 14, chatManager.isSlowed() ? "§7Ralenti » §c" + chatManager.getDelay() + "s" : (chatManager.isMuted() ? "§cMuté" : "§aNormal"));

		} else {
			Phase phase = br.getRunningPhase();
			String gazUni = "\u2620";
			
			if (flashText >= 0 && flashText <= 3)
				gazUni = "§e" + gazUni;
			else if (flashText >= 4 && flashText <= 7)
				gazUni = "§6" + gazUni;
			else if (flashText >= 8 && flashText <= 11)
				gazUni = "§c" + gazUni;
			else if (flashText >= 12 && flashText <= 15)
				gazUni = "§4" + gazUni;	
			else if (flashText >= 16 && flashText <= 19) {
				gazUni = "§f" + gazUni;
				flashText = 0;
			}
			
			flashText++;
			
			tab.set(2, 4, "§eBattle Royale :");
			tab.set(2, 5, "§7" + border + " x " + border);
			tab.set(2, 6, (!phase.isFrozen() ? gazUni : "\u2605") + LongTime.setHMSFormatOnlySeconds(phase.getTimeLeft()));
			
			tab.set(2, 9, "§dJoueurs en ligne :");
			tab.set(2, 10, "§7" + Bukkit.getOnlinePlayers().size() + '/' + Bukkit.getMaxPlayers());
			
			tab.set(2, 12, "§dSpawn :");
			tab.set(2, 13, "§70, 0");
			
			tab.set(2, 15, "§dEtat du chat :");
			tab.set(2, 16, chatManager.isSlowed() ? "§7Ralenti » §c" + chatManager.getDelay() + "s" : (chatManager.isMuted() ? "§cMuté" : "§aNormal"));
		}
	}

	@Override
	public TabType getTabType() {
		return TabType.FACTION_SERV_INFO;
	}

}
