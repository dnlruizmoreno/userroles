package controller;

import controller.utils.CryptoSessionUtils;
import junit.framework.TestCase;
import org.junit.Assert;

public class CryptoSessionUtilsTest extends TestCase {

    private String seed="seedeed";
    private String anotherSeed="another";
    private String text="ipsum";

    public void testEncryptAndDecryptTrue() throws Exception {
        String enc = CryptoSessionUtils.encrypt(seed, text);
        String decr = CryptoSessionUtils.decrypt(seed, enc);
        Assert.assertEquals(text, decr);
    }

}