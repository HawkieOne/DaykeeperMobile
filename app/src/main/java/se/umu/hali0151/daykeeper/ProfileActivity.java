package se.umu.hali0151.daykeeper;

/**
 * @author id18hll
 * @version 1.0
 * Starts the ProfileFragment
 */


import androidx.fragment.app.Fragment;

public class ProfileActivity extends SingleFragmentActivity{

    /**
     * Starts a ProfileFragment through SingleFragmentActivity
     * @return ProfileFragment
     */
    @Override
    protected Fragment createFragment() {
        return new ProfileFragment();
    }
}
