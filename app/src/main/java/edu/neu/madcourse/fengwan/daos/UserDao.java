package edu.neu.madcourse.fengwan.daos;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import edu.neu.madcourse.fengwan.models.User;

public class UserDao extends BaseDao {

    static private DatabaseReference userDbRef;

    public UserDao() {
        super();
        userDbRef = getDbRef().child("users").child(getClientToken());
        userDbRef.keepSynced(true);
    }

    public void addUser(final User user) {
        if (user.getName() != null) {
            userDbRef.setValue(user);
            return;
        }
        userDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User tmp = dataSnapshot.getValue(User.class);
                if (tmp == null) {
                    user.setName(getClientToken().substring(0, 10));
                    userDbRef.setValue(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static DatabaseReference getUserDbRef(String clientToken) {
        return getDbRef().child("users").child(clientToken);
    }

    public static DatabaseReference getUserDbRef() {
        return userDbRef;
    }
}
