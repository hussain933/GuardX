package com.guardx.app.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.guardx.app.R;
import com.guardx.app.service.GuardXService;
import com.guardx.app.utils.Constants;
import java.util.ArrayList;
import java.util.List;

public class WarningPopupActivity extends AppCompatActivity {

    private TextView riskLevel;
    private TextView threatCount;
    private LinearLayout threatList;
    private Button fixAllBtn;
    private Button cancelBtn;
    private List<String> threats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);

        threats = getIntent().getStringArrayListExtra("threats");
        if (threats == null) threats = new ArrayList<>();

        initViews();
        displayThreats();
    }

    private void initViews() {
        riskLevel = findViewById(R.id.riskLevel);
        threatCount = findViewById(R.id.threatCount);
        threatList = findViewById(R.id.threatList);
        fixAllBtn = findViewById(R.id.fixAllBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        // Risk level
        String risk = getRiskLevel();
        riskLevel.setText("Risk Level: " + risk);

        // Threat count
        threatCount.setText("Threats Found: " + threats.size());

        // Fix All button
        fixAllBtn.setOnClickListener(v -> fixAll());

        // Cancel button
        cancelBtn.setOnClickListener(v -> {
            finish();
        });
    }

    private void displayThreats() {
        for (String threat : threats) {
            TextView tv = new TextView(this);
            tv.setText("🔴 " + threat);
            tv.setTextSize(14);
            tv.setPadding(16, 8, 16, 8);
            threatList.addView(tv);
        }
    }

    private void fixAll() {
        fixAllBtn.setText("Fixing...");
        fixAllBtn.setEnabled(false);

        // Kill suspicious apps
        ActivityManager am = (ActivityManager)
            getSystemService(Context.ACTIVITY_SERVICE);

        new Thread(() -> {
            try { Thread.sleep(1500); } catch (Exception e) {}

            runOnUiThread(() -> {
                // Send fix action to service
                Intent intent = new Intent(this, GuardXService.class);
                intent.setAction(Constants.ACTION_FIX_ALL);
                startService(intent);
                finish();
            });
        }).start();
    }

    private String getRiskLevel() {
        if (threats.size() >= 4) return "🔴 CRITICAL";
        if (threats.size() >= 2) return "🟠 HIGH";
        return "🟡 MEDIUM";
    }

    @Override
    public void onBackPressed() {
        // Prevent back press
    }
}
