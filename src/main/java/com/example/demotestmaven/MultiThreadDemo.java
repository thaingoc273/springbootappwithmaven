package com.example.demotestmaven;

public class MultiThreadDemo {
    public static void main(String[] args) {
        // Create two threads
        Thread thread1 = new Thread(new NumberPrinter("Thread 1"));
        Thread thread2 = new Thread(new NumberPrinter("Thread 2"));

        // Start both threads
        thread1.start();
        thread2.start();

        try {
            // Wait for both threads to complete
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Both threads have completed their execution!");
    }
}

// A class that implements Runnable to print numbers
class NumberPrinter implements Runnable {
    private String threadName;

    public NumberPrinter(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println(threadName + ": Count " + i);
            try {
                // Sleep for a random time between 0 and 1000 milliseconds
                Thread.sleep((long) (Math.random() * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
} 