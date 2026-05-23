package com.guardx.app.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PDFExporter {

    private Context context;

    public PDFExporter(Context context) {
        this.context = context;
    }

    public String exportHistory(List<Map<String, String>> history) {
        PdfDocument document = new PdfDocument();
        Paint titlePaint = new Paint();
        Paint textPaint = new Paint();
        Paint linePaint = new Paint();

        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(20);
        titlePaint.setFakeBoldText(true);

        textPaint.setColor(Color.DKGRAY);
        textPaint.setTextSize(12);

        linePaint.setColor(Color.LTGRAY);
        linePaint.setStrokeWidth(1);

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
            595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Title
        canvas.drawText("GuardX V2.5.0 — Scan Report", 40, 50, titlePaint);
        canvas.drawText("Generated: " + getCurrentDate(), 40, 75, textPaint);
        canvas.drawLine(40, 85, 555, 85, linePaint);

        int y = 110;
        for (Map<String, String> entry : history) {
            if (y > 800) break;
            canvas.drawText("Date: " + entry.get("date") +
                " " + entry.get("time"), 40, y, textPaint);
            y += 18;
            canvas.drawText("Threats: " + entry.get("threats"), 40, y, textPaint);
            y += 18;
            canvas.drawText("Action: " + entry.get("action") +
                " | Risk: " + entry.get("risk"), 40, y, textPaint);
            y += 18;
            canvas.drawLine(40, y, 555, y, linePaint);
            y += 15;
        }

        document.finishPage(page);

        // Save file
        String fileName = "GuardX_Report_" +
            new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date()) + ".pdf";

        File file = new File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), fileName);

        try {
            document.writeTo(new FileOutputStream(file));
            document.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            document.close();
            return null;
        }
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm",
            Locale.getDefault()).format(new Date());
    }
}
