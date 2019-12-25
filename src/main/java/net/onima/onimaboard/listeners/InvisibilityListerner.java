package net.onima.onimaboard.listeners;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import net.minecraft.server.v1_7_R4.DataWatcher;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import net.onima.onimaapi.event.PlayerShowInvisibleEvent;
import net.onima.onimaboard.utils.InvisibilityHandler;

public class InvisibilityListerner implements Listener {
	
	@EventHandler
	public void onPlayerShow(PlayerShowInvisibleEvent event) {
		Player clicker = event.getClicker();
		PlayerConnection connection = ((CraftPlayer) clicker).getHandle().playerConnection;
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (!online.hasPotionEffect(PotionEffectType.INVISIBILITY) || clicker.getUniqueId().equals(online.getUniqueId()))
				continue;
		
			DataWatcher watcher = ((CraftPlayer) online).getHandle().getDataWatcher();
			byte metadata = watcher.getByte(0);
			metadata = (byte) (((metadata & InvisibilityHandler.INVISIBILITY_MASK) == InvisibilityHandler.INVISIBILITY_MASK) ? metadata & ~(1 << 5) : metadata | 1 << 5);
			
			watcher.watch(0, metadata);
					
			connection.sendPacket(new PacketPlayOutEntityMetadata(online.getEntityId(), watcher, false));
		}
	}
	
}
