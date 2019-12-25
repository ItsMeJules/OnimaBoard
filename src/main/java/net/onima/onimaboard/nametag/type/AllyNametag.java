package net.onima.onimaboard.nametag.type;

import org.bukkit.entity.Player;

import net.onima.onimaboard.nametag.Nametag;
import net.onima.onimaboard.nametag.NametagType;
import net.onima.onimaboard.players.BoardPlayer;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Relation;
import net.onima.onimafaction.players.FPlayer;

public class AllyNametag extends Nametag {

	public AllyNametag() {
		super(NametagType.ALLY, Relation.ALLY.getColor().toString(), null, false);
	}

	@Override
	public boolean isApplicable(Player receiver, BoardPlayer viewer) {
		PlayerFaction viewerFaction = viewer.getFPlayer().getFaction();
		PlayerFaction receiverFaction = FPlayer.getPlayer(receiver).getFaction();
			
		return viewerFaction != null && receiverFaction != null && viewerFaction.getRelation(receiverFaction) == Relation.ALLY;
	}

}
