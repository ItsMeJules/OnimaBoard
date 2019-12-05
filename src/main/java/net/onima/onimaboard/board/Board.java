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
import net.onima.onimaboard.players.BoardPlayer;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Relation;
import net.onima.onimafaction.players.FPlayer;

/**
 * This class is handling the scoreboard and the player's name. Each scorebaord is unique to each online player on the server.
 */
public class Board { //TODO Ajouter des teams pour colorer les noms meme s'il est déguisé (exemple [Déguisé] §cGrosPD ou §cGrosPD [Déguisé]).

	private List<BoardLine> lines;
	private Scoreboard scoreboard;
	private Objective objective;
	private String tag;
	private int lastSentCount;
	private Player player;
	private Team faction, allies, enemies, archerTagged1, archerTagged2, focus, disguised;
	private ScoreboardTemplate scoreboardTemplate;
	
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
	public Board(Scoreboard scoreboard, ScoreboardTemplate scoreboardTemplate, Player player) {
		Preconditions.checkState(scoreboardTemplate.getTitle().length() < 33, "The title is " + (scoreboardTemplate.getTitle().length() - 32) + " character(s) too long.");
		this.scoreboard = scoreboard;
		this.tag = Methods.colors(scoreboardTemplate.getTitle());
		this.scoreboardTemplate = scoreboardTemplate;
		this.player = player;
		
		(this.objective = getOrCreateObjective(tag)).setDisplaySlot(DisplaySlot.SIDEBAR);
		
		this.faction = scoreboard.registerNewTeam("faction");
		this.faction.setPrefix("§2");
		this.faction.setCanSeeFriendlyInvisibles(true);
		
		(this.allies = scoreboard.registerNewTeam("allies")).setPrefix("§9");
		(this.enemies = scoreboard.registerNewTeam("enemies")).setPrefix("§e");
		(this.archerTagged1 = scoreboard.registerNewTeam("archer1")).setPrefix("§c");
		(this.archerTagged2 = scoreboard.registerNewTeam("archer2")).setPrefix("§4");
		(this.focus = scoreboard.registerNewTeam("focus")).setPrefix("§d");
		(this.disguised = scoreboard.registerNewTeam("disguised")).setPrefix("§f[§eDéguisé§f] ");
	}
	
