package se.umu.hali0151.daykeeper;

/**
 * @author id18hll
 * @version 1.0
 * Handles the addition of a memory
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.exifinterface.media.ExifInterface;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import se.umu.hali0151.daykeeper.databinding.FragmentMemoryBinding;

public class MemoryFragment extends Fragment implements MemoryFragmentDialog.OnDialogFragmentClickListener{

    private static final String TAG = "MemoryFragment";
    private static final int CAMERA_PIC_REQUEST = 500;
    private static final int GALLERY_PIC_REQUEST = 501;
    public static final int PERMISSIONS_GPS = 10;

    private FragmentMemoryBinding binding;
    private String currentPhotoPath;
    private MemoryViewModel memoryViewModel;
    private Activity activity;
    private Memory memory;
    private boolean memoryModified;
    private boolean deleted;

    /**
     * Called when the fragment is attached
     * @param context of the fragment
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (Activity)context;
    }

    /**
     * Creates a new instance of the fragment
     * @param date of the memory
     * @param memory to open
     * @return MemoryFragment
     */
    public static MemoryFragment newInstance(Long date, Memory memory) {
        Bundle args = new Bundle();
        args.putLong("memoryDate", date);
        args.putParcelable("memory", memory);
        MemoryFragment fragment = new MemoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates the memory and sets listeners
     * @param inflater
     * @param container
     * @param savedInstanceState bundle with saved data
     * @return
     */
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMemoryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        memoryViewModel = new ViewModelProvider(requireActivity()).get(MemoryViewModel.class);

        setRetainInstance(true);

        memoryModified = false;
        deleted = false;
        Bundle bundle = this.getArguments();
        Long tmpDate = bundle.getLong("memoryDate");
        setInitialState(savedInstanceState, tmpDate, bundle);

        setImageButtonListener();
        setMoodButtonListener();
        setWeatherButtonListener();
        setSaveButtonListener();
        setDeleteButtonListener();
        setHasOptionsMenu(true);
        return view;
    }

    /**
     * Handles GPS and some Date text
     * @param view
     * @param savedInstanceState bundle with saved data
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Sets the date of the memory
        SimpleDateFormat localDateFormatDate;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            localDateFormatDate = new SimpleDateFormat("E dd MMM YYYY");
        }
        else{
            localDateFormatDate = new SimpleDateFormat("E dd MMM yyyy");
        }
        String date = localDateFormatDate.format(memory.getDate());
        binding.memoryChangeDate.setText(date);

        setBackButtonListener();
        fetchGPSData();

        //Sets the time of the memory
        SimpleDateFormat localDateFormatTime = new SimpleDateFormat("HH:mm");
        String time = localDateFormatTime.format(memory.getDate());
        binding.memoryChangeTime.setText(time);
    }

    /**
     * Gets the result from an Intent
     * @param requestCode of the intent
     * @param resultCode of the Intent
     * @param data Intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PIC_REQUEST) {
            currentPhotoPath = getImagePath();
            Bitmap imgFile = BitmapFactory.decodeFile(currentPhotoPath);
            Bitmap rotatedBitmap = null;
            if(imgFile != null){
                rotatedBitmap = getRotatedImage(imgFile, currentPhotoPath);
                memory.setImagePath(currentPhotoPath);
                setImage(rotatedBitmap, imgFile);
            }
        }
        else if(requestCode == GALLERY_PIC_REQUEST && resultCode == Activity.RESULT_OK){
            try {
                final Uri imageUri = data.getData();
                String path = imageUri.getPath();
                final InputStream imageStream = getActivity().getContentResolver().
                        openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                saveImage(selectedImage);
                Bitmap rotatedBitmap = null;
                rotatedBitmap = getRotatedImage(selectedImage, currentPhotoPath);
                memory.setImagePath(currentPhotoPath);
                setImage(rotatedBitmap, selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a new file in the app's internal directory
     * @return file created
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * @return currentPhotoPath path of the photo
     */
    public String getImagePath() {
        return currentPhotoPath;
    }

    /**
     * Rotates an image based on EXIF data
     * @param imgFile Bitmap to rotate
     * @param path of the image
     * @return rotatedBitmap
     */
    public Bitmap getRotatedImage(Bitmap imgFile, String path){
        Bitmap rotatedBitmap = null;
        try{
            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(imgFile, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(imgFile, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(imgFile, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = imgFile;
            }
            return rotatedBitmap;
        }
        catch (IOException io){
            return null;
        }
    }

    /**
     * Rotates an image according to EXIF data
     * @param source Bitmap to rotate
     * @param angle that is to be rotated
     * @return Bitmap
     */
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    /**
     * Saves an image to the apps internal memory
     * @param image to save
     */
    public void saveImage(Bitmap image){
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the Fil
        }
        if (photoFile != null) {
            try {
                FileOutputStream fos = new FileOutputStream(photoFile);
                currentPhotoPath = getImagePath();
                image.compress(Bitmap.CompressFormat.JPEG, 85, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets an image to an the camera ImageView
     * @param image to set
     * @param backupImage to set in case image is null
     */
    public void setImage(Bitmap image, Bitmap backupImage){
        if(image != null){
            binding.memoryImageCamera.invalidate();
            binding.memoryImageCamera.setImageBitmap(image);
            System.out.printf("");
        }
        else {
            binding.memoryImageCamera.invalidate();
            binding.memoryImageCamera.setImageBitmap(backupImage);
            System.out.println();
        }
    }

    /**
     * Sets listener for mood button
     * @param emoji view
     * @param drawable to set
     * @param alert to close
     */
    public void setEmojListener(ImageView emoji, final int drawable, final AlertDialog alert){
        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.memoryButtonMoodDrawable.setImageResource(drawable);
                memory.setMood(drawable);
                alert.dismiss();
            }
        });
    }

    /**
     * Sets listener for weather button
     * @param emoji view
     * @param drawable to set
     * @param alert to close
     */
    public void setWeatherListener(ImageView emoji, final int drawable, final AlertDialog alert){
        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.memoryButtonWeatherDrawable.setImageResource(drawable);
                memory.setWeather(drawable);
                alert.dismiss();
            }
        });
    }

    /**
     * Handles positive button click
     * @param dialog to hanlde
     */
    @Override
    public void OnPositiveButtonClicked(MemoryFragmentDialog dialog) {
        memoryViewModel.delete(memory);
        dialog.dismiss();
        getActivity().finish();
    }

    /**
     * Handles negative button click
     * @param dialog to handle
     */
    @Override
    public void OnNegativeButtonClicked(MemoryFragmentDialog dialog) {
        dialog.dismiss();
    }

    /**
     * Set visual properties from the memory
     * @param memory to get properties from
     */
    public void setMemoryProperties(Memory memory){
        if (memory.getTitle() != null) {
            binding.editTextTitle.setText(memory.getTitle());
        }
        if (memory.getTextDay() != null) {
            binding.editTextStory.setText(memory.getTextDay());
        }
        if(memory.getGps() != null){
            binding.memoryChangeGps.setText(memory.getGps());
        }
        if (memory.getImagePath() != null) {
            Bitmap rotatedBitmap = null, selectedImage = BitmapFactory.decodeFile(memory.getImagePath());
            if(memory.getImagePath() != null){
                rotatedBitmap = getRotatedImage(selectedImage, memory.getImagePath());
            }
            if(memory.getImagePath() != null && rotatedBitmap != null){
                binding.memoryImageCamera.setImageBitmap(rotatedBitmap);
            }
            else if(memory.getImagePath() != null){
                binding.memoryImageCamera.setImageBitmap(selectedImage);
            }
        }
        if (memory.getMood() != 0) {
            binding.memoryButtonMoodDrawable.setImageResource(memory.getMood());
        }
        if (memory.getWeather() != 0) {
            binding.memoryButtonWeatherDrawable.setImageResource(memory.getWeather());
        }
    }

    /**
     * Checks if the inserted memory is null
     * @param memory to check
     * @return if the memory is null
     */
    public boolean checkMemoryNull(Memory memory){
        return memory.getTitle() == null && memory.getTextDay() == null && memory.getMood() == 0
                && memory.getWeather() == 0 && memory.getImagePath() == null;
    }

    /**
     * Sets the GPS data
     * @param location where the user is right now
     */
    public void setGPSData(Location location){
        if(location != null){
            // Got last known location. In some rare situations this can be null.
            Geocoder geocoder = new Geocoder(activity);
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String result = null;
            if (addresses != null && addresses.size() > 0) {
                for (Address adr : addresses) {
                    if (adr.getLocality() != null && adr.getLocality().length() > 0) {
                        result =  adr.getLocality();
                    }
                }
            }
            binding.memoryChangeGps.setText(result);
        }
    }

    /**
     * Fetches GPS data from the device
     */
    public void fetchGPSData(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationClient = LocationServices.
                    getFusedLocationProviderClient(getActivity());
            if(activity != null){
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                setGPSData(location);
                            };
                        }).addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }/*else if (shouldShowRequestPermissionRationale("HELLO")) {

        }*/ else {
            requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSIONS_GPS);
        }
    }

    /**
     * Returns if storage permission is granted or not
     * @return true or false if permission is granted
     */
    public boolean isStoragePermissionGranted(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    /**
     * Starts an intent for gallery
     */
    public void startGalleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_PIC_REQUEST);
    }

    /**
     * Starts an intent for camera
     */
    public void startCameraIntent(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the Fil
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "se.umu.hali0151.daykeeper.fileprovider",
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        }
    }

    /**
     * Saves data before an activity is destroyed
     * @param outState saved data
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("savedMemory", memory);
        outState.putString("imagePath", currentPhotoPath);
    }

    /**
     * Restores the data saved from when the activity is destroyed
     * @param savedInstanceState saved data
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null){
            memory = savedInstanceState.getParcelable("savedMemory");
            currentPhotoPath = savedInstanceState.getString("imagePath");
        }
    }

    /**
     * Sets the listener for the image button
     */
    public void setImageButtonListener(){
        binding.memoryButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStoragePermissionGranted()){
                    final MaterialAlertDialogBuilder mADB =
                            new MaterialAlertDialogBuilder(getActivity(),
                                    R.style.ThemeOverlay_App_MaterialAlertDialog);
                    mADB.setTitle(R.string.insert_image);
                    mADB.setIcon(R.drawable.ic_baseline_image_24);
                    mADB.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    mADB.setItems(R.array.memory_image_dialog_array, (dialog, item) -> {
                        if (item == 0) {
                            startGalleryIntent();
                        } else if (item == 1) {
                            startCameraIntent();
                        }
                    });
                    mADB.create().show();
                }
            }
        });
    }

    /**
     * Sets the listener for the mood button
     */
    public void setMoodButtonListener(){
        binding.memoryButtonMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View viewDialog = inflater.inflate(R.layout.dialog_view_mood, null);
                final MaterialAlertDialogBuilder mADB =
                        new MaterialAlertDialogBuilder(getActivity(),
                                R.style.ThemeOverlay_App_MaterialAlertDialog);
                mADB.setTitle(R.string.memory_mood);
                mADB.setView(viewDialog);
                mADB.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                final AlertDialog alert = mADB.create();
                alert.show();

                ImageView dialogEmojiHappy = (ImageView) viewDialog.findViewById(R.id.dialog_emoji_happy);
                ImageView dialogEmojiNeutral = (ImageView) viewDialog.findViewById(R.id.dialog_emoji_neutral);
                ImageView dialogEmojiSad = (ImageView) viewDialog.findViewById(R.id.dialog_emoji_sad);
                ImageView dialogEmojiExcited = (ImageView) viewDialog.findViewById(R.id.dialog_emoji_excited);
                ImageView dialogEmojiLove = (ImageView) viewDialog.findViewById(R.id.dialog_emoji_love);
                ImageView dialogEmojiCry = (ImageView) viewDialog.findViewById(R.id.dialog_emoji_cry);
                setEmojListener(dialogEmojiHappy, R.drawable.emoji_smile, alert);
                setEmojListener(dialogEmojiNeutral, R.drawable.emoji_neutral, alert);
                setEmojListener(dialogEmojiSad, R.drawable.emoji_sad, alert);
                setEmojListener(dialogEmojiExcited, R.drawable.emoji_excited, alert);
                setEmojListener(dialogEmojiLove, R.drawable.emoji_love, alert);
                setEmojListener(dialogEmojiCry, R.drawable.emoji_cry, alert);
            }
        });
    }

    /**
     * Sets the listener for the weather button
     */
    public void setWeatherButtonListener(){
        binding.memoryButtonWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View viewDialog = inflater.inflate(R.layout.dialog_view_weather, null);
                final MaterialAlertDialogBuilder mADB = new
                        MaterialAlertDialogBuilder(getActivity(),
                        R.style.ThemeOverlay_App_MaterialAlertDialog);
                mADB.setTitle(R.string.memory_weather);
                mADB.setView(viewDialog);
                mADB.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                final AlertDialog alert = mADB.create();
                alert.show();

                ImageView dialogWeatherSun = (ImageView) viewDialog.findViewById(R.id.dialog_weather_sun);
                ImageView dialogWeatherCloud = (ImageView) viewDialog.findViewById(R.id.dialog_weather_cloud);
                ImageView dialogWeatherRain = (ImageView) viewDialog.findViewById(R.id.dialog_weather_rain);
                ImageView dialogWeatherWind = (ImageView) viewDialog.findViewById(R.id.dialog_weather_wind);
                ImageView dialogWeatherSnow = (ImageView) viewDialog.findViewById(R.id.dialog_weather_snow);
                ImageView dialogWeatherStorm = (ImageView) viewDialog.findViewById(R.id.dialog_weather_storm);
                setWeatherListener(dialogWeatherSun, R.drawable.sun, alert);
                setWeatherListener(dialogWeatherCloud, R.drawable.cloud, alert);
                setWeatherListener(dialogWeatherRain, R.drawable.rain, alert);
                setWeatherListener(dialogWeatherWind, R.drawable.wind, alert);
                setWeatherListener(dialogWeatherSnow, R.drawable.snow, alert);
                setWeatherListener(dialogWeatherStorm, R.drawable.storm, alert);
            }
        });
    }

    /**
     * Sets the listener for the save button
     */
    public void setSaveButtonListener(){
        binding.saveMemoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memory.setTitle(binding.editTextTitle.getText().toString());
                memory.setTextDay(binding.editTextStory.getText().toString());
                memoryViewModel.insert(memory);
                getActivity().getSupportFragmentManager().beginTransaction().
                        remove(MemoryFragment.this).commit();
                getActivity().finish();
            }
        });
    }

    /**
     * Sets the listener for the delete button
     */
    public void setDeleteButtonListener(){
        binding.deleteMemoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemoryFragmentDialog memoryDialogFragment = MemoryFragmentDialog.
                        newInstance(R.string.cancel, R.string.cancelDialog);
                memoryDialogFragment.show(getChildFragmentManager(), "Dialog");
                deleted = true;
            }
        });
    }

    /**
     * Sets the listener for the back button
     */
    public void setBackButtonListener(){
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().
                        remove(MemoryFragment.this).commit();
                getActivity().finish();
            }
        });
    }

    /**
     * Sets the initial state of the memory depending on some circumstances
     * @param savedInstanceState saved data from earlier activity or fragment
     * @param date to see if the bundle is valid
     * @param bundle sent from parent activity
     */
    public void setInitialState(Bundle savedInstanceState, Long date, Bundle bundle){
        if(savedInstanceState != null){
            memory = savedInstanceState.getParcelable("savedMemory");
            currentPhotoPath = savedInstanceState.getString("imagePath");
            setMemoryProperties(memory);
        }
        else if (date != 0) {
            memory = bundle.getParcelable("memory");
            setMemoryProperties(memory);
        } else if(memory != null){
            setMemoryProperties(memory);
        }
        else{
            memory = new Memory();
        }
    }
}
