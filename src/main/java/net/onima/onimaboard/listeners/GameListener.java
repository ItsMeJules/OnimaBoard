package net.onima.onimaboard.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.onima.onimaboard.OnimaBoard;
import net.onima.onimaboard.players.BoardPlayer;
import net.onima.onimaboard.tab.Tab;
import net.onima.onimaboard.tab.template.FactionServerInfoTemplate;
import net.onima.onimaboard.tab.template.NoFactionServerInfoTemplate;
import net.onima.onimafaction.events.battle_royale.BattleRoyaleStartedEvent;
import net.onima.onimagames.event.GameStopEvent;
import net.onima.onimagames.event.capable.CapableWinEvent;
import net.onima.onimagames.event.dtc.DTCWinEvent;

public class GameListener implements Listener {
	
	@EventHandler
	public void onGameStop(GameStopEvent event) {
		Bukkit.getScheduler().runTask(OnimaBoard.getInstance(), () -> clearLastLines(0));
	}
	
	@EventHandler
	public void onCapableWin(CapableWinEvent event) {
		Bukkit.getScheduler().runTask(OnimaBoard.getInstance(), () -> clearLastLines(0));
	}
	
	@EventHandler
	public void onDTCWin(DTCWinEvent event) {
		Bukkit.getScheduler().runTask(OnimaBoard.getInstance(), () -> clearLastLines(0));
	}
	
	@EventHandler
	public void onBRStart(BattleRoyaleStartedEvent event) {
		for (BoardPlayer boardPlayer : BoardPlayer.getOnlineBoardPlayers()) {
			Tab tab = boardPlayer.getTab();
			
			if (tab.getTemplate() instanceof FactionServerInfoTemplate || tab.getTemplate() instanceof NoFactionServerInfoTemplate) {
				tab.set(2, 4, "");
				tab.set(2, 5, "");
				tab.set(2, 6, "");
				tab.set(2, 7, "");
				tab.set(2, 8, "");
				tab.set(2, 9, "");
				tab.set(2, 10, "");
				tab.set(2, 11, "");
				tab.set(2, 12, "");
				tab.set(2, 13, "");
				tab.set(2, 14, "");
				tab.set(2, 15, "");
				tab.set(2, 16, "");
				tab.set(2, 17, "");
				tab.set(2, 18, "");
				tab.set(2, 19, "");
			}
		}
	}

	private void clearLastLines(int x) {
		for (BoardPlayer boardPlayer : BoardPlayer.getOnlineBoardPlayers()) {
			Tab tab = boardPlayer.getTab();
			
			if (tab.getTemplate() instanceof FactionServerInfoTemplate || tab.getTemplate() instanceof NoFactionServerInfoTemplate) {
				tab.set(x, 15, "");
				tab.set(x, 16, "");
				tab.set(x, 17, "");
				tab.set(x, 18, "");
				tab.set(x, 19, "");
			}
		}
	}
	
}
