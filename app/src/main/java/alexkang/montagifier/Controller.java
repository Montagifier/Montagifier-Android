package alexkang.montagifier;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class Controller {

    private static final String URL_STRING = "http://montagifier.henryz.me";

    private static Controller instance;

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }

        return instance;
    }

    public void checkIn() {
        new Thread() {

            @Override
            public void run() {
                try {
                    URL url = new URL(URL_STRING);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");

                    OutputStream out = urlConnection.getOutputStream();
                    out.write(("checkin:").getBytes());
                    out.flush();
                    out.close();

                    urlConnection.getResponseCode();
                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    public void checkOut() {
        new Thread() {

            @Override
            public void run() {
                try {
                    URL url = new URL(URL_STRING);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");

                    OutputStream out = urlConnection.getOutputStream();
                    out.write(("checkout:").getBytes());
                    out.flush();
                    out.close();

                    urlConnection.getResponseCode();
                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    public void getSounds(final Callback callback) {
        new AsyncTask<Void, Void, ArrayList<SoundCategory>>() {

            @Override
            protected ArrayList<SoundCategory> doInBackground(Void... params) {
                try {
                    URL url = new URL(URL_STRING);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Content-Type", "application/json");

                    if (urlConnection.getResponseCode() != 200) {
                        return null;
                    }

                    InputStream in = urlConnection.getInputStream();
                    StringBuilder buffer = new StringBuilder();

                    while (true) {
                        int i = in.read();
                        if (i == -1) {
                            break;
                        }
                        buffer.append((char) i);
                    }

                    String dataString = buffer.toString();
                    JSONObject jsonObject = new JSONObject(dataString);
                    Iterator<String> categories = jsonObject.keys();
                    ArrayList<SoundCategory> result = new ArrayList<>();
                    while (categories.hasNext()) {
                        String name = categories.next();
                        ArrayList<String> sounds = new ArrayList<>();

                        JSONArray soundsList = jsonObject.getJSONArray(name);
                        for (int i = 0; i < soundsList.length(); i++) {
                            sounds.add(soundsList.getString(i));
                        }

                        result.add(new SoundCategory(name, sounds));
                    }

                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ArrayList<SoundCategory> result) {
                callback.onDataRetrieved(result);
            }

        }.execute();
    }

    public void requestSkip(final Callback callback) {
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    URL url = new URL(URL_STRING);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");

                    OutputStream out = urlConnection.getOutputStream();
                    out.write("skip:".getBytes());
                    out.flush();
                    out.close();

                    int response = urlConnection.getResponseCode();
                    urlConnection.disconnect();

                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Integer result) {
                if (result != 202) {
                    callback.onDataRetrieved(null);
                } else {
                    callback.onDataRetrieved(result);
                }
            }

        }.execute();
    }

    public void requestVideo(String url, final Callback callback) {
        final String id = extractVideoId(url);

        if (id != null) {
            new AsyncTask<Void, Void, Integer>() {

                @Override
                protected Integer doInBackground(Void... params) {
                    try {
                        URL url = new URL(URL_STRING);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("POST");

                        OutputStream out = urlConnection.getOutputStream();
                        out.write(("video:" + id).getBytes());
                        out.flush();
                        out.close();

                        int response = urlConnection.getResponseCode();
                        urlConnection.disconnect();

                        return response;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Integer result) {
                    if (result != 202) {
                        callback.onDataRetrieved(null);
                    } else {
                        callback.onDataRetrieved(result);
                    }
                }

            }.execute();
        } else {
            callback.onDataRetrieved(null);
        }
    }

    public void requestSound(final String category, final String sound) {
        new Thread() {

            @Override
            public void run() {
                try {
                    URL url = new URL(URL_STRING);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");

                    OutputStream out = urlConnection.getOutputStream();
                    out.write(("sound:" + category + ":" + sound).getBytes());
                    out.flush();
                    out.close();

                    urlConnection.getResponseCode();
                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    private String extractVideoId(String url) {
        int startIndex = url.indexOf("youtube.com/");

        if (startIndex == -1) {
            startIndex = url.indexOf("youtu.be/");
            if (startIndex != -1) {
                startIndex += 9;
            }
        } else {
            startIndex += 12;
        }

        if (startIndex != -1) {
            return url.substring(startIndex, url.length());
        } else {
            return null;
        }
    }

}
