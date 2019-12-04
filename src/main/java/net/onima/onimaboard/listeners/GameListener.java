package net.onima.onimaboard.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.onima.onimaboard.OnimaBoard;
import net.onima.onimaboard.players.BoardPlayer;
import net.onima.onimaboard.tab.Tab;
import net.onima.onimaboard.tab.template.DefaultTemplate;
import net.onima.onimafaction.events.battle_royale.BattleRoyaleStartEvent;
import net.onima.onimagames.event.GameStopEvent;
import net.onima.onimagames.event.capable.CapableWinEvent;
import net.onima.onimagames.event.dtc.DTCWinEvent;

public class GameListener implements Listener {
	
	@EventHandler
	public void onGameStop(GameStopEvent event) {
		clearLastLines();
	}
	
	@EventHandler
	public void onCapableWin(CapableWinEvent event) {
		clearLastLines();
	}
	
	@EventHandler
	public void onDTCWin(DTCWinEvent event) {
		clearLastLines();
	}
	
	@EventHandler
	public void onBRStart(BattleRoyaleStartEvent event) {
		Bukkit.getScheduler().runTaskAsynchronously(OnimaBoard.getInstance(), () -> clearLastLines());
	}

	private void clearLastLines() {
		for (BoardPlayer boardPlayer : BoardPlayer.getOnlineBoardPlayers()) {
			Tab tab = boardPlayer.getTab();
			
			if (!(tab.getTemplate() instanceof DefaultTemplate)) {
				tab.set(0, 15, "");
				tab.set(0, 16, "");
				tab.set(0, 17, "");
				tab.set(0, 18, "");
				tab.set(0, 19, "");
			}
		}
	}
	
}
