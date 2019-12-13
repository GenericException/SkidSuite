package net.contra.jmd.transformers.jshrink;

import net.contra.jmd.util.NonClassEntries;

import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: Nov 29, 2010
 * Time: 10:25:20 PM
 */
public class StoreHandler {
    static byte[] WSVZ;
    static String[] append = new String[256];
    static int[] close = new int[256];

    public static synchronized String I(int paramInt) {
        int i = paramInt & 0xFF;
        if (close[i] != paramInt) {
            close[i] = paramInt;
            if (paramInt < 0) {
                paramInt &= 65535;
            }
            String str = new String(WSVZ, paramInt, WSVZ[(paramInt - 1)] & 0xFF).intern();
            append[i] = str;
        }
        return append[i];
    }

    static {
        try {
            InputStream localInputStream = NonClassEntries.ins.get(NonClassEntries.getByName("I/I.gif"));
            if (localInputStream != null) {
                int i = localInputStream.read() << 16 | localInputStream.read() << 8 | localInputStream.read();
                WSVZ = new byte[i];
                int j = 0;
                int k = (byte) i;
                byte[] arrayOfByte = WSVZ;
                while (i != 0) {
                    int m = localInputStream.read(arrayOfByte, j, i);
                    if (m == -1) {
                        break;
                    }
                    i -= m;
                    m += j;
                    while (j < m) {
                        int int2 = j;
                        arrayOfByte[int2] = (byte) (arrayOfByte[int2] ^ k);
                        j++;
                    }
                }
                localInputStream.reset();
                localInputStream.close();
            }
        } catch (Exception localException) {
        }
    }
}
