package se.umu.hali0151.daykeeper;

/**
 * @author id18hll
 * @version 1.0
 * Interface for clicks in a Recyclerview
 */

import android.view.View;

import java.util.Date;

public interface RecyclerViewClickListener {
    void onClick(View view, int position, Memory memory);
}
