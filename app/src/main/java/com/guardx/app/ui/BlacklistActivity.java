package com.guardx.app.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.guardx.app.R;
import com.guardx.app.database.BlacklistDAO;
import java.util.List;
import java.util.Map;

public class BlacklistActivity extends AppCompatActivity {

    private EditText searchBox;
    private LinearLayout listContainer;
    private BlacklistDAO blacklistDAO;
    private List<Map<String, String>> allApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);

        blacklistDAO = new BlacklistDAO(this);
        searchBox = findViewById(R.id.searchBox);
        listContainer = findViewById(R.id.listContainer);

        loadApps();

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterApps(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadApps() {
        allApps = blacklistDAO.getAllApps();
        displayApps(allApps);
    }

    private void displayApps(List<Map<String, String>> apps) {
        listContainer.removeAllViews();
        for (Map<String, String> app : apps) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(16, 12, 16, 12);

            TextView nameView = new TextView(this);
            String risk = app.get("risk");
            String emoji = "CRITICAL".equals(risk) ? "🔴" :
                          "HIGH".equals(risk) ? "🟠" : "🟡";
            nameView.setText(emoji + " " + app.get("name"));
            nameView.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            nameView.setTextSize(14);

            Switch toggle = new Switch(this);
            toggle.setChecked("1".equals(app.get("enabled")));
            toggle.setOnCheckedChangeListener((btn, isChecked) -> {
                blacklistDAO.toggleApp(app.get("package"), isChecked);
            });

            row.addView(nameView);
            row.addView(toggle);
            listContainer.addView(row);
        }
    }

    private void filterApps(String query) {
        if (allApps == null) return;
        List<Map<String, String>> filtered = new java.util.ArrayList<>();
        for (Map<String, String> app : allApps) {
            if (app.get("name").toLowerCase()
                .contains(query.toLowerCase())) {
                filtered.add(app);
            }
        }
        displayApps(filtered);
    }
}
