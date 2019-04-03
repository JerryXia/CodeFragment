deleted file mode 100644
@@ 1,108 +0,0 @@
/**
 * 
 */
package com.github.jerryxia.mvnPackageIntegrityCheck;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.val;

/**
 * @author Administrator
 *
 */
public class Util {
    private static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f' };

    private static MessageDigest messagedigest = null;

    static {
        try {
            messagedigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void write(Object x) {
        System.out.println(x);
    }

    public static void write(String format, Object... args) {
        System.out.println(String.format(format, args));
    }

    public static String readFile(File file) throws FileNotFoundException {
        val sb = new StringBuffer();
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
        int tempchar;
        try {
            while ((tempchar = reader.read()) != -1) {
                sb.append((char)tempchar);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /***
     * calc sha1 code
     * 
     * @return String origin file
     * @throws FileNotFoundException 
     * @throws NoSuchAlgorithmException
     */
    public static String getSha1(File file) throws FileNotFoundException {
        FileInputStream in = new FileInputStream(file);

        byte[] buffer = new byte[1024 * 1024 * 10];
        int len = 0;
        try {
            while ((len = in.read(buffer)) > 0) {
                messagedigest.update(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferToHex(messagedigest.digest());
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }
}

