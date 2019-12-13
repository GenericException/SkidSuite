package me.lpk.hijack.match;

/**
 * A matcher that returns true if a given name starts with a defined package.
 */
public class PackageMatcher extends AbstractMatcher<String> {
	private final String pkg;

	public PackageMatcher(String pkg) {
		this.pkg = pkg;
	}

	@Override
	public boolean isMatch(String t) {
		return t.startsWith(pkg);
	}
}
