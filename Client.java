import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    //Constructs a client and connects to the given host / port.
    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true); 
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    // underlying socket checker     
    public Socket getSocket() {
        return socket;
    }

    // sends the handshake key "12345" to the server.
    public void handshake() {
        out.println("12345");
        out.flush();
    }

    // sends number as a request to the server 
    //returns response line.
    public String request(String message) throws IOException {
        out.println(message);
        out.flush();
        return in.readLine();
    }

    // disconnects client 
    public void disconnect() {
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
