public class Simulator {
    //Fields
    boolean powerIsOn = false;
    boolean lightIsOn = false;
    boolean doorIsOpen = false;
    boolean topHeaterIsOn = false;
    boolean heatersDead = false;
    boolean isCooking = false;
    boolean bottomHeaterIsOn = false;

    //The live temperature of the cooking cavity
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
     * Booleans relevant to the threads
     * */
    boolean threadLive=false;
    boolean stopButtonPressed=false;

    //No explicitly defined constructor needed since only one will be made.


    //Method to just toggle everything used as a test method
    public void testToggles(){
        this.printInfo();
        togglePower();
        toggleLight();
        toggleDoorSensor();
        toggleBottomHeater();
        toggleTopHeater();
        this.printInfo();
    }

    //Toggle methods for toggleable fields
    public void togglePower() {
        powerIsOn = !powerIsOn;
        System.out.print("Turned power ");
        if(powerIsOn){
            System.out.println("on");
        }
        else{
            System.out.println("off");
        }
    }
    public void toggleLight(){
        lightIsOn = !lightIsOn;
        System.out.print("Turned light ");
        if(lightIsOn){
            System.out.println("on.");
        }
        else{
            System.out.println("off.");
        }
    }
    public void toggleDoorSensor(){
        doorIsOpen = !doorIsOpen;
        if(doorIsOpen){
            System.out.print("Opened ");
        }
        else{
            System.out.print("Closed ");
        }
        System.out.println("door.");
    }
    public void toggleTopHeater(){
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
    public void toggleBottomHeater(){
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
            int cookTime = convertMinSecToSec();
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


    public void handleHeaters(){
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

    public synchronized void stopCooking(){
        stopButtonPressed=false;
        threadLive=false;
        topHeaterIsOn = false;
        bottomHeaterIsOn = false;
        lightIsOn = true;
        /** Miguel, this is where I need you to put the saved time whenever stop is called
         * -Greg
         */
        int cookTime = 0;   //timer.getTIME
        cookingInfo[0] = cookTime/60;
        cookingInfo[1] = cookTime%60;
    }

    public void resetOven(){
        stop();
        toggleLight();
        cookingInfo = new int[4];
    }


    public void startCooking(int temp, int cookTime){
        System.out.println("Starting cook at " + temp + " degrees fahrenheit for " + cookTime + " seconds (" + cookingInfo[0] + " minutes " + cookingInfo[1] + " seconds)");
        //Start a timer for cooktime
        int startTime = (int)System.currentTimeMillis()/1000;
        //Turn on all the stuff
        //Watch interrupts while timer runs
        //At interrupt, pause
        //At timer finish, stop and reset

        //setting up threads to run and cook
        threadLive=true;

        timerThread timerRunner = new timerThread(startTime, cookTime);
        tempSensorThread tempRunner = new tempSensorThread();
        pauseButtonSensor pauseRunner = new pauseButtonSensor();

        timerRunner.start();
        tempRunner.start();
        pauseRunner.start();
    }


    public void stop(){
        threadLive=false;
        boolean lightIsOn = false;
        boolean doorIsClosed = true;
        boolean topHeaterIsOn = false;
        boolean bottomHeaterIsOn = false;
        cookingInfo[0] = 0;
        cookingInfo[1] = 0;
        cookingInfo[2] = 1;
    }

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


    private class tempSensorThread extends Thread{
        @Override
        public void run(){
            while (threadLive){
                handleHeaters();
            }
        }
    }
    private class pauseButtonSensor extends Thread{
        @Override
        public void run(){
            while (threadLive){
                if(stopButtonPressed){stopCooking();}
            }
        }
    }
    private class timerThread extends Thread{
        int startTime, cookTime;
        timerThread(int startTime, int cookTime){
            this.startTime=startTime;
            this.cookTime=cookTime;
        }
        public synchronized boolean timeUp(){
            if(((int)System.currentTimeMillis()/1000)-startTime >= cookTime){
                return true;
            }
            return false;
        }

        @Override
        public void run(){
            while (threadLive){
                if(!timeUp()){
                    resetOven();
                }
            }
        }
    }
    public class doorThread extends Thread{
        @Override
        public void run(){
            while (threadLive){
                if(doorIsOpen){stopCooking();}
            }
        }
    }
}


