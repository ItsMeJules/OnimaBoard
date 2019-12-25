package net.onima.onimaboard.nametag.type;

import org.bukkit.entity.Player;

import net.onima.onimaboard.nametag.Nametag;
import net.onima.onimaboard.nametag.NametagType;
import net.onima.onimaboard.players.BoardPlayer;
import net.onima.onimafaction.players.FPlayer;

public class ArcherTagNametag1 extends Nametag  {

	public ArcherTagNametag1() {
		super(NametagType.ARCHER_TAG_1, "§6", null, false);
	}

	@Override
	public boolean isApplicable(Player receiver, BoardPlayer viewer) {
		return FPlayer.getPlayer(receiver).getArcherTag() == 1;
	}

}
