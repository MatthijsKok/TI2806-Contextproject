package nl.tudelft.ewi.ds.bankchain;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

/**
 * Static tools
 *
 * @author Jos Kuijpers
 */
public class Tools {

    private Tools(){
    }

    /**
     * Run a piece of code on the main thread.
     *
     * GUI code can't be run on another thread than the main thread.
     * The Future system runs all code on other threads, unless you call
     * <code>future.get()</code> on the main thread, but that would then
     * blocking.
     *
     * Instead, run this function after <code>future.thenAccept</code> or
     * the like, and give a lambda with the code to run in the main thread.
     *
     * Example:
     * <code>
     * Tools.runOnMainThread(() -> {
     *     Toast.makeText(getApplicationContext(), "Created session!", Toast.LENGTH_LONG).show();
     * });
     * </code>
     *
     * @param runnable
     */
    public static void runOnMainThread(@NonNull Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(runnable);
    }
}
