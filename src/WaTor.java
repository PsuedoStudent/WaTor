

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

//FIXME class header comment
////////////////////ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
//Title:            WarTor
//Files:            WarTor.java
//Semester:         Spring 2018
//
//Author:           Kudirat Alimi
//Email:            kalimi@wisc.edu
//CS Login:         kalimi
//Lecturer's Name:  Marc Renault      
//
///////////////////////////// CREDIT OUTSIDE HELP /////////////////////////////
//
//No help received from any person or other source.
//
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////

public class WaTor { 
    




	/**
     * This is the main method for WaTor simulation.
     * Based on: http://home.cc.gatech.edu/biocs1/uploads/2/wator_dewdney.pdf
     * This method contains the main simulation loop. In main the Scanner
     * for System.in is allocated and used to interact with the user.
     *  
     * @param args (unused)
     */

	 public static void main(String[] args) throws FileNotFoundException {
	         File file = new File("C:/Temp/JAVA/WaTor/src/INPUT2.txt");
	        
	        Scanner scnr = new Scanner(System.in);
	        String userInput = " ";
	        Random randGen = new Random();
	        
	        // an array for simulation parameters
	        // to be used when saving or loading parameters in Milestone 3
	        int[] simulationParameters = null;

	        // Variables declared to be used later in the main method
	        int fishCount, sharkCount, fishPlaced, sharkPlaced;

	        boolean promptUser = false;
	        // This is the welcoming message
	        System.out.println("Welcome to Wa-Tor");

	        
	        // Ask user if they would like to load simulation parameters from a file.
	        System.out.println("Do you want to load simulation parameters from a file (y/n) : ");
	        userInput = scnr.next();
	        // If the user enters a y or Y as the only non-whitespace characters
	        if (userInput.equals("y") || userInput.equals("Y") ) {
	        	System.out.print("Enter filename: ");
	        	String userInput2 = scnr.nextLine();
	        	try {
	        	simulationParameters = loadSimulationParameters(userInput2);
	        }
	        // then prompt for filename and call loadSimulationParameters
	        // TODO in Milestone 3
	        catch (Exception e) {
	    	   System.out.print("File not found: " + userInput2);
	       }


	        // This condition checks to see if the array is null, and if that's true
	        //it will call the array SIM_PARAMS and get the length
	        if (simulationParameters == null) {
	        	promptUser = true;
	            simulationParameters = new int[Config.SIM_PARAMS.length];
	            //This loop prints out each of the elements in the SIM_PARAMS array
	            for (int i = 0; i < Config.SIM_PARAMS.length; i++) {
	                System.out.print("Enter " + Config.SIM_PARAMS[i] + ": ");
	                simulationParameters[i] = scnr.nextInt();
	            }
	            scnr.nextLine(); //Ensures that scanner is empty
	        }

	        // This loop checks if the index of the "seed" element is > 0, then it sets 
	        //the random number generator equal to the index that is "seed"
	        if (simulationParameters[indexForParam("seed")] > 0) {
	            randGen.setSeed(simulationParameters[indexForParam("seed")]);
	        }

	        // Initiating variables to the corresponding elements in the 
	        //simulationParameters array
	        int oceanWidth = simulationParameters[indexForParam("ocean_width")];
	        int oceanHeight = simulationParameters[indexForParam("ocean_height")];
	        int startingFish = simulationParameters[indexForParam("starting_fish")];
	        int startingSharks = simulationParameters[indexForParam("starting_sharks")];
	        int fishBreed = simulationParameters[indexForParam("fish_breed")];
	        int sharksBreed = simulationParameters[indexForParam("sharks_breed")];
	        int sharksStarve = simulationParameters[indexForParam("sharks_starve")];
	        
	        int [][] fish = new int[oceanHeight][oceanWidth];
	        int [][]sharks= new int[oceanHeight][oceanWidth];
	        int [][] starve = new int[oceanHeight][oceanWidth];
	        boolean [][]fishMoved = new boolean[oceanHeight][oceanWidth];
	        boolean [][] sharksMoved  = new boolean[oceanHeight][oceanWidth];
	        
	        //call to emptyArray to ensure that all arrays have no values
	        emptyArray(fish);
	        emptyArray(sharks);
	        emptyArray(starve);
	        
	        //gets the numbers of fish and sharks placed; these values will then be printed
	        int numFish = placeFish(fish, startingFish, fishBreed, randGen);
	        int numSharks = placeSharks(fish, sharks, startingSharks, sharksBreed, randGen);

	        System.out.println("Placed " + numFish + " fish.");
	        System.out.println("Placed " + numSharks + " sharks.");
	        
	        int currChronon = 1;
	        int chrononNum = 0;
	    
	        //this array will store all the values of the creatures (sharks and fish) during each chornon
	        ArrayList<int[]> history = new ArrayList<int[]>();
	        int[] setHistory = new int[3];
	        int[] currHistory = new int[3];
	        
	        // simulation ends when numFish and numSharks are less than or equal to 0
	        boolean simulationEnd = numFish <= 0 || numSharks <= 0;
	        //This while loop, while simulationEnd is not true, will 
	        //use showFishAndSharks, to print the current fish and sharks
	        String response = " ";
	        while (!simulationEnd) {
	        	setHistory = new int[3];
	            showFishAndSharks(currChronon, fish, sharks);

	            //Prompts the user for Enter, # of chronon, or 'end'
	            //Enter advances to next chronon
	            //# will show the number of chronon
	            //'end' will end the simulation
	            System.out.print("Press Enter, # of chronon, or 'end': ");
	            response = scnr.nextLine().trim();

	            //if this evaluates to true, then creature value will be stored at each chronon
	            //chronon increments
	            //arrays with fish and sharks will be set to Config.EMPTY
	          if (userInput.isEmpty()) {
	        	  setHistory[0] = currChronon;
	                setHistory[1] = numFish;
	                setHistory[2] = numSharks;
	                history.add(setHistory);
	                
	                currChronon++;
	                
	                clearMoves(fishMoved);
	                clearMoves(sharksMoved);

	                fishSwimAndBreed(fish, sharks, fishMoved, fishBreed, randGen);

	                sharksHuntAndBreed(fish, sharks, fishMoved, sharksMoved, sharksBreed, starve,
	                    sharksStarve, randGen);
	                
	                numFish = countCreatures(fish);
	                numSharks = countCreatures(sharks);
	            //  if the while loop no longer evaluates to true
	           //  and all the fish or sharks are gone then end simulation
	            simulationEnd = numFish <= 0 || numSharks <= 0;
	            if (simulationEnd) {
                    break;
                }
	            // check to see if a string has at least one digit
	            } else if (userInput.matches(".*\\d+.*")) {
	                chrononNum = Integer.parseInt(userInput);
	                for (int i = 0; i < chrononNum; i++) {
	                    currHistory = new int[3];
	                    currHistory[0] = currChronon;
	                    currHistory[1] = numFish;
	                    currHistory[2] = numSharks;
	                    history.add(currHistory);
	                    currChronon++;

	                    // clear fishMoved and sharksMoved from previous chronon
	                    clearMoves(fishMoved);
	                    clearMoves(sharksMoved);

	                    fishSwimAndBreed(fish, sharks, fishMoved, fishBreed, randGen);

	                    sharksHuntAndBreed(fish, sharks, fishMoved, sharksMoved, sharksBreed, starve,
	                        sharksStarve, randGen);

	                    if (Config.DEBUG) {
	                        
	                    }

	                    // count the current number of fish and sharks
	                    numFish = countCreatures(fish);
	                    numSharks = countCreatures(sharks);


	                    // if simulation does not evaluate to true then end simulation
	                    simulationEnd = numFish <= 0 || numSharks <= 0;
	                    if (simulationEnd) {
	                        break;
	                    }
	                }
	            } else if (response.equalsIgnoreCase("end")) {
	                break; // leave simulation loop
	            }
	        }
	        currHistory = new int[3];
	        currHistory[0] = currChronon;
	        currHistory[1] = numFish;
	        currHistory[2] = numSharks;
	        history.add(currHistory);
	        // prints the final ocean contents
	        showFishAndSharks(currChronon, fish, sharks);
	        numFish = countCreatures(fish);
	        numSharks = countCreatures(sharks);


	        // Depending on the condition that was met
	        //this tells the user why the simulation was ended
	        if (numSharks <= 0) {
	            System.out.println("Wa-Tor simulation ended since no sharks remain.");
	        } else if (numFish <= 0) {
	            System.out.println("Wa-Tor simulation ended since no fish remain.");
	        } else {
	            System.out.println("Wa-Tor simulation ended at user request.");
	        }

	        boolean fileSaved = false; 
	        // If the user was prompted to enter simulation parameters
	        
  
	        if (promptUser = true) {
	            while (!fileSaved) {
	                System.out.print("Save simulation parameters (y/n): ");
	                String userInput2 = scnr.nextLine().trim();
	                if (response.equalsIgnoreCase("y")) {
	                    System.out.print("Enter filename: ");
	                    String filename = scnr.nextLine();
	                    if (!fileSaved) {
	                        try {
	                            saveSimulationParameters(simulationParameters, filename);
	                            fileSaved = true;
	                        } catch (Exception IOExceptions) {
	                            System.out.print("Unable to save to: " + filename);
	                            fileSaved = false;

	                        }
	                    }

	                } else {
	                    fileSaved = true;
	                }
	            }
	        }

	        		
	        	
	        		
	        // then prompt the user to see if they would like to save them.
	        boolean chartSaved = false;
	        	
//	        	
//	        // If the user enters a y or Y as the only non-whitespace characters
//	        // then prompt for filename and save, otherwise don't save chart.
//	        // call savePopulationChart to save the parameters to the file.
//	        // If savePopulationChart throws an IOException then catch it and
//	        // repeat the code to prompt asking the user if they want to save
//	        // the population chart.
//	        // TODO Milestone 3
	        while (!chartSaved) {
	            System.out.print("Save population chart (y/n): ");
	            String userInput3 = scnr.nextLine();
	            if (response.equalsIgnoreCase("y")) {
	                System.out.print("Enter filename: ");
	                String filename = scnr.nextLine();
	                if (!chartSaved) {
	                    try {
	                        savePopulationChart(simulationParameters, history, oceanWidth, oceanHeight,
	                            filename);
	                        chartSaved = true;

	                    } catch (Exception IOExceptions) {
	                        System.out.print("Unable to save to: " + filename);
	                        chartSaved = false;
	                    }
	                }
	            } else {
	                chartSaved = true;
	            }
	        }
	        }
	        	scnr.close();
	    }
    
