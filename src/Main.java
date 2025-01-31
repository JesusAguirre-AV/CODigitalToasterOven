public class Main {
    //Main will serve to test the toaster oven simulation
    public static void main(String[] args) {
        //Test cases for different cooks
        int[] cook1 = {5, 30, 375, 1};
        int[] cook2 = {3, 0, 350, 3};
        int[] cook3 = {15, 0, 450, 2};
        // need to start a thread for the main function that calls functions in the functionality program.


        // create the object of the simulator
        Simulator simulator = new Simulator();
        simulator.printInfo();

        simulator.setCookingInfo(cook1);
        simulator.testToggles();
        simulator.toggleDoorSensor();
        simulator.startCooking();



        // call any methods on this simulator here.




    }
}