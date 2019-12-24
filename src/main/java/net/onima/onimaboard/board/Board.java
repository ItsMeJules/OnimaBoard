package net.onima.onimaboard.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.base.Preconditions;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaboard.board.utils.ScoreboardTemplate;
import net.onima.onimaboard.nametag.Nametag;
import net.onima.onimaboard.nametag.NametagType;
import net.onima.onimaboard.nametag.type.AllyNametag;
import net.onima.onimaboard.nametag.type.ArcherTagNametag1;
import net.onima.onimaboard.nametag.type.ArcherTagNametag2;
import net.onima.onimaboard.nametag.type.EnemyNametag;
import net.onima.onimaboard.nametag.type.FactionNametag;
import net.onima.onimaboard.nametag.type.FocusNametag;
import net.onima.onimaboard.nametag.type.staff.DisguiseAllyNametag;
import net.onima.onimaboard.nametag.type.staff.DisguiseArcherTagNametag1;
import net.onima.onimaboard.nametag.type.staff.DisguiseArcherTagNametag2;
import net.onima.onimaboard.nametag.type.staff.DisguiseEnemyNametag;
import net.onima.onimaboard.nametag.type.staff.DisguiseFactionNametag;
import net.onima.onimaboard.nametag.type.staff.DisguiseFocusNametag;
import net.onima.onimaboard.players.BoardPlayer;

/**
 * This class is handling the scoreboard and the player's name. Each scorebaord is unique to each online player on the server.
 */
public class Board {

	private List<BoardLine> lines;
	private Scoreboard scoreboard;
	private Objective objective;
	private String tag;
	private int lastSentCount;
	private BoardPlayer player;
	private ScoreboardTemplate scoreboardTemplate;
	private Nametag ally, archerTag1, archerTag2, enemy, faction, focus;
	private Nametag disguiseAlly, disguiseArcherTag1, disguiseArcherTag2, disguiseEnemy, disguiseFaction, disguiseFocus;
//	private net.minecraft.server.v1_7_R4.Scoreboard nmsBoard;
	
	{
		this.lines = new ArrayList<>();
		this.tag = "PlaceHolder";
		this.lastSentCount = -1;
	}
	
	/**
	 * The constructor is creating the scoreboard, the teams and is displaying the board on the sidebar.
	 * The title length must be <= to 32 charcaters
	 * 
	 * @param scoreboard - The scoreboard to set.
	 * @param title - The scoreboard's title
	 * @param profile - The profile to set this board.
	 */
	public Board(Scoreboard scoreboard, ScoreboardTemplate scoreboardTemplate, BoardPlayer player) {
		Preconditions.checkState(scoreboardTemplate.getTitle().length() < 33, "The title is " + (scoreboardTemplate.getTitle().length() - 32) + " character(s) too long.");
		this.scoreboard = scoreboard;
		this.tag = Methods.colors(scoreboardTemplate.getTitle());
		this.scoreboardTemplate = scoreboardTemplate;
		this.player = player;
		
		(objective = getOrCreateObjective(tag)).setDisplaySlot(DisplaySlot.SIDEBAR);
		
		ally = new AllyNametag();
		archerTag1 = new ArcherTagNametag1();
		archerTag2 = new ArcherTagNametag2();
		enemy = new EnemyNametag();
		faction = new FactionNametag(player.getApiPlayer().toPlayer());
		focus = new FocusNametag();
		
		disguiseAlly = new DisguiseAllyNametag();
		disguiseArcherTag1 = new DisguiseArcherTagNametag1();
		disguiseArcherTag2 = new DisguiseArcherTagNametag2();
		disguiseEnemy = new DisguiseEnemyNametag();
		disguiseFaction = new DisguiseFactionNametag();
		disguiseFocus = new DisguiseFocusNametag();
		
		ally.initInScoreboard(scoreboard);
		archerTag1.initInScoreboard(scoreboard);
		archerTag2.initInScoreboard(scoreboard);
		enemy.initInScoreboard(scoreboard);
		faction.initInScoreboard(scoreboard);
		focus.initInScoreboard(scoreboard);
		
		disguiseAlly.initInScoreboard(scoreboard);
		disguiseArcherTag1.initInScoreboard(scoreboard);
		disguiseArcherTag2.initInScoreboard(scoreboard);
		disguiseEnemy.initInScoreboard(scoreboard);
		disguiseFaction.initInScoreboard(scoreboard);
		disguiseFocus.initInScoreboard(scoreboard);
	}
	
