package com.guardx.app.ui;

import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.RadioButton;
import androidx.appcompat.app.AppCompatActivity;
import com.guardx.app.R;
import com.guardx.app.utils.Constants;
import com.guardx.app.utils.PrefsManager;

public class SettingsActivity extends AppCompatActivity {

    private Switch bootSwitch;
    private Switch autoFixSwitch;
    private RadioGroup sensitivityGroup;
    private RadioGroup notifGroup;
    private PrefsManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = new PrefsManager(this);
        initViews();
        loadSettings();
    }

    private void initViews() {
        bootSwitch = findViewById(R.id.bootSwitch);
        autoFixSwitch = findViewById(R.id.autoFixSwitch);
        sensitivityGroup = findViewById(R.id.sensitivityGroup);
        notifGroup = findViewById(R.id.notifGroup);

        bootSwitch.setOnCheckedChangeListener((btn, isChecked) ->
            prefs.setBootStart(isChecked));

        autoFixSwitch.setOnCheckedChangeListener((btn, isChecked) ->
            prefs.setAutoFix(isChecked));

        sensitivityGroup.setOnCheckedChangeListener((group, id) -> {
            if (id == R.id.sensitivityLow)
                prefs.setScanSensitivity(Constants.SENSITIVITY_LOW);
            else if (id == R.id.sensitivityMed)
                prefs.setScanSensitivity(Constants.SENSITIVITY_MEDIUM);
            else if (id == R.id.sensitivityHigh)
                prefs.setScanSensitivity(Constants.SENSITIVITY_HIGH);
        });

        notifGroup.setOnCheckedChangeListener((group, id) -> {
            if (id == R.id.notifSilent)
                prefs.setNotifStyle("SILENT");
            else if (id == R.id.notifNormal)
                prefs.setNotifStyle("NORMAL");
            else if (id == R.id.notifLoud)
                prefs.setNotifStyle("LOUD");
        });
    }

    private void loadSettings() {
        bootSwitch.setChecked(prefs.isBootStart());
        autoFixSwitch.setChecked(prefs.isAutoFix());

        int sensitivity = prefs.getScanSensitivity();
        if (sensitivity == Constants.SENSITIVITY_LOW)
            ((RadioButton) findViewById(R.id.sensitivityLow)).setChecked(true);
        else if (sensitivity == Constants.SENSITIVITY_MEDIUM)
            ((RadioButton) findViewById(R.id.sensitivityMed)).setChecked(true);
        else
            ((RadioButton) findViewById(R.id.sensitivityHigh)).setChecked(true);

        String notif = prefs.getNotifStyle();
        if ("SILENT".equals(notif))
            ((RadioButton) findViewById(R.id.notifSilent)).setChecked(true);
        else if ("NORMAL".equals(notif))
            ((RadioButton) findViewById(R.id.notifNormal)).setChecked(true);
        else
            ((RadioButton) findViewById(R.id.notifLoud)).setChecked(true);
    }
}
