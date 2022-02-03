package se.umu.hali0151.daykeeper;

/**
 * @author id18hll
 * @version 1.0
 * Class which starts the MemoryListFragment
 */

import android.content.Intent;
import androidx.fragment.app.Fragment;


public class MemoryListActivity extends SingleFragmentActivity {

    /**
     * Starts the MemoryListFragment through SingleFragmentActivity
     * @return
     */
    @Override
    protected Fragment createFragment() {
        Intent intent = getIntent();
        String textSearch = (String) intent.getStringExtra("textSearch");
        return MemoryListFragment.newInstance(textSearch);
    }
}
