package net.onima.onimaboard.nametag.type;

import org.bukkit.entity.Player;

import net.onima.onimaboard.nametag.Nametag;
import net.onima.onimaboard.nametag.NametagType;
import net.onima.onimaboard.players.BoardPlayer;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Relation;
import net.onima.onimafaction.players.FPlayer;

public class EnemyNametag extends Nametag {

	public EnemyNametag() {
		super(NametagType.ENEMY, Relation.ENEMY.getColor().toString(), null, false);
	}
	
	@Override
	public boolean isApplicable(Player receiver, BoardPlayer viewer) {
		PlayerFaction viewerFaction = viewer.getFPlayer().getFaction();
		PlayerFaction receiverFaction = FPlayer.getPlayer(receiver).getFaction();
		
		return viewerFaction == null || receiverFaction == null || viewerFaction.getRelation(receiverFaction) == Relation.ENEMY;
	}

}
