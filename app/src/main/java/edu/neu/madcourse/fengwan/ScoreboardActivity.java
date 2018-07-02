package edu.neu.madcourse.fengwan;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.neu.madcourse.fengwan.daos.GameDao;
import edu.neu.madcourse.fengwan.daos.UserDao;
import edu.neu.madcourse.fengwan.fcm.FCMAsyncTask;
import edu.neu.madcourse.fengwan.models.Game;
import edu.neu.madcourse.fengwan.models.User;

public class ScoreboardActivity extends AppCompatActivity {
    private ListView scoreboardListView;
    final List<Game> topGames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        scoreboardListView = findViewById(R.id.scoreboard_listview);
        GameDao gameDao = new GameDao();
        Query query = gameDao.queryScoreboard();
        query.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        topGames.add(0, snapshot.getValue(Game.class));
                    }
                }
                scoreboardListView.setAdapter(new ScoreboardAdapter(ScoreboardActivity.this, topGames));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

class ScoreboardAdapter extends ArrayAdapter {
    private final Activity context;
    private final List<Game> topGames;

    public ScoreboardAdapter(Activity context, List<Game> topGames) {
        super(context, R.layout.scoreboard_row_layout, topGames);
        this.context = context;
        this.topGames = topGames;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.scoreboard_row_layout, null, true);

        final Game game = topGames.get(position);

        final TextView userName = rowView.findViewById(R.id.scoreboard_username);
        TextView userScore = rowView.findViewById(R.id.scoreboard_score);
        TextView playTime = rowView.findViewById(R.id.scoreboard_time);
        Button kudosButton = rowView.findViewById(R.id.kudos_button);
        DatabaseReference userDbRef = UserDao.getUserDbRef(game.getClientToken());
        userDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userName.setText(user == null ? "N/A" : user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userScore.setText(String.valueOf(game.getScore()));
        kudosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FCMAsyncTask(
                        game.getClientToken(),
                        "Congrats!",
                        "Congrats, good game! ",
                        false
                ).execute();
                Toast toast = Toast.makeText(
                        context,
                        "Kudos sent successfully!",
                        Toast.LENGTH_SHORT
                );
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        playTime.setText(format.format(new Date(game.getTimeStamp() * 1000)));
        return rowView;
    }
}