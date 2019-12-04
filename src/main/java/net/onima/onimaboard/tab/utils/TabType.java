package net.onima.onimaboard.tab.utils;

import net.onima.onimaboard.tab.template.DefaultTemplate;
import net.onima.onimaboard.tab.template.FactionFactionListTemplate;
import net.onima.onimaboard.tab.template.FactionServerInfoTemplate;
import net.onima.onimaboard.tab.template.NoFactionFactionListTemplate;
import net.onima.onimaboard.tab.template.NoFactionServerInfoTemplate;

public enum TabType {
	
	DEFAULT(new DefaultTemplate()),
	NO_FACTION_SERV_INFO(new NoFactionServerInfoTemplate()),
	NO_FACTION_FACTION_LIST(new NoFactionFactionListTemplate()),
	FACTION_SERV_INFO(new FactionServerInfoTemplate()),
	FACTION_FACTION_LIST(new FactionFactionListTemplate());
	
	private TabTemplate template;
	
	private TabType(TabTemplate template) {
		this.template = template;
	}
	
	public TabTemplate getTemplate() {
		return template;
	}
	
	public static TabType fromString(String str) {
		switch(str) {
		case "DEFAULT":
			return DEFAULT;
		case "NO_FACTION_SERV_INFO":
			return NO_FACTION_SERV_INFO;
		case "NO_FACTION_FACTION_LIST":
			return NO_FACTION_FACTION_LIST;
		case "FACTION_SERV_INFO":
			return FACTION_SERV_INFO;
		case "FACTION_FACTION_LIST":
			return FACTION_FACTION_LIST;
		default:
			return null;
		}
	}
	
}
