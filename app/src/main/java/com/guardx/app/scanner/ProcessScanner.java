package com.guardx.app.scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessScanner {

    private static final String[] DANGEROUS_PROCESSES = {
        "gameguardian", "cheatengine", "inject",
        "xposed", "substrate", "frida", "magisk"
    };

    public List<String> scan() {
        List<String> threats = new ArrayList<>();
        threats.addAll(scanProcDirectory());
        threats.addAll(scanMaps());
        return threats;
    }

    private List<String> scanProcDirectory() {
        List<String> threats = new ArrayList<>();
        File proc = new File("/proc");
        File[] processes = proc.listFiles();

        if (processes == null) return threats;

        for (File process : processes) {
            if (!process.isDirectory()) continue;
            try {
                Integer.parseInt(process.getName());
                File cmdline = new File(process, "cmdline");
                if (cmdline.exists()) {
                    BufferedReader reader = new BufferedReader(
                        new FileReader(cmdline));
                    String line = reader.readLine();
                    reader.close();

                    if (line != null) {
                        line = line.toLowerCase();
                        for (String dangerous : DANGEROUS_PROCESSES) {
                            if (line.contains(dangerous)) {
                                threats.add("Dangerous process: " + line);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Skip
            }
        }
        return threats;
    }

    private List<String> scanMaps() {
        List<String> threats = new ArrayList<>();
        try {
            File maps = new File("/proc/self/maps");
            BufferedReader reader = new BufferedReader(new FileReader(maps));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.toLowerCase();
                for (String dangerous : DANGEROUS_PROCESSES) {
                    if (line.contains(dangerous)) {
                        threats.add("Injected library detected: " + line);
                        break;
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            // Skip
        }
        return threats;
    }
}
