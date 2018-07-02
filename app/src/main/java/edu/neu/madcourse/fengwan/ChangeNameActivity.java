package edu.neu.madcourse.fengwan;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import edu.neu.madcourse.fengwan.daos.UserDao;
import edu.neu.madcourse.fengwan.models.User;

public class ChangeNameActivity extends AppCompatActivity {
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        userDao = new UserDao();
        final EditText editText = findViewById(R.id.change_name_edit_text);
        Button submitButton = findViewById(R.id.change_name_submit_button);

        DatabaseReference userDbRef = UserDao.getUserDbRef();
        userDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user =  dataSnapshot.getValue(User.class);
                if (user != null) {
                    editText.setText(user.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User(editText.getText().toString());
                userDao.addUser(user);
                Toast toast = Toast.makeText(
                        ChangeNameActivity.this,
                        "User name updated successfully!",
                        Toast.LENGTH_SHORT
                );
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

    }
}
