package edu.neu.madcourse.fengwan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class DictionaryActivity extends AppCompatActivity {
    TextView detectedWordsTextView;
    MediaPlayer sound;
    ProgressDialog progressDialog;
    Set<String> dictionary;
    Set<String> detectedWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        getSupportActionBar().setTitle("Test Dictionary");

        detectedWords = new HashSet<>();
        detectedWordsTextView = findViewById(R.id.textView8);

        App app = (App) getApplicationContext();
        dictionary = app.getDictionary();
        // initialize dictionary if not already initialized
        if (dictionary == null) {
            new LoadDictionary().execute();

            progressDialog = new ProgressDialog(DictionaryActivity.this);
            progressDialog.setMessage("Loading Dictionary...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        EditText inputEditText = findViewById(R.id.editText);
        // check input
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString().trim().toLowerCase();
                if (s.length() > 2 && dictionary.contains(str)) {
                    detectedWords.add(str);
                    updateWordsView();
                    playSound();
                }
            }
        });
    }

    // Open Ackknowledgements page
    public void showAcknowledgements(View view) {
        Intent intent = new Intent(this, AcknowledgementsActivity.class);
        startActivity(intent);
    }

    // Clear input and detected words
    public void clearInput(View view) {
        EditText userInput = findViewById(R.id.editText);
        userInput.setText("");
        detectedWords.clear();
        updateWordsView();
    }

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

    // Update detected words text view
    public void updateWordsView() {
        String str = "";
        for (String word : detectedWords) {
            str += word + "\n";
        }
        detectedWordsTextView.setText(str);
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
                progressDialog.dismiss();
            }
        }
    }
}
