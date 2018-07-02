package edu.neu.madcourse.fengwan.daos;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import edu.neu.madcourse.fengwan.fcm.FCMAsyncTask;
import edu.neu.madcourse.fengwan.models.Game;

public class GameDao extends BaseDao {

    private DatabaseReference gamesDbRef;
    private DatabaseReference userGamesDbRef;

    public GameDao() {
        super();
        gamesDbRef = getDbRef().child("games");
        gamesDbRef.keepSynced(true);

        userGamesDbRef = getDbRef().child("userGames").child(getClientToken());
        userGamesDbRef.keepSynced(true);
    }

    public String addGame(Game game) {
        game.setClientToken(getClientToken());
        game.setTimeStamp(System.currentTimeMillis() / 1000);

        DatabaseReference gameDbRef = gamesDbRef.push();
        gameDbRef.setValue(game);
        userGamesDbRef.child(gameDbRef.getKey()).setValue(game);

        return gameDbRef.getKey();
    }

    public void updateGame(final Game game, final String firebaseKey) {
        game.setClientToken(getClientToken());
        game.setTimeStamp(System.currentTimeMillis() / 1000);

        DatabaseReference gameDbRef = gamesDbRef.child(firebaseKey);
        gameDbRef.setValue(game);

        DatabaseReference userGameDbRef = userGamesDbRef.child(firebaseKey);
        userGameDbRef.setValue(game);

        Query queryLeader = queryLeader();
        queryLeader.keepSynced(true);
        queryLeader.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    GenericTypeIndicator<Map<String, Game>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Game>>() {};
                    Map<String, Game> games = dataSnapshot.getValue(genericTypeIndicator);
                    for (String topGameId : games.keySet()) {
                        Log.i("XXX topGameId", topGameId);
                        if (firebaseKey.equals(topGameId)) {
                            new FCMAsyncTask("news","New highest score!", "New highest score is created!", true).execute();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public Query queryScoreboard() {
        return userGamesDbRef.orderByChild("score").limitToLast(5);
    }

    public Query queryLeaderboard() {
        return gamesDbRef.orderByChild("score").limitToLast(5);
    }

    public Query queryLeader() {
        return gamesDbRef.orderByChild("score").limitToLast(1);
    }
}
