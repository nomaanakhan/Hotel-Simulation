//package com.hotel;

// Nomaan Khan
// CS 4348-001
// Project 2


class FrontDesk implements Runnable{
    
    public int id; // Front desk employee id.
    public static int roomNumber = 0; // Assigned rooms for the guests.

    public FrontDesk(int num, Hotel hotel) { // Initializing all the required variables.
        id = num;
        System.out.println("Front desk employee "  + id + " created");
        Thread frontDesk = new Thread(this);
        frontDesk.start();
    }

    @Override
    public void run() {
        try {
            while(true) {
                
                Hotel.guestAvailable.acquire(); // Wait for guest. 

                Hotel.frontDeskMutex.acquireUninterruptibly(); // Give guest a room without interrupts.
                Guest guest = Hotel.guestQueue.remove(); // Remove guest from queue to front desk.
                roomNumber++;
                guest.roomNumber = roomNumber; // Store room number.
                Hotel.frontDeskMutex.release(); // Unlock mutex.

                Hotel.frontDeskID[guest.id] = id; // Store front desk employee id for the guest.
                Hotel.roomRegistered.release(); // Room has been registered and assigned.
                System.out.println("Front desk employee " + id + 
                		" registers guest " + guest.id + 
                		" and assigns room " + guest.roomNumber);

                Hotel.guestsCompleted[guest.id].release(); // Guest completed.
                Hotel.leftFrontDesk.acquire(); // Guest leaves front desk.
                Hotel.frontDeskEmp.release(); // One front desk employee is freed.
            }
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
    }
}
