package com.guardx.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.guardx.app.R;
import com.guardx.app.permission.PermissionChecker;
import com.guardx.app.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView statusText;
    private Handler handler;
    private int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progressBar);
        statusText = findViewById(R.id.statusText);
        handler = new Handler(Looper.getMainLooper());

        startLoading();
    }

    private void startLoading() {
        String[] steps = {
            "Initializing GuardX V2.5.0...",
            "Loading Blacklist...",
            "Checking Permissions...",
            "Starting Protection Engine...",
            "Activating Ultra Shield...",
            "All Systems Ready!"
        };

        for (int i = 0; i < steps.length; i++) {
            final int index = i;
            handler.postDelayed(() -> {
                statusText.setText(steps[index]);
                progress = (index + 1) * (100 / steps.length);
                progressBar.setProgress(progress);

                if (index == steps.length - 1) {
                    handler.postDelayed(this::goToDashboard, 500);
                }
            }, i * 500L);
        }
    }

    private void goToDashboard() {
        if (!PermissionChecker.hasAllPermissions(this)) {
            startActivity(new Intent(this, DashboardActivity.class)
                .putExtra("show_permission_dialog", true));
        } else {
            startActivity(new Intent(this, DashboardActivity.class));
        }
        finish();
    }
}
