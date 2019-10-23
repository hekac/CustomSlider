package com.example.customslider;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;




public class BTConnectActivity extends AppCompatActivity {
    private Toolbar mToolBar = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btconnect);
        mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
    }
}
