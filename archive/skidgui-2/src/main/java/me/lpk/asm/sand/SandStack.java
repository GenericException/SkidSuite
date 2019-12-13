package me.lpk.asm.sand;

import java.util.Stack;

public class SandStack extends Stack<StackObject> {
	private static final long serialVersionUID = 1242342L;

	public void pushOn(Object object) {
		StackObject so = new StackObject(object);
		super.push(so);
	}
}
