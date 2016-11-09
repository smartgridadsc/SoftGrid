package org.openmuc.j60870;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by prageethmahendra on 16/6/2016.
 */
public class SecureServerThread  extends Thread {

    private final ServerSocket serverSocket;
    private final ConnectionSettings settings;
    private final int maxConnections;
    private final ServerSapListener serverSapListener;

    private boolean stopServer = false;
    private volatile int numConnections = 0;

    SecureServerThread(ServerSocket serverSocket, ConnectionSettings settings, int maxConnections,
                 ServerSapListener serverSapListener) {
        this.serverSocket = serverSocket;
        this.settings = settings;
        this.maxConnections = maxConnections;
        this.serverSapListener = serverSapListener;
    }

    private class SecureConnectionHandler extends Thread {

        private final Socket socket;
        private final SecureServerThread serverThread;

        public SecureConnectionHandler(Socket socket, SecureServerThread serverThread) {
            this.socket = socket;
            this.serverThread = serverThread;
        }

        @Override
        public void run() {
            SecureConnection serverConnection;
            try {
                serverConnection = new SecureConnection(socket, serverThread, settings);
            } catch (IOException e) {
                numConnections--;
                serverSapListener.connectionAttemptFailed(e);
                return;
            }
            serverSapListener.connectionIndication(serverConnection);
        }
    }

    @Override
    public void run() {

        ExecutorService executor = Executors.newFixedThreadPool(maxConnections);
        try {

            Socket clientSocket = null;

            while (true) {
                try {
                    clientSocket = serverSocket.accept();
                } catch (IOException e) {
                    if (stopServer == false) {
                        serverSapListener.serverStoppedListeningIndication(e);
                    }
                    return;
                }

                if (numConnections < maxConnections) {
                    numConnections++;
                    SecureConnectionHandler connectionHandler = new SecureConnectionHandler(clientSocket, this);
                    executor.execute(connectionHandler);
                }
                else {
                    serverSapListener.connectionAttemptFailed(new IOException(
                            "Maximum number of connections reached. Ignoring connection request. Maximum number of connections: "
                                    + maxConnections));
                }

            }
        } finally {
            executor.shutdown();
        }
    }

    void connectionClosedSignal() {
        numConnections--;
    }

    /**
     * Stops listening for new connections. Existing connections are not touched.
     */
    void stopServer() {
        stopServer = true;
        if (serverSocket.isBound()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
            }
        }
    }

}

