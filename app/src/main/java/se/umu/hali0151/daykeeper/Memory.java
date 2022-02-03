package se.umu.hali0151.daykeeper;

/**
 * @author id18hll
 * @version 1.0
 * Memory holds all information about a memory.
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


@Entity(tableName = "memory_table")
public class Memory implements Parcelable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "date")
    private Date date;
    private String dateDay;
    private String dateMonth;
    private String dateYear;
    private String title;
    private String textDay;
    private int mood;
    private int weather;
    private String imagePath;
    private String gps;

    /**
     * Constructor for Memory class, sets the date and time to current date and time
     * The other variables are null or zero
     */
    public Memory() {
        this.date = Calendar.getInstance(Locale.getDefault()).getTime();
        this.dateDay = (String) DateFormat.format("dd",   date);
        this.dateMonth = (String) DateFormat.format("MMMM",  date);
        this.dateYear = (String) DateFormat.format("yyyy", date);
        this.title = null;
        this.textDay = null;
        this.mood = 0;
        this.weather = 0;
        this.imagePath = null;
        this.gps = null;
    }

    /**
     * @return title of the memory
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title of the memory
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return date of the memory
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date of the memory
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return date in day format
     */
    public String getDateDay(){
        return dateDay;
    }

    /**
     * @return date in month format
     */
    public String getDateMonth(){
        return dateMonth;
    }

    /**
     * @return date in year format
     */
    public String getDateYear(){
        return dateYear;
    }

    /**
     * @return text description of the memory
     */
    public String getTextDay() {
        return textDay;
    }

    /**
     *
     * @param textDay description of the memory
     */
    public void setTextDay(String textDay) {
        this.textDay = textDay;
    }

    /**
     * @param dateDay in format day
     */
    public void setDateDay(String dateDay) {
        this.dateDay = dateDay;
    }

    /**
     * @param dateMonth in format month
     */
    public void setDateMonth(String dateMonth) {
        this.dateMonth = dateMonth;
    }

    /**
     * @param dateYear in format year
     */
    public void setDateYear(String dateYear) {
        this.dateYear = dateYear;
    }

    /**
     * @return mood internal ind ID
     */
    public int getMood() {
        return mood;
    }

    /**
     * @param mood internal int ID
     */
    public void setMood(int mood) {
        this.mood = mood;
    }

    /**
     * @return internal int ID
     */
    public int getWeather() {
        return weather;
    }

    /**
     * @param weather internal int ID
     */
    public void setWeather(int weather) {
        this.weather = weather;
    }

    /**
     * @return imagePath in String format
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * @param imagePath in String format
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * @return gps location in String format
     */
    public String getGps() {
        return gps;
    }

    /**
     * @param gps location in String format
     */
    public void setGps(String gps) {
        this.gps = gps;
    }

    /**
     * Method needed for Parcelable implementation
     * @return int
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes data to parcel
     * @param dest Bundle to put data in
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeString(this.dateDay);
        dest.writeString(this.dateMonth);
        dest.writeString(this.dateYear);
        dest.writeString(this.title);
        dest.writeString(this.textDay);
        dest.writeInt(this.mood);
        dest.writeInt(this.weather);
        dest.writeString(this.imagePath);
        dest.writeString(this.gps);
    }

    /**
     * Reads data from parcel
     * @param in Parcel to read
     */
    protected Memory(Parcel in) {
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.dateDay = in.readString();
        this.dateMonth = in.readString();
        this.dateYear = in.readString();
        this.title = in.readString();
        this.textDay = in.readString();
        this.mood = in.readInt();
        this.weather = in.readInt();
        this.imagePath = in.readString();
        this.gps = in.readString();
    }

    /**
     * Creates Parcel
     */
    public static final Creator<Memory> CREATOR = new Creator<Memory>() {
        @Override
        public Memory createFromParcel(Parcel source) {
            return new Memory(source);
        }

        @Override
        public Memory[] newArray(int size) {
            return new Memory[size];
        }
    };
}
