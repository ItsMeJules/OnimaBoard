package net.onima.onimaboard.nametag.type;

import java.util.Collection;

import org.bukkit.entity.Player;

import net.onima.onimaboard.nametag.Nametag;
import net.onima.onimaboard.nametag.NametagType;
import net.onima.onimaboard.players.BoardPlayer;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Relation;
import net.onima.onimafaction.players.FPlayer;

public class FactionNametag extends Nametag {

	private Player player;
	
	public FactionNametag(Player player) {
		super(NametagType.FACTION, Relation.MEMBER.getColor().toString(), null, true);
		
		this.player = player;
	}

	@Override
	public boolean isApplicable(Player receiver, BoardPlayer viewer) {
		PlayerFaction viewerFaction = viewer.getFPlayer().getFaction();
		PlayerFaction receiverFaction = FPlayer.getPlayer(receiver).getFaction();
			
		return viewerFaction != null && receiverFaction != null && viewerFaction.getRelation(receiverFaction) == Relation.MEMBER;
	}

	@Override
	public void show(Collection<Player> receivers) {
		if (!team.getPlayers().contains(player))
			team.addPlayer(player);

		super.show(receivers);
	}

}
