package me.lpk.asm.threat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.lpk.asm.threat.types.Threat;

public class JarThreat {
	private final String jarName;
	private final List<ClassThreat> classes;

	public JarThreat(String jarName) {
		this.jarName = jarName;
		classes = new ArrayList<ClassThreat>();
	}

	public void add(ClassThreat ct) {
		classes.add(ct);
	}

	public String getJarName() {
		return jarName;
	}

	public List<ClassThreat> getClasses() {
		return classes;
	}

	public String asHTML() throws IOException {
		int maxLenClassName = 70;
		int maxLenData = 200;
		String css = "*{font-family:Arial,sans-serif; overflow: auto;}.centTxt{text-align: center;} h3 {margin-bottom: 0px; font-weight: bold; text-decoration: underline;} table{width=90%;} th,td{border:1px solid black;} .detcon{width:100%; display: table; width: 100%; table-layout: fixed;}.detect{display:table-cell;} .tmain{margin-left:auto;margin-right:auto; }";
		StringBuilder tableTypes = new StringBuilder("<tr><td><h3>Class</h3></td><td><h3>Detection Type</h3></td><td><h3>Detections</h3></td></tr>");
		StringBuilder tableThreats = new StringBuilder("<tr><td><h3>Theat Name</h3></td><td><h3>Description</h3></td></tr>");
		for (EnumThreatType type : EnumThreatType.values()){
			tableThreats.append("<tr>");
			tableThreats.append("<td><b>"+type.getName()+"</b></td>");
			tableThreats.append("<td>"+type.getDesc() + "<br> Threat level:" + type.getPoints() + "</td>");
			tableThreats.append("</tr>");
		}
		for (ClassThreat ct : classes){
			for (EnumThreatType type : ct.getThreatsKeys()){			
				List<Threat> threats = ct.getTheatListByType(type);
				if (threats == null) continue;
				tableTypes
				.append("<tr>")
					.append("<td>").append(splitSize(ct.getClassName(), maxLenClassName)).append("</td>")
					.append("<td>").append("<b>" +type.getName() + "</b><br>" + type.getDesc() + "<br>").append("</td>")
					.append("<td>");
				for (Threat t : threats){
					StringBuilder threatTable = new StringBuilder("<table class=\"detcon\"><tr><td class=\"detect\"><b>Location</b></td><td class=\"detect\"><b>Data</b></td></tr>");
					threatTable.append("<tr>");
						threatTable.append("<td class=\"detect\">"  + splitSize(t.getLocation(), maxLenData) + "</td>");
						threatTable.append("<td class=\"detect\">"  + splitSize(t.getData(), maxLenData) + "</td>");
					threatTable.append("</tr></table>");
					tableTypes.append(threatTable.toString());
				}
				tableTypes.append("</td>").append("</tr>");
			}
		}
		StringBuilder ht = new StringBuilder();
		ht.append("<html><head><style>" + css + "</style></head>");
		ht.append("<body><h1>Analysis of: "+ jarName + "</h1><hr>");
		ht.append("<h2>Table of contents:</h2>");
		ht.append("<ul><li><p><a href=\"#detTable\">Detection Table</a></p><li><p><a href=\"#tTable\">Threat Table</a></p></ul><hr>");
		ht.append("<h1 class=\"centTxt\">Detection Table:</h1>");
		ht.append("<table class=\"tmain\">" + tableThreats.toString() + "</table>");
		ht.append("<hr>");
		ht.append("<table class=\"tmain\">" + tableTypes.toString() + "</table>");
		ht.append("<hr>");
		ht.append("</body></html>");
		return ht.toString().replace("<init>", "&lt;init&gt;").replace("<clinit>", "&lt;clinit&gt;");
	}

	private String splitSize(String input, int maxLen) {
		int len = 0;
		StringBuilder sb = new StringBuilder();
		for (char c : input.toCharArray()){
			len++;
			sb.append(c);
			if (len == maxLen){
				len = 0;
				sb.append(" ");
			}
		}
		return sb.toString();
	}

}
