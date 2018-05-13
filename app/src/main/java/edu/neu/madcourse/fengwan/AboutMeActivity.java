package edu.neu.madcourse.fengwan;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;


public class AboutMeActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_READ_PHONE_STATE = 1;
    private String LabelUniqueID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        // Initialize strings
        LabelUniqueID = getResources().getString(R.string.labe_unique_id);

        // Get and set phone unique ID
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_READ_PHONE_STATE);
        } else {
            TextView textView = findViewById(R.id.textView6);
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            textView.setText(LabelUniqueID + " = " + telephonyManager.getDeviceId());
        }
    }

    // Get and set phone unique ID after requested permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_READ_PHONE_STATE
                && permissions[0].equals(Manifest.permission.READ_PHONE_STATE)
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                TextView textView = findViewById(R.id.textView6);
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                textView.setText("Unique ID = " + telephonyManager.getDeviceId());
            }
        }
    }
}
