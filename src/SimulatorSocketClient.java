import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SimulatorSocketClient {
    private Socket socket;
    private ObjectOutputStream out;

    public SimulatorSocketClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());

    }

    /**
     * Method to send a message based upon a key of int messages given
     * @param messageNumber the message number
     * @throws IOException for errors
     */
    public void sendMessage(int messageNumber) throws IOException {
        if (out != null){
            out.writeObject(messageNumber);
        }
    }
}
