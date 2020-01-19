package com.mwb.maf.core.web;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 描述:
 *
 * @author mengweibo@kanzhun.com
 * @create 2020/1/16
 */
public class ServletOutputStreamCopier extends ServletOutputStream {
    private OutputStream outputStream;
    private ByteArrayOutputStream copy;

    public ServletOutputStreamCopier(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.copy = new ByteArrayOutputStream(1024);
    }

    @Override
    public void write(int b) throws IOException {
        this.outputStream.write(b);
        this.copy.write(b);
    }

    public byte[] getCopy() {
        return this.copy.toByteArray();
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
    }
}
