package alexkang.videojerk;

import android.content.Context;

public class Controller {

    private static Controller instance;

    private Context mContext;

    public static Controller getInstance(Context context) {
        if (instance == null) {
            instance = new Controller(context);
        }

        return instance;
    }

    private Controller(Context context) {
        mContext = context;
    }

    public void getSounds(Callback callback) {
        callback.onDataRetrieved(null);
    }

    public void requestSkip(Callback callback) {
        callback.onDataRetrieved("Yaaas");
    }

    public void requestVideo(String url, Callback callback) {
        int startIndex = url.lastIndexOf("youtube.com/");

        if (startIndex == -1) {
            startIndex = url.lastIndexOf("youtu.be/");
            if (startIndex != -1) {
                startIndex += 9;
            }
        } else {
            startIndex += 12;
        }

        if (startIndex != -1) {
            String videoId = url.substring(startIndex, url.length());
            callback.onDataRetrieved(videoId);
        } else {
            callback.onDataRetrieved(null);
        }
    }

    public void requestSound(String sound) {

    }

}
