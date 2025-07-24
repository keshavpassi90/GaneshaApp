package com.ganeshji.app.retrofit.wallpaper_model;

import android.os.Parcel;
import android.os.Parcelable;

public class GaneshItem implements Parcelable {

    // ────────── fields ──────────
    private String  title;
    private String  description;
    private boolean isAudio;
    private int     iconData;      // drawable resource ID
    private boolean isFavourite;
    private String  typeString;
    private int audioResId;        // 🔹 This holds the raw file resource ID

    // ────────── constructors ──────────
    public GaneshItem() { }

    public GaneshItem(String title,
                      String description,
                      boolean isAudio,
                      int iconData,

                      boolean isFavourite,
                      String typeString,  int audioResId) {
        setData(title, description, isAudio, iconData, isFavourite, typeString,audioResId);
    }

    // ────────── Parcelable plumbing ──────────
    protected GaneshItem(Parcel in) {
        title       = in.readString();
        description = in.readString();
        isAudio     = in.readByte() != 0;
        iconData    = in.readInt();
        isFavourite = in.readByte() != 0;
        typeString  = in.readString();
        audioResId  = in.readInt();
    }

    public static final Creator<GaneshItem> CREATOR = new Creator<GaneshItem>() {
        @Override public GaneshItem createFromParcel(Parcel in) { return new GaneshItem(in); }
        @Override public GaneshItem[] newArray(int size)        { return new GaneshItem[size]; }
    };

    @Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeByte((byte) (isAudio ? 1 : 0));
        dest.writeInt(iconData);
        dest.writeByte((byte) (isFavourite ? 1 : 0));
        dest.writeString(typeString);
        dest.writeInt(audioResId);
    }

    // ────────── getters ──────────
    public String  getTitle()       { return title; }
    public String  getDescription() { return description; }
    public boolean isAudio()        { return isAudio; }
    public int     getIconData()    { return iconData; }
    public boolean isFavourite()    { return isFavourite; }
    public String  getTypeString()  { return typeString; }
 public int  getAudioResId()  { return audioResId; }

    // ────────── setters ──────────
    public void setTitle      (String title)        { this.title = title; }
    public void setDescription(String description)  { this.description = description; }
    public void setAudio      (boolean isAudio)     { this.isAudio = isAudio; }
    public void setIconData   (int iconData)        { this.iconData = iconData; }
    public void setFavourite  (boolean isFavourite) { this.isFavourite = isFavourite; }
    public void setTypeString (String typeString)   { this.typeString = typeString; }
    public void setAudioResId  (int audioResId)        { this.audioResId = audioResId; }

    // ────────── convenience helpers ──────────
    /** Chain‑style setter to fill everything at once. */
    public GaneshItem setData(String  title,
                              String  description,
                              boolean isAudio,
                              int     iconData,
                              boolean isFavourite,
                              String  typeString,int audioResId) {
        this.title       = title;
        this.description = description;
        this.isAudio     = isAudio;
        this.iconData    = iconData;
        this.isFavourite = isFavourite;
        this.typeString  = typeString;
        this.audioResId  = audioResId;
        return this;  // enables chaining
    }

    /** Handy dump for logs / debug UIs. */
    public Object[] getData() {
        return new Object[] {
                title,
                description,
                isAudio,
                iconData,
                isFavourite,
                typeString,audioResId
        };
    }
}
