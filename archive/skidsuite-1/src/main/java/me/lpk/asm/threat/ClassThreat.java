package me.lpk.asm.threat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.lpk.asm.threat.types.Threat;
import me.lpk.gui.tabs.AnalyzeTab;

public class ClassThreat {
	private Map<EnumThreatType, List<Threat>> threats = new HashMap<EnumThreatType, List<Threat>>();
	private final String className;

	public ClassThreat(String className) {
		this.className = className;
	}

	public List<Threat> getTheatListByType(EnumThreatType type) {
		return threats.get(type);
	}

	public Set<EnumThreatType> getThreatsKeys() {
		return threats.keySet();
	}

	public String getClassName() {
		return className;
	}

	public void addThreat(EnumThreatType type, Threat threat) {
		if (AnalyzeTab.skip(type)) {
			return;
		}
		if (threats.get(type) == null) {
			List<Threat> newList = new ArrayList<Threat>();
			newList.add(threat);
			threats.put(type, newList);
		} else {
			threats.get(type).add(threat);
		}
	}
}
