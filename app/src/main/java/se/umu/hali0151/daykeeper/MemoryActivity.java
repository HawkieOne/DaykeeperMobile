package se.umu.hali0151.daykeeper;

/**
 * @author id18hll
 * @version 1.0
 * Starts the MemoryFragment and handles back button clicks
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Date;

public class    MemoryActivity extends SingleFragmentActivity
        implements MemoryFragmentDialog.OnDialogFragmentClickListener{

    /**
     * Creates MemoryFragment through the abstract class SingleFragmentActivity
     * @return MemoryFragment
     */
    @Override
    protected Fragment createFragment() {
        Long date = (Long) getIntent().getLongExtra("memoryDate", 0);
        return MemoryFragment.newInstance(date, null);
    }

    /**
     * Puts data in a Intent (NOT USED)
     * @param packageContext context of bundle
     * @param date used for ID
     * @return Intent with data
     */
    public static Intent newIntent(Context packageContext, Date date){
        Intent intent = new Intent(packageContext, MemoryActivity.class);
        intent.putExtra("memoryDate", date != null ? date.getTime() : -1);
        return intent;
    }

    /**
     * Handles back button clicks
     */
    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            MemoryFragmentDialog memoryDialogFragment = MemoryFragmentDialog.
                    newInstance(R.string.cancel, R.string.cancelDialog);
            memoryDialogFragment.show(getSupportFragmentManager(), "Dialog");
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    /**
     * Handles dialog positive button
     * @param dialog to handle
     */
    @Override
    public void OnPositiveButtonClicked(MemoryFragmentDialog dialog) {
        dialog.dismiss();
        finish();
    }

    /**
     * Handles dialog negative button
     * @param dialog to handle
     */
    @Override
    public void OnNegativeButtonClicked(MemoryFragmentDialog dialog) {
        dialog.dismiss();
    }
}
