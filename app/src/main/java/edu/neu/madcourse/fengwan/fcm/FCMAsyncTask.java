package edu.neu.madcourse.fengwan.fcm;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class FCMAsyncTask extends AsyncTask<Void, Void, Void> {

    private final static String SERVER_KEY = "key=AAAAVHiAzgo:APA91bFWM4SPbeeJOGLj2gC9XQ5nBGl8VzDj4aMZFS_Al3dObej1VJ5EEiCjRMtS05eDSFaValJ-9jfKuLJS4Ggg9VIGyfXsD9obGZCGSoY9wr6NQQ7h_7O1bawFPPokITAQ8T-jfwZM6wZlULravlW3mUMYTkxeoA";

    private String target;
    private String notifTitle;
    private String notifBody;
    private boolean isGroup;

    public FCMAsyncTask(String target, String notifTitle, String notifBody, boolean isGroup) {
        this.target = target;
        this.notifTitle = notifTitle;
        this.notifBody = notifBody;
        this.isGroup = isGroup;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotif = new JSONObject();
        try {
            jNotif.put("title", notifTitle);
            jNotif.put("body", notifBody);
            jNotif.put("sound", "default");

            if (isGroup) {
                jPayload.put("to", "/topics/" + target);
            } else {
                jPayload.put("to", target);
            }
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotif);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
}
