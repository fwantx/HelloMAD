package edu.neu.madcourse.fengwan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

public class WordGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_game);

        subscribeToNews();
    }

    public void subscribeToNews(){
        // [START subscribe_topics]
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        // [END subscribe_topics]

        // Log and toast
        String msg = "Subscribed!";
        Toast.makeText(WordGameActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

}
