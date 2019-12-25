package net.onima.onimaboard.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.onima.onimaboard.players.BoardPlayer;

public class PreKeepVisbileEvent extends Event implements Cancellable {
	
	private static HandlerList handlers = new HandlerList();
	
	private BoardPlayer invisiblePlayer;
	private Player viewer;
	private boolean cancelled;
	
	public PreKeepVisbileEvent(BoardPlayer invisiblePlayer, Player viewer) {
		this.invisiblePlayer = invisiblePlayer;
		this.viewer = viewer;
	}
	
	public BoardPlayer getInvisiblePlayer() {
		return invisiblePlayer;
	}

	public Player getViewer() {
		return viewer;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
