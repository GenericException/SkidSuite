package me.lpk.hijack.match;

/**
 * A matcher that returns true for everything.
 */
public class EverythingMatcher extends AbstractMatcher<String> {
	@Override
	public boolean isMatch(String t) {
		return true;
	}
}
