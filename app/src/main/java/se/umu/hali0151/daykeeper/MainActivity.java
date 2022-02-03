package se.umu.hali0151.daykeeper;

/**
 * @author id18hll
 * @version 1.0
 * The main activity which controls the application. Through this activity most other
 * activities and actions is started.
 */

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.commons.lang3.StringUtils;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import se.umu.hali0151.daykeeper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    private MemoryViewModel memoryViewModel;
    private List<Memory> memories;

    /**
     * Sets listeners for elements and starts the ViewModel
     * @param savedInstanceState bundle with saved data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        memoryViewModel = new ViewModelProvider(this).get(MemoryViewModel.class);


        binding.bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_profile) {
                    startActivity(ProfileActivity.class);
                }
                return true;
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                startActivity(MemoryActivity.class);
            }
        });

        binding.allMemoriesViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(MemoryListActivity.class);
            }
        });

        binding.buttonSearchDrawable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = binding.textFieldSearchText.getText().toString();
                startActivitySearch(MemoryListActivity.class, "textSearch", text);
            }
        });

        binding.textFieldSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH){
                    String text = textView.getText().toString();
                    startActivitySearch(MemoryListActivity.class, "textSearch", text);
                }
                return false;
            }
        });

        String timeStampDay = new SimpleDateFormat("d").format(new Date());
        String timeStampMonth = new SimpleDateFormat("MMMM").format(new Date());
        String timeStampYear = new SimpleDateFormat("yyyy").format(new Date());
        timeStampMonth = StringUtils.capitalize(timeStampMonth);
        binding.todayDate.setText(MessageFormat.format("{0}:e {1} {2}", timeStampDay,
                                  timeStampMonth, timeStampYear));
    }

    /**
     * Sets the top menu bar (ISN*T IN USE RIGHT NOW)
     * @param menu the menu to be inflated
     * @return true if successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.textFieldSearchText.setText(null);
        binding.textFieldSearch.clearFocus();
    }

    private void startActivity(Class className){
        Intent intent = new Intent(MainActivity.this, className);
        MainActivity.this.startActivity(intent);
    }

    private void startActivitySearch(Class classname, String bundleID, String extra){
        Intent intent = new Intent(MainActivity.this, classname);
        intent.putExtra(bundleID, extra);
        MainActivity.this.startActivity(intent);
    }
}