	public void onJoin() {
		player.getApiPlayer().toPlayer().setScoreboard(scoreboard);
		initNametag(Methods.getOnlinePlayers(player.getApiPlayer().toPlayer()));
	}
	
	public void add(String text) {
		BoardLine line;
		
		int length = text.length();
		
		if (length <= 16) 
			line = new BoardLine(text, "");
		else {
			Preconditions.checkState(length < 33, "The line is " + (length - 32) + " character(s) too long.");
			
			String left = text.substring(0, 16);
			String right = text.substring(16, text.length());
			
			if (left.endsWith("§")) {
				left = left.substring(0, left.length()-1);
				right = "§" + right;
			}
			
			String lastColors = ChatColor.getLastColors(left);
			right = lastColors + right;
			line = new BoardLine(left, StringUtils.left(right, 16));
		}
		lines.add(line);
	}
	
	public void reset() {
		lines.clear();
	}
	
	public void remove(int index) { //TODO essayer de changer avec juste des ints (pas d'opératins lourdes).
		String name = getNameForIndex(index);
		scoreboard.resetScores(name);
		getOrCreateTeam(ChatColor.stripColor(StringUtils.left(tag, 14)) + index, index).unregister();
	}
	
	public void update() {
		int i;
		for (i = 0; i < lines.size(); i++) {
			//Getting the team "Mc-Market i"
			Team team = getOrCreateTeam(ChatColor.stripColor(StringUtils.left(tag, 14)) + i, i);
			int pos = lines.size() - i - 1;
			
			if (pos < 0)
				continue;
			
			BoardLine str = lines.get(pos);
			
			team.setPrefix(str.getLeft());
			team.setSuffix(str.getRight());
			//Showing the team
			objective.getScore(getNameForIndex(i)).setScore(i + 1);
		}
		
		if (lastSentCount != -1) {
			i = lines.size();
			
			for (int j = 0; j < lastSentCount - i; j++)
				remove(i + j);
		}
		
		lastSentCount = lines.size();
	}
	
	private Objective getOrCreateObjective(String objective) {
		Objective obj = scoreboard.getObjective("onima");
		
		if (obj == null)
			obj = scoreboard.registerNewObjective("onima", "dummy");
		
		obj.setDisplayName(objective);
	
		return obj;
	}
	
	private Team getOrCreateTeam(String team, int i) {
		Team sTeam = scoreboard.getTeam(team);
		
		if (Objects.isNull(sTeam)) {
			sTeam = scoreboard.registerNewTeam(team);
			sTeam.addEntry(getNameForIndex(i));
		}
		
		return sTeam;
	}
	
	private String getNameForIndex(int index) {
		return ChatColor.values()[index].toString() + ChatColor.RESET;
	}
	
	/**
	 * This method returns this class scoreboard.
	 */
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
	
	public BoardPlayer getPlayer() {
		return player;
	}
	
	public Nametag getNametag(NametagType type) {
		switch (type) {
		case ALLY:
			return ally;
		case ARCHER_TAG_1:
			return archerTag1;
		case ARCHER_TAG_2:
			return archerTag2;
		case ENEMY:
			return enemy;
		case FACTION:
			return faction;
		case FOCUS:
			return focus;
		case DISGUISED_ALL:
			return disguiseAlly;
		case DISGUISED_TAG_1:
			return disguiseArcherTag1;
		case DISGUISED_TAG_2:
			return disguiseArcherTag2;
		case DISGUISED_ENE:
			return disguiseEnemy;
		case DISGUISED_FAC:
			return disguiseFaction;
		case DISGUISED_FOC:
			return disguiseFocus;
		default:
			return null;
		}
	}
	
	/**
	 * This method sets a nametag to a player.
	 * 
	 * @param nametag - {@link Nametag} to set.
	 * @param player - Player to set the Nametag.
	 */
	public void setNameTag(NametagType type, Player player) {
		setNameTag(type, Arrays.asList(player));
	}
	
	/**
	 * This method sets a Nametag to all the players given in the argument profiles.
	 * 
	 * @param nametag - {@link Nametag} to set.
	 * @param players - A collection of players to set the Nametag.
	 */
	public void setNameTag(NametagType type, Collection<Player> players) {
		getNametag(type).show(players);
	}
	
