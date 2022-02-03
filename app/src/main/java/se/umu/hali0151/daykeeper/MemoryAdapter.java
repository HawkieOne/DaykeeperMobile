package se.umu.hali0151.daykeeper;

/**
 * @author id18hll
 * @version 1.0
 * Adapter for RecyclerView which handles memories
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.RecyclerView;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import se.umu.hali0151.daykeeper.databinding.ListItemMemoryBinding;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder> {

    private ListItemMemoryBinding binding;
    private Context context;
    private Memory current;
    private RecyclerViewClickListener listener;

    /**
     * Local class for the ViewHolder
     */
    class MemoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /**
         * Constructor for MemoryViewHolder
         * @param itemView current view
         * @param clickListener for the view
         */
        private MemoryViewHolder(View itemView, RecyclerViewClickListener clickListener) {
            super(itemView);
            listener = clickListener;
            itemView.setOnClickListener(this);
        }

        /**
         * Handles when a view in the RecyclerView is clicked
         * @param view that is clicked
         */
        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition(), memories.get(getAdapterPosition()));
        }
    }

    private final LayoutInflater mInflater;
    private List<Memory> memories; // Cached copy of memories

    /**
     * Constructor for MemoryAdapter
     * @param context for the adapter
     * @param clickListener for the RecyclerView
     */
    MemoryAdapter(Context context, RecyclerViewClickListener clickListener) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        listener = clickListener;
    }

    /**
     * Called when the view is hold
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MemoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.list_item_memory, parent, false);
        return new MemoryViewHolder(itemView, listener);
    }

    /**
     * Called when a view is bund
     * @param holder
     * @param position int the RecyclerView
     */
    @Override
    public void onBindViewHolder(MemoryViewHolder holder, int position) {
        if (memories != null) {
            current = memories.get(position);
            TextView listCardMemoryTitle = (TextView)holder.itemView.
                    findViewById(R.id.list_card_memory_title);
            listCardMemoryTitle.setText(current.getTitle());
            TextView listMemoryDay = (TextView)holder.itemView.
                    findViewById(R.id.list_memory_day);
            listMemoryDay.setText(current.getDateDay());
            TextView listMemoryMonth = (TextView)holder.itemView.
                    findViewById(R.id.list_memory_month);
            listMemoryMonth.setText(DateFormat.format("MMM",  current.getDate()));
            TextView listMemoryYear = (TextView)holder.itemView.
                    findViewById(R.id.list_memory_year);
            listMemoryYear.setText(current.getDateYear());
            TextView listCardMemoryDay = (TextView)holder.itemView.
                    findViewById(R.id.list_card_memory_day);
            listCardMemoryDay.setText(current.getTextDay());

            Bitmap selectedImage = getBitmap(current.getImagePath());
            Bitmap rotatedBitmap = null;
            if(current.getImagePath() != null){
                rotatedBitmap = getRotatedImage(selectedImage, current.getImagePath());
            }

            ImageView listCardMemoryImage = (ImageView)holder.itemView.
                    findViewById(R.id.list_card_memory_image);
            if(current.getImagePath() != null && rotatedBitmap != null){
                listCardMemoryImage.setImageBitmap(rotatedBitmap);
            }
            else if(current.getImagePath() != null){
                listCardMemoryImage.setImageBitmap(selectedImage);
            }

        } else {
            // Covers the case of data not being ready yet.
            binding.listCardMemoryTitle.setText(R.string.error);
        }
    }

    /**
     * Sets the ArrayList of memories
     * @param memories to set
     */
    void setMemories(List<Memory> memories){
        this.memories = memories;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).

    /**
     * Returns the current size of the ArrayList
     * @return memories or if empty 0
     */
    @Override
    public int getItemCount() {
        if (memories != null)
            return memories.size();
        else return 0;
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

    public Bitmap getBitmap(String filepath) {
        boolean done = false;
        int downsampleBy = 2;
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, options);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        while (!done) {
            options.inSampleSize = downsampleBy++;
            try {
                bitmap = BitmapFactory.decodeFile(filepath, options);
                done = true;
            } catch (OutOfMemoryError e) {
                // Ignore.  Try again.
            }
        }
        return bitmap;
    }
}