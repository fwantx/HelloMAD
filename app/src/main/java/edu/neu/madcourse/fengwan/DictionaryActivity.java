package edu.neu.madcourse.fengwan;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class DictionaryActivity extends AppCompatActivity {
    Trie wordTrie;
    Set<String> wordSet;
    TextView wordListTextView;
    MediaPlayer sound;
    Set<String> dictSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        getSupportActionBar().setTitle("Test Dictionary");

        wordTrie = new Trie();
        wordSet = new HashSet<>();
        wordListTextView = findViewById(R.id.textView8);

        App app = (App) getApplicationContext();
        dictSet = app.getDictSet();
        if (dictSet == null) {
            dictSet = new HashSet<>();
            // populate data
            try (InputStream inputStream = getResources().openRawResource(R.raw.wordlist);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
//                wordTrie.insert(line.trim().toLowerCase());
                    dictSet.add(line.trim().toLowerCase());
                }
                app.setDictSet(dictSet);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
//                if (s.length() > 2 && wordTrie.contains(str)) {
                if (s.length() > 2 && dictSet.contains(str)) {
                    playSound();

                    wordSet.add(str);
                    updateWordsView();
                }
            }
        });
    }

    public void showAcknowledgements(View view) {
        Intent intent = new Intent(this, AcknowledgementsActivity.class);
        startActivity(intent);
    }

    public void clearInput(View view) {
        EditText userInput = findViewById(R.id.editText);
        userInput.setText("");

        wordSet.clear();
        updateWordsView();
    }

    // play sound
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

    // update words view
    public void updateWordsView() {
        // display data
        String string = "";
        for (String word : wordSet) {
            string += word + "\n";
        }
        wordListTextView.setText(string);
    }

}
