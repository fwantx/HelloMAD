package edu.neu.madcourse.fengwan;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private String MyName;
    private String ErrorGenerated;
    private String LabelVersionCode;
    private String LabelVersionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize strings
        MyName = getResources().getString(R.string.my_name);
        ErrorGenerated = getResources().getString(R.string.error_generated);
        LabelVersionCode = getResources().getString(R.string.label_version_code);
        LabelVersionName = getResources().getString(R.string.labe_version_name);

        // Show my name on the title bar
        getSupportActionBar().setTitle(MyName);

        // Get version code
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            TextView textViewVersionCode = findViewById(R.id.textView4);
            textViewVersionCode.setText(String.format(
                    "%s = %s \n%s = %d",
                    LabelVersionName,
                    packageInfo.versionName,
                    LabelVersionCode,
                    packageInfo.versionCode
            ));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Show about me page
    public void showAboutMe(View view) {
        Intent intent = new Intent(this, AboutMeActivity.class);
        startActivity(intent);
    }

    // Generate an error
    public void generateError(View view) {
        throw new RuntimeException(ErrorGenerated);
    }

    // Show dictionary page
    public void showDictionary(View view) {
        Intent intent = new Intent(this, DictionaryActivity.class);
        startActivity(intent);
    }
}
