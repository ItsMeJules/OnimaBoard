package net.onima.onimaboard.nametag.type.staff;

import org.bukkit.entity.Player;

import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaboard.board.Board;
import net.onima.onimaboard.nametag.Nametag;
import net.onima.onimaboard.nametag.NametagType;
import net.onima.onimaboard.nametag.StaffNametag;
import net.onima.onimaboard.players.BoardPlayer;
import net.onima.onimafaction.faction.struct.Relation;

public class DisguiseEnemyNametag extends StaffNametag {

	public DisguiseEnemyNametag() {
		super(NametagType.DISGUISED_ENE, "§e[Disguise]" + Relation.ENEMY.getColor().toString() + ' ', null, false);
	}

	@Override
	public Nametag applicableNametag(Board board) {
		return board.getNametag(NametagType.ENEMY);
	}

	@Override
	public boolean isApplicable(Player receiver, BoardPlayer viewer) {
		return APIPlayer.getPlayer(receiver).getDisguiseManager().isDisguised() 
				&& OnimaPerm.ONIMAAPI_DISGUISE_COMMAND_LIST.has(viewer.getApiPlayer().toPlayer())
				&& applicableNametag(viewer.getBoard()).isApplicable(receiver, viewer);
	}

}
