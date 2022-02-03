package se.umu.hali0151.daykeeper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import se.umu.hali0151.daykeeper.databinding.ActivityMemoryPagerBinding;

/**
 * @author id18hll
 * @version 1.0
 * Class which handles the detail view of the memories as well as the animations between them
 */

public class MemoryPagerActivity extends AppCompatActivity
        implements  MemoryFragmentDialog.OnDialogFragmentClickListener  {

    private ActivityMemoryPagerBinding binding;
    private MemoryViewModel memoryViewModel;
    private List<Memory> memories;
    private Memory memory;
    private FragmentManager fragmentManager;
    private FragmentStateAdapter adapter;

    /**
     * Creates an Intent with data
     * @param packageContext context of the activity
     * @param date for ID
     * @param memories ArrayList
     * @return
     */
    public static Intent newIntent(Context packageContext, Date date, List<Memory> memories){
        Intent intent = new Intent(packageContext, MemoryPagerActivity.class);
        intent.putExtra("memoryDate", date != null ? date.getTime() : -1);
        intent.putParcelableArrayListExtra("memoryList", new ArrayList<Memory>(memories));
        return intent;
    }

    /**
     * Sets the adapter for the MemoryFragments and sets listeners
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMemoryPagerBinding.inflate(getLayoutInflater());
        memoryViewModel = new ViewModelProvider(this).get(MemoryViewModel.class);
        View view = binding.getRoot();
        setContentView(view);

        Long tmpDate = (Long) getIntent().getLongExtra("memoryDate", 0);
        Date date = tmpDate == -1 ? null : new Date(tmpDate);

        memories = getIntent().getParcelableArrayListExtra("memoryList");

        fragmentManager = getSupportFragmentManager();
        binding.memoryViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.memoryViewPager.setUserInputEnabled(true);

        // Adapter fo the Viewpager
        adapter = new FragmentStateAdapter(fragmentManager, getLifecycle()){

            @Override
            public int getItemCount() {
                return memories.size();
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                memory = memories.get(position);
                Date date = memory.getDate();
                return MemoryFragment.newInstance(date != null ? date.getTime() : -1, memory);
            }
        };
        binding.memoryViewPager.setAdapter(adapter);

        // Gets the user to the latest added memory
        binding.buttonFirstPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.memoryViewPager.
                        setCurrentItem(0, false);
            }
        });

        // Gets the user to the first added memory
        binding.buttonLastPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.memoryViewPager.setCurrentItem(memories.size() - 1, false);
            }
        });

        // Adjusts the buttons visibility
        binding.memoryViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if(memories.size() > 1){
                    if(binding.memoryViewPager.getCurrentItem() == 0){
                        binding.buttonFirstPageContainer.setVisibility(View.INVISIBLE);
                        binding.buttonLastPageContainer.setVisibility(View.VISIBLE);
                    }
                    else if(binding.memoryViewPager.getCurrentItem() == memories.size() - 1){
                        binding.buttonFirstPageContainer.setVisibility(View.VISIBLE);
                        binding.buttonLastPageContainer.setVisibility(View.INVISIBLE);
                    }
                    else{
                        binding.buttonFirstPageContainer.setVisibility(View.VISIBLE);
                        binding.buttonLastPageContainer.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        // The opened fragment is the one the user press
        for(int i = 0; i < memories.size(); i++){
            if (memories.get(i).getDate().equals(date)) {
                binding.memoryViewPager.setCurrentItem(i, false);
                break;
            }
        }
    }

    /**
     * Handles positive clicks when removing a memory
     * @param dialog
     */
    @Override
    public void OnPositiveButtonClicked(MemoryFragmentDialog dialog) {
        int position = binding.memoryViewPager.getCurrentItem();
        memoryViewModel.delete(memories.get(position));
        if(position == memories.size() - 1){
            binding.memoryViewPager.setCurrentItem( position - 1, false);
        }
        else{
            binding.memoryViewPager.setCurrentItem( position, false);
        }
        memories.remove(position);
        adapter.notifyDataSetChanged();
    }

    /**
     * Handles negative clicks when removing a memory
     * @param dialog
     */
    @Override
    public void OnNegativeButtonClicked(MemoryFragmentDialog dialog) {

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        binding.memoryViewPager.unregisterOnPageChangeCallback();
//    }
}
