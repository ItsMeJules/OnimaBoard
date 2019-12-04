package net.onima.onimaboard.tab;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.onima.onimaboard.tab.template.DefaultTemplate;
import net.onima.onimaboard.tab.utils.TabTemplate;
import net.onima.onimaboard.tab.utils.TabUtils;

/** Thanks to bizarrealex
 * https://github.com/bizarre/azazel (updated a lot)
 */
public class Tab {

	private Player player;
	private Scoreboard scoreboard;
    private Map<TabEntry, String> entries;
    private TabTemplate template;
    private PlayerConnection connection;
    
    {
    	entries = new ConcurrentHashMap<>();
    }
    
    public Tab(Player player, TabTemplate template) {
		this.player = player;
		this.template = template;
		scoreboard = player.getScoreboard();
		connection = ((CraftPlayer) player).getHandle().playerConnection;
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			 ((CraftPlayer) online).getHandle().playerConnection.sendPacket(PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer)player).getHandle()));
			 connection.sendPacket(PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer)online).getHandle()));
		}
		
		initialize(player);
	}
	
	public Tab(Player player) {
		this(player, new DefaultTemplate());
	}
	    
    private void initialize(Player player) {
    	for (int i = 0; i < 60; i++) {
    		int x = i % 3;
    		int y = i / 3;
    		String key = getNextBlank();
                
    		entries.put(new TabEntry(x, y, key), key);
    		connection.sendPacket(getPlayerPacket(key));

    		Team team = scoreboard.getTeam(key);

    		if (team == null) team = scoreboard.registerNewTeam(key);

    		team.addEntry(key);
    	}
    }
    
    private String getNextBlank() {
        outer: for(String blank : TabUtils.getBlanks()) {

            for (String identifier : entries.values()) {
                if (identifier.equals(blank))
                    continue outer;
            }
            
            return blank;
        }
        return null;
    }
    
    private static Packet getPlayerPacket(String name) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();

        Field action, username, player;
        
        try {
            action = PacketPlayOutPlayerInfo.class.getDeclaredField("action");
            username = PacketPlayOutPlayerInfo.class.getDeclaredField("username");
            player = PacketPlayOutPlayerInfo.class.getDeclaredField("player");

            action.setAccessible(true);
            username.setAccessible(true);
            player.setAccessible(true);

            action.set(packet, 0);
            username.set(packet, name);
            player.set(packet, new GameProfile(UUID.randomUUID(), name));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        
        return packet;
    }
    
	public Set<TabEntry> getEntries() {
		return entries.keySet();
	}
	
	public TabTemplate getTemplate() {
		return template;
	} 
	
	public void setTemplate(TabTemplate template) {
		this.template = template;
	}
	
    public Team getByLocation(int x, int y) {
        for (TabEntry position : entries.keySet()) {
            if (position.getX() == x && position.getY() == y) {
            	return scoreboard.getTeam(position.getKey());
            }
        }
        return null;
    }
    
    public TabEntry getEntryByLocation(int x, int y) {
        for (TabEntry position : entries.keySet()) {
            if (position.getX() == x && position.getY() == y) {
            	return position;
            }
        }
        return null;
    }
    
    public void set(int x, int y, String text) {
    	String prefix, suffix;
    	
    	if (text.length() > 16) {
    		int splitAt = text.charAt(15) == ChatColor.COLOR_CHAR ? 15 : 16;
    		prefix = text.substring(0, splitAt);
    		String suffixTemp = ChatColor.getLastColors(prefix) + text.substring(splitAt);
    		suffix = (suffixTemp.substring(0, Math.min(suffixTemp.length(), 16)));
    	} else {
    		prefix = text;
    		suffix = "";
    	}
          
    	Team team = getByLocation(x, y);
    	
    	if (!team.getPrefix().equalsIgnoreCase(prefix)) team.setPrefix(prefix);
    	if (!team.getSuffix().equalsIgnoreCase(suffix)) team.setSuffix(suffix);
    }
    
    public void empty() {
		for (TabEntry position : entries.keySet()) {
			Team team = player.getScoreboard().getTeam(position.getKey());
                
			if (team != null) {
				if (team.getPrefix() != null && !team.getPrefix().isEmpty()) team.setPrefix("");
				if(team.getSuffix() != null && !team.getSuffix().isEmpty()) team.setSuffix("");
			}
		}
    }
    
	public Player getPlayer() {
		return player;
	}
    
	public static class TabEntry {
        private final int x, y;
        private final String key;

        public TabEntry(int x, int y, String key) {
            this.x = x;
            this.y = y;
            this.key = key;
        }
        
        public int getX() {
        	return x;
        }
        
        public int getY() {
        	return y;
        }
        
        public String getKey() {
        	return key;
        }
        
    }

}
