package net.onima.onimaboard.tab.template;

import org.bukkit.entity.Player;

import net.onima.onimaapi.utils.Methods;
import net.onima.onimaboard.tab.Tab;
import net.onima.onimaboard.tab.utils.TabTemplate;
import net.onima.onimaboard.tab.utils.TabType;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Relation;
import net.onima.onimafaction.players.FPlayer;

public class DefaultTemplate implements TabTemplate {

	@Override
	public void fill(Tab tab) {
		Player player = tab.getPlayer();
		int x = 0, y = 0;
		
		for (Player online : Methods.getOnlinePlayers(player)) {
			if (!player.canSee(online))
				continue;
			
			FPlayer fOnline = FPlayer.getPlayer(online);
			
			tab.set(x, y, getDisplayColor(FPlayer.getPlayer(player), fOnline) + fOnline.getApiPlayer().getName());
			
			if (x >= 3) {
				x = 0;
				y++;
			} else
				x++;
			
			if (y >= 20)
				return;
		}
	}

	@Override
	public TabType getTabType() {
		return TabType.DEFAULT;
	}
	
	private String getDisplayColor(FPlayer player, FPlayer player2) {
		PlayerFaction faction = player.getFaction(), faction2 = player2.getFaction();
		
		if (faction == null)
			return Relation.ENEMY.getColor().toString();
		else {
			if (faction.getFocused() != null && faction.getFocused().getUniqueId().equals(player2.getApiPlayer().getUUID()))
				return "§d";
			else
				return faction.getRelation(faction2).getColor().toString();
		}
	}

}
