package com.github.jerryxia.mavenPomVersionFind;

import java.io.IOException;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    @Test
    public void contextLoads() throws IOException {
//        String[] args = { 
//                "/Users/guqiankun/git/fphoto/source/foliday-photo-web", 
//                "foliday-photo-web", 
//                "/Users/guqiankun/git/fphoto/build_shell"
//        };
        String[] args = { 
              "D:\\Develop\\gitaliyun\\fphoto\\source\\foliday-photo-job", 
              "foliday-photo-web", 
              "D:\\Develop\\gitaliyun\\fphoto\\build_shell"
        };
        App.main(args);
    }
}
