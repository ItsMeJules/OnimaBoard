package net.onima.onimaboard.nametag.type;

import org.bukkit.entity.Player;

import net.onima.onimaboard.nametag.Nametag;
import net.onima.onimaboard.nametag.NametagType;
import net.onima.onimaboard.players.BoardPlayer;
import net.onima.onimafaction.players.FPlayer;

public class ArcherTagNametag2 extends Nametag  {

	public ArcherTagNametag2() {
		super(NametagType.ARCHER_TAG_2, "§c", null, false);
	}

	@Override
	public boolean isApplicable(Player receiver, BoardPlayer viewer) {
		return FPlayer.getPlayer(receiver).getArcherTag() == 2;
	}
	
}
