package me.lpk.hijack.match;

/**
 * A matcher that returns true for all classes in the default package.
 */
public class DefaultPackageMatcher extends AbstractMatcher<String> {
	@Override
	public boolean isMatch(String t) {
		return !t.contains("/");
	}
}
