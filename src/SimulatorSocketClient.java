import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SimulatorSocketClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private BlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<>();

    public SimulatorSocketClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        new Thread(() -> {
            try {
                waitForMessages();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
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

    /**
     * Method to wait for messages from the server
     * @throws IOException ..
     * @throws ClassNotFoundException ..
     */
    public void waitForMessages() throws IOException, ClassNotFoundException, InterruptedException {
        while (true){
            int num = (int) in.readObject();
            System.out.println("The message number is " + num);
            blockingQueue.put(num);
            // TODO: handle the messages
        }
    }

    /**
     * Method to constantly take messages
     */
    public int grabMessage() throws InterruptedException {
        return blockingQueue.take();
    }



    /**
     * Method to close the socket
     * @throws IOException
     */
    public void close() throws IOException {
        socket.close();
        out.close();
    }
}
