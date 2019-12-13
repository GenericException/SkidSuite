package me.lpk.threat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.threat.handlers.ClassHandler;
import me.lpk.threat.handlers.MethodHandler;
import me.lpk.threat.result.ThreatResult;

/**
 * Handles the scanning process of a given class node.
 */
public class ThreatScanner {
	private final Map<String, List<ThreatResult>> threatsByClass = new HashMap<String, List<ThreatResult>>();
	private final List<ClassHandler> classHandlers = new ArrayList<ClassHandler>();
	private final List<MethodHandler> methodHandlers = new ArrayList<MethodHandler>();

	/**
	 * Scans a class node.
	 * 
	 * @param cn
	 */
	public void scan(ClassNode cn) {
		List<ThreatResult> threats = new ArrayList<ThreatResult>();
		for (ClassHandler classHandler : classHandlers) {
			ThreatResult result = classHandler.scanClass(cn);
			if (result != null) {
				threats.add(result);
			}
		}
		for (MethodNode mn : cn.methods) {
			for (MethodHandler methodHandler : methodHandlers) {
				ThreatResult result = methodHandler.scanMethod(mn);
				if (result != null) {
					threats.add(result);
				}
			}
		}
		if (threats.size() > 0) {
			threatsByClass.put(cn.name, threats);
		}
	}
	
	public void reset(){
		threatsByClass.clear();
		classHandlers.clear();
		methodHandlers.clear();
	}

	/**
	 * Registers a threat handler.
	 * 
	 * @param handler
	 */
	public void registerClassHandler(ClassHandler handler) {
		classHandlers.add(handler);
	}

	/**
	 * Registers a threat handler.
	 * 
	 * @param handler
	 */
	public void registerMethodHandler(MethodHandler handler) {
		methodHandlers.add(handler);
	}

	/**
	 * Outputs all scanned objects as an HTML string.
	 * 
	 * @return
	 */
	public String toHTML(String name, boolean includeCss) {
		String jarName =  name;
		String css = 
				"*{font-family:Arial,sans-serif; overflow: auto;}"
				+ "h3 {margin-bottom: 0px; font-weight: bold; text-decoration: underline;}" 
				+ "table{width=90%;} th, td{border:1px solid black;}"
				+ ".centTxt{text-align: center;}" 
				+ ".shaded{background: #afd0db;}"
				+ ".tmain{margin-left:auto;margin-right:auto; }";
		StringBuilder tableEntries = new StringBuilder("<table class=\"tmain\">");
		for (String className : threatsByClass.keySet()) {
			tableEntries.append("<th colspan=\"2\" class=\"shaded\"><h3>" + className + "</h3></th>");
			for (ThreatResult tr : threatsByClass.get(className)) {
				tableEntries.append(tr.toHTMLTableRow());
			}
		}
		tableEntries.append("</table>");
		StringBuilder content = new StringBuilder();
		content.append("<html><head><meta charset=\"utf-8\">" );
		if (includeCss){
			content.append("<style>" + css + "</style>");
		}
		content.append("</head><body>");
		content.append("<h1>Analysis of: " + jarName + "</h1><hr><hr>");
		// TODO: Table of contents
		// * Link to each class
		// * Link to statistics
		//
		// TODO: Statistics
		// Top 10: Most detections per class (and/or package?)
		// Most common detection type
		//
		// TODO: NavBar at bottom of page
		// * Back to Top
		// * Back to Statistics
		content.append(tableEntries.toString());
		content.append("</body></html>");
		return content.toString().replace("<init>", "&lt;init&gt;").replace("<clinit>", "&lt;clinit&gt;");
	}
}
