package com.employersapps.employersapp.framework.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamHelper {

    public static byte[] readAllBytes(InputStream is) throws IOException {

        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int b = bis.read();

        while(b != -1) {
            out.write(b);
            b = bis.read();
        }

        bis.close();

        return out.toByteArray();
    }

}
