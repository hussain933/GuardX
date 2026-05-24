package com.guardx.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.guardx.app.R;
import com.guardx.app.utils.PrefsManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScanHomeActivity extends AppCompatActivity {

    private Button startScanBtn;
    private TextView lastScanText;
    private TextView statusText;
    private TextView filesStatus;
    private TextView appsStatus;
    private TextView servicesStatus;
    private TextView networkStatus;
    private TextView memoryStatus;
    private PrefsManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_home);

        prefs = new PrefsManager(this);
        initViews();
        updateUI();
    }

    private void initViews() {
        startScanBtn = findViewById(R.id.startScanBtn);
        lastScanText = findViewById(R.id.lastScanText);
        statusText = findViewById(R.id.statusText);
        filesStatus = findViewById(R.id.filesStatus);
        appsStatus = findViewById(R.id.appsStatus);
        servicesStatus = findViewById(R.id.servicesStatus);
        networkStatus = findViewById(R.id.networkStatus);
        memoryStatus = findViewById(R.id.memoryStatus);

        startScanBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScanRunningActivity.class);
            startActivity(intent);
        });
    }

    private void updateUI() {
        long lastScan = prefs.getLastScan();
        if (lastScan == 0) {
            lastScanText.setText("Last Scan: Never");
        } else {
            String time = new SimpleDateFormat("hh:mm a",
                Locale.getDefault()).format(new Date(lastScan));
            lastScanText.setText("Last Scan: " + time);
        }

        statusText.setText("Status: PROTECTED");
        filesStatus.setText("○ Not Scanned");
        appsStatus.setText("○ Not Scanned");
        servicesStatus.setText("○ Not Scanned");
        networkStatus.setText("○ Not Scanned");
        memoryStatus.setText("○ Not Scanned");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}
