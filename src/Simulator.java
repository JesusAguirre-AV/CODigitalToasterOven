import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Simulator {
    //Fields
    Timer timer = new Timer();

    boolean powerIsOn = false;
    boolean lightIsOn = false;
    boolean doorIsOpen = false;
    boolean topHeaterIsOn = false;
    boolean heatersDead = false;
    boolean bottomHeaterIsOn = false;
    public SimulatorSocketClient socketClient;
    int cookTime = 0;
    TimerTask task = new TimerTask() {
        @Override
        public void run() {

            //Here is where it will handle the pause check, if it is false, it should continue, otherwise it waits
            synchronized (lock){
                while (timerPause){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            try {
                handleHeaters();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("The current cavity temperature is: " + cavityTemp);

            if(!threadLive){
                timer.cancel();
            }

        }
    };

    /**Methods and objects used to halt the cooking process or continue it should certain events occur. The lock object
     * is used to */
    boolean timerPause = false;
    Object lock = new Object();
    public void pause(){
        synchronized (lock){
            timerPause = true;
        }
    }
    public void resume(){
        synchronized (lock){
            timerPause = false;
            lock.notifyAll();
        }
    }
    interruptCheckThread interrupter = new interruptCheckThread();

    int cavityTemp = 70;
    /**An array of integers that stores 4 ints with the following values
     * [minutes, seconds, temperature, mode]
     * Mode key:
     * 1 - Roast (both heaters)
     * 2 - Bake (bottom heater)
     * 3 - Broil (top heater)
     */
    int cookingInfo[] = new int[4];
    /**
     * An array of booleans that shows which heater will be used during the cook.
     * The first element represents the top heater and the second element represents the bottom heater
     */
    boolean heatersUsed[] = new boolean[2];
    /**
     * Booleans relevant to the threads, threadLive is a way to kill the threads in the event of an interruption or
     * if the timer runs out. stopButtonPressed will serve a similar purpose but more to help with event handling from
     * the javaFX or any future port connections.
     * */
    boolean threadLive=false;
    boolean stopButtonPressed=false;

    /**
     * Constructor that just initializes the new socket object
     * @param host the host info
     * @param port the port
     */
    public Simulator(String host, int port){
        try {
            socketClient = new SimulatorSocketClient(host, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Toggle methods for toggleable fields
    public void togglePower() throws IOException {

        /**
         * I think this is where we should start/stop the system thread
         * -G
         */

        socketClient.sendMessage(1);
        powerIsOn = !powerIsOn;
        System.out.print("Turned power ");
        if(powerIsOn){
            System.out.println("on");
        }
        else{
            System.out.println("off");
        }
    }
    public void toggleLight() throws IOException {
        socketClient.sendMessage(4);
        lightIsOn = !lightIsOn;
        System.out.print("Turned light ");
        if(lightIsOn){
            System.out.println("on.");
        }
        else{
            System.out.println("off.");
        }
    }
    public void toggleDoorSensor() throws IOException {
        socketClient.sendMessage(5);
        doorIsOpen = !doorIsOpen;
        if(doorIsOpen){
            System.out.print("Opened ");
        }
        else{
            System.out.print("Closed ");
        }
        System.out.println("door.");
    }
    public void toggleTopHeater() throws IOException {
        socketClient.sendMessage(2);
        if(heatersDead && !topHeaterIsOn){
            System.out.println("Top heater is dead.");
            return;
        }
        topHeaterIsOn = !topHeaterIsOn;
        System.out.print("Turned top heater ");
        if(topHeaterIsOn){
            System.out.println("on");
        }
        else{
            System.out.println("off");
        }
    }
    public void toggleBottomHeater() throws IOException {
        socketClient.sendMessage(3);
        if(heatersDead && !bottomHeaterIsOn){
            System.out.println("Bottom heater is dead.");
            return;
        }
        bottomHeaterIsOn = !bottomHeaterIsOn;
        System.out.print("Turned bottom heater ");
        if(bottomHeaterIsOn){
            System.out.println("on");
        }
        else{
            System.out.println("off");
        }
    }

    //Method to set the cooking info when a packet is recieved from FXdevice simulator over the socket.
    public void setCookingInfo(int[] info){
        cookingInfo[0] = info[0];
        cookingInfo[1] = info[1];
        cookingInfo[2] = info[2];
        cookingInfo[3] = info[3];
    }

    /**
     * Method to start the cooking process. When it is called the method checks to see if the power is on. If the power is on and
     * the door is closed then the cook starts (Begins a thread method that starts a cook).
     */
    public Boolean checkCook(){
        if(!powerIsOn){
            System.out.println("Power is off, cannot start cooking.");
            return false;
        }
        else if(doorIsOpen){
            System.out.println("Door is open, cannot start cooking.");
            return false;
        }
        else {
            cookTime = convertMinSecToSec();
            int temp = cookingInfo[2];
            if(cookingInfo[3] == 1 || cookingInfo[3] == 2){
                heatersUsed[1] = true;
            }
            if(cookingInfo[3] == 1 || cookingInfo[3] == 3){
                heatersUsed[0] = true;
            }
            return true;
        }
    }


    /**
     * Method to handle the temperature in the oven and turn on/off the heaters.
     * If the cavity temperature exceeds 500 degrees, then the heaters are killed.
     * If the temperature is below the set temperature, turn on the heaters
     * If the temperature is above the set temperature, turn off the heaters
     * @throws IOException
     */
    public void handleHeaters() throws IOException {
        if(cavityTemp >= 500){
            heatersDead = true;
            topHeaterIsOn = false;
            bottomHeaterIsOn = false;
        }
        else if(cavityTemp < cookingInfo[2]){
            int tempChange = 0;
            if(heatersUsed[0]){
                tempChange += 5;
                if(!topHeaterIsOn){
                    toggleTopHeater();
                }
            }
            if(heatersUsed[1]){
                tempChange += 5;
                if(!bottomHeaterIsOn){
                    toggleBottomHeater();
                }
            }
            cavityTemp += tempChange;
        }
        else if(cavityTemp > cookingInfo[2]){
            toggleTopHeater();
            toggleBottomHeater();
            cavityTemp -= 2;
        }
    }

    /**
     * Stops the cook, but doesn't reset the time or anything
     * Saves the time left on the timer and the temperature.
     */
    public synchronized void stopCooking(){
        pause();
        stopButtonPressed=false;
        threadLive=false;
        topHeaterIsOn = false;
        bottomHeaterIsOn = false;
        lightIsOn = true;
        cookingInfo[0] = cookTime/60;
        cookingInfo[1] = cookTime%60;
    }

    /**
     * Resets the oven
     * @throws IOException
     */
    public void resetOven() throws IOException {
        stop();
        toggleLight();
        cookingInfo = new int[4];
    }

    /**
     * Method to start the cook
     * @param temp
     * @param cookTime
     */
    public void startCooking(int temp, int cookTime){
        cookingInfo[2] = temp;
        System.out.println("Starting cook at " + temp + " degrees fahrenheit" +
                " for " +
                cookTime + " seconds (" + cookingInfo[0] + " minutes " + cookingInfo[1] + " seconds)");

        timer.scheduleAtFixedRate(task, 0, 1000);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Cancelling the task after " + cookTime + " seconds.");
                timer.cancel();
            }
        }, cookTime*1000);

        //Turn on all the stuff
        //Watch interrupts while timer runs
        //At interrupt, pause
        //At timer finish, stop and reset

        //setting up threads to run and cook

        /**
         * I don't think we need this boolean anymore do we?
         */
        threadLive=true;

        interrupter.start();
    }

    /**
     * Method to stop the cook, but save the time and the temperature
     */
    public synchronized void stop(){
        pause();
        threadLive=false;
        boolean lightIsOn = false;
        boolean doorIsClosed = true;
        boolean topHeaterIsOn = false;
        boolean bottomHeaterIsOn = false;
        cookingInfo[0] = 0;
        cookingInfo[1] = 0;
        cookingInfo[2] = 1;
    }

    /**
     * Method that converts the minutes from the first two elements of cookingInfo into seconds and returns them as an int
     * @return
     */
    private int convertMinSecToSec(){
        return(cookingInfo[0]*60 + cookingInfo[1]);
    }


    public void printInfo(){
        System.out.print("\n\n\nPower: ");
        if(powerIsOn){
            System.out.println("on");
        }
        else{
            System.out.println("off");
        }
        System.out.print("Door: ");
        if(doorIsOpen){
            System.out.println("open");
        }
        else{
            System.out.println("closed");
        }
        System.out.print("Light: ");
        if(lightIsOn){
            System.out.println("on");
        }
        else{
            System.out.println("off");
        }
        System.out.print("Top heater: ");
        if(topHeaterIsOn){
            System.out.println("on");
        }
        else{
            System.out.println("off");
        }
        System.out.print("Bottom heater: ");
        if(bottomHeaterIsOn){
            System.out.println("on");
        }
        else{
            System.out.println("off");
        }
        System.out.println("\nCooking cavity temperature: " + cavityTemp);
        System.out.println("Current cooking info:");
        System.out.println("[" + cookingInfo[0] + ", " + cookingInfo[1] + ", " +  cookingInfo[2] + ", " + cookingInfo[3] + "]");
    }

    /**
     * Thread to act as the simulator cooking conditions other than the timer. This is meant to check for interruptions
     * such as the oven being opened, the pause/reset button pressed, and any other possible interruptions we may wish
     * to include in the future. Calls the stopCooking method that will pause the timer.
     */
    public class interruptCheckThread extends Thread{
        @Override
        public void run(){
            while (threadLive){
                if(!powerIsOn && doorIsOpen && stopButtonPressed){stopCooking();}
            }
        }
    }
}