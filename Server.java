import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;


public class Server {
    private ServerSocket serverSocket;
    private ArrayList<LocalDateTime> connectedTimes;


    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        connectedTimes = new ArrayList<>();
    }

    // each accepted client, records the connection time and handler thread manages handshake and requests.
    public void serve(int numberOfClients) {
        for (int i = 0; i < numberOfClients; i++) {
            try {
                Socket clientSocket = serverSocket.accept();
                // Record connection time when the client is accepted
                connectedTimes.add(LocalDateTime.now());

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // returns sorted list of all client connection times.
    public ArrayList<LocalDateTime> getConnectedTimes() {
        ArrayList<LocalDateTime> copy = new ArrayList<>(connectedTimes);
        Collections.sort(copy);
        return copy;
    }

    // closes server socket.
    public void disconnect() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {}
    }

    // counts the number of positive integer factors of n.
    private int countFactors(int n) {
        int count = 0;
        for (int i = 1; (long) i * i <= n; i++) {
            if (n % i == 0) {
                count += 2; 
            }
        }
        int root = (int) Math.sqrt(n);
        if (root * root == n) {
            // perfect square counted one factor twice
            count--;
        }
        return count;
    }

    // internal handler for each client connection.
    private class ClientHandler implements Runnable {
        private Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        
        @Override
        public void run() {
            BufferedReader in = null;
            PrintWriter out = null;

            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // First line must be handshake
                String firstLine = in.readLine();
                if (!"12345".equals(firstLine)) {
                    out.println("couldn't handshake");
                    out.flush();
                    return;
                }

                //  factorization requests
                String line;
                while ((line = in.readLine()) != null) {
                    String response;
                    try {
                        int value = Integer.parseInt(line.trim());
                        int factors = countFactors(value);
                        response = "The number " + value + " has " + factors + " factors";
                    } catch (Exception e) {
                        response = "There was an exception on the server";
                    }
                    out.println(response);
                    out.flush();
                }

            } catch (IOException e) {} 
            finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {}
                if (out != null) {
                    out.close();
                }
                try {
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }
                } catch (IOException e) {}
            }
        }
    }
}
