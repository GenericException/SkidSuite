package me.genericskid.util.gui;

import java.io.File;
import javax.swing.JFileChooser;
import java.awt.Rectangle;

public class ComponentUtil
{
    private static final int padding = 5;
    private static final int compWidth = 150;
    private static final int compWidthWide = 328;
    private static final int compHeight = 33;
    
    public static Rectangle createButtonBounds(final int index, final int objHeight, final int objWidth, final boolean usePadding) {
        final int x = 0;
        final int y = usePadding ? (5 * index + objHeight * index) : (objHeight * index);
        final Rectangle rect = new Rectangle(x, y, objWidth, objHeight);
        return rect;
    }
    
    public static JFileChooser makeFileChooser() {
        final JFileChooser temp = new JFileChooser();
        final String dir = System.getProperty("user.dir");
        final File fileDir = new File(dir);
        temp.setCurrentDirectory(fileDir);
        return temp;
    }
    
    public static Rectangle createPanelBounds(final int index, final int yBase, final int height) {
        final int yindex = 0;
        int x = 160;
        final int y = yBase + 5 + 33 * yindex;
        final int width = 162;
        if (index == 1) {
            x += 169;
        }
        final Rectangle rect = new Rectangle(x, y, width, height);
        return rect;
    }
    
    public static Rectangle createCheckbox(final int index) {
        final int compHeight = 16;
        final Rectangle rect = new Rectangle(5, 25 + 5 * index + compHeight * index, 150, compHeight);
        return rect;
    }
    
    public static int getPadding() {
        return 5;
    }
}