	/**
	 * This method toggles on/off the scoreboard.
	 * 
	 * @param toggle - Toggling on/off the board.
	 */
	public void toggle(boolean toggle) {
		if (!toggle) {
			reset();
			update();
		}
	}
	
	public ScoreboardTemplate getTemplate() {
		return scoreboardTemplate;
	}
	
	public void setTemplate(ScoreboardTemplate scoreboardTemplate) {
		this.scoreboardTemplate = scoreboardTemplate;
	}
	
	/**
	 * This method inits all the nametags for the given players.
	 */
	public void initNametag(Collection<Player> players) {
		Player normalPlayer = player.getApiPlayer().toPlayer();
		
		for (Player player : players) {
			if (player.getUniqueId().equals(normalPlayer.getUniqueId()))
				continue;
			
			BoardPlayer boardPlayer = BoardPlayer.getPlayer(player);
			boolean testOthers1 = true, testOthers2 = true;
			
			for (Nametag nametag : Nametag.getByPriority(this, OnimaPerm.ONIMAAPI_SEE_INVISIBLE.has(player))) {
				if (testOthers1 && nametag.isApplicable(player, this.player)) {
					nametag.show(player);
					testOthers1 = false;
				}
				
				if (testOthers2 && nametag.isApplicable(normalPlayer, boardPlayer)) {
					boardPlayer.getBoard().getNametag(nametag.getType()).show(normalPlayer);
					testOthers2 = false;
				}
				
				if (!testOthers1 && !testOthers1)
					break;
			}
		}
		
	}
	
	class BoardLine {
		
		private String left, right;
		
		public BoardLine(String left, String right) {
			this.left = left;
			this.right = right;
		}

		public String getLeft() {
			return left;
		}

		public String getRight() {
			return right;
		}

	}
	
//	public void showInvisible(BoardPlayer player) {
//		Board board = player.getBoard();
//		
//		if (!board.hasInvisibleName())
//			return;
//		
//		ScoreboardScore score = nmsBoard.getPlayerScoreForObjective(Methods.getRealName(player.getApiPlayer().getOfflinePlayer()), objective);
//		
//		score.setScore(0);
//		
//		PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(score, 0);
//	}
//
//	public void setInvisibleName(boolean invisible) {
//		if (invisible) {
//			if (nmsBoard == null)
//				nmsBoard = ((CraftScoreboard) scoreboard).getHandle();
//			
//			ScoreboardObjective objective = nmsBoard.getObjective("invisible");
//			PlayerConnection connection = ((CraftPlayer) player.getApiPlayer().toPlayer()).getHandle().playerConnection;
//			
//			if (objective == null)
//				nmsBoard.registerObjective("invisible", new ScoreboardBaseCriteria("Invisible"));
//			
//			connection.sendPacket(new PacketPlayOutScoreboardObjective(objective, 0));
//			connection.sendPacket(new PacketPlayOutScoreboardDisplayObjective(2, objective));
//			
//			ScoreboardScore score = nmsBoard.getPlayerScoreForObjective(Methods.getRealName(player.getApiPlayer().getOfflinePlayer()), objective);
//			
//			score.setScore(0);
//			
//			PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(score, 0);
//			
//			for (Player online : Bukkit.getOnlinePlayers()) {
//				if (OnimaPerm.ONIMAAPI_SEE_INVISIBLE.has(online))
//					((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
//			}
//		} else {
//			if (nmsBoard == null)
//				return;
//		
//			ScoreboardObjective objective = nmsBoard.getObjective("invisible");
//			
//			if (objective == null)
//				return;
//			
//			PlayerConnection connection = ((CraftPlayer) player.getApiPlayer().toPlayer()).getHandle().playerConnection;
//			
//			connection.sendPacket(new PacketPlayOutScoreboardObjective(objective, 1));
//			
//			PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(
//					nmsBoard.getPlayerScoreForObjective(Methods.getRealName(player.getApiPlayer().getOfflinePlayer()), objective), 0);
//			
//			for (Player online : Bukkit.getOnlinePlayers()) {
//				if (OnimaPerm.ONIMAAPI_SEE_INVISIBLE.has(online))
//					((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
//			}
//
//			nmsBoard.handleObjectiveRemoved(objective);
//			nmsBoard = null;
//		}
//	}
//	
//	public boolean hasInvisibleName() {
//		return nmsBoard != null;
//	}
	
}
