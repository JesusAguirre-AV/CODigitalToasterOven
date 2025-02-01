import java.io.IOException;

public class Main {
    //Main will serve to test the toaster oven simulation
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
        // need to start a thread for the main function that calls functions in the functionality program.



        // create the object of the simulator
        Simulator simulator = new Simulator("localhost", 1234);

        // just testing that we are getting the messages
        simulator.togglePower();

        // call any methods on this simulator here.





        // close the socket
        simulator.socketClient.close();
    }
}