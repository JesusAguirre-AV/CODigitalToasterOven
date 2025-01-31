public class Simulator {
    boolean powerIsOn = false;
    boolean lightIsOn = false;
    boolean doorIsOpen = false;
    boolean topHeaterIsOn = false;
    boolean bottomHeaterIsOn = false;
    int cavityTemp = 70;
    int cookingInfo[] = new int[4];

    public Simulator(){
    }

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
        bottomHeaterIsOn = !bottomHeaterIsOn;
        System.out.print("Turned bottom heater ");
        if(bottomHeaterIsOn){
            System.out.println("on");
        }
        else{
            System.out.println("off");
        }
    }

    public void setCookingInfo(int[] info){
        cookingInfo[0] = info[0];
        cookingInfo[1] = info[1];
        cookingInfo[2] = info[2];
        cookingInfo[3] = info[3];
    }

    public Boolean startCooking(){
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
            int mode = cookingInfo[3];

            System.out.println("Starting cook at " + temp + " degrees fahrenheit for " + cookTime + " seconds (" + cookingInfo[0] + " minutes " + cookingInfo[1] + " seconds)");
            //Start a timer for cooktime
            int startTime = (int)System.currentTimeMillis()/1000;
            //Turn on all the stuff
            //Watch interrupts while timer runs
            //At interrupt, pause
            //At timer finish, stop and reset
            /**while( ((int)System.currentTimeMillis()/1000)-startTime <= cookTime){

             }**/
            return true;
        }
    }



    public void stopCooking(){
        boolean powerIsOn = false;
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

    public Boolean isCooking(){return false;}

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
}


