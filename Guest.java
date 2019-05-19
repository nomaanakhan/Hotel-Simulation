//package com.hotel;

// Nomaan Khan
// CS 4348-001
// Project 2

import java.util.Random;

class Guest implements Runnable{

    public Hotel hotel;
    public int id; // Guest id.
    public int roomNumber; // The guest's room number.
    public int bagCount; // Count of bags the guest has.
    public Thread guest; // Guest thread.
    
    public Guest(int ID, Hotel hotel) { // Initializing all of the required variables for each instance of Guest.
        Random random = new Random();
        this.hotel = hotel;
        id = ID;
        bagCount  = random.nextInt(5 - 0 + 1);

        System.out.println("Guest "  + id + " created");
        guest = new Thread(this);
        guest.start();
    }
    
    @Override
    public void run() {
        
    	try {
            
        	System.out.println("Guest " + id + " enters the hotel with " 
            		+ bagCount + " bags");

            Hotel.guestMutex.acquire(); // Lock mutual exclusion
            Hotel.guestQueue.add(this); // Adding guest to queue to the front desk.
            Hotel.guestMutex.release(); // Unlock mutual exclusion

            Hotel.frontDeskEmp.acquire(); // Wait for front desk employee
            Hotel.guestAvailable.release(); // Signal guest available.
            Hotel.guestsCompleted[id].acquire();
            Hotel.roomRegistered.acquire(); // Wait for room from front desk.
            System.out.println("Guest " + id + " receives room key for room " + roomNumber 
            		+ " from front desk employee " + Hotel.frontDeskID[id]);

            Hotel.leftFrontDesk.release(); // Guest has left front desk.
            
            // Guests with more than 2 bags requests a bellhop.
            if (bagCount > 2) {
                // Guest requests help with bagCount and waits for a bellhop.
                Hotel.bellhop.acquire();
                System.out.println("Guest " + id + " requests help with bags");
                Hotel.bellhopQueue.add(this);
                Hotel.bellhopRequested.release();

                // Guest goes to their room.
                Hotel.bagsReceived.acquire();
                System.out.println("Guest " + id + " enters room " + roomNumber);
                Hotel.reachedRoom[id].release();
                
                Hotel.bagsDelivered.acquire(); // Wait for the bellhop to bring bagCount before going to bed.
                System.out.println("Guest " + id + " receives bags from bellhop " 
                		+ Hotel.bellhopID[id] + " and gives tip");
            }
            
            else { // Guest with fewer than 2 bags goes to their room.
                Hotel.reachedRoom[id].release();
                System.out.println("Guest " + id + " enters room " + roomNumber);
            }

            System.out.println("Guest " + id + " retires for the evening"); // Now the guest can retire.
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        try { // Join the thread.
        	Hotel.joinedGuests();
            System.out.println("Guest " + id + " joined");
            guest.join();       
        } 
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }
}
