
package me.lpk.hijack;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation dictating the true name of a class, field, or method. Source names
 * will be replaced with the true name by a ShellReplacementModder at runtime.
 * <br>
 * Example:
 * 
 * <pre>
 * &#64;RemappedName(name = "com/example/RealClassName")
 * public class FakeClassName {
 * 
 * 	&#64;RemappedName(name = "realFieldName")
 * 	public int fakeFieldName;
 * 
 * 	&#64;RemappedName(name = "realMethodName")
 * 	public void fakeMethodName(OtherFakeClassName parametersDontNeedAnnotations) {
 * 	}
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RemappedName {
	public String name();
}