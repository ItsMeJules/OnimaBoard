package net.onima.onimaboard.board.utils;

import net.onima.onimaboard.board.Board;
import net.onima.onimaboard.players.BoardPlayer;

/**
 * This class is the super class of every scoreboard template.
 * It's useful to fill up the board, though with this the player can choose the scoreboard he likes.
 */
public interface ScoreboardTemplate {
	
	/**
	 * This method returns the scoreboard title.
	 * 
	 * @return The scoreboard title.
	 */
	public String getTitle();
	
	/**
	 * This method fills the board in the given parameter.
	 * 
	 * @param boardPlayer - BoardPlayer to send the board.
	 * @param board - Board to fill.
	 */
	public void fill(BoardPlayer boardPlayer, Board board);
	
	/**
	 * This method returns the board type of this template.
	 * 
	 * @return - The {@link BoardType} related to this template.
	 */
	public BoardType getType();
	
}
