package com.guardx.app.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.guardx.app.R;
import com.guardx.app.database.HistoryDAO;
import com.guardx.app.utils.PDFExporter;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout historyContainer;
    private Button exportBtn;
    private Button clearBtn;
    private HistoryDAO historyDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyDAO = new HistoryDAO(this);
        historyContainer = findViewById(R.id.historyContainer);
        exportBtn = findViewById(R.id.exportBtn);
        clearBtn = findViewById(R.id.clearBtn);

        loadHistory();

        exportBtn.setOnClickListener(v -> exportPDF());
        clearBtn.setOnClickListener(v -> clearHistory());
    }

    private void loadHistory() {
        historyContainer.removeAllViews();
        List<Map<String, String>> history = historyDAO.getAllHistory();

        if (history.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No scan history yet");
            empty.setTextSize(16);
            empty.setPadding(16, 32, 16, 16);
            historyContainer.addView(empty);
            return;
        }

        for (Map<String, String> entry : history) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(16, 12, 16, 12);

            TextView dateTime = new TextView(this);
            dateTime.setText("📅 " + entry.get("date") +
                " ⏰ " + entry.get("time"));
            dateTime.setTextSize(12);

            TextView threats = new TextView(this);
            threats.setText("🔴 " + entry.get("threats"));
            threats.setTextSize(13);

            TextView action = new TextView(this);
            action.setText("Action: " + entry.get("action") +
                " | Risk: " + entry.get("risk"));
            action.setTextSize(12);

            card.addView(dateTime);
            card.addView(threats);
            card.addView(action);
            historyContainer.addView(card);
        }
    }

    private void exportPDF() {
        PDFExporter exporter = new PDFExporter(this);
        String path = exporter.exportHistory(historyDAO.getAllHistory());
        if (path != null) {
            Toast.makeText(this, "PDF saved: " + path,
                Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Export failed",
                Toast.LENGTH_SHORT).show();
        }
    }

    private void clearHistory() {
        historyDAO.clearHistory();
        loadHistory();
        Toast.makeText(this, "History cleared",
            Toast.LENGTH_SHORT).show();
    }
}
