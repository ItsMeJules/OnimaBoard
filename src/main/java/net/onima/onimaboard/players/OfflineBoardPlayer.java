package net.onima.onimaboard.players;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import net.onima.onimaapi.OnimaAPI;
import net.onima.onimaapi.caching.UUIDCache;
import net.onima.onimaapi.event.mongo.AbstractPlayerLoadEvent;
import net.onima.onimaapi.mongo.api.result.MongoQueryResult;
import net.onima.onimaapi.mongo.saver.NoSQLSaver;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.utils.callbacks.VoidCallback;
import net.onima.onimafaction.players.OfflineFPlayer;

public class OfflineBoardPlayer implements NoSQLSaver {

	private static Map<UUID, OfflineBoardPlayer> offlinePlayers;
	
	static {
		offlinePlayers = new HashMap<>();
	}
	
	private OfflineAPIPlayer offlineApiPlayer;
	private OfflineFPlayer offlineFPlayer;
	private boolean boardToggled, staffBoard, firstJoined;
	
	{
		boardToggled = true;
	}
	
	public OfflineBoardPlayer(OfflineAPIPlayer offlineApiPlayer) {
		this.offlineApiPlayer = offlineApiPlayer;
		
		offlineFPlayer = OfflineFPlayer.getOfflineFPlayers().get(offlineApiPlayer.getUUID());
		
		if (this instanceof BoardPlayer)
			transferInstance(offlineApiPlayer.getUUID());
		
		OfflineBoardPlayer ofb = offlinePlayers.get(offlineApiPlayer.getUUID());
			
		if (OnimaAPI.getSavers().contains(ofb))
			OnimaAPI.getSavers().remove(ofb);
		
		offlinePlayers.put(offlineApiPlayer.getUUID(), this);
		OnimaAPI.getSavers().add(this);
	}
	
	private void transferInstance(UUID uuid) {
		getPlayer(uuid, (old) -> {
			if (old == null)
				return;
			
			boardToggled = old.boardToggled;
		});
	}
	

	public OfflineAPIPlayer getOfflineApiPlayer() {
		return offlineApiPlayer;
	}

	public OfflineFPlayer getOfflineFPlayer() {
		return offlineFPlayer;
	}
	
	public void toggleBoard(boolean toggle) {
		boardToggled = toggle;
	}
	
	public boolean hasBoardToggled() {
		return boardToggled;
	}
	
	public void setStaffBoard(boolean staffBoard) {
		this.staffBoard = staffBoard;
	}
	
	public boolean hasStaffBoard() {
		return staffBoard;
	}
	
	public void setFirstJoin(boolean firstJoined) {
		this.firstJoined = firstJoined;
	}
	
	public boolean isFirstJoin() {
		return firstJoined;
	}
	
	@Override
	public void save() {
		offlinePlayers.put(offlineApiPlayer.getUUID(), this);
		OnimaAPI.getSavers().add(this);
	}

	@Override
	public void remove() {
		offlinePlayers.remove(offlineApiPlayer.getUUID());
		OnimaAPI.getSavers().remove(this);
	}

	@Override
	public boolean isSaved() {
		return offlinePlayers.containsKey(offlineApiPlayer.getUUID()) && OnimaAPI.getSavers().contains(this);
	}

	@Override
	public void queryDatabase(MongoQueryResult result) {
		Document document = result.getValue("player", Document.class);
		
		staffBoard = document.getBoolean("staff_board");
		boardToggled = document.getBoolean("scoreboard");
	}
	
	@Deprecated
	@Override
	public Document getDocument(Object... objects) {return null;}
	
	@Deprecated
	@Override
	public boolean shouldDelete() {return false;}
	
	public static void getPlayer(UUID uuid, VoidCallback<OfflineBoardPlayer> callback) {
		if (!offlinePlayers.containsKey(uuid)) {
			AbstractPlayerLoadEvent event = new AbstractPlayerLoadEvent(uuid) {
				
				@Override
				public void done() {
					callback.call(offlinePlayers.get(uuid));
				}
			};
			
			Bukkit.getPluginManager().callEvent(event);
		} else
			callback.call(offlinePlayers.get(uuid));
	}
	
	
	public static void getPlayer(String name, VoidCallback<OfflineBoardPlayer> callback) {
		OfflinePlayer offline = Bukkit.getOfflinePlayer(UUIDCache.getUUID(name));

		if (offline.hasPlayedBefore())
			getPlayer(offline.getUniqueId(), callback);
	}
	
	public static void getPlayer(OfflinePlayer offline, VoidCallback<OfflineBoardPlayer> callback) {
		getPlayer(offline.getUniqueId(), callback);
	}
	
	public static Map<UUID, OfflineBoardPlayer> getOfflineBoardPlayers() {
		return offlinePlayers;
	}
	
	public static Collection<OfflineBoardPlayer> getDisconnectedBoardPlayers() {
		return offlinePlayers.values();
	}

}