	/**
	 * This method inits the every online players nametag and sets the current player nametag on faction.
	 */
	public void onJoin() {
		player.setScoreboard(scoreboard);
		initNametag(Methods.getOnlinePlayers(player));
		addFactionMate(player);
	}
	
//	public void add(String text) {
//		Iterator<String> iterator = Splitter.fixedLength(16).split(text).iterator();
//		BoardLine boardLine = new BoardLine(iterator.next());
//		
//		while (iterator.hasNext()) {
//			String next = iterator.next();
//			String lastSet = boardLine.getLastSet();
//			
//			if (lastSet.endsWith("§")) {
//				boardLine.shrinkLastField(1);
//				next = "§" + next;
//			}
//			
//			boardLine.addText(StringUtils.left(ChatColor.getLastColors(lastSet) + next, 16));
//		}
//		
//		if (boardLine.getSecond() == null)
//			boardLine.addText(getNameForIndex(lines.size()));
//		
//		lines.add(boardLine);
//	}
//	
//	/**
//	 * This method clears all the scoreboard's lines.
//	 */
//	public void reset() {
//		lines.clear();
//	}
//	
//	/**
//	 * This method removes a scoreboard line at the given index.
//	 * 
//	 * @param index - Index to remove the line.
//	 */
//	public void removeTeam(int index) {
//		String name = getNameForIndex(index);
//		
//		scoreboard.resetScores(name);
//		Team team = getOrCreateTeam(ChatColor.stripColor(StringUtils.left(tag, 14)) + index, index);
//		
//		team.unregister();
//	}
//	
//	public void update() {
//		int i;
//		
//		for (i = 0; i < lines.size(); i++) {
//			Team team = getOrCreateTeam(ChatColor.stripColor(StringUtils.left(tag, 14)) + i);
//			BoardLine boardLine = lines.get(lines.size() - i - 1);
//			
//			team.setPrefix(boardLine.getFirst());
//			team.addEntry(boardLine.getSecond());
//			team.setSuffix(boardLine.getThird());
//			
//			objective.getScore(boardLine.getSecond()).setScore(i + 1);
//			
////			if (boardLine.hasThird()) {
////				team.addEntry(boardLine.getSecond());
////				team.setSuffix(boardLine.getThird());
////				objective.getScore(boardLine.getSecond()).setScore(i + 1);
////			} else {
////				String entry = getNameForIndex(i);
////				team.addEntry(getNameForIndex(i));
////				team.setSuffix(boardLine.getSecond());
////				objective.getScore(getNameForIndex(i)).setScore(i + 1);
////			}
//		}
//		
//		if (lastSentCount != -1) {
//			i = lines.size();
//			
//			for (int j = 0; j < lastSentCount - i; j++)
//				removeTeam(i + j);
//		}
//		lastSentCount = lines.size();
//	}
//	
//	private Objective getOrCreateObjective(String objective) {
//		Objective obj = scoreboard.getObjective("onima");
//		
//		if (obj == null)
//			obj = scoreboard.registerNewObjective("onima", "dummy");
//		
//		obj.setDisplayName(objective);
//	
//		return obj;
//	}
//	
//	private Team getOrCreateTeam(String team) {
//		Team sTeam = scoreboard.getTeam(team);
//		
//		if (sTeam == null)
//			sTeam = scoreboard.registerNewTeam(team);
//		
//		return sTeam;
//	}
//	
	
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
		for(i = 0; i < lines.size(); i++) {
			//Getting the team "Mc-Market i"
			Team team = getOrCreateTeam(ChatColor.stripColor(StringUtils.left(tag, 14)) + i, i);
			BoardLine str = lines.get(lines.size() - i - 1);
			
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
		Objective obj = scoreboard.getObjective("gueden");
		
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
	
	public Player getPlayer() {
		return player;
	}
	
	private void checkTeam(Player player) {
		removeFactionMate(player);
		removeAllyMate(player);
		removeEnemyPrick(player);
		removeArcherVictim(player, 1);
		removeArcherVictim(player, 2);
		removeSneakyDisguised(player);
	}
	
	private void addFactionMate(Player player) {
		checkTeam(player);
		faction.addPlayer(player);
	}
	
	private void removeFactionMate(Player player) {
		if(faction.getPlayers().contains(player)) faction.removePlayer(player);
	}
	
	private void addAllyMate(Player player) {
		checkTeam(player);
		allies.addPlayer(player);
	}
	
	private void removeAllyMate(Player player) {
		if(allies.getPlayers().contains(player)) allies.removePlayer(player);
	}
	
	private void addEnemyPrick(Player player) {
		checkTeam(player);
		enemies.addPlayer(player);
	}
	
	private void addSneakyDisguised(Player player) {
		checkTeam(player);
		disguised.addPlayer(player);
	}
	
	private void removeEnemyPrick(Player player) {
		if(enemies.getPlayers().contains(player)) enemies.removePlayer(player);
	}
	
	private void addArcherVictim(Player player, int mark) {
		checkTeam(player);
		if (mark == 1) 
			archerTagged1.addPlayer(player);
		else if (mark == 2)
			archerTagged2.addPlayer(player);
	}
	
	private void removeArcherVictim(Player player, int mark) {
		if(mark == 1) 
			if(archerTagged1.getPlayers().contains(player)) archerTagged1.removePlayer(player);
		else if(mark == 2)
			if(archerTagged2.getPlayers().contains(player)) archerTagged2.removePlayer(player);
	}
	
	private void addFocusPlayer(Player player) {
		checkTeam(player);
		focus.addPlayer(player);
	}
	
	private void removeSneakyDisguised(Player player) {
		if (disguised.getPlayers().contains(player))
			disguised.removePlayer(player);
	}
	
	/**
	 * This method sets a nametag to a player.
	 * 
	 * @param nametag - {@link Nametag} to set.
	 * @param player - Player to set the Nametag.
	 */
	public void setNameTag(Nametag nametag, Player player) {
		setNameTag(nametag, Arrays.asList(player));
	}
	
	/**
	 * This method sets a Nametag to all the players given in the argument profiles.
	 * 
	 * @param nametag - {@link Nametag} to set.
	 * @param players - A collection of players to set the Nametag.
	 */
	public void setNameTag(Nametag nametag, Collection<Player> players) {
		for (Player player : players) {
			if (this.player.equals(player)) return;
			
			switch (nametag) {
			case ALLY:
				addAllyMate(player);
				break;
			case ARCHER_TAG_1:
				addArcherVictim(player, 1);
				break;
			case ARCHER_TAG_2:
				addArcherVictim(player, 2);
				break;
			case ENEMY:
				addEnemyPrick(player);
				break;
			case FACTION:
				addFactionMate(player);
				break;
			case FOCUS:
				addFocusPlayer(player);
				break;
			case DISGUISED_ADMIN:
				addSneakyDisguised(player);
				break;
			default:
				break;
			}
		}
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
	 * This method inits all the nametags for all online players.
	 */
	public void initNametag(Collection<Player> players) {
		FPlayer fPlayer = FPlayer.getPlayer(player);
		PlayerFaction faction = fPlayer.getFaction();
		boolean disguised = fPlayer.getApiPlayer().getDisguiseManager().isDisguised();
		
		for (Player player : players) {
			if (player.equals(this.player)) continue;
			
			BoardPlayer boardPlayer = BoardPlayer.getPlayer(player);
			PlayerFaction otherFaction = boardPlayer.getFPlayer().getFaction();
			
			if (otherFaction == null || faction == null || otherFaction.getRelation(faction) == Relation.ENEMY) {
				setNameTag(Nametag.ENEMY, player);
				boardPlayer.getBoard().setNameTag(Nametag.ENEMY, this.player);
			} else if(otherFaction.getRelation(faction) == Relation.MEMBER) {
				setNameTag(Nametag.FACTION, player);
				boardPlayer.getBoard().setNameTag(Nametag.FACTION, this.player);
			} else if(otherFaction.getRelation(faction) == Relation.ALLY) {
				setNameTag(Nametag.ALLY, player);
				boardPlayer.getBoard().setNameTag(Nametag.ALLY, this.player);
			}
			
			if (otherFaction != null && otherFaction.getFocused() != null && otherFaction.getFocused().equals(this.player.getPlayer()))
				boardPlayer.getBoard().setNameTag(Nametag.FOCUS, this.player);
			
			if (faction != null && faction.getFocused() != null && faction.getFocused().equals(player))
				setNameTag(Nametag.FOCUS, player);
			
			if (disguised && OnimaPerm.ONIMAAPI_DISGUISE_COMMAND_LIST.has(player))
				boardPlayer.getBoard().setNameTag(Nametag.DISGUISED_ADMIN, this.player);
			
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

//		private String first, second, third, lastSet;
//		private int lastField;
//		
//		public BoardLine(String first) {
//			this.first = first;
//			lastSet = first;
//			lastField = 0;
//		}
//		
//		public void shrinkLastField(int toShrink) {
//			switch (lastField) {
//			case 0:
//				first = first.substring(0, first.length() - toShrink);
//				break;
//			case 1:
//				second = second.substring(0, second.length() - toShrink);
//				break;
//			case 2:
//				third = third.substring(0, third.length() - toShrink);
//				break;
//			default:
//				break;
//			}
//		}
//		
//		public void addText(String text) {
//			if (first == null)
//				first = text;
//			else if (second == null)
//				second = text;
//			else if (third == null)
//				third = text;
//		}
//		
//		public String getFirst() {
//			return first;
//		}
//		
//		public String getSecond() {
//			return second;
//		}
//		
//		public String getThird() {
//			return third;
//		}
//		
//		public boolean hasThird() {
//			return third != null;
//		}
//		
//		public String getLastSet() {
//			return lastSet;
//		}
		
	}
	
}
