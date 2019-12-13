package me.lpk.asm.sand;

public class StackObject {
	private Object o;

	public StackObject(Object o) {
		this.o = o;
	}

	public Object get() {
		return o;
	}

	public boolean iequals(StackObject o) {
		return iequals(((Number) o.get()).intValue());
	}

	public boolean iequals(int i) {
		try {
			return Integer.parseInt((String) this.o) == i;
		} catch (Exception e) {
			return false;
		}
	}

	public int iadd(StackObject o) {
		return iadd(((Number) o.get()).intValue());
	}

	public int iadd(int i) {
		try {
			return Integer.parseInt((String) this.o) + i;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int isub(StackObject o) {
		return isub(((Number) o.get()).intValue());	}

	public int isub(int i) {
		try {
			return Integer.parseInt((String) this.o) - i;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int bitAnd(StackObject o) {
		return bitAnd(((Number) o.get()).intValue());	}

	public int bitAnd(int i) {
		try {
			return Integer.parseInt((String) this.o) - i;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int idivide(StackObject o) {
		return idivide(((Number) o.get()).intValue());	}

	public int idivide(int i) {
		try {
			return Integer.parseInt((String) this.o) * i;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int imultiply(StackObject o) {
		return imultiply(((Number) o.get()).intValue());	}

	public int imultiply(int i) {
		try {
			return Integer.parseInt((String) this.o) * i;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int iand(StackObject o) {
		return iand(((Number) o.get()).intValue());	}

	public int iand(int i) {
		try {
			return Integer.parseInt((String) this.o) & i;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int ishl(StackObject o) {
		return ishl(((Number) o.get()).intValue());	}

	public int ishl(int i) {
		try {
			return Integer.parseInt((String) this.o) << i;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int ishr(StackObject o) {
		return ishr(((Number) o.get()).intValue());	}

	public int ishr(int i) {
		try {
			return Integer.parseInt((String) this.o) >> i;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int iushr(StackObject o) {
		return iushr(((Number) o.get()).intValue());	}

	public int iushr(int i) {
		try {
			return Integer.parseInt((String) this.o) >>> i;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int irem(StackObject o) {
		return irem(((Number) o.get()).intValue());	}

	public int irem(int i) {
		try {
			return Integer.parseInt((String) this.o) % i;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int ior(StackObject o) {
		return ior(((Number) o.get()).intValue());	}

	public int ior(int i) {
		try {
			return Integer.parseInt((String) this.o) | i;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int ixor(StackObject o) {
		return ixor(((Number) o.get()).intValue());	}

	public int ixor(int i) {
		try {
			return Integer.parseInt((String) this.o) ^ i;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
