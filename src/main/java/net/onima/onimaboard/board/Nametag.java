package net.onima.onimaboard.board;

import net.onima.onimafaction.faction.struct.Relation;

public enum Nametag {
	
	FACTION,
	ALLY,
	ENEMY,
	FOCUS,
	ARCHER_TAG_1,
	ARCHER_TAG_2,
	DISGUISED_ADMIN;
	
	public static Nametag fromRelation(Relation relation) {
		switch (relation) {
		case ALLY:
			return Nametag.ALLY;
		case ENEMY:
			return Nametag.ENEMY;
		default:
			return null;
		}
	}
}
