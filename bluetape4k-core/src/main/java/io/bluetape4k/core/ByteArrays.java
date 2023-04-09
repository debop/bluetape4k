package io.bluetape4k.core;

import java.nio.ByteBuffer;

/**
 *
 */
public abstract class ByteArrays {

    public static byte[] intToByteArray(int number) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(4).putInt(number);
        buffer.flip();
        byte[] result = new byte[4];
        buffer.get(result);
        return result;
    }

    public static byte[] longToByteArray(long number) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(8).putLong(number);
        buffer.flip();
        byte[] result = new byte[8];
        buffer.get(result);
        return result;
    }

    public static int byteArrayToInt(byte[] bytes) {
        return byteArrayToInt(bytes, 0);
    }

    public static int byteArrayToInt(byte[] bytes, int offset) {
        return ByteBuffer.wrap(bytes, offset, 4).getInt();
    }

    public static long byteArrayToLong(byte[] bytes) {
        return byteArrayToLong(bytes, 0);
    }

    public static long byteArrayToLong(byte[] bytes, int offset) {
        return ByteBuffer.wrap(bytes, offset, 8).getLong();
    }
}
