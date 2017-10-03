package base62;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;

public class CodecTest {

    @Test
    public void testToBase62() throws Exception {
        checkBase62("A", 0);
        checkBase62("C", 2);
        checkBase62("9", 61);
        checkBase62("Bm", 100);
        checkBase62("hoj", 0x01, 0xf9, 0x57);
    }

    private void checkBase62(String expected, int... digits) {
        byte[] bytes = new byte[32];
        for (int i = 0; i < digits.length; i++) {
            bytes[31 - i] = (byte) digits[digits.length - 1 - i];
        }
        assertEquals(expected, Codec.toBase62(bytes));
    }

    @Test
    public void testToByteArray() {
        checkByteArray("A", 0);
        checkByteArray("C", 2);
        checkByteArray("9", 61);
        checkByteArray("Bm", 100);
        checkByteArray("Ahoj", 0x01, 0xf9, 0x57);
    }

    private void checkByteArray(String encoded, int... expected) {

        int[] expectedExtended = new int[32];
        for (int i = 0; i < 32; i++) {
            int is = i < expected.length ? expected[expected.length - 1 - i] : 0;
            expectedExtended[31 - i] = is;
        }

        byte[] actual = Codec.toByteArray(encoded);

        for (int i = 0; i < 32; i++) {
            assertEquals(expectedExtended[i], actual[i] & 0xFF);
        }
    }


    @Test
    public void testThereAndBack() {
        long timeAcc = 0;
        for (int c = 0; c < 10000; c++) {
            byte[] input = new byte[32];
            ThreadLocalRandom.current().nextBytes(input);
            timeAcc -= System.currentTimeMillis();
            String encoded = Codec.toBase62(input);
            Assert.assertTrue(encoded.length() <= 43);
            byte[] result = Codec.toByteArray(encoded);

            timeAcc += System.currentTimeMillis();
            Assert.assertArrayEquals(input, result);
            System.out.println("OK: " + Arrays.toString(input));
        }
        System.out.printf("Average time for toBase62+toByteArray: %d us", timeAcc / 10);
    }

    @Test
    public void testEndodeDecode() {
        assertEquals("EFDE6A2D6D2FB711AD3AD1561080150560D6273DA17348677B459FA69FF6100E",
                     Codec.decode(Codec.encode("EFDE6A2D6D2FB711AD3AD1561080150560D6273DA17348677B459FA69FF6100E")));
        assertEquals("C4D2ECEC9CB5DF71EC57A0EC154A5BB319C192D0598E09B67B7BBD6E39849AA0",
                     Codec.decode(Codec.encode("C4D2ECEC9CB5DF71EC57A0EC154A5BB319C192D0598E09B67B7BBD6E39849AA0")));
        assertEquals("A3ED6D24FB83327BF16F8D2A377BA07E5946BAF46AE87F15D3BAF603661A42B2",
                     Codec.decode(Codec.encode("A3ED6D24FB83327BF16F8D2A377BA07E5946BAF46AE87F15D3BAF603661A42B2")));
        assertEquals("259583F7740A082D219D258EB69823BD1E515D8D33803F9484D8A6C130D84762",
                     Codec.decode(Codec.encode("259583F7740A082D219D258EB69823BD1E515D8D33803F9484D8A6C130D84762")));
    }

}