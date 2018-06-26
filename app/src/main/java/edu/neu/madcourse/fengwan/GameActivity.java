package edu.neu.madcourse.fengwan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class GameActivity extends Activity {
    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";
    private GameFragment mGameFragment;
    private Set<String> dictionary;
    ProgressDialog progressDialog;
    private MediaPlayer sound;

    // Play sound
    public void playSound() {
        if (sound != null) {
            sound.stop();
            sound.release();
        }
        sound = MediaPlayer.create(this, R.raw.plucky);
        if (sound != null) {
            sound.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // load dictionary
        App app = (App) getApplicationContext();
        dictionary = app.getDictionary();
        // initialize dictionary if not already initialized
        if (dictionary == null) {
            new LoadDictionary().execute();

            progressDialog = new ProgressDialog(GameActivity.this);
            progressDialog.setMessage("Loading Dictionary...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }
//        Log.d("TAG TAG TAG TAG TAG", "onCreate: " + dictionary.size());

        // Restore game here...
        mGameFragment = (GameFragment) getFragmentManager().findFragmentById(R.id.fragment_game);
        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE).getString(PREF_RESTORE, null);
            if (gameData != null) {
                mGameFragment.putState(gameData);
            }
        }
        Log.d("UT3", "restore = " + restore);
    }

    public void restartGame() {
        mGameFragment.restartGame();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String gameData = mGameFragment.getState();
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREF_RESTORE, gameData)
                .commit();
        Log.d("UT3", "state = " + gameData);
    }

    protected void onResume() {
        super.onResume();

        mGameFragment.setDictionary(dictionary);
        mGameFragment.startTimerNow();
    }

    public Set<String> getDictionary() {
        return dictionary;
    }

    // Load data into dictionary
    private class LoadDictionary extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            App app = (App) getApplicationContext();
            dictionary = new HashSet<>();
            try (InputStream inputStream = getResources().openRawResource(R.raw.wordlist);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    dictionary.add(line.trim().toLowerCase());
                }
                app.setDictionary(dictionary);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            if (progressDialog != null && progressDialog.isShowing()) {
                mGameFragment.setDictionary(dictionary);
                progressDialog.dismiss();
            }
        }
    }
}