package edu.neu.madcourse.fengwan;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.fengwan.daos.GameDao;
import edu.neu.madcourse.fengwan.models.Game;

public class LeaderboardActivity extends AppCompatActivity {
    private ListView leaderboardListView;
    final List<Game> topGames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        leaderboardListView = findViewById(R.id.leaderboard_listview);
        GameDao gameDao = new GameDao();
        Query query = gameDao.queryLeaderboard();
        query.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        topGames.add(0, snapshot.getValue(Game.class));
                    }
                }
                leaderboardListView.setAdapter(new ScoreboardAdapter(LeaderboardActivity.this, topGames));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
