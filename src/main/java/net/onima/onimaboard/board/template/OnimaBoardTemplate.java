package net.onima.onimaboard.board.template;

import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.bukkit.GameMode;

import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimaboard.board.Board;
import net.onima.onimaboard.board.utils.BoardType;
import net.onima.onimaboard.board.utils.ScoreboardTemplate;
import net.onima.onimaboard.players.BoardPlayer;
import net.onima.onimafaction.armorclass.Archer;
import net.onima.onimafaction.armorclass.ArmorClass;
import net.onima.onimafaction.armorclass.Bard;
import net.onima.onimafaction.armorclass.Reaper;
import net.onima.onimafaction.armorclass.Reaper.ReaperStage;
import net.onima.onimafaction.cooldowns.ArcherJumpCooldown;
import net.onima.onimafaction.cooldowns.ArcherSpeedCooldown;
import net.onima.onimafaction.cooldowns.BardItemCooldown;
import net.onima.onimafaction.cooldowns.ReaperPowerCooldown;
import net.onima.onimafaction.cooldowns.ReaperStealthCooldown;
import net.onima.onimafaction.faction.struct.Chat;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimafaction.task.TimedTask;
import net.onima.onimafaction.timed.TimedEvent;
import net.onima.onimagames.game.Game;
import net.onima.onimagames.game.citadel.Citadel;
import net.onima.onimagames.game.conquest.Conquest;
import net.onima.onimagames.game.conquest.ConquestType;
import net.onima.onimagames.game.dtc.DTC;
import net.onima.onimagames.game.koth.Koth;

public class OnimaBoardTemplate implements ScoreboardTemplate {

	/**
	 * This method returns the scoreboard title which is : <i>�eOnima �6[�cMap 1�6]</i>
	 * 
	 * @return The scoreboard title.
	 */
	@Override
	public String getTitle() {
		return "§eOnima §6[§cMap 1§6]";
	}
	
