package com.example.demotestmaven;

public class MyThread extends Thread {
    public void run() {
        // Code to be executed in the new thread

        for (int i = 1; i <= 5; i++) {
            System.out.println("Printing thread: " + Thread.currentThread().threadId() + ": Count " + i);
            try {
                // Sleep for a random time between 0 and 1000 milliseconds
                Thread.sleep((long) (Math.random() * 100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
