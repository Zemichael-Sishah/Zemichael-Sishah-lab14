import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Client for Lab 14 – connects to a server, performs a handshake,
 * and sends integer factorization requests.
 */
public class Client {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * Constructs a client and connects to the given host/port.
     */
    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true); // auto-flush
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Used by the tester to check the underlying socket.
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Sends the handshake key "12345" to the server.
     */
    public void handshake() {
        out.println("12345");
        out.flush();
    }

    /**
     * Sends a number as a request to the server and returns the response line.
     */
    public String request(String message) throws IOException {
        out.println(message);
        out.flush();
        return in.readLine();
    }

    /**
     * Disconnects the client cleanly.
     */
    public void disconnect() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            // ignore
        }
        if (out != null) {
            out.close();
        }
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            // ignore
        }
    }
}