	@Override
	public void fill(BoardPlayer boardPlayer, Board board) {
		APIPlayer apiPlayer = boardPlayer.getApiPlayer();
		FPlayer fPlayer = boardPlayer.getFPlayer();

		boolean bottomBarsStaff = false;
		
		if (boardPlayer.hasStaffBoard()) {
			GameMode mode = apiPlayer.toPlayer().getGameMode();
			Chat chat = fPlayer.getChat();
			
			board.add(ConfigurationService.SCOREBOARD_BAR);
			board.add("§bGamemode§6: " + (mode == GameMode.CREATIVE ? "§a" : (mode == GameMode.ADVENTURE ? "§e" : "§c")) + mode.name());
			board.add("§eChat §6: " + (chat == Chat.GLOBAL ? "§c" : chat == Chat.STAFF ? "§a" : "§9") + chat.getChat());
			board.add("§9Vanish §6: " + (apiPlayer.isVanished() ? "§aInvisible" : "§cVisible"));
			board.add(ConfigurationService.SCOREBOARD_BAR);
			bottomBarsStaff = true;
		}
		
		if (!bottomBarsStaff)
			setBar(fPlayer, board);
		
		TimedTask timedTask = null;
		
		if ((timedTask = TimedEvent.getRunningTask()) != null) {
			if (timedTask.isDelayed())
				board.add(timedTask.getTimedEvent().getDelayScoreboardLine() + LongTime.setHMSFormatOnlySeconds(timedTask.getDelay() * 1000));
			else
				board.add(timedTask.getTimedEvent().getRunningScoreboardLine() + LongTime.setHMSFormatOnlySeconds(timedTask.getTime() * 1000));
		}
		
		ArmorClass armorClass = null;
		
		if ((armorClass = fPlayer.getEquippedClass()) != null) {
			board.add("§7§lClasse §6: " + armorClass.getNiceName());
			
			if (armorClass instanceof Archer) {
				Cooldown speedCooldown = Cooldown.getCooldown(ArcherSpeedCooldown.class);
				Cooldown jumpCooldown = Cooldown.getCooldown(ArcherJumpCooldown.class);
				long speedLeft, jumpLeft;
				
				if ((speedLeft = apiPlayer.getTimeLeft(speedCooldown)) > 0L)
					board.add(speedCooldown.scoreboardDisplay(speedLeft));
				if ((jumpLeft = apiPlayer.getTimeLeft(jumpCooldown)) > 0L)
					board.add(jumpCooldown.scoreboardDisplay(jumpLeft));
			} else if (armorClass instanceof Bard) {
				board.add("  §e» §bPower§6 : §c" + ((Bard) armorClass).getPower());
				
				Cooldown bardCooldown = BardItemCooldown.getCooldown(BardItemCooldown.class);
				long bardLeft;
				
				if ((bardLeft = apiPlayer.getTimeLeft(bardCooldown)) > 0L)
					board.add(bardCooldown.scoreboardDisplay(bardLeft));
			} else if (armorClass instanceof Reaper) {
				Reaper reaper = (Reaper) armorClass;
				ReaperStage stage = reaper.getReaperStage();
				
				if (stage == ReaperStage.PASSIVE_MODE)
					board.add("  §e» Mode §aPassif");
				else if (stage == ReaperStage.POWER_MODE)
					board.add("  §e» Mode §cPower (" + LongTime.setHMSFormat(apiPlayer.getTimeLeft(ReaperPowerCooldown.class)) + ")");
				else if (stage == ReaperStage.STEALTH_MODE)
					board.add("  §e» Mode §5Stealth §c(" + LongTime.setHMSFormat(apiPlayer.getTimeLeft(ReaperStealthCooldown.class)) + ")");
			}
		}
		
		for (byte id : apiPlayer.getCooldownsById()) {
			if ((id == 10 || id == 11) && fPlayer.getEquippedClass() != null) continue;
			if (id == 4
					|| id == 12 
					|| id == 14
					|| id == 13
					|| id == 8) continue;
				
			Cooldown cooldown = Cooldown.getCooldown(id);
			String line = cooldown.scoreboardDisplay(apiPlayer.getTimeLeft(cooldown));

			if (line != null) {
				if (id == 9) {
					board.add(StringUtils.replace(line, "%mark-number%", String.valueOf(fPlayer.getArcherTag())));
					continue;
				}
					
				board.add(line);
			}
		}
		
		Game game = null;
		
		if ((game = Game.getStartedGame()) != null) {
			if (game instanceof Koth)
				board.add(game.getGameType().getColor() + (game instanceof Citadel ? "Citadel" : game.getName()) + " §6: §c" + LongTime.setHMSFormatOnlySeconds(((Koth) game).getCapTimeLeft()));
			else if (game instanceof DTC) { 
				DTC dtc = (DTC) game;
				board.add(game.getGameType().getColor() + "DTC §6: §c" + dtc.getPoints() + '/' + dtc.getInitialPoints());
			} else if (game instanceof Conquest) {
				Conquest conquest = (Conquest) game;
				
				board.add(game.getGameType().getColor() + "Conquest §7(" + conquest.getPointsToWin() + " points) §6:");
				board.add("          §d• " + LongTime.setHMSFormatOnlySeconds(conquest.getZone(ConquestType.MAIN).getCapTimeLeft()));
				board.add("      §9• " + LongTime.setHMSFormatOnlySeconds(conquest.getZone(ConquestType.BLUE).getCapTimeLeft()) + " §2• " + LongTime.setHMSFormatOnlySeconds(conquest.getZone(ConquestType.GREEN).getCapTimeLeft()));
				board.add("      §c• " + LongTime.setHMSFormatOnlySeconds(conquest.getZone(ConquestType.RED).getCapTimeLeft()) + " §e• " + LongTime.setHMSFormatOnlySeconds(conquest.getZone(ConquestType.YELLOW).getCapTimeLeft()));
				
				for (Entry<String, Short> entry : Methods.getTop(conquest.getCappingFactions(), 3).collect(Collectors.toList()))
					board.add("§d§l" + entry.getKey() + " §7» " + entry.getValue());
			}
		}
		
		setBar(fPlayer, board);
	}

	@Override
	public BoardType getType() {
		return BoardType.ONIMA;
	}
	
	public void setBar(FPlayer fPlayer, Board board) {
		boolean hasBars = false;
		boolean hasCooldown = false;
		
		for (Cooldown cooldown : fPlayer.getApiPlayer().getCooldowns()) {
			switch (cooldown.getId()) {
			case 8:
				if (!fPlayer.getArmorClass(Bard.class).isActivated())
					continue;
			case 10:
			case 17:
				if (!fPlayer.getArmorClass(Archer.class).isActivated())
					continue;
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 18:
			case 19:
			case 4:
			case 20:
			case 21:
				if (!fPlayer.getArmorClass(Reaper.class).isActivated())
					continue;
			case 22:
			case 23:
				continue;
			default:
				break;
			}
			
			hasCooldown = true;
			break;
		}
		
		if (!hasBars && (hasCooldown || Game.getStartedGame() != null || fPlayer.getEquippedClass() != null || TimedEvent.getRunningTask() != null)) {
			hasBars = true;
			board.add(ConfigurationService.SCOREBOARD_BAR);
		}
	}

}