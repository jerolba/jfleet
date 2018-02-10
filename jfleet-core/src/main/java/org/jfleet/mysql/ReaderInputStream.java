/**
 * Copyright 2002-2017 The Apache Software Foundation
 *
 * This file is part of Apache Commons IO
 * This product includes software developed at
 * The Apache Software Foundation (http://www.apache.org/).
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is copied from:
 * https://github.com/apache/commons-io/blob/master/src/main/java/org/apache/commons/io/input/ReaderInputStream.java
 * to avoid including entire Commons IO dependency.
 */
package org.jfleet.mysql;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

public class ReaderInputStream extends InputStream {

    /**
     * Represents the end-of-file (or stream).
     */
    public static final int EOF = -1;

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private final Reader reader;
    private final CharsetEncoder encoder;

    /**
     * CharBuffer used as input for the decoder. It should be reasonably large as we read data from the underlying
     * Reader into this buffer.
     */
    private final CharBuffer encoderIn;

    /**
     * ByteBuffer used as output for the decoder. This buffer can be small as it is only used to transfer data from the
     * decoder to the buffer provided by the caller.
     */
    private final ByteBuffer encoderOut;

    private CoderResult lastCoderResult;
    private boolean endOfInput;

    /**
     * Construct a new {@link ReaderInputStream}.
     *
     * @param reader
     *            the target {@link Reader}
     * @param encoder
     *            the charset encoder
     * @since 2.1
     */
    public ReaderInputStream(final Reader reader, final CharsetEncoder encoder) {
        this(reader, encoder, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Construct a new {@link ReaderInputStream}.
     *
     * @param reader
     *            the target {@link Reader}
     * @param encoder
     *            the charset encoder
     * @param bufferSize
     *            the size of the input buffer in number of characters
     * @since 2.1
     */
    public ReaderInputStream(final Reader reader, final CharsetEncoder encoder, final int bufferSize) {
        this.reader = reader;
        this.encoder = encoder;
        this.encoderIn = CharBuffer.allocate(bufferSize);
        this.encoderIn.flip();
        this.encoderOut = ByteBuffer.allocate(128);
        this.encoderOut.flip();
    }

    /**
     * Construct a new {@link ReaderInputStream}.
     *
     * @param reader
     *            the target {@link Reader}
     * @param charset
     *            the charset encoding
     * @param bufferSize
     *            the size of the input buffer in number of characters
     */
    public ReaderInputStream(final Reader reader, final Charset charset, final int bufferSize) {
        this(reader,
                charset.newEncoder()
                        .onMalformedInput(CodingErrorAction.REPLACE)
                        .onUnmappableCharacter(CodingErrorAction.REPLACE),
                bufferSize);
    }

    /**
     * Construct a new {@link ReaderInputStream} with a default input buffer size of 1024 characters.
     *
     * @param reader
     *            the target {@link Reader}
     * @param charset
     *            the charset encoding
     */
    public ReaderInputStream(final Reader reader, final Charset charset) {
        this(reader, charset, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Fills the internal char buffer from the reader.
     *
     * @throws IOException
     *             If an I/O error occurs
     */
    private void fillBuffer() throws IOException {
        if (!endOfInput && (lastCoderResult == null || lastCoderResult.isUnderflow())) {
            encoderIn.compact();
            final int position = encoderIn.position();
            // We don't use Reader#read(CharBuffer) here because it is more efficient
            // to write directly to the underlying char array (the default implementation
            // copies data to a temporary char array).
            final int c = reader.read(encoderIn.array(), position, encoderIn.remaining());
            if (c == EOF) {
                endOfInput = true;
            } else {
                encoderIn.position(position + c);
            }
            encoderIn.flip();
        }
        encoderOut.compact();
        lastCoderResult = encoder.encode(encoderIn, encoderOut, endOfInput);
        encoderOut.flip();
    }

    /**
     * Read the specified number of bytes into an array.
     *
     * @param b
     *            the byte array to read into
     * @param off
     *            the offset to start reading bytes into
     * @param len
     *            the number of bytes to read
     * @return the number of bytes read or <code>-1</code> if the end of the stream has been reached
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    public int read(final byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException("Byte array must not be null");
        }
        if (len < 0 || off < 0 || (off + len) > b.length) {
            throw new IndexOutOfBoundsException("Array Size=" + b.length + ", offset=" + off + ", length=" + len);
        }
        int read = 0;
        if (len == 0) {
            return 0; // Always return 0 if len == 0
        }
        while (len > 0) {
            if (encoderOut.hasRemaining()) {
                final int c = Math.min(encoderOut.remaining(), len);
                encoderOut.get(b, off, c);
                off += c;
                len -= c;
                read += c;
            } else {
                fillBuffer();
                if (endOfInput && !encoderOut.hasRemaining()) {
                    break;
                }
            }
        }
        return read == 0 && endOfInput ? EOF : read;
    }

    /**
     * Read the specified number of bytes into an array.
     *
     * @param b
     *            the byte array to read into
     * @return the number of bytes read or <code>-1</code> if the end of the stream has been reached
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    public int read(final byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * Read a single byte.
     *
     * @return either the byte read or <code>-1</code> if the end of the stream has been reached
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    public int read() throws IOException {
        for (;;) {
            if (encoderOut.hasRemaining()) {
                return encoderOut.get() & 0xFF;
            }
            fillBuffer();
            if (endOfInput && !encoderOut.hasRemaining()) {
                return EOF;
            }
        }
    }

    /**
     * Close the stream. This method will cause the underlying {@link Reader} to be closed.
     *
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        reader.close();
    }
}
