package net.onima.onimaboard.players;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaboard.OnimaBoard;
import net.onima.onimaboard.board.Board;
import net.onima.onimaboard.board.template.OnimaBoardTemplate;
import net.onima.onimaboard.board.utils.ScoreboardTemplate;
import net.onima.onimaboard.tab.Tab;
import net.onima.onimaboard.tab.utils.TabType;
import net.onima.onimaboard.task.ScoreboardEntryTask;
import net.onima.onimaboard.task.TabEntryTask;
import net.onima.onimafaction.players.FPlayer;

public class BoardPlayer extends OfflineBoardPlayer {

	private static Map<UUID, BoardPlayer> players;
	
	static {
		players = new HashMap<>();
	}
	
	private APIPlayer apiPlayer;
	private FPlayer fPlayer;
	private Tab tab;
	private Board board;
	
	public BoardPlayer(APIPlayer apiPlayer) {
		super(apiPlayer);
		
		this.apiPlayer = apiPlayer;
		fPlayer = FPlayer.getPlayer(apiPlayer.getUUID());
		
		save();
	}
	
	public void loadLogin() {
		setBoard(new OnimaBoardTemplate());
	}
	
	public void loadJoin() {
		Bukkit.getScheduler().runTask(OnimaBoard.getInstance(), () -> {
			board.onJoin();
			
			if (fPlayer.hasFaction())
				setTab(TabType.FACTION_SERV_INFO);
			else
				setTab(TabType.NO_FACTION_SERV_INFO);
			
			ScoreboardEntryTask.get().insert(this);
			TabEntryTask.get().insert(this);
			
			apiPlayer.setLoaded(true);
		});
	}
	
	public APIPlayer getApiPlayer() {
		return apiPlayer;
	}
	
	public FPlayer getFPlayer() {
		return fPlayer;
	}
	
	public Tab getTab() {
		return tab;
	}
	
	public void setTab(TabType type) {
		if (((CraftPlayer) apiPlayer.toPlayer()).getHandle().playerConnection.networkManager.getVersion() >= 47) {
    		apiPlayer.sendMessage("§cVu que votre version de Minecraft est supérieur à la 1.7, vous ne pouvez pas avoir de tab custom. Il est très fortement conseillé de se connecter en 1.7 pour avoir une expérience de jeu optimale.");
    		return;
		}
		
		if (tab == null)
			tab = new Tab(apiPlayer.toPlayer());
		
		tab.empty();
		tab.setTemplate(type == null ? TabType.NO_FACTION_SERV_INFO.getTemplate() : type.getTemplate());
	}
	
	public Board getBoard() {
		return board;
	}
	
	/**
	 * This method sets or remove the scoreboard for this profile.
	 * 
	 * @param set - Whether set the board or remove it.
	 * @param scoreboardTemplate - The template to display (ie: {@link OnimaBoardTemplate}).
	 */
	public void setBoard(ScoreboardTemplate scoreboardTemplate) {
		board = new Board(Bukkit.getScoreboardManager().getNewScoreboard(), scoreboardTemplate, this);
	}
	
	
	@Override
	public void toggleBoard(boolean toggle) {
		super.toggleBoard(toggle);
		board.toggle(toggle);
		
	}
	
	@Override
	public void save() {
		super.save();
		players.put(apiPlayer.getUUID(), this);
	}
	
	@Override
	public void remove() {
		super.remove();
		players.remove(apiPlayer.getUUID());
		ScoreboardEntryTask.get().safeRemove(this);
		TabEntryTask.get().safeRemove(this);
		
		if (fPlayer.hasFaction()) {
			for (FPlayer member : fPlayer.getFaction().getOnlineMembers(null)) {
				for (int y = 3; y < 20; y++)
					getPlayer(member.getApiPlayer().getUUID()).tab.set(1, y, "");
			}
		}
	}
	
	public static BoardPlayer getPlayer(UUID uuid) {
		return players.get(uuid);
	}
	
	public static BoardPlayer getPlayer(String name) {
		Player online = Bukkit.getPlayer(name);
		
		if (online == null)
			return null;
		else
			return getPlayer(online.getUniqueId());
	}
	
	public static BoardPlayer getPlayer(Player player) {
		return players.get(player.getUniqueId());
	}
	
	public static Map<UUID, BoardPlayer> getBoardPlayers() {
		return players;
	}
	
	public static Collection<BoardPlayer> getOnlineBoardPlayers() {
		return players.values();
	}
	
}
