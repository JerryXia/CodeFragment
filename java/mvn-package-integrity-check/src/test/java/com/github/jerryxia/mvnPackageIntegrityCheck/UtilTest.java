package com.github.jerryxia.mvnPackageIntegrityCheck;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.junit.Test;
import org.junit.Assert;

/**
 * @author Administrator
 *
 */
public class UtilTest {

    @Test
    public void testSha1IsOk() throws FileNotFoundException {
        String userDir = StrSubstitutor.replaceSystemProperties(Const.USER_DIR);
        String sha1 = Util.getSha1(new File(userDir + "/src/test/resources/slf4j-api-1.7.25.jar"));
        Assert.assertEquals("da76ca59f6a57ee3102f8f9bd9cee742973efa8a", sha1);
    }
}
