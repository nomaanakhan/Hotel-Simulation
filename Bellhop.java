//package com.hotel;

// Nomaan Khan
// CS 4348-001
// Project 2

class Bellhop implements Runnable{
	
    public int id;

    public Bellhop(int num, Hotel hotel) { // Initializing variables for Bellhop instance.
        id = num;
        System.out.println("Bellhop "  + id + " created");
        Thread bellhop = new Thread(this);
        bellhop.start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                
                Hotel.bellhopRequested.acquire(); // Wait for a customer to request bellhop.
                Hotel.bellhopMutex.acquireUninterruptibly(); // Mutual exclusion
                Guest guest = Hotel.bellhopQueue.remove(); // Remove guest from bellhop queue.

                Hotel.bellhopID[guest.id] = id; // Save bellhop id for guest.
                Hotel.bellhopMutex.release(); // Unlock mutex.

                System.out.println("Bellhop " + id + " receives bags from guest " + guest.id);
                Hotel.bagsReceived.release(); // Guest got their bags.

                Hotel.reachedRoom[guest.id].acquire(); // Wait for the guest to enter their room
                System.out.println("Bellhop " + id + " delivers bags to guest " + guest.id);
                Hotel.bagsDelivered.release(); // Bags delivered to room.
                Hotel.bellhop.release(); // Free bellhop.
            }
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
    }
}

