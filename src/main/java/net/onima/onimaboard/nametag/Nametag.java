package net.onima.onimaboard.nametag;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import net.onima.onimaboard.board.Board;
import net.onima.onimaboard.players.BoardPlayer;

public abstract class Nametag {

	protected NametagType type;
	protected String prefix, suffix;
	protected boolean displayGhost;
	protected Team team;
	
	public Nametag(NametagType type, String prefix, String suffix, boolean displayGhost) {
		this.type = type;
		this.prefix = prefix;
		this.suffix = suffix;
		this.displayGhost = displayGhost;
	}

	public abstract boolean isApplicable(Player receiver, BoardPlayer viewer);
	
	public void show(Collection<Player> receivers) {
		for (Player player : receivers)
			team.addPlayer(player);
	}
	
	public void show(Player receiver) {
		show(Arrays.asList(receiver));
	}
	
	public void initInScoreboard(Scoreboard scoreboard) {
		team = scoreboard.registerNewTeam(type.name());
		
		if (prefix != null)
			team.setPrefix(prefix);
		
		if (suffix != null)
			team.setSuffix(suffix);
		
		team.setCanSeeFriendlyInvisibles(displayGhost);
	}

	public NametagType getType() {
		return type;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public boolean isDisplayGhost() {
		return displayGhost;
	}
	
	public static List<Nametag> getByPriority(Board board, boolean includeDisguised) {
		LinkedList<Nametag> list = new LinkedList<>();
		
		list.add(board.getNametag(NametagType.ARCHER_TAG_1));
		list.add(board.getNametag(NametagType.ARCHER_TAG_2));
		list.add(board.getNametag(NametagType.FOCUS));
		list.add(board.getNametag(NametagType.ENEMY));
		list.add(board.getNametag(NametagType.FACTION));
		list.add(board.getNametag(NametagType.ALLY));
		
		if (includeDisguised) {
			list.addFirst(board.getNametag(NametagType.DISGUISED_TAG_1));
			list.addFirst(board.getNametag(NametagType.DISGUISED_TAG_2));
			list.addFirst(board.getNametag(NametagType.DISGUISED_FOC));
			list.addFirst(board.getNametag(NametagType.DISGUISED_ENE));
			list.addFirst(board.getNametag(NametagType.DISGUISED_FAC));
			list.addFirst(board.getNametag(NametagType.DISGUISED_ALL));
		}
		
		return list;
	}
	
}
