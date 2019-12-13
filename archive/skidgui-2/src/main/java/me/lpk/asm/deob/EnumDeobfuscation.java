package me.lpk.asm.deob;

import org.objectweb.asm.tree.ClassNode;

import me.lpk.asm.AbstractMethodTransformer;
import me.lpk.asm.deob.method.*;

public enum EnumDeobfuscation {
	Allatori, DashO, SimpleStrings, Stringer, ZKM;

	public AbstractMethodTransformer getMethodTransformer(DeobfuscationVisitor dv, ClassNode node) {
		switch (this) {
		case Allatori:
			return new AllatoriMethodTransformer(node);
		case DashO:
			return new DashMethodTransformer(dv, node);
		case SimpleStrings:
			return new SimpleStringTransformer(node);
		case Stringer:
			return new StringerMethodTransformer(node);
		case ZKM:
			return new ZKMMethodTransformer(node);
		default:
			break;
		}
		return null;
	}
}
