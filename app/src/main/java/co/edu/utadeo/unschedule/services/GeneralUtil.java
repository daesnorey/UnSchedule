package co.edu.utadeo.unschedule.services;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.util.Objects;

public class GeneralUtil {

    /**
     * @param activity current activity
     * @param show     if keyboard is set to be show
     */
    public static void toggleKeyBoard(@NonNull Activity activity, boolean show) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }

        if (show) {
            view.requestFocus();
            boolean done = Objects.requireNonNull(imm).showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            if (!done) {
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        } else {
            Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
            view.getParent().clearChildFocus(view);
        }
    }
}
