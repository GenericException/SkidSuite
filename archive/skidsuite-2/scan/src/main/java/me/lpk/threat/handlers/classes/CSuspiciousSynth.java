package me.lpk.threat.handlers.classes;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.threat.handlers.ClassHandler;

import me.lpk.threat.result.ThreatResult;
import me.lpk.util.AccessHelper;

public class CSuspiciousSynth extends ClassHandler {
	private boolean ignoreDollas = true;
	private double threshhold = 0.55;

	@Override
	public ThreatResult scanClass(ClassNode cn) {
		int synthFields = 0, synthMethods = 0, totalFields = 0, totalMethods = 0;
		// Scan for synthetic fields
		for (FieldNode fn : cn.fields) {
			if (fn == null) {
				continue;
			}
			totalFields++;
			if (AccessHelper.isSynthetic(fn.access) && (ignoreDollas ? !fn.name.contains("$") : true)) {
				synthFields++;
			}
		}
		// Scan methods for unnatural synthetic tag occurrences.
		for (MethodNode mn : cn.methods) {
			totalMethods++;
			if (AccessHelper.isSynthetic(mn.access) && (ignoreDollas ? !mn.name.contains("$") : true)) {
				// Discount <init> and <clinit> from calculations. Shouldn't be
				// sythetic anyways...
				if (mn.name.contains("<")) {
					continue;
				}
				synthMethods++;
			}
		}
		double synthFieldPercent = totalFields == 0 ? 0 : synthFields / totalFields;
		double synthMethodPercent = totalMethods == 0 ? 0 : synthMethods / totalMethods;
		if ((synthFieldPercent + synthMethodPercent) / 2 > threshhold) {
			return new ThreatResult(getName(), getDesc(), cn.name);

		}
		return null;
	}

	@Override
	public String getName() {
		return "Unnatural Synthetics";
	}

	@Override
	public String getDesc() {
		return "The class seems to be modified forcing an unnatrual amount of members to be synthetic.<br>Known tactic for anti-reverse engineering.";
	}

}
