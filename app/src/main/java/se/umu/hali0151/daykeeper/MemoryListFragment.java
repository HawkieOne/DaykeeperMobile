package se.umu.hali0151.daykeeper;

/**
 * @author id18hll
 * @version 1.0
 * Class which handles the list of memories created. Both used when showing all memories or just
 * a few
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.Collections;
import java.util.List;

import se.umu.hali0151.daykeeper.databinding.FragmentMemoryListBinding;

public class MemoryListFragment extends Fragment {

    private FragmentMemoryListBinding binding;
    private MemoryAdapter adapter;
    private MemoryViewModel memoryViewModel;
    private List<Memory> memories;
    private String searchText;
    private int sortMode;

    /**
     * Creates a new instance of MemoryListFragment
     * @param textSearch
     * @return MemoryListFragment
     */
    public static MemoryListFragment newInstance(String textSearch) {
        Bundle args = new Bundle();
       args.putString("textSearch", textSearch);
        MemoryListFragment fragment = new MemoryListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Sets the memories and adapter needed for the RecyclerView list
     * @param inflater
     * @param container
     * @param savedInstanceState bundle with date
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMemoryListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        memoryViewModel = new ViewModelProvider(this).get(MemoryViewModel.class);

        setRetainInstance(true);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(getActivity(), binding.drawerLayout, null,
                R.string.openDrawer, R.string.closerDrawer);
        searchText = null;

        Bundle bundle = this.getArguments();
        if(bundle != null){
            searchText = bundle.getString("textSearch");
        }

        binding.memoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerViewClickListener listener = (v, position, memory) -> {
            Intent intent = MemoryPagerActivity.newIntent(getActivity(), memory.getDate(), memories);
            getActivity().startActivity(intent);
        };
        adapter = new MemoryAdapter(getActivity(), listener);
        binding.memoryRecyclerView.setAdapter(adapter);
        setInitialState(savedInstanceState);
        return view;
    }

    /**
     * Handles the sorting functionality
     * @param view
     * @param savedInstanceState bundle with data
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTopAppBarListener();
        binding.radioGroup.check(R.id.radio_button_newest);
        setRadioGroupListener();
        setFabListener();
        setBackButtonListener();
    }

    /**
     * Updates the UI when returning from another fragment
     */
    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        if(searchText != null){
            updateUISearch();
        }
        else{
            updateUI();
        }
    }

    public void setInitialState(Bundle savedInstanceState){
        if(savedInstanceState != null){
            sortMode = savedInstanceState.getInt("sortMode");
        }
        if(sortMode != 0){
            adapter.setMemories(memories);
        }
        if(searchText != null){
            updateUISearch();
        }
        else{
            updateUI();
        }
    }

    /**
     * Updates the UI
     */
    private void updateUI(){
        if(sortMode != 2){
            getMemoriesDesc();
        }
        else if(sortMode == 2){
            getMemories();
        }
    }

    /**
     * Updates the UI
     */
    private void updateUISearch(){
        if(sortMode == 0){
            getMemoriesSearch();
        }
    }

    /**
     * Fetches searched memories from database
     */
    public void getMemoriesSearch(){
        memoryViewModel.getMemoriesSearch(searchText).observe(getActivity(), new Observer<List<Memory>>() {
            @Override
            public void onChanged(List<Memory> memoriesList) {
                memories = memoriesList;
                adapter.setMemories(memories);
                if(memories.size() == 0){
                    binding.noResultsFound.setText(R.string.noResults);
                    binding.memoryRecyclerView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * Fetches memories in decreasing order from database
     */
    public void getMemoriesDesc(){
        memoryViewModel.getAllMemoriesDesc().observe(getActivity(), new Observer<List<Memory>>() {
            @Override
            public void onChanged(List<Memory> memoriesList) {
                memories = memoriesList;
                adapter.setMemories(memories);
            }
        });
    }

    /**
     * Fetches searched memories in decreasing order from database
     */
    public void getMemoriesSearchDesc(){
        memoryViewModel.getMemoriesSearchDesc(searchText).observe(getActivity(), new Observer<List<Memory>>() {
            @Override
            public void onChanged(List<Memory> memoriesList) {
                memories = memoriesList;
                adapter.setMemories(memories);
                if(memories.size() == 0){
                    binding.noResultsFound.setText(R.string.noResults);
                    binding.memoryRecyclerView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * Fetches  memories from database
     */
    public void getMemories(){
        memoryViewModel.getAllMemories().observe(getActivity(), new Observer<List<Memory>>() {
            @Override
            public void onChanged(List<Memory> memoriesList) {
                memories = memoriesList;
                adapter.setMemories(memories);
            }
        });
    }

    /**
     * Saves data before an activity is destroyed
     * @param outState saved data
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("sortMode", sortMode);
    }

    /**
     * Restores the data saved from when the activity is destroyed
     * @param savedInstanceState saved data
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null){
            sortMode = savedInstanceState.getInt("sortMode");
        }
    }

    /**
     * Sets a listener for the sort button in the top app bar
     */
    public void setTopAppBarListener(){
        // Sets listener for sorting button
        binding.topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menu_sort){
                    binding.drawerLayout.openDrawer(Gravity.RIGHT);
                }
                return true;
            }
        });
    }

    /**
     * Sets a listener for the different sort modes
     */
    public void setRadioGroupListener(){
        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radio_button_newest) {
                    sortMode = 1;
                    if(searchText != null){
                        getMemoriesSearch();
                    }
                    else{
                        getMemoriesDesc();
                    }
                }
                else if(i == R.id.radio_button_oldest){
                    sortMode = 2;
                    if(searchText != null){
                        getMemoriesSearchDesc();
                    }
                    else{
                        getMemories();
                    }
                }
            }
        });
    }

    /**
     * Sets a listener for the floating action button
     */
    public void setFabListener(){
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MemoryActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    /**
     * Sets a listener for the back button
     */
    public void setBackButtonListener(){
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }

}
