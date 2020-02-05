package net.onima.onimaboard.task;

import java.util.Iterator;

import net.onima.onimaapi.utils.TaskPerEntry;
import net.onima.onimaboard.OnimaBoard;
import net.onima.onimaboard.players.BoardPlayer;
import net.onima.onimaboard.tab.Tab;
import net.onima.onimaboard.tab.utils.TabTemplate;

public class TabEntryTask extends TaskPerEntry<BoardPlayer> {

	private static TabEntryTask tabTask;

	public TabEntryTask() {
		super(100);
	}

	@Override
	public void run(Iterator<BoardPlayer> iterator) {
		while (iterator.hasNext()) {
			BoardPlayer boardPlayer = iterator.next();
			Tab tab = boardPlayer.getTab();
			
			if (tab != null) {
				TabTemplate template = tab.getTemplate();
				
				if (template != null)
					template.fill(tab);
			}
			
		}
	}
	
	public static void init(OnimaBoard plugin) {
		tabTask = new TabEntryTask();
		
		tabTask.task(task -> task.runTaskTimerAsynchronously(plugin, 20L, 2L));
		tabTask.insert(BoardPlayer.getOnlineBoardPlayers());
	}
	
	public static TabEntryTask get() {
		return tabTask;
	}

}