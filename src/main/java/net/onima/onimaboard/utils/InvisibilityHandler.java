package net.onima.onimaboard.utils;

import java.util.List;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.players.utils.PlayerOption;
import net.onima.onimaapi.rank.RankType;
import net.onima.onimaboard.OnimaBoard;

public class InvisibilityHandler {
	
	public static final int INVISIBILITY_MASK = 0x20;
	
	public static void hook(OnimaBoard plugin) {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.ENTITY_METADATA) {
			@Override
			public void onPacketSending(PacketEvent event) {
				Player receiver = event.getPlayer();
				
				if (RankType.getRank(receiver).getValue() < 10 || !APIPlayer.getPlayer(receiver).getOptions().getBoolean(PlayerOption.GlobalOptions.SHOW_INVISIBLE_PLAYERS))
					return;
				
				StructureModifier<List<WrappedWatchableObject>> modifier = event.getPacket().getWatchableCollectionModifier();
				List<WrappedWatchableObject> objects = modifier.read(0);
				
				if (objects == null || objects.isEmpty())
					return;
				
				for (WrappedWatchableObject wwo : objects) {
					if (wwo.getIndex() == 0) {
						if (!(wwo.getValue() instanceof Byte))
							return;
						
						byte metadata = (byte) wwo.getValue();
						
						if ((metadata & INVISIBILITY_MASK) == INVISIBILITY_MASK) {
							int id = event.getPacket().getIntegers().read(0);
							
							if (receiver.getEntityId() == id)
								return;
							
							metadata &= ~(1 << 5);
							
							wwo.setValue(metadata);
//							boardPlayer.getBoard().setInvisibleName(true);
						}
					}
				}
			}
		
		});
	}

}
