package net.onima.onimaboard.tab.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class TabUtils {
	
	private static List<String> blanks;
	
	static {
		blanks = initBlanks();
	}
	
	public static void checkMaxPlayers() {
        if (Bukkit.getMaxPlayers() < 60)
        	throw new IllegalArgumentException("§cMax players must be at least 60");
	}
	
	public static List<String> getBlanks() {
		return blanks;
	}

	private static List<String> initBlanks() {
        List<String> toReturn = new ArrayList<>();

        for (ChatColor color : ChatColor.values()) {
            for (int i = 1; i < 4; i++)
                toReturn.add(StringUtils.repeat(color + "", 5 - i) + ChatColor.RESET);
        }

        return toReturn;
	}
	

}
