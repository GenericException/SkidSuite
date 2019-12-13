package me.genericskid.util.io;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileIO
{
    public static List<String> loadAllLines(final String file) {
        final ArrayList<String> lines = new ArrayList<>();
        final File f = new File(file);
        try {
            final BufferedReader br = new BufferedReader(new FileReader(f));
            String s = null;
            while ((s = br.readLine()) != null) {
                lines.add(s);
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
    
    public static void saveAllLines(final String file, final ArrayList<String> text) {
        final File f = new File(file);
        if (!f.exists()) {
            try {
                f.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            final PrintWriter out = new PrintWriter(file);
            for (final String line : text) {
                out.println(line);
            }
            out.close();
        }
        catch (FileNotFoundException e2) {
            e2.printStackTrace();
        }
    }
}
