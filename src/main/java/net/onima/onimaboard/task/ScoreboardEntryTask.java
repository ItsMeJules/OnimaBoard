package net.onima.onimaboard.task;

import java.util.Iterator;

import net.onima.onimaapi.utils.TaskPerEntry;
import net.onima.onimaboard.OnimaBoard;
import net.onima.onimaboard.board.Board;
import net.onima.onimaboard.board.utils.ScoreboardTemplate;
import net.onima.onimaboard.players.BoardPlayer;

public class ScoreboardEntryTask extends TaskPerEntry<BoardPlayer> {
	
	private static ScoreboardEntryTask scoreboardTask;
	
	public ScoreboardEntryTask() {
		super(100);
	}

	@Override
	public void run(Iterator<BoardPlayer> iterator) {
		while (iterator.hasNext()) {
			BoardPlayer boardPlayer = iterator.next();
			Board board = boardPlayer.getBoard();
			
			if (board != null && boardPlayer.hasBoardToggled()) {
				ScoreboardTemplate template = board.getTemplate();
				
				if (template != null) {
					board.reset();
					template.fill(boardPlayer, board);
					board.update();
				}
			}
		}
	}
	
	public static void init(OnimaBoard plugin) {
		scoreboardTask = new ScoreboardEntryTask();
		
		scoreboardTask.task(task -> task.runTaskTimerAsynchronously(plugin, 20L, 2L));
		scoreboardTask.insert(BoardPlayer.getOnlineBoardPlayers());
	}
	
	public static ScoreboardEntryTask get() {
		return scoreboardTask;
	}
	
}