package net.onima.onimaboard.nametag.type.staff;

import org.bukkit.entity.Player;

import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaboard.board.Board;
import net.onima.onimaboard.nametag.Nametag;
import net.onima.onimaboard.nametag.NametagType;
import net.onima.onimaboard.nametag.StaffNametag;
import net.onima.onimaboard.players.BoardPlayer;

public class DisguiseArcherTagNametag1 extends StaffNametag {

	public DisguiseArcherTagNametag1() {
		super(NametagType.DISGUISED_TAG_1, "§e[Disguise]§6 ", null, false);
	}

	@Override
	public Nametag applicableNametag(Board board) {
		return board.getNametag(NametagType.ARCHER_TAG_1);
	}

	@Override
	public boolean isApplicable(Player receiver, BoardPlayer viewer) {
		return APIPlayer.getPlayer(receiver).getDisguiseManager().isDisguised() 
				&& OnimaPerm.ONIMAAPI_DISGUISE_COMMAND_LIST.has(viewer.getApiPlayer().toPlayer())
				&& applicableNametag(viewer.getBoard()).isApplicable(receiver, viewer);
	}

}
