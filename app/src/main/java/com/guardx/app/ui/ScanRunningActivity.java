package com.guardx.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.guardx.app.R;
import com.guardx.app.scanner.AppScanner;
import com.guardx.app.scanner.FileScanner;
import com.guardx.app.scanner.EnvScanner;
import com.guardx.app.scanner.ProcessScanner;
import com.guardx.app.scanner.AIFilter;
import com.guardx.app.utils.PrefsManager;
import java.util.ArrayList;
import java.util.List;

public class ScanRunningActivity extends AppCompatActivity {

    private TextView riskText;
    private ProgressBar riskBar;
    private TextView filesStatus;
    private TextView appsStatus;
    private TextView servicesStatus;
    private TextView networkStatus;
    private TextView memoryStatus;
    private TextView threatCount;
    private LinearLayout threatList;
    private Button stopBtn;
    private Handler handler;
    private boolean isRunning = true;
    private PrefsManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_running);

        prefs = new PrefsManager(this);
        handler = new Handler(Looper.getMainLooper());
        initViews();
        startScan();
    }

    private void initViews() {
        riskText = findViewById(R.id.riskText);
        riskBar = findViewById(R.id.riskBar);
        filesStatus = findViewById(R.id.filesStatus);
        appsStatus = findViewById(R.id.appsStatus);
        servicesStatus = findViewById(R.id.servicesStatus);
        networkStatus = findViewById(R.id.networkStatus);
        memoryStatus = findViewById(R.id.memoryStatus);
        threatCount = findViewById(R.id.threatCount);
        threatList = findViewById(R.id.threatList);
        stopBtn = findViewById(R.id.stopBtn);

        stopBtn.setOnClickListener(v -> {
            isRunning = false;
            finish();
        });
    }

    private void startScan() {
        new Thread(() -> {
            List<String> allThreats = new ArrayList<>();

            // Layer 1: Files
            updateStatus(filesStatus, "🔄 SCANNING...");
            List<String> fileThreats = new FileScanner().scan();
            allThreats.addAll(fileThreats);
            updateStatus(filesStatus, "✅ DONE (" + fileThreats.size() + ")");
            updateRisk(allThreats.size());

            if (!isRunning) return;

            // Layer 2: Apps
            updateStatus(appsStatus, "🔄 SCANNING...");
            List<String> appThreats = new AppScanner(this).scan();
            allThreats.addAll(appThreats);
            updateStatus(appsStatus, "✅ DONE (" + appThreats.size() + ")");
            updateRisk(allThreats.size());

            if (!isRunning) return;

            // Layer 3: Services
            updateStatus(servicesStatus, "🔄 SCANNING...");
            List<String> envThreats = new EnvScanner(this).scan();
            allThreats.addAll(envThreats);
            updateStatus(servicesStatus, "✅ DONE (" + envThreats.size() + ")");
            updateRisk(allThreats.size());

            if (!isRunning) return;

            // Layer 4: Network/Process
            updateStatus(networkStatus, "🔄 SCANNING...");
            List<String> procThreats = new ProcessScanner().scan();
            allThreats.addAll(procThreats);
            updateStatus(networkStatus, "✅ DONE (" + procThreats.size() + ")");
            updateRisk(allThreats.size());

            if (!isRunning) return;

            // Layer 5: Memory/AI
            updateStatus(memoryStatus, "🔄 SCANNING...");
            List<String> aiThreats = new AIFilter(this).scan();
            allThreats.addAll(aiThreats);
            updateStatus(memoryStatus, "✅ DONE (" + aiThreats.size() + ")");
            updateRisk(allThreats.size());

            // Show results
            prefs.setLastScan(System.currentTimeMillis());
            showResults(allThreats);

        }).start();
    }

    private void updateStatus(TextView tv, String text) {
        handler.post(() -> tv.setText(text));
    }

    private void updateRisk(int threatCount) {
        handler.post(() -> {
            int risk = Math.min(threatCount * 20, 100);
            riskBar.setProgress(risk);
            if (risk == 0) riskText.setText("RISK: SAFE");
            else if (risk < 40) riskText.setText("RISK: LOW " + risk + "%");
            else if (risk < 70) riskText.setText("RISK: MEDIUM " + risk + "%");
            else riskText.setText("RISK: HIGH " + risk + "%");
        });
    }

    private void showResults(List<String> threats) {
        handler.post(() -> {
            threatCount.setText("THREATS: " + threats.size());
            threatList.removeAllViews();

            if (threats.isEmpty()) {
                TextView tv = new TextView(this);
                tv.setText("○ No threats found");
                tv.setTextColor(0xFFFFFFFF);
                tv.setTextSize(14);
                tv.setPadding(0, 8, 0, 8);
                threatList.addView(tv);
            } else {
                for (String threat : threats) {
                    TextView tv = new TextView(this);
                    tv.setText("● " + threat);
                    tv.setTextColor(0xFFFFFFFF);
                    tv.setTextSize(14);
                    tv.setPadding(0, 8, 0, 8);
                    threatList.addView(tv);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }
}
