package base62;

import java.math.BigInteger;

import javax.xml.bind.DatatypeConverter;

/**
 * Codec for Base62 encoding / decoding. Base62 is alphabet containing only letters and numbers ([A-Za-z0-9]) and no special characters.
 */
public class Codec {

    private static final BigInteger BASE = new BigInteger("62");
    private static final BigInteger BYTE_BASE = new BigInteger("256");

    private static final String INDEX = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * Transforms hexadecimal input to byte array and encodes result to Base62 string
     *
     * @param source hexadecimal string (max 64 symbols)
     * @return Base62 string (max 43 symbols)
     */
    public static String encode(String source) {
        if (source.length() > 64) {
            throw new IllegalArgumentException("Input shall not be longer than 64 characters, is " + source.length());
        }
        return toBase62(DatatypeConverter.parseHexBinary(source));
    }

    /**
     * Decodes Base62 string to byte array and converts that array to hexadecimal string
     *
     * @param base62 base62 string (max 44 symbols)
     * @return hexadecimal string (64 symbols)
     */
    public static String decode(String base62) {
        if (base62.length() > 43) {
            throw new IllegalArgumentException("Input shall not be longer than 43 characters, is " + base62.length());
        }
        return DatatypeConverter.printHexBinary(toByteArray(base62));
    }


    /**
     * Encodes byte array to base62 string
     *
     * @param data byte array input
     * @return Base62 string (max 43 symbols)
     */
    public static String toBase62(byte[] data) {
        if (data.length > 32) {
            throw new IllegalArgumentException("Input shall not be longer than 32 bytes, is " + data.length);
        }
        StringBuilder result = new StringBuilder();
        BigInteger value = BigInteger.ZERO;
        for (byte b : data) {
            value = value.multiply(BYTE_BASE).add(BigInteger.valueOf(b & 0xFF));
        }
        do {
            BigInteger[] division = value.divideAndRemainder(BASE);
            value = division[0];
            BigInteger reminder = division[1];
            result.append(INDEX.charAt(reminder.intValue()));
        } while (!value.equals(BigInteger.ZERO));

        return result.reverse().toString();
    }

    /**
     * Decodes Base62 string to byte array
     *
     * @param base62 base62 string (max 43 symbols)
     * @return hexadecimal installation id (64 symbols)
     */
    public static byte[] toByteArray(String base62) {
        if (base62.length() > 43) {
            throw new IllegalArgumentException("Input shall not be longer than 43 characters, is " + base62.length());
        }

        byte[] result = new byte[32];
        for (int i = 0; i < 32; i++) {
            result[i] = 0;
        }

        BigInteger value = BigInteger.ZERO;
        for (int i = 0; i < base62.length(); i++) {
            long symbolOrdinal = INDEX.indexOf(base62.charAt(i));
            value = value.multiply(BASE).add(BigInteger.valueOf(symbolOrdinal));
        }

        byte[] rigthBytes = value.toByteArray();

        if (rigthBytes.length > 32 &&
            // Ingore signum leading zero
            !(rigthBytes.length == 33 && rigthBytes[0] == 0)) {
            throw new IllegalArgumentException("Input entropy shall not be greater than 256 bits, is " + rigthBytes.length);
        }

        // Copy from back, pad left with 0 if needed
        for (int i = 0; i < 32; i++) {
            int idx = rigthBytes.length - 1 - i;
            result[31 - i] = idx >= 0 ? rigthBytes[idx] : 0;
        }

        return result;
    }
}
