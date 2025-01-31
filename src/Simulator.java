public class Simulator {
    boolean powerIsOn = false;
    boolean lightIsOn = false;
    boolean doorIsClosed = true;
    boolean topHeaterIsOn = false;
    boolean bottomHeaterIsOn = false;
    int cavityTemp = 70;
    int cookingInfo[] = new int[4];

    public Simulator(){

    }


    //Toggle methods for toggleable fields
    private void togglePower(){
        powerIsOn = !powerIsOn;}
    private void toggleLight(){
        lightIsOn = !lightIsOn;}
    private void toggleDoorSensor(){
        doorIsClosed = !doorIsClosed;}
    private void toggleTopHeater(){
        topHeaterIsOn = !topHeaterIsOn;}
    private void toggleBottomHeater(){
        bottomHeaterIsOn = !bottomHeaterIsOn;}

    private void setCookingInfo(int[] info){
        cookingInfo[0] = info[0];
        cookingInfo[1] = info[1];
        cookingInfo[2] = info[2];
        cookingInfo[3] = info[3];
    }

    public Boolean startCooking(){
        if(!powerIsOn){
            System.out.println("Power is off, returning false");
            return false;
        }
        else if(!doorIsClosed){
            System.out.println("Door is open, cannot start cooking, returning false");
            return false;
        }
        else {
            int cookTime = convertMinSecToSec();
            //Start a timer for cooktime
            int startTime;
            int currentTime = startTime = (int)System.currentTimeMillis()/1000;
            //Turn on all the stuff
            //Watch interrupts while timer runs
            //At interrupt, pause
            //At timer finish, stop and reset
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
}
