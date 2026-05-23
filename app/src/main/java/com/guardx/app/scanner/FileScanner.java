package com.guardx.app.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileScanner {

    private static final String[] DANGEROUS_PATHS = {
        "/data/local/tmp/",
        "/sdcard/Android/hack/",
        "/sdcard/cheat/",
        "/sdcard/GameGuardian/"
    };

    private static final String[] DANGEROUS_EXTENSIONS = {
        ".lua", ".cfg", ".mod"
    };

    private static final String[] DANGEROUS_NAMES = {
        "gameguardian", "cheatengine", "inject",
        "hack", "mod_menu", "esp", "aimbot"
    };

    public List<String> scan() {
        List<String> threats = new ArrayList<>();
        threats.addAll(scanDangerousPaths());
        threats.addAll(scanSdcard());
        return threats;
    }

    private List<String> scanDangerousPaths() {
        List<String> threats = new ArrayList<>();
        for (String path : DANGEROUS_PATHS) {
            File dir = new File(path);
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files != null && files.length > 0) {
                    threats.add("Suspicious files in: " + path);
                }
            }
        }
        return threats;
    }

    private List<String> scanSdcard() {
        List<String> threats = new ArrayList<>();
        File sdcard = new File("/sdcard/");
        if (sdcard.exists()) {
            scanDirectory(sdcard, threats, 0);
        }
        return threats;
    }

    private void scanDirectory(File dir, List<String> threats, int depth) {
        if (depth > 3) return;
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isFile()) {
                String name = file.getName().toLowerCase();
                for (String ext : DANGEROUS_EXTENSIONS) {
                    if (name.endsWith(ext)) {
                        for (String dangerous : DANGEROUS_NAMES) {
                            if (name.contains(dangerous)) {
                                threats.add("Dangerous file: " + file.getPath());
                            }
                        }
                    }
                }
            } else if (file.isDirectory()) {
                scanDirectory(file, threats, depth + 1);
            }
        }
    }
}
