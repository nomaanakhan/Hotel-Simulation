//package com.hotel;

// Nomaan Khan
// CS 4348-001
// Project 2

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Hotel implements Runnable { 
	
	public static Queue<Guest> guestQueue; // Queue for guests to wait for front desk employees.
    public static Queue<Guest> bellhopQueue; // Queue for guests to wait for bellhops.

    public static Semaphore guestMutex; // Mutex for guest thread queue operations. 
    public static Semaphore guestAvailable; // Semaphore for when guest is ready to be dequeued. 
    public static Semaphore bellhopRequested; // Semaphore for when guest requests a bellhop.
    public static Semaphore frontDeskMutex; // Mutex for front desk queue operations.
    public static Semaphore frontDeskEmp; // Semaphore for front desk employees.
    public static Semaphore leftFrontDesk; // Semaphore for when guest leaves front desk.
    public static Semaphore bellhopMutex; // Mutex for bellhop queue operations.
    public static Semaphore bellhop; // Semaphore for bellhops.
    public static Semaphore bagsDelivered; // Semaphore for when bags are delivered by bellhop.
    public static Semaphore roomRegistered; // Semaphore for when room is assigned to guest.
    public static Semaphore bagsReceived; // Semaphore for when bags are received by guest.
    
    public static Semaphore reachedRoom[]; // When guests have reached their rooms.
    public static Semaphore guestsCompleted[]; // When guest threads are terminated.
    
    public static int guestJoined;
    
    public static int frontDeskID[]; // Array to store IDs of front desk employees for each guest.
    public static int bellhopID[]; // Array to store IDs of bellhops for each guest.
    
    
    public Thread hotel;

    public Hotel() {
        
    	guestAvailable = new Semaphore(0, true);
        bellhopRequested = new Semaphore(0, true);
        leftFrontDesk = new Semaphore(0, true);
        bagsDelivered = new Semaphore(0, true);
        bagsReceived = new Semaphore(0, true);
        guestMutex = new Semaphore(1, true);
        frontDeskMutex = new Semaphore(1, true);
        bellhopMutex = new Semaphore(1, true);
        roomRegistered = new Semaphore(0, true);
        frontDeskEmp = new Semaphore(2, true);
        bellhop = new Semaphore(2, true);
        guestsCompleted = new Semaphore[25];
        reachedRoom = new Semaphore[25];
        
        guestQueue = new LinkedList<Guest>();
        bellhopQueue = new LinkedList<Guest>(); 
        
        frontDeskID = new int [25];
        bellhopID = new int [25];
        guestJoined = 0;
        
        for (int i = 0; i < 25; i++) {
            guestsCompleted[i] = new Semaphore(0, true);
            reachedRoom[i] = new Semaphore(0, true);
            frontDeskID[i] = 0;
            bellhopID[i] = 0;
        }

        hotel = new Thread();
    }

    public static void main(String[] args) {
        
    	Hotel hotel = new Hotel();
    	System.out.println("Simulation starts");

        // Creating two front-desk employees.
        for (int i = 0; i < 2; i++)
        	new FrontDesk(i, hotel);
        
        // Creating two bellhops.
        for (int i = 0; i < 2; i++)
            new Bellhop(i, hotel);

        // Creating 25 guests.
        for (int i = 0; i < 25; i++)
            new Guest(i, hotel);

        // This loop ensures that program runs until
        // there are guests who are not in their rooms.
        while(Hotel.guestJoined < 25)
            System.out.print("");

        System.out.println("Simulation ends");
        System.exit(0);
    }

    public static void joinedGuests() { // When guests are joined.
    	++Hotel.guestJoined;
    }

    @Override
    public void run() {}
}