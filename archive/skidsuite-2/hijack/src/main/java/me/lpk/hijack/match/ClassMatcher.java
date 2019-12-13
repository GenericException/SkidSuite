package me.lpk.hijack.match;

/**
 * A matcher that returns true if a given name is an exact match.
 */
public class ClassMatcher extends AbstractMatcher<String> {
	private final String name;

	public ClassMatcher(String name) {
		this.name = name;
	}

	@Override
	public boolean isMatch(String t) {
		return t.equals(name);
	}
}
