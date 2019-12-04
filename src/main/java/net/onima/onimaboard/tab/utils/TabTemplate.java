package net.onima.onimaboard.tab.utils;

import net.onima.onimaboard.tab.Tab;

public interface TabTemplate {
	
	public void fill(Tab tab);
	public TabType getTabType();
	
}
