package com.enosiot.enos.iot_mqtt_sdk.core.codec;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class EnosByteArrayOutputStream extends OutputStream {
    private final static int CACHE_BYTES_NUM = Runtime.getRuntime().availableProcessors();
    private final static BlockingQueue<BytesInfo> CACHED_BYTES = new LinkedBlockingQueue<>();

    static {
        for (int i = 0; i < CACHE_BYTES_NUM; ++i) {
            CACHED_BYTES.add(new BytesInfo(new byte[512 * 1024], true));
        }
    }

    @AllArgsConstructor
    private static class BytesInfo {
        private byte[] bytes;
        private boolean fromCache;
    }

    /**
     * The buffer where data is stored.
     */
    private BytesInfo buf;

    /**
     * The number of valid bytes in the buffer.
     */
    private int count;

    private AtomicBoolean closed = new AtomicBoolean();
    private byte[] data = null;

    /**
     * Creates a new byte array output stream. The buffer capacity is
     * initially 32 bytes, though its size increases if necessary.
     */
    public EnosByteArrayOutputStream() {
        buf = CACHED_BYTES.poll();

        if (buf == null) {
            buf = new BytesInfo(new byte[4096], false);
        }
        count = 0;
    }

    /**
     * Increases the capacity if necessary to ensure that it can hold
     * at least the number of elements specified by the minimum
     * capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     * @throws OutOfMemoryError if {@code minCapacity < 0}.  This is
     *                          interpreted as a request for the unsatisfiably large capacity
     *                          {@code (long) Integer.MAX_VALUE + (minCapacity - Integer.MAX_VALUE)}.
     */
    private void ensureCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - buf.bytes.length > 0) {
            grow(minCapacity);
        }
    }

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Increases the capacity to ensure that it can hold at least the
     * number of elements specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = buf.bytes.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            newCapacity = hugeCapacity(minCapacity);
        }
        byte[] tmpBuf = Arrays.copyOf(buf.bytes, newCapacity);

        if (buf.fromCache) {
            CACHED_BYTES.add(buf);
            Preconditions.checkArgument(CACHED_BYTES.size() <= CACHE_BYTES_NUM, "BUG");
        }

        buf = new BytesInfo(tmpBuf, false);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) {
            throw new OutOfMemoryError();
        }
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    /**
     * Writes the specified byte to this byte array output stream.
     *
     * @param b the byte to be written.
     */
    @Override
    public void write(int b) {
        ensureCapacity(count + 1);
        buf.bytes[count] = (byte) b;
        count += 1;
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array
     * starting at offset <code>off</code> to this byte array output stream.
     *
     * @param b   the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     */
    @Override
    public void write(byte b[], int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(count + len);
        System.arraycopy(b, off, buf.bytes, count, len);
        count += len;
    }

    /**
     * Writes the complete contents of this byte array output stream to
     * the specified output stream argument, as if by calling the output
     * stream's write method using <code>out.write(buf, 0, count)</code>.
     *
     * @param out the output stream to which to write the data.
     * @throws IOException if an I/O error occurs.
     */
    public void writeTo(OutputStream out) throws IOException {
        out.write(buf.bytes, 0, count);
    }

    /**
     * Resets the <code>count</code> field of this byte array output
     * stream to zero, so that all currently accumulated output in the
     * output stream is discarded. The output stream can be used again,
     * reusing the already allocated buffer space.
     */
    public void reset() {
        count = 0;
    }

    /**
     * Creates a newly allocated byte array. Its size is the current
     * size of this output stream and the valid contents of the buffer
     * have been copied into it.
     *
     * @return the current contents of this output stream, as a byte array.
     * @see java.io.ByteArrayOutputStream#size()
     */
    public byte[] toByteArray() {
        if (buf != null) {
            initData();
        }
        Preconditions.checkArgument(data != null, "BUG");
        return data;
    }

    /**
     * Returns the current size of the buffer.
     *
     * @return the value of the <code>count</code> field, which is the number
     * of valid bytes in this output stream.
     */
    public int size() {
        return count;
    }

    /**
     * Converts the buffer's contents into a string decoding bytes using the
     * platform's default character set. The length of the new <tt>String</tt>
     * is a function of the character set, and hence may not be equal to the
     * size of the buffer.
     *
     * <p> This method always replaces malformed-input and unmappable-character
     * sequences with the default replacement string for the platform's
     * default character set. The {@linkplain java.nio.charset.CharsetDecoder}
     * class should be used when more control over the decoding process is
     * required.
     *
     * @return String decoded from the buffer's contents.
     * @since JDK1.1
     */
    @Override
    public String toString() {
        return new String(buf.bytes, 0, count, StandardCharsets.UTF_8);
    }

    /**
     * Converts the buffer's contents into a string by decoding the bytes using
     * the named {@link java.nio.charset.Charset charset}. The length of the new
     * <tt>String</tt> is a function of the charset, and hence may not be equal
     * to the length of the byte array.
     *
     * <p> This method always replaces malformed-input and unmappable-character
     * sequences with this charset's default replacement string. The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param charsetName the name of a supported
     *                    {@link java.nio.charset.Charset charset}
     * @return String decoded from the buffer's contents.
     * @throws UnsupportedEncodingException If the named charset is not supported
     * @since JDK1.1
     */
    public String toString(String charsetName)
            throws UnsupportedEncodingException {
        return new String(buf.bytes, 0, count, charsetName);
    }

    /**
     * Closing a <tt>ByteArrayOutputStream</tt> has no effect. The methods in
     * this class can be called after the stream has been closed without
     * generating an <tt>IOException</tt>.
     */
    @Override
    public void close() throws IOException {
        if (!closed.getAndSet(true)) {
            Preconditions.checkArgument(buf != null, "BUG");
            initData();

            if (buf.fromCache) {
                // return the buffer to the pool
                CACHED_BYTES.add(buf);
                Preconditions.checkArgument(CACHED_BYTES.size() <= CACHE_BYTES_NUM, "BUG");
            }

            buf = null;
        }
    }

    private void initData() {
        if (buf != null && (data == null || data.length != count)) {
            data = Arrays.copyOf(buf.bytes, count);
        }
    }

}
