package se.umu.hali0151.daykeeper;
/**
 * @author id18hll
 * @version 1.0
 * Abstract class for dialogs
 */

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public abstract class BaseDialogFragment<T> extends DialogFragment {
    private T mActivityInstance;

    public final T getActivityInstance() {
        return mActivityInstance;
    }

    /**
     * Saves the context for the activity which called the dialog
     * @param context 
     */
    @Override
    public void onAttach(@NonNull Context context) {
        mActivityInstance = (T) context;
        super.onAttach(context);
    }

    /**
     * Deletes the reference
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mActivityInstance = null;
    }
}