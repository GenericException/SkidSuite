package me.lpk.threat.result;

import java.util.List;

import org.objectweb.asm.tree.MethodNode;

/**
 * Holds the data of a given threat detection.
 */
public class ThreatResult {
	private final String name, description, location;

	public ThreatResult(String name, String description, String location) {
		this.name = name;
		this.description = description;
		this.location = location;
	}

	/**
	 * Creates a ThreatResult given data from a method.
	 * 
	 * @param type
	 * @param desc
	 * @param mn
	 * @param detections
	 * @return
	 */
	public static ThreatResult withData(String type, String desc, MethodNode mn, List<String> detections) {
		String detectionStr = "";
		for (String detection : detections) {
			detectionStr += detection + "<br>";
		}
		return new ThreatResult(type, desc, "<b>" + mn.name + "()</b>:<br>Detections:<br><br>" + detectionStr);
	}

	/**
	 * Creates a ThreatResult given data.
	 * 
	 * @param type
	 * @param desc
	 * @param detections
	 * @return
	 */
	public static ThreatResult withData(String type, String desc, List<String> detections) {
		String detectionStr = "";
		for (String detection : detections) {
			detectionStr += detection + "<br>";
		}
		return new ThreatResult(type, desc, "<br>Detections:<br><br>" + detectionStr);
	}

	public String toHTMLTableRow() {
		return "<tr><td><u><b>" + getName() + "</b></u>:<br>" + getDescription() + "</td><td>" + getLocation() + "</td></tr>";
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getLocation() {
		return location;
	}
}
