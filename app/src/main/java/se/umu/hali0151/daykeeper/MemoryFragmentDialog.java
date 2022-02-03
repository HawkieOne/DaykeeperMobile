package se.umu.hali0151.daykeeper;

/**
 * @author id18hll
 * @version 1.0
 * Class for dialogs
 */

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MemoryFragmentDialog extends BaseDialogFragment<MemoryFragmentDialog.OnDialogFragmentClickListener> {

    /**
     * Interface so the activity which called the dialog can handle clicks
     */
    public interface OnDialogFragmentClickListener {
        public void OnPositiveButtonClicked(MemoryFragmentDialog dialog);
        public void OnNegativeButtonClicked(MemoryFragmentDialog dialog);
    }

    /**
     * Create an instance of the dialog with the input
     * @param title of the dialog
     * @param message of the dialog
     * @return MemoryFragmentDialog
     */
    public static MemoryFragmentDialog newInstance(int title, int message) {
        MemoryFragmentDialog frag = new MemoryFragmentDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putInt("msg", message);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Create a dialog using default AlertDialog builder , if not inflate custom view in onCreateView
     * @param savedInstanceState bundle with data
     * @return AlertDialog
     */
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(getActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle(getResources().getString(getArguments().getInt("title")))
                .setMessage(getResources().getString(getArguments().getInt("msg")))
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Positive button clicked
                                getActivityInstance().
                                        OnPositiveButtonClicked(MemoryFragmentDialog.this);
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // negative button clicked
                                getActivityInstance().
                                        OnNegativeButtonClicked(MemoryFragmentDialog.this);
                            }
                        }
                )
                .create();
    }

}