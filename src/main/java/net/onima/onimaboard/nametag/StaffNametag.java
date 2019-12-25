package net.onima.onimaboard.nametag;

import net.onima.onimaboard.board.Board;

public abstract class StaffNametag extends Nametag {

	public StaffNametag(NametagType type, String prefix, String suffix, boolean displayGhost) {
		super(type, prefix, suffix, displayGhost);
	}
	
	public abstract Nametag applicableNametag(Board board);

}
