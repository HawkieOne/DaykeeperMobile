package se.umu.hali0151.daykeeper;

/**
 * @author id18hll
 * @version 1.0
 * Abstract class for creating fragments
 */

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public abstract class SingleFragmentActivity extends AppCompatActivity{

    protected abstract Fragment createFragment();

    /**
     * If fragment isn't in stack, create the fragment
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = createFragment();
            if(savedInstanceState != null){
                fragment.setArguments(savedInstanceState);
            }
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }
}
