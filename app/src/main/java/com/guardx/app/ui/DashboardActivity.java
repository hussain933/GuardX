package com.guardx.app.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.guardx.app.R;
import com.guardx.app.database.HistoryDAO;
import com.guardx.app.permission.PermissionChecker;
import com.guardx.app.service.GuardXService;
import com.guardx.app.utils.PrefsManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private Switch protectionSwitch;
    private TextView statusText;
    private TextView permissionLevel;
    private TextView lastScanText;
    private TextView threatsTodayText;
    private TextView activeGameText;
    private Button scanBtn;
    private Button historyBtn;
    private Button settingsBtn;
    private Button blacklistBtn;
    private PrefsManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        prefs = new PrefsManager(this);
        initViews();
        updateUI();
        startGuardXService();
    }

    private void initViews() {
        protectionSwitch = findViewById(R.id.protectionSwitch);
        statusText = findViewById(R.id.statusText);
        permissionLevel = findViewById(R.id.permissionLevel);
        lastScanText = findViewById(R.id.lastScanText);
        threatsTodayText = findViewById(R.id.threatsTodayText);
        activeGameText = findViewById(R.id.activeGameText);
        scanBtn = findViewById(R.id.scanBtn);
        historyBtn = findViewById(R.id.historyBtn);
        settingsBtn = findViewById(R.id.settingsBtn);
        blacklistBtn = findViewById(R.id.blacklistBtn);

        protectionSwitch.setChecked(prefs.isProtectionOn());
        protectionSwitch.setOnCheckedChangeListener((btn, isChecked) -> {
            prefs.setProtectionOn(isChecked);
            statusText.setText(isChecked ?
                "Protection: ON ⚡" : "Protection: OFF");
        });

        // Scan button → ScanHomeActivity
        scanBtn.setOnClickListener(v ->
            startActivity(new Intent(this, ScanHomeActivity.class)));

        historyBtn.setOnClickListener(v ->
            startActivity(new Intent(this, HistoryActivity.class)));

        settingsBtn.setOnClickListener(v ->
            startActivity(new Intent(this, SettingsActivity.class)));

        blacklistBtn.setOnClickListener(v ->
            startActivity(new Intent(this, BlacklistActivity.class)));
    }

    private void updateUI() {
        if (PermissionChecker.hasDeviceOwner(this)) {
            permissionLevel.setText("Permission: ⚡ ULTRA");
        } else if (PermissionChecker.hasDeviceAdmin(this)) {
            permissionLevel.setText("Permission: 🔴 ADMIN");
        } else {
            permissionLevel.setText("Permission: ⚪ BASIC");
        }

        long lastScan = prefs.getLastScan();
        if (lastScan == 0) {
            lastScanText.setText("Last Scan: Never");
        } else {
            String time = new SimpleDateFormat("hh:mm a",
                Locale.getDefault()).format(new Date(lastScan));
            lastScanText.setText("Last Scan: " + time);
        }

        HistoryDAO dao = new HistoryDAO(this);
        int threats = dao.getAllHistory().size();
        threatsTodayText.setText("Threats Today: " + threats);
        activeGameText.setText("Active Game: Watching...");
    }

    private void startGuardXService() {
        Intent intent = new Intent(this, GuardXService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}
