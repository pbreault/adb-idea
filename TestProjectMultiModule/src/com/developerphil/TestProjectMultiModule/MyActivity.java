package com.developerphil.TestProjectMultiModule;

import android.app.Activity;
import android.os.Bundle;
import com.developerphil.adbidea.test.TestProjectMultiModule.R;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