    /**
     * This is called when a fish cannot move.  This increments the fish's age and notes in
     * the fishMove array that it has been updated this chronon.
     * 
     * @param fish The array containing all the ages of all the fish.
     * @param fishMove The array containing the indicator of whether each fish moved this 
     *        chronon.
     * @param row The row of the fish that is staying.
     * @param col The col of the fish that is staying.
     */
    public static void aFishStays(int[][] fish, boolean[][] fishMove, int row, int col) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG fish %d,%d stays\n", row, col);
        }
        fish[row][col]++; // this increases the age of the fish; increments by 1
        fishMove[row][col] = true;  
        
    }
    
    /**
     * The fish moves from fromRow,fromCol to toRow,toCol.  The age of the fish is incremented. The
     * fishMove array records that this fish has moved this chronon.
     * 
     * @param fish The array containing all the ages of all the fish.
     * @param fishMove The array containing the indicator of whether each fish moved this 
     *        chronon.
     * @param fromRow  The row the fish is moving from.
     * @param fromCol The column the fish is moving from.
     * @param toRow  The row the fish is moving to.
     * @param toCol  The column the fish is moving to.
     */
    public static void aFishMoves(int[][] fish, boolean[][] fishMove, int fromRow, int fromCol,
        int toRow, int toCol) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG fish moved from %d,%d to %d,%d\n", fromRow, fromCol, toRow, toCol);
        }
        //moves fish
        fish[toRow][toCol] = fish[fromRow][fromCol] + 1; // increments the age of the fish by 1
        fishMove[toRow][toCol] = true;

        // this clears the value at the location
        //called to prior by setting to to -1
        fish[fromRow][fromCol] = Config.EMPTY;
        fishMove[fromRow][fromCol] = false;
    }
    
    /**
     * The fish moves from fromRow,fromCol to toRow,toCol. This fish breeds so its
     * age is reset to 0.  The new fish is put in the fromRow,fromCol with an age of 0.   The
     * fishMove array records that both fish moved this chronon.
     * 
     * @param fish The array containing all the ages of all the fish.
     * @param fishMove The array containing the indicator of whether each fish moved this 
     *        chronon.
     * @param fromRow  The row the fish is moving from and where the new fish is located.
     * @param fromCol The column the fish is moving from and where the new fish is located.
     * @param toRow  The row the fish is moving to.
     * @param toCol  The column the fish is moving to.
     */
    public static void aFishMovesAndBreeds(int[][] fish, boolean[][] fishMove, int fromRow,
        int fromCol, int toRow, int toCol) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG fish moved from %d,%d to %d,%d and breed\n", fromRow, fromCol, toRow,
                toCol);
        }
        // moves fish, then resets the age to 0
        fish[toRow][toCol] = 0;
        fishMove[toRow][toCol] = true;

        fish[fromRow][fromCol] = 0; // puts a new fish in the old location
        fishMove[fromRow][fromCol] = true;
    }

    /**
     * This removes the shark from the sharks, sharksMove and starve arrays.
     * 
     * @param sharks The array containing all the ages of all the sharks.
     * @param sharksMove The array containing the indicator of whether each shark moved this 
     *        chronon.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param row The row the shark is in.
     * @param col The column the shark is in.
     */
    public static void sharkStarves(int[][] sharks, boolean[][] sharksMove, int[][] starve, int row,
        int col) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG shark %d,%d starves\n", row, col);
        }
        //removing the values in all arrays by setting both int array to Config.EMPTY and the boolean array to false
        sharks[row][col] = Config.EMPTY;
        starve[row][col] = Config.EMPTY;
        sharksMove[row][col] = false;
    }
    
    /**
     * This is called when a shark cannot move.  This increments the shark's age and time since
     * the shark last ate and notes in the sharkMove array that it has been updated this chronon.
     * 
     * @param sharks The array containing all the ages of all the sharks.
     * @param sharksMove The array containing the indicator of whether each shark moved this 
     *        chronon.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param row The row the shark is in.
     * @param col The column the shark is in.
     */
    public static void sharkStays(int[][] sharks, boolean[][] sharksMove, int[][] starve, int row,
        int col) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG shark %d,%d can't move\n", row, col);
        }
        sharks[row][col]++;
        starve[row][col]++; // this increments the time since last ate
        sharksMove[row][col] = true;
    }
    
    /**
     * This moves a shark from fromRow,fromCol to toRow,toCol.  This increments the age and time
     * since the shark last ate and notes that this shark has moved this chronon.
     * 
     * @param sharks The array containing all the ages of all the sharks.
     * @param sharksMove The array containing the indicator of whether each shark moved this 
     *        chronon.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param fromRow  The row the shark is moving from.
     * @param fromCol The column the shark is moving from.
     * @param toRow  The row the shark is moving to.
     * @param toCol  The column the shark is moving to.
     */
    public static void sharkMoves(int[][] sharks, boolean[][] sharksMove, int[][] starve,
        int fromRow, int fromCol, int toRow, int toCol) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG shark moved from %d,%d to %d,%d\n", fromRow, fromCol, toRow, toCol);
        }
        // sharks move back to previous location
        sharks[toRow][toCol] = sharks[fromRow][fromCol] + 1; //age at previous location is incremented
        sharksMove[toRow][toCol] = true;
        starve[toRow][toCol] = starve[fromRow][fromCol] + 1; //incrementing the time in which the sharks last ate

        sharks[fromRow][fromCol] = Config.EMPTY;
        sharksMove[fromRow][fromCol] = false;
        starve[fromRow][fromCol] = 0;
    }

    /**
     * The shark moves from fromRow,fromCol to toRow,toCol. This shark breeds so its
     * age is reset to 0 but its time since last ate is incremented.  
     * The new shark is put in the fromRow,fromCol with an age of 0 and 0 time since last ate. The
     * fishMove array records that both fish moved this chronon. 
     * 
     * @param sharks The array containing all the ages of all the sharks.
     * @param sharksMove The array containing the indicator of whether each shark moved this 
     *        chronon.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param fromRow  The row the shark is moving from.
     * @param fromCol The column the shark is moving from.
     * @param toRow  The row the shark is moving to.
     * @param toCol  The column the shark is moving to.
     */
    public static void sharkMovesAndBreeds(int[][] sharks, boolean[][] sharksMove, int[][] starve,
        int fromRow, int fromCol, int toRow, int toCol) {

        if (Config.DEBUG) {
            System.out.printf("DEBUG shark moved from %d,%d to %d,%d and breeds\n", fromRow, fromCol,
                toRow, toCol);
        }
        sharks[toRow][toCol] = 0; // reset age in new location
        sharks[fromRow][fromCol] = 0; // new fish in previous location

        sharksMove[toRow][toCol] = true;
        sharksMove[fromRow][fromCol] = true;

        starve[toRow][toCol] = starve[fromRow][fromCol] + 1;
        starve[fromRow][fromCol] = 0;
    }

    /**
     * The shark in fromRow,fromCol moves to toRow,toCol and eats the fish. The sharks age is 
     * incremented, time since it last ate and that this shark moved this chronon are noted. 
     * The fish is now gone.
     * 
     * @param sharks The array containing all the ages of all the sharks.
     * @param sharksMove The array containing the indicator of whether each shark moved this 
     *        chronon.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param fish The array containing all the ages of all the fish.
     * @param fishMove The array containing the indicator of whether each fish moved this 
     *        chronon.
     * @param fromRow  The row the shark is moving from.
     * @param fromCol The column the shark is moving from.
     * @param toRow  The row the shark is moving to.
     * @param toCol  The column the shark is moving to.  
     */
    public static void sharkEatsFish(int[][] sharks, boolean[][] sharksMove, int[][] starve,
        int[][] fish, boolean[][] fishMove, int fromRow, int fromCol, int toRow, int toCol) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG shark moved from %d,%d and ate fish %d,%d\n", fromRow, fromCol,
                toRow, toCol);
        }
        //Sharks eat fish
        fish[toRow][toCol] = Config.EMPTY;
        fishMove[toRow][toCol] = false;

        //fish moves
        sharks[toRow][toCol] = sharks[fromRow][fromCol] + 1; // age will be incremented
        sharksMove[toRow][toCol] = true;
        starve[toRow][toCol] = starve[fromRow][fromCol] = 0;

        // resets the age at the old location
        sharks[fromRow][fromCol] = Config.EMPTY;
        sharksMove[fromRow][fromCol] = true;
        starve[fromRow][fromCol] = 0;
    }

    /**
     * The shark in fromRow,fromCol moves to toRow,toCol and eats the fish. The fish is now gone.
     * This shark breeds so its age is reset to 0 and its time since last ate is incremented.
     * The new shark is put in the fromRow,fromCol with an age of 0 and 0 time since last ate. 
     * That these sharks moved this chronon is noted.
     * 
     * @param sharks The array containing all the ages of all the sharks.
     * @param sharksMove The array containing the indicator of whether each shark moved this 
     *        chronon.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param fish The array containing all the ages of all the fish.
     * @param fishMove The array containing the indicator of whether each fish moved this 
     *        chronon.
     * @param fromRow  The row the shark is moving from.
     * @param fromCol The column the shark is moving from.
     * @param toRow  The row the shark is moving to.
     * @param toCol  The column the shark is moving to.  
     */
    public static void sharkEatsFishAndBreeds(int[][] sharks, boolean[][] sharksMove,
        int[][] starve, int[][] fish, boolean[][] fishMove, int fromRow, int fromCol, int toRow,
        int toCol) {
        if (Config.DEBUG) {
            System.out.printf("DEBUG shark moved from %d,%d and ate fish %d,%d and breed\n", fromRow,
                fromCol, toRow, toCol);
        }
        // shark eats fish and breeds 
        fish[toRow][toCol] = Config.EMPTY;
        fishMove[toRow][toCol] = false;

        // sharks moves to a new location
        sharks[toRow][toCol] = 0; // resets age at new location
        sharksMove[toRow][toCol] = true; //The Sharks have moved!
        starve[toRow][toCol] = 0;

        // Sharks breed a new shark in new
        //location. Age starts at 0
        sharks[fromRow][fromCol] = 0; // 
        sharksMove[fromRow][fromCol] = true;
        starve[fromRow][fromCol] = 0;
    }

    /**
     * This sets all elements within the array to Config.EMPTY.
     * This does not assume any array size but uses the .length attribute of the array.
     * If arr is null the method prints an error message and returns.
     * 
     * @param arr The array that only has EMPTY elements when method has executed.
     */
    public static void emptyArray(int[][] arr) {
        if ( arr == null) {
            System.out.println("emptyArray arr is null");
            return;
        }
        for (int row = 0; row < arr.length; row++) {
            for (int col = 0; col < arr[row].length; col++) {
                arr[row][col] = Config.EMPTY;
            }
        }
    }

    /**
     * This sets all elements within the array to false, indicating not moved this chronon.
     * This does not assume any array size but uses the .length attribute of the array.
     * If arr is null the method prints a message and returns.
     * 
     * @param arr The array will have only false elements when method completes.
     */
    public static void clearMoves(boolean[][] arr) {
        if ( arr == null) {
            System.out.println("clearMoves arr is null");
            return;
        }        
        for (int row = 0; row < arr.length; row++) {
            for (int col = 0; col < arr[row].length; col++) {
                arr[row][col] = false;
            }
        }
    }
    
    /**
     * Shows the locations of all the fish and sharks noting a fish with Config.FISH_MARK,
     * a shark with Config.SHARK_MARK and empty water with Config.WATER_MARK.
     * At the top is a title "Chronon: " with the current chronon and at the bottom is a count
     * of the number of fish and sharks.
     * Example of a 3 row, 5 column ocean. Note every mark is also followed by a space.
     * Chronon: 1
     *     O . . 
     * O   . . . 
     *   . .   O 
     * fish:7 sharks:3
     * 
     * @param chronon  The current chronon.
     * @param fish The array containing all the ages of all the fish.
     * @param sharks The array containing all the ages of all the sharks.
     */
    public static void showFishAndSharks(int chronon, int[][] fish, int[][] sharks) {
//        //TODO Milestone 1

    	int fishCount = 0;

    	int sharkCount = 0;

    	char [][] newOcean = new char[fish.length][fish[0].length];

    	System.out.println("Chronon: " + chronon);
    	
    	//checking to see if locations are and aren't empty
    	//if fish array is empty we print a fish mark and 
    	// increment fishcount
    	//if shark array is empty we print a shark mark
    	//and increment sharkcount
    	  for(int i= 0; i < newOcean.length; i++) {
    		  for(int j= 0; j < newOcean[0].length ; j++) {
    			  if (fish[i][j] != Config.EMPTY) {              
    				  System.out.print(Config.FISH_MARK + " ");
    				  fishCount ++;
    			  	}  else if (sharks[i][j] != Config.EMPTY ) {
    			  		System.out.print(Config.SHARK_MARK + " ");
    			  			sharkCount ++;
    	                } else {                 
    	                    System.out.print(Config.WATER_MARK + " ");
    	                }   
    	            }
    	            System.out.println();
    	            }
    	        System.out.println("fish:" + sharkCount + " sharks:" + sharkCount);
    }

    /**
     * This places up to startingFish fish in the fish array. This randomly chooses a location and
     * age for each fish.
     * Algorithm:
     * For each fish this tries to place
     *     reset the attempts to place the particular fish to 0.
     *     Try to place a single fish up to Config.MAX_PLACE_ATTEMPTS times
     *          Randomly choose a row, then column using randGen.nextInt( ) with the appropriate fish 
     *          array dimension as the parameter. 
     *          Increment the number of attempts to place the fish.
     *          If the location is empty in the fish array then 
     *              place the fish in that location, randomly choosing its age from 0 up to 
     *              and including fishBreed.
     *          If the location is already occupied, generate another location and try again.
     *    * On the Config.MAX_PLACE_ATTEMPTS try, whether or not the fish is successfully placed,
* stop trying to place additional fish. 
     * Return the number of fish actually placed.
     * 
     * @param fish The array containing all the ages of all the fish.
     * @param startingFish The number of fish to attempt to place in the fish array.
     * @param fishBreed The age at which fish breed.
     * @param randGen The random number generator.
     * @return the number of fish actually placed.
     */
    public static int placeFish(int[][] fish, int startingFish, int fishBreed, Random randGen) {
        int numFishPlaced = 0;
        //TODO Milestone 1
        int i;
        int j;
        
        int cols = 0; 
    	int rows = 0;
    	int placeTries = 0;
    	int randomAge = 0;
    	 //used to continue the do-while loop as long as placeTries != Config.MAX_PLACE_ATTEMPTS
        boolean cont = false;

        for (i = 0; i < startingFish; i++) {
        	placeTries = 0;
        	cont = false;
        		while(placeTries < Config.MAX_PLACE_ATTEMPTS && cont == false) {
        			cols = randGen.nextInt(fish.length);
        			rows = randGen.nextInt(fish[0].length);
        			placeTries++;
        		
        		if (fish[cols][rows] == Config.EMPTY) {
        			numFishPlaced ++;
        			fish[cols][rows] = randGen.nextInt(fishBreed - 0 + 1) - 0;
        			cont = true;	
        		}
        		else {
        			cont = false;
        		}
        	
        		if (placeTries == Config.MAX_PLACE_ATTEMPTS) {
        			return numFishPlaced;
        		}
        }
   			
	}
        return numFishPlaced;
    }

    /**
     * This places up to startingSharks sharks in the sharks array. This randomly chooses a 
     * location and age for each shark.
     * Algorithm:
     * For each shark this tries to place
     *     reset the attempts to place the particular shark to 0.
     *     Try to place a single shark up to Config.MAX_PLACE_ATTEMPTS times
     *          Randomly choose a row, then column using randGen.nextInt( ) with the appropriate shark 
     *          array dimension as the parameter. 
     *          Increment the number of attempts to place the shark.
     *          If the location is empty in both the fish array and sharks array then 
     *              place the shark in that location, randomly choosing its age from 0 up to 
     *              and including sharkBreed.
     *          If the location is already occupied, generate another location and try again.
     *    * On the Config.MAX_PLACE_ATTEMPTS try, whether or not the shark is successfully placed,
* stop trying to place additional sharks.
     * Return the number of sharks actually placed.     *     
     * 
     * @param fish The array containing all the ages of all the fish.
     * @param sharks The array containing all the ages of all the sharks.
     * @param startingSharks The number of sharks to attempt to place in the sharks array.
     * @param sharksBreed The age at which sharks breed.
     * @param randGen The random number generator.
     * @return the number of sharks actually placed.
     */
    public static int placeSharks(int[][] fish, int[][] sharks, int startingSharks,
        int sharksBreed, Random randGen) {
    	int numSharksPlaced = 0;
    	int attempts;
    	int age = 0;
    	boolean fishreturned;
    		for(int i = 0; i < startingSharks; i++) {
    			attempts = 0;
    			fishreturned = false;
    		while(Config.MAX_PLACE_ATTEMPTS > attempts && fishreturned == false) {
    			int row = randGen.nextInt(fish.length);
    			int col = randGen.nextInt(fish[row].length);
    			attempts ++;
    			if(fish[row][col] == Config.EMPTY && sharks[row][col] == Config.EMPTY ) {
    				numSharksPlaced ++;
    				age = randGen.nextInt(sharksBreed+1);
    				fishreturned = true;
    				sharks[row][col] = age;
    					}else {
    						fishreturned= false;
    					}
			if((attempts == Config.MAX_PLACE_ATTEMPTS)) {
					return numSharksPlaced;

    	                }

    	                    }

    	            }

    	 return numSharksPlaced;
    }
    
    /**
     * This counts the number of fish or the number of sharks depending on the array passed in.
     * 
     * @param fishOrSharks Either an array containing the ages of all the fish or an array
     *        containing the ages of all the sharks.
     * @return The number of fish or number of sharks, depending on the array passed in.
     */
    public static int countCreatures(int[][] fishOrSharks) {
        int numCreatures = 0;
        int numSharks=0;
        int numFish=0;
        int i;
        int j;
        
        for(i = 0; i < fishOrSharks.length; i++) {
        	for(j = 0; j < fishOrSharks[0].length; j++) {
        		if (fishOrSharks[i][j] != Config.EMPTY) {
        			numCreatures++;
        		}
        	}
        	
        }
        
        //TODO Milestone 1
        return numCreatures;
    }

    /**
     * This returns a list of the coordinates (row,col) of positions around the row, col
     * parameters that do not contain a fish or shark. The positions that are considered
     * are directly above, below, left and right of row, col and IN THAT ORDER. Where 0,0 is 
     * the upper left corner when fish and sharks arrays are printed out.  Remember that creatures
     * moving off one side of the array appear on the opposite side. For example, those moving left
     * off the array appear on the right side and those moving down off the array appear at the top.
     * 
     * @param fish A non-Config.EMPTY value indicates the age of the fish occupying the location.
     * @param sharks A non-Config.EMPTY value indicates the age of the shark occupying the location.
     * @param row The row of a creature trying to move.
     * @param col The column of a creature trying to move.
     * @return An ArrayList containing 0 to 4, 2-element arrays with row,col coordinates of 
     *      unoccupied locations. In each coordinate array the 0 index is the row, the 1 index 
     *      is the column.
     */
    public static ArrayList<int[]> unoccupiedPositions(int[][] fish, int[][] sharks, 
            int row, int col) {
        ArrayList<int[]> unoccupied = new ArrayList<>();
        
        int rowBounds = fish.length;
        int colBounds = fish[0].length;
        

        // Checks above to see if it's empty at specified locatioln
        if (row - 1 < 0) {
            if (fish[rowBounds - 1][col] == Config.EMPTY
                && sharks[rowBounds - 1][col] == Config.EMPTY) {
                unoccupied.add(new int[] {rowBounds - 1, col});
            }
        }

        else {
            if (fish[row - 1][col] == Config.EMPTY && sharks[row - 1][col] == Config.EMPTY) {
                unoccupied.add(new int[] {row - 1, col});
            }
        }
        // Checks below to see if array is empty at that location
        if (row == fish.length - 1) {
            if (fish[0][col] == Config.EMPTY && sharks[0][col] == Config.EMPTY) {
                unoccupied.add(new int[] {0, col});
            }
        } else {
            if (fish[row + 1][col] == Config.EMPTY && sharks[row + 1][col] == Config.EMPTY) {
                unoccupied.add(new int[] {row + 1, col});
            }
        }
        // Checks left to see if array is empty at that location
        if (col - 1 < 0) {
            if (fish[row][fish[row].length - 1] == Config.EMPTY
                && sharks[row][sharks[row].length - 1] == Config.EMPTY) {
                unoccupied.add(new int[] {row, fish[row].length - 1});
            }
        } else {
            if (fish[row][col - 1] == Config.EMPTY && sharks[row][col - 1] == Config.EMPTY) {
                unoccupied.add(new int[] {row, col - 1});
            }
        }
        // Checks right to see if array is empty at that location
        if (col == fish[0].length - 1) {
            if (fish[row][0] == Config.EMPTY && sharks[row][0] == Config.EMPTY) {
                unoccupied.add(new int[] {row, 0});
            }
        } else {
            if (fish[row][col + 1] == Config.EMPTY && sharks[row][col + 1] == Config.EMPTY) {
                unoccupied.add(new int[] {row, col + 1});
            }
        }
        //TODO Milestone 2
        return unoccupied;
    }


    /**
     * This randomly selects, with the Random number generator passed as a parameter, 
     * one of elements (array of int) in the neighbors list.  If the size of neighbors is 0 (empty)
     * then null is returned. If neighbors contains 1 element then that element is returned. The 
     * randGen parameter is only used to select 1 element from a neighbors list containing 
     * more than 1 element. If neighbors or randGen is null then an error message is 
     * printed to System.err and null is returned.
     * 
     * @param neighbors A list of potential neighbors to choose from.
     * @param randGen The random number generator used throughout the simulation.
     * @return A int[] containing the coordinates of a creatures move or null as specified above.
     */
    public static int[] chooseMove(ArrayList<int[]> neighbors, Random randGen) {
    	 int[] selectedArray = null;
         Object returnVal;
		int neighborEmpty;
		int randomElement = 0;

         if (neighbors.size() == 0) {
             returnVal = null;
         } else if (neighbors.size() == 1) { 
             return neighbors.get(0); 
         }
         if (neighbors.size() > 1) {
             randomElement = randGen.nextInt(neighbors.size());
             return neighbors.get(randomElement);
         } else {
             if (neighbors == null || randGen == null) {
                 System.err.print("Neighbor is Negative. ERROR Message!");
             }
             return null;
         }
        
        //TODO Milestone 2
   //change in Milestone 2
    	
    }

    /**
     * This attempts to move each fish each chronon.  
     * 
     * This is a key method with a number of parameters. Check that the parameters are valid 
     * prior to writing the code to move a fish. The parameters are checked in the order they
     * appear in the parameter list.  
     * If any of the array parameters are null or not at least 1 element in size then 
     * a helpful error message is printed out and -1 is returned. An example message for 
     * an invalid fish array is "fishSwimAndBreed Invalid fish array: Null or not at least 1 in 
     * each dimension.". Testing will not check the content of the message but will check whether
     * the correct number is returned for the situation.  Passing this test means we know fish[0]
     * exists and so won't cause a runtime error and also that fish[0].length is the width. For this
     * project it is safe to assume rectangular arrays (arrays where all the rows are the same 
     * length).
     * If fishBreed is less than zero a helpful error message is printed out
     * and -2 is returned. 
     * If randGen is null then a helpful error message is printed out and -3 is returned.
     *  
     *
     * Algorithm:
     * for each fish that has not moved this chronon
     *     get the available unoccupied positions for the fish to move (call unoccupiedPositions)
     *     choose a move from those positions (call chooseMove)
     *     Based on the move chosen, either the
     *         fish stays (call aFishStays)
     *         fish moves (call aFishMoves)
     *         or fish moves and breeds (call aFishMovesAndBreeds)
     *         
     *         
     * @param fish The array containing all the ages of all the fish.
     * @param sharks The array containing all the ages of all the sharks.
     * @param fishMove The array containing the indicator of whether each fish moved this 
     *        chronon.
     * @param fishBreed The age in chronon that a fish must be to breed.
     * @param randGen The instance of the Random number generator.
     * @return -1, -2, -3 for invalid parameters as specified above. After attempting to move all
     *          fish 0 is returned indicating success. 
     */
    public static int fishSwimAndBreed(int[][] fish, int[][] sharks, boolean[][] fishMove,
        int fishBreed, Random randGen) {
    	
        ArrayList<int[]> potentialMoves;
        int currRow, currCol;
        int[] finalMove;
        ArrayList<int[]> finalList;
        
    	int row, col, returnVal = 0;
    	int rowBound = 0, colBound = 0;
    	
    	if (fish.length == 0 || sharks.length == 0 || fishMove.length == 0) {
            System.out.print("fishSwimAndBreed Invalid fish array: Null or not at least 1 in \n"
                + "* each dimension.");
            return -1;
        }
        // checks to that fishBreed is positive
        if (fishBreed < 0) {
            System.err.print("Fish Breed is less than Zero");
            return -2;
        }
        // checks to see if the Random parameter = null
        if (randGen == null) {
            System.err.print("RandGen is null!");
            return -3;
        }
        for (int i = 0; i < fish.length; i++) {
            for (int j = 0; j < fish[i].length; j++) {
                if (fishMove[i][j] == false && fish[i][j] != Config.EMPTY) {
                    finalList = unoccupiedPositions(fish, sharks, i, j);
                    finalMove = chooseMove(finalList, randGen);
                    if (finalList == null) { 
                        aFishStays(fish, fishMove, i, j); 

                    } else if (finalMove.length > 0 && fish[i][j] >= fishBreed) {
                        
                        aFishMovesAndBreeds(fish, fishMove, i, j, finalMove[0], finalMove[1]);
                        
                    } else if (finalMove.length > 0) {
                       
                        aFishMoves(fish, fishMove, i, j, finalMove[0], finalMove[1]);
                    }
                }
            }
        }
        return 0;
    	
//    	if (fish == null || !(fish.length == ) && (sharks == null || sharks.length < 1)) {
//    		System.err.println("ERROR: fishSwimAndBreed Invalid fish array: Null or not at least 1 in each\r\n"
//                    + " * dimension.");
//    		returnVal = -1;
//    	}
//    				
//    		if (fishBreed < 0) {
//    			System.err.println("ERROR: fishSwimAndBreed fishBreed is less than 0");
//    			returnVal = -2;
//    		}
//    		
//    		if (randGen == null) {
//    			System.err.println("ERROR: fishSwimAndBreed randGen is null");
//    			returnVal = -3;
//    		}
//    	try {
//    	
//    		for (row = 0; row < rowBound; row++) {
//                for (col = 0; col < colBound; col++) {
//                    if (!(fishMove[row][col]) && (fish[row][col] != Config.EMPTY)) {
//                        potentialMoves = unoccupiedPositions(fish, sharks, row, col);
//                        finalMove = chooseMove(potentialMoves, randGen);
//
//                        if (finalMove == null) {
//                            aFishStays(fish, fishMove, row, col);
//                        } else {
//                            currRow = finalMove[0];
//                            currCol = finalMove[1];
//
//                            if (fish[row][col] == fishBreed) {
//                                aFishMovesAndBreeds(fish, fishMove, row, col, currRow, currCol);
//                            }
//                            else if (finalMove.length > 0) {
//                            	aFishMoves(fish, fishMove, row, col, currRow, currCol);
//                            }
//                        }
//
//
//                    }
//                }
//    		}
//    	} catch (ArrayIndexOutOfBoundsException exception) {
//    		
//    		System.out.println(
//                    "ERROR: fishSwimAndBreed Invalid fish array: Null or not at least 1 in each\r\n"
//                        + " * dimension.");
//                return -1;
//            } catch (Exception e) {
//
//            }
    
        //TODO Milestone 2
       // return 0;
    }

  

	/**
     * This returns a list of the coordinates (row,col) of positions around the row, col
     * parameters that contain a fish. The positions that are considered are directly above, 
     * below, left and right of row, col and IN THAT ORDER. Where 0,0 is the upper left corner 
     * when fish array is printed out.  Remember that sharks moving off one side of the array appear
     * on the opposite side. For example, those moving left off the array appear on the right side 
     * and those moving down off the array appear at the top.
     * 
     * @param fish A non-Config.EMPTY value indicates the age of the fish occupying a location.
     * @param row The row of a hungry shark.
     * @param col The column of a hungry shark.
     * @return An ArrayList containing 0 to 4, 2-element arrays with row,col coordinates of 
     *      fish locations. In each coordinate array the 0 index is the row, the 1 index 
     *      is the column.
     */
    public static ArrayList<int[]> fishPositions(int[][] fish, int row, int col) {
        ArrayList<int[]> fishPositions = new ArrayList<>();
        
        int aboveFish, belowFish, leftFish, rightFish, currRow, currCol;
        int rowBounds = fish.length; 
        int colBounds = fish[0].length;

        if (row - 1 < 0) {
            if (fish[rowBounds - 1][col] != Config.EMPTY) {
                // storages the values in an array and storages the array in the fishPosition
                fishPositions.add(new int[] {rowBounds - 1, col});

            }
        } else {
            if (fish[row - 1][col] != Config.EMPTY) { // checks to see if its not empty
                fishPositions.add(new int[] {row - 1, col});
            }
        }
     // Checks below to see if fish is at that location
        if (row == fish.length - 1) {
            if (fish[0][col] != Config.EMPTY) {
                fishPositions.add(new int[] {0, col});
            }
        } else {
            if (fish[row + 1][col] != Config.EMPTY) {
                fishPositions.add(new int[] {row + 1, col});
            }
        }
     // Checks left to see if fish is at that location
        if (col - 1 < 0) {
            if (fish[row][fish[row].length - 1] != Config.EMPTY) {
                fishPositions.add(new int[] {row, fish[row].length - 1});
            }
        } else {
            if (fish[row][col - 1] != Config.EMPTY) {
                fishPositions.add(new int[] {row, col - 1});
            }
        }
     // Checks right to see if array is empty at that location
        if (col == fish[0].length - 1) {
            if (fish[row][0] != Config.EMPTY) {
                fishPositions.add(new int[] {row, 0});
            }
        } else {
            if (fish[row][col + 1] != Config.EMPTY) {
                fishPositions.add(new int[] {row, col + 1});
            }
        }
        
        
        
        //TODO Milestone 2
        return fishPositions;
    }

    /**
     * This attempts to move each shark each chronon.  
     *
     * This is a key method with a number of parameters. Check that the parameters are valid 
     * prior to writing the code to move a shark. The parameters are checked in the order they
     * appear in the parameter list.  
     * If any of the array parameters are null or not at least 1 element in size then 
     * a helpful error message is printed out and -1 is returned. An example message for 
     * an invalid fish array is "sharksHuntAndBreed Invalid fish array: Null or not at least 1 in 
     * each dimension.". Testing will not check the content of the message but will check whether
     * the correct number is returned for the situation.  Passing this test means we know fish[0]
     * exists and so won't cause a runtime error and also that fish[0].length is the width. For this
     * project it is safe to assume rectangular arrays (arrays where all the rows are the same 
     * length).
     * If sharksBreed or sharksStarve are less than zero a helpful error message is printed out
     * and -2 is returned. 
     * If randGen is null then a helpful error message is printed out and -3 is returned.
     * 
     * Algorithm to move a shark:
     * for each shark that has not moved this chronon
     *     check to see if the shark has starved, if so call sharkStarves
     *     otherwise get the available positions of neighboring fish (call fishPositions)
     *     if there are no neighboring fish to eat then determine available positions 
     *              (call unoccupiedPositions)
     *          choose a move (call chooseMove) and based on the move chosen
     *          call sharkStays, sharkMoves or sharkMovesAndBreeds appropriately, using
     *          the sharkBreed parameter to see if a shark breeds.
     *     else if there are neighboring fish then choose the move (call chooseMove),
     *          eat the fish (call sharkEatsFish or sharkEatsFishAndBreeds) appropriately.
     * return 0, meaning success.
     *         
     * @param fish The array containing all the ages of all the fish.
     * @param sharks The array containing all the ages of all the sharks.
     * @param fishMove The array containing the indicator of whether each fish moved this 
     *        chronon.
     * @param sharksMove The array containing the indicator of whether each shark moved this 
     *        chronon.
     * @param sharksBreed The age the sharks must be in order to breed.
     * @param starve The array containing the time in chronon since the sharks last ate.
     * @param sharksStarve The time in chronon since the sharks last ate that results in them
     *          starving to death.
     * @param randGen The instance of the Random number generator.
     * @return -1, -2, -3 for invalid parameters as specified above. After attempting to move all
     * sharks 0 is returned indicating success.
     */
    public static int sharksHuntAndBreed(int[][] fish, int[][] sharks, boolean[][] fishMove,
        boolean[][] sharksMove, int sharksBreed, int[][] starve, int sharksStarve, Random randGen) {
    	
    	ArrayList<int[]> potentialMoves, potentialEat;
    	int[] choosenMove;
    	
    	 int fishLeft = 0;
    	 int currRow = 0;
    	 int currCol = 0;
         
         int rowBounds = fish.length;
         int colBounds = fish[0].length;

         if((fish==null || fish.length == 0) || (sharks==null || sharks.length == 0)) {
    		if ((fishMove.length == 0 ) || (sharksMove.length == 0 )) {
    			if (starve.length == 0) {
    			System.out.println(
                 "ERROR: fishSwimAndBreed Invalid fish array: Null or not at least 1 in each\r\n"
                     + " * dimension.");
             return -1;
         }
         
         if (sharksBreed < 0 || sharksStarve < 0 ) {
             System.out.println("ERROR: fishSwimAndBreed fishBreed is less than 0");
             return -2;
         }

         if (randGen == null) {
             System.out.println("ERROR: fishSwimAndBreed randGen is null");
             return -3;
         }

         try {
             for (int row = 0; row < rowBounds; row++) {
                 for (int col = 0; col < colBounds; col++) {
                     if ((sharksMove[row][col])==false && sharks[row][col] != Config.EMPTY) {
                         if (starve[row][col] >= sharksStarve) {
                             sharkStarves(sharks, sharksMove, starve, row, col);
                         } else {
                             potentialEat = fishPositions(fish, row, col);

                             if (potentialEat == null) {
                                 potentialMoves = unoccupiedPositions(fish, sharks, row, col);
                                 choosenMove = chooseMove(potentialMoves, randGen);
                                 if (choosenMove == null) {
                                     sharkStays(sharks, sharksMove, starve, row, col);
                                 } else {
                                     currRow = choosenMove[0];
                                     currCol = choosenMove[1];

                                     if (choosenMove.length > 0 && (sharks[row][col] >= sharksBreed)) {
                                         sharkMovesAndBreeds(sharks, sharksMove, starve, row, col,
                                             currRow, currCol);
                                      
                                     } else if (choosenMove.length > 0){
                                         sharkMoves(sharks, sharksMove, starve, row, col, currRow,
                                             currCol);
                                      
                                     }
                                 }
                             } else {
                                 choosenMove = chooseMove(potentialEat, randGen);
                                 
                                 currRow = choosenMove[0];
                                 currCol = choosenMove[1];

                                 if (sharks[row][col] >= sharksBreed) {
                                     sharkEatsFishAndBreeds(sharks, sharksMove, starve, fish,
                                         fishMove, row, col, currRow, currCol);
                                     
                                 } else {
                                     sharkEatsFish(sharks, sharksMove, starve, fish, fishMove, row,
                                         col, currRow, currCol);
                                     

                                 }

                             }
                         }
                     }
                 }
             }

         } catch (ArrayIndexOutOfBoundsException exception) {
             System.out.println(
                 "ERROR: fishSwimAndBreed Invalid fish array: Null or not at least 1 in each\r\n"
                     + " * dimension.");
             return -1;
         } catch (Exception e) {

         }
    	
    		}
    		}
        //TODO Milestone 2
        return 0;
    }
    
    /**
     * This looks up the specified paramName in this Config.SIM_PARAMS array,
     * ignoring case.  If found then the array index is returned.
     * @param paramName The parameter name to look for, ignoring case.
     * @return The index of the parameter name if found, otherwise returns -1.
     */
    public static int indexForParam(String paramName) {
        for ( int i = 0; i < Config.SIM_PARAMS.length; i++) {
            if ( paramName.equalsIgnoreCase( Config.SIM_PARAMS[i])) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Writes the simulationParameters to the file named filename.
     * The format of the file is the name of the parameter and value
     * on one line separated by =. The order of the lines does not matter.
     * Algorithm:
     * Open the file named filename for writing. Any IOExceptions should be handled with a throws
     *     clause and not a try-catch block.
     * For each of the simulation parameters whose names are found in Config.SIM_PARAMS
     *     Write out the name of the parameter, =, the parameter value and then newline.
     * Close the file.
     * 
     * Example contents of file:
     * seed=233
     * ocean_width=20
     * ocean_height=10
     * starting_fish=100
     * starting_sharks=10
     * fish_breed=3
     * sharks_breed=10
     * sharks_starve=4
     *  
     * @param simulationParameters The values of the parameters to write out.
     * @param filename The name of the file to write the parameters to.
     */
    public static void saveSimulationParameters(int[] simulationParameters, String filename) throws IOException {
    	int i, j;
    	
    	int value = 0;
    	 File file = new File("filename");
    	 PrintWriter outputWriter = null;
    	 outputWriter = (new PrintWriter(filename));

    		 for (i = 0; i <= simulationParameters.length; i++) {
    			
    				 //how do you access values to print???
    			 outputWriter.printf(Config.SIM_PARAMS[i] + " = " +  value + '\n');
    		 }
    		 
    	 
    	 if (outputWriter != null) outputWriter.close();
    	
}
        //TODO Milestone 3
    
    
    /**
     * This loads the simulation parameters from the file named filename.
     * The names of the parameters are in the Config.SIM_PARAMS array and the array returned from
     * this method is a parallel array containing the parameter values. The name corresponds to 
     * the value with the same index.
     * Algorithm:
     *      Try to open filename for reading. If the FileNotFoundException is thrown print the
     *      message printing out the filename without < > and return null;
     *      
     * File not found: <filename>
     * 
     *      Read lines from the file as long as each line contains "=".  As soon as a line does not
     *      contain "=" then stop reading from the file. The order of the lines in the
     *      file is not significant.
     *      In a line the part before "=" is the name and the part after is the value.
     *      The separate method you wrote in P7 is helpful here.
     *      Find the index of the name within Config.SIM_PARAMS (call indexForParam).
     *      If the index is found then convert the value into an int and store in the corresponding 
     *          index in the array of int that will be returned from this method.
     *      If the index is not found then print out the message followed by the entire line
     *      without the < >.
     *      
     * Unrecognized: <line>
     * 
     * @param filename The name of the from which to read simulation parameters.
     * @return The array of parameters.
     */
    public static int[] loadSimulationParameters(String filename) {
        int[] params = null;
        int i, j = 0;
        params = new int[Config.SIM_PARAMS.length];
        
        int parameterValue = 0;//Integer.parseInt(parameter); 
   
           
            
            try {
                File newFile = new File(filename);
                Scanner scnr = new Scanner(newFile); //reads from file
                String parameter;
                
                 
            for ( i = 0; i < Config.SIM_PARAMS.length; i++) {
                parameter = scnr.nextLine(); //reads from  file
                String [] parameters = parameter.split("=");
                parameterValue = Integer.parseInt(parameters[1]); 
                params[i] = parameterValue;
            }
            scnr.close();
           
            } catch (FileNotFoundException e) {
                System.err.print("File not found: " + filename);
               return null;
            } finally { 
                System.out.print("");
            }

            return params;
        

}
        //TODO Milestone 3
      
    
    
    /**
     * This writes the simulation parameters and the chart of the simulation to a file.
     * If simulationParameters is null or history is null then print an error message
     * and leave the method before any output.
     * If filename cannot be written to then this method should throw an IOException.     * 
     * 
     * Parameters are written first, 1 per line in the file. Use an = to separate the
     * name from the value. Then write a blank line and then the Population Chart.
     * Example file contents are:
     * seed=111
     * ocean_width=5
     * ocean_height=2
     * starting_fish=6
     * starting_sharks=2
     * fish_breed=3
     * sharks_breed=3
     * sharks_starve=3
     * 
     * Population Chart
     * Numbers of fish(.) and sharks(O) in units of 1.
     * F  6,S  2    1)OO....                                            
     * F  4,S  2    2)OO..                                              
     * F  2,S  4    3)..OO                                              
     * F  1,S  4    4).OOO                                              
     * F  0,S  4    5)OOOO       
     * 
     * Looking at one line in detail
     * F  6,S  2    1)OO.... 
     *                ^^^^^^ 6 fish (the larger of sharks or fish is in the background)
     *                ^^ 2 sharks  
     *          ^^^^^ chronon 1
     *      ^^^^ the number of sharks        
     * ^^^^ the number of fish
     *                                      
     * The unit size is determined by dividing the maximum possible number of a creature
     * (oceanWidth * oceanHeight) by Config.POPULATION_CHART_WIDTH.
     * Then iterate through the history printing out the number of fish and sharks.
     * PrintWriter has a printf method that is helpful for formatting.
     * printf("F%3d", 5) 
     * prints "F  5", a 5 right justified in a field of size 3.
     * 
     * @param simulationParameters The array of simulation parameter values.
     * @param history The ArrayList containing the number of fish and number of sharks at each chronon.
     * @param oceanWidth The width of the ocean.
     * @param oceanHeight The height of the ocean.
     * @param filename The name of the file to write the parameters and chart to.
     */
    public static void savePopulationChart(int[]simulationParameters, ArrayList<int[]> history, 
        int oceanWidth, int oceanHeight, String filename) throws IOException {
        //TODO Milestone 3
    	int fish, sharks, units, spaces, i;
    	int numFish, numSharks;
    	int[] historyVals;
        File file = new File(filename); // creates new File
        PrintWriter Printer = new PrintWriter(file); // Creates a new PrintWriter

        int chronon = 0;
        // checks to see if simulationParameters or history is null
        //if evaluated to true, prints out Error Message
        //closes the scanner
        if (simulationParameters == null || history == null) {
            System.out.print("Error message is printed");
            Printer.close();
        }
        //Printer prints all simulation parameter names and values
        //to file
        Printer.println("seed=" + simulationParameters[0]);
        Printer.println("ocean_width=" + simulationParameters[1]);
        Printer.println("ocean_height=" + simulationParameters[2]);
        Printer.println("starting_fish=" + simulationParameters[3]);
        Printer.println("starting_sharks=" + simulationParameters[4]);
        Printer.println("fish_breed=" + simulationParameters[5]);
        Printer.println("sharks_breed=" + simulationParameters[6]);
        Printer.println("sharks_starve=" + simulationParameters[7]);
        Printer.println("");
        Printer.println("Population Chart");

        units = (oceanWidth * oceanHeight) / Config.POPULATION_CHART_WIDTH;
        Printer.println("Numbers of fish(" + Config.FISH_MARK + ") and sharks("
            + Config.SHARK_MARK + ") in units of " + units + ".");

        if (units == 0) {
            units = 1;
        }


        if (Config.DEBUG) {
            System.out.println("seed=" + simulationParameters[0]);
            System.out.println("ocean_width=" + simulationParameters[1]);
            System.out.println("ocean_height=" + simulationParameters[2]);
            System.out.println("starting_fish=" + simulationParameters[3]);
            System.out.println("starting_sharks=" + simulationParameters[4]);
            System.out.println("fish_breed=" + simulationParameters[5]);
            System.out.println("sharks_breed=" + simulationParameters[6]);
            System.out.println("sharks_starve=" + simulationParameters[7]);
            System.out.println("");
            System.out.println("Population Chart");
            System.out.println("Numbers of fish(" + Config.FISH_MARK + ") and sharks("
                + Config.SHARK_MARK + ") in units of " + units + ".");

        }

        for (i = 0; i < history.size(); i++) {
            historyVals = history.get(i);
            chronon = historyVals[0];
            fish = historyVals[1];
            sharks = historyVals[2];
            Printer.printf("F%3d", fish);
            Printer.printf(",S%3d", sharks);
            Printer.printf("  %3d)", chronon);

            if (Config.DEBUG) {
                System.out.printf("F%3d", fish);
                System.out.printf(",S%3d", sharks);
                System.out.printf(chronon+ ")");
            }

            numSharks = (int) Math.ceil((double)sharks / units);
            numFish = (int) Math.ceil((double)fish / units) ;

            if (Config.DEBUG) {

                System.out.println("sharks num " + sharks);
                System.out.println("Fish num " + fish);
                System.out.println("chronon num " + chronon);
            }

            if (fish >= sharks) {
                for (int s = 0; s < numSharks; s++) {
                    Printer.print(Config.SHARK_MARK);
                    if (Config.DEBUG) {
                        System.out.print(Config.SHARK_MARK);
                    }
                }

                int printFish = numFish - numSharks;

                for (i= 0; i < printFish; i++) {
                    Printer.print(Config.FISH_MARK );
                    if (Config.DEBUG) {
                        System.out.print(Config.FISH_MARK );
                    }
                }
                
                
                int space = 50 -(printFish + numSharks);
                
                for(i = 0; i < space; i++ ) {
                    Printer.print(" ");
                    if (Config.DEBUG) {
                        System.out.print(" ");
                    }
                }
                
                Printer.println("");
                if (Config.DEBUG) {
                    System.out.println("");
                }

            } else if (sharks >= fish){
                for (int f = 0; f < numFish; f++) {
                    Printer.print(Config.FISH_MARK );
                    if (Config.DEBUG) {
                        System.out.print(Config.FISH_MARK );
                    }
                }

                int printShark = numSharks - numFish;

                for (int s = 0; s < printShark; s++) {
                    Printer.print(Config.SHARK_MARK );
                    if (Config.DEBUG) {
                        System.out.print(Config.SHARK_MARK );
                    }
                }
                
  spaces = 50 -(printShark + numFish);
                
                for(i = 0; i < spaces; i++ ) {
                    Printer.print(" ");
                    if (Config.DEBUG) {
                        System.out.print(" ");
                    }
                }
                
                Printer.println("");
                if (Config.DEBUG) {
                    System.out.println("");
                }
            }

        }

        Printer.close();
    }

    
}