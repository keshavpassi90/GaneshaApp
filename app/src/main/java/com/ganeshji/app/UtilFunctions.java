package com.ganeshji.app;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class UtilFunctions {
    static String LOG_CLASS = "UtilMusicFunctions";

    /**
     * @param context
     * @return
     */
    public static Bitmap getDefaultAlbumArt(Context context){
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try{
            bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher, options);
        } catch(Error ee){}
        catch (Exception e) {}
        return bm;
    }
    /**
     * Convert milliseconds into time hh:mm:ss
     * @param milliseconds
     * @return time in String
     */
    public static String getDuration(long milliseconds) {
        long sec = (milliseconds / 1000) % 60;
        long min = (milliseconds / (60 * 1000))%60;
        long hour = milliseconds / (60 * 60 * 1000);

        String s = (sec < 10) ? "0" + sec : "" + sec;
        String m = (min < 10) ? "0" + min : "" + min;
        String h = "" + hour;

        String time = "";
        if(hour > 0) {
            time = h + ":" + m + ":" + s;
        } else {
            time = m + ":" + s;
        }
        return time;
    }

    public static boolean currentVersionSupportBigNotification() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        if(sdkVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN){
            return true;
        }
        return false;
    }

    public static boolean currentVersionSupportLockScreenControls() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        if(sdkVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            return true;
        }
        return false;
    }
}
