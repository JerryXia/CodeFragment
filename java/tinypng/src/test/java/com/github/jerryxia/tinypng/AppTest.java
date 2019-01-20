package com.github.jerryxia.tinypng;

import java.io.File;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest  {
    @Test
    public void contextLoads() {
        TinyPngClient tinypngClient =  new TinyPngClient();
        File file = new File("D:/3d-desktop-1600x900.jpg");
        tinypngClient.min(file, file);
    }
}
