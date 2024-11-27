package com.intlimit.grsplugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

import com.intellij.openapi.diagnostic.Logger;
import com.redhat.devtools.lsp4ij.server.CannotStartProcessException;
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider;

public class GrSConnectionProvider implements StreamConnectionProvider {

    private final Logger LOG = Logger.getInstance(GrSConnectionProvider.class);
    private final int port;

    private Socket sock;

    public GrSConnectionProvider(int port) {
        this.port = port;
    }

    @Override
    public void start() throws CannotStartProcessException {
        LOG.info("Connecting to GrS LSP Server, at Port " + port + "...");

        try {
            sock = new Socket((String) null, port);
        } catch (IOException e) {
            throw new CannotStartProcessException(e);
        }
    }

    @Override
    public InputStream getInputStream() {
        if (sock == null) return null;

        try {
            return sock.getInputStream();
        } catch (IOException e) {
            LOG.error(e);

            return null;
        }
    }

    @Override
    public OutputStream getOutputStream() {
        if (sock == null) return null;

        try {
            return sock.getOutputStream();
        } catch (IOException e) {
            LOG.error(e);

            return null;
        }
    }

    @Override
    public void stop() {
        if (sock != null) {
            try {
                sock.close();
            } catch (IOException e) {
                LOG.error(e);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GrSConnectionProvider that)) return false;
        return port == that.port;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(port);
    }
}
