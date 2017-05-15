import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class Project2 extends Thread {

 /* PREREQUISITES */

 /*
  * we create the semaphores. 
  */
 //Semaphore to signal person is out of elevator
 public static Semaphore persons = new Semaphore(0,true);
 //Semaphore to signal person in elevator
 public static Semaphore elevator = new Semaphore(0,true);
 //Semaphore to control access to elevator
 public static Semaphore accessElevator = new Semaphore(0,true);
 //Semaphore who's only purpose is for main to know when to terminate program
 public static Semaphore terminate = new Semaphore(0);
 //Array of semaphores with each index i representing a floor from 2 onward (floor=i+2)
 private static Semaphore [] sem = new Semaphore [9];
  static {
     for(int i = 0; i < 9; i++) {
         sem[i] = new Semaphore(0, true);
     }   
  }

 //List that keeps track of different people's floors
 public static ArrayList<Integer> numbers = new ArrayList<Integer>();
 //Set that keeps track of different people's floors without duplicates
 public static Set<Integer> hash = new HashSet<Integer>();


 /* THE PERSON THREAD */

 class Person extends Thread {

  /*
   * we create the integer iD which is a unique ID number for every
   * person and a boolean notRide which is used in the Person waiting
   * loop. Also a randomized floor they want to go to
   */
  int iD;
  int floor;
  boolean notRide = true;

  /* Constructor for the Person */

  public Person(int i) {
   iD = i;
   Random rand = new Random();
   floor = rand.nextInt(9) + 2;
  }

  public void run() {
   while (notRide) { // as long as the person has not ridden
    try {
    	
     accessElevator.acquire(); // tries to get access to the elevator
     
     numbers.add(this.floor); //adds it's floor number to a list
     
      System.out.println("Customer " + this.iD
        + " entered elevator to go to floor " + this.floor);
      
      elevator.release(); //signals that customer is in elevator

      sem[this.floor-2].acquire(); //waits for a signal from the floor's
      							   //semaphore
      
      System.out.println("Customer " + this.iD + " leaves elevator");
      this.notRide = false; //person is at correct floor
      persons.release(); //signals that person has left elevator

    } catch (InterruptedException ex) {
    }
   }
  }
 }

 /* THE ELEVATOR THREAD */

 class Elevator extends Thread {

  public Elevator() {
  }

  public void run() {
   int a=1; //counter to keep track of cycles
   while (true) { // runs in an infinite loop
    
	 System.out.println("Elevator door opens at floor 1"); //Always starts on floor 1
     for(int i =0; i < 7; i++)
    	 accessElevator.release(); // signals to allow 7 people access
     
     for(int i =0; i < 7; i++){ //ensures that 7 people are on synchronized, to help
		try {					//make sure list gets sorted with all people in list
			elevator.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
     
     Collections.sort(numbers); //sorts the list of person's floors for elevator ordering
     
     hash.addAll(numbers); //hash set to get rid of duplicate numbers from numbers list
     System.out.println();

     int num=0;
     for(int p: hash){ //cycles through hash set, with p being the floor number
    	 
    	 System.out.println("Elevator door opens at floor " + p);
    	 
    	//num is important to be able to know how how many signals elevator should signal 
    	 //on each floor to people waiting to get off, i.e. counts how many duplicates
    	 //in numbers list
    	 num=Collections.frequency(numbers, p); 
    	 
    	 //signals num amount of times for people to get off according to person's floor's semaphore
    	 for(int i=0; i<num; i++)
    		 sem[p-2].release(); //p-2 because array starts at index 0 but floor starts at 2
    	 
    	 try {
    		for(int i=0; i<num; i++) //once people get off on their respective floor, must signal
    		persons.acquire();		//so elevator can continue and be in sync
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 System.out.println("Elevator door closes");
     }
     System.out.println();
    
     //list.clear();
     numbers.clear();
     hash.clear();
     
     //keeps track of how many cycles, terminates after last round
     if(a++==7)
    	 terminate.release();
   }
  }
 }

 /* MAIN METHOD */

 public static void main(String args[]) {

  Project2 project = new Project2(); // Creates a new project
  project.start(); // Let the simulation begin
  try { //waits for signal to terminate program
	terminate.acquire();
	System.out.println("Simulation ended");
	System.exit(0);
	
} catch (InterruptedException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
 }

 public void run() {
  Elevator elevator = new Elevator(); // Initializes the elevator
  elevator.start(); // Elevator begins running

  /* This method will create 49 people and begin running*/
  for (int i = 1; i < 50; i++) {
   Person aCustomer = new Person(i);
   aCustomer.start();
   
  }
 }
}