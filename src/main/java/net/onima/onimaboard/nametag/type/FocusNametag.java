package net.onima.onimaboard.nametag.type;

import org.bukkit.entity.Player;

import net.onima.onimaboard.nametag.Nametag;
import net.onima.onimaboard.nametag.NametagType;
import net.onima.onimaboard.players.BoardPlayer;
import net.onima.onimafaction.faction.PlayerFaction;

public class FocusNametag extends Nametag {

	public FocusNametag() {
		super(NametagType.FOCUS, "§d", null, false);
	}

	@Override
	public boolean isApplicable(Player receiver, BoardPlayer viewer) {
		PlayerFaction viewerFaction = viewer.getFPlayer().getFaction();
		
		if (viewerFaction == null)
			return false;
		
		return receiver.equals(viewerFaction.getFocused());
	}

}
