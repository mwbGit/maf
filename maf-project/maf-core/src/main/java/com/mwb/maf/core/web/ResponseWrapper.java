package com.mwb.maf.core.web;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * 描述:
 *
 * @author mengweibo@kanzhun.com
 * @create 2020/1/16
 */
public class ResponseWrapper extends HttpServletResponseWrapper {
    public static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";
    private ServletOutputStream outputStream;
    private PrintWriter writer;
    private ServletOutputStreamCopier copier;

    public ResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (this.writer != null) {
            throw new IllegalStateException("getWriter() has already been called on this response.");
        } else {
            if (this.outputStream == null) {
                this.outputStream = this.getResponse().getOutputStream();
                this.copier = new ServletOutputStreamCopier(this.outputStream);
            }

            return this.copier;
        }
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (this.outputStream != null) {
            throw new IllegalStateException("getOutputStream() has already been called on this response.");
        } else {
            if (this.writer == null) {
                this.copier = new ServletOutputStreamCopier(this.getResponse().getOutputStream());
                this.writer = new PrintWriter(new OutputStreamWriter(this.copier, "UTF-8"), true);
            }

            return this.writer;
        }
    }

    @Override
    public void flushBuffer() throws IOException {
        if (this.writer != null) {
            this.writer.flush();
        } else if (this.outputStream != null) {
            this.copier.flush();
        }

    }

    public byte[] getCopy() {
        return this.copier != null ? this.copier.getCopy() : new byte[0];
    }
}
