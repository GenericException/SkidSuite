package me.genericskid.util.io;

import java.util.ArrayList;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

public class ConfigFile extends File
{
    private static final long serialVersionUID = 1L;
    private static final String themePrefix = "Theme:";
    
    public ConfigFile(final String pathname) {
        super(pathname);
    }
    
    public int getTheme() {
        if (this.exists()) {
            try {
                Throwable t = null;
                try {
                    try(BufferedReader br = new BufferedReader(new FileReader(this))) {
                        String line;
                        while((line = br.readLine()) != null) {
                            if(line.startsWith("#")) {
                                continue;
                            }
                            if(line.contains("Theme:")) {
                                return Integer.parseInt(line.substring("Theme:".length()));
                            }
                        }
                    }
                }
                finally {

                }
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }
        this.write();
        return 0;
    }
    
    private void write() {
        final ArrayList<String> lines = new ArrayList<>();
        lines.add("#List of themes:");
        lines.add("#\t0: Metal");
        lines.add("#\t1: Nimbus");
        lines.add("#\t2: Simple");
        lines.add("#\t3: Plain");
        lines.add("#\t4: Classy");
        lines.add("#\t5: AluOxide");
        lines.add("#\t6: Silver");
        lines.add("#\t7: White");
        lines.add("#\t8: BlackEye");
        lines.add("#\t9: BlackMoon");
        lines.add("");
        lines.add("Theme:0");
        FileIO.saveAllLines(this.getAbsolutePath(), lines);
    }
}
