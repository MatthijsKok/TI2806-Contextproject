package sun.misc;

/**
 * Such a hack!
 *
 * Android is all messed up with Java 8, and versions of grade and
 * of android studio.
 * Without this code, Jack generates:
 * <code>Error:(97, 57) The type sun.misc.Unsafe cannot be found in source files, imported jack libs or
 * the classpath</code>
 *
 * This is a known issue at Google but they won't fix it because Jack is deprecated
 * in favor of an unreleased Preview of Android Studio. However, the preview
 * gives even more issues.
 */

@SuppressWarnings("unused")
public class Unsafe {
}
