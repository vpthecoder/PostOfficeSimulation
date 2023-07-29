/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 *
 * @author vedantprakash
 */

public class Project2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        


        System.out.println("Simulating Post Office with 50 customers and 3 postal workers");
        System.out.println();

        // Define semaphores
        Semaphore customerQueue = new Semaphore(10, true);
        Semaphore postalWorkerQueue = new Semaphore(3, true);
        Semaphore customerReady = new Semaphore(0, true);
        Semaphore scales = new Semaphore(1, true);
        
        Queue<Integer> queue = new LinkedList<>();
        Semaphore addToQueue = new Semaphore(1, true);
        Queue<Integer> custList = new LinkedList<>();
        Semaphore getFromQueue = new Semaphore(1, true);
        Semaphore done = new Semaphore(0, true);
        Queue<Integer> workerList = new LinkedList<>();
        Semaphore workerReady = new Semaphore(0, true);
        
        

        // Define task table
        double[] taskTable = {1, 1.5, 2}; // task times in seconds
        String[] taskList = {"buy stamps", "mail a letter", "mail a package"};
        String[] taskList2 = {"buying stamps", "mailing a letter", "mailing a package"};

        // Define customer thread
        class Customer implements Runnable {
            int id;
            int task;
            int pid;
            
    Random rand = new Random();
    int randomNumber = rand.nextInt(3);
            
            

            Customer(int id) {
                this.id = id;
                
                
            }

            public void run() {
                try {
                    // Enter the customer queue
                    customerQueue.acquire();
                    System.out.println("Customer " + id + " enters post office");
                       
                    // Wait for a postal worker to become available
                    postalWorkerQueue.acquire();
                    
                    task = randomNumber;
                    addToQueue.acquire();
                    queue.add(task);
                    custList.add(id);
                    addToQueue.release();
                    customerReady.release();
                    
                    workerReady.acquire();
                    pid = workerList.poll();
                    
                    System.out.println("Customer " + id + " asks postal worker " +  pid + " to " + taskList[task]);
                    
                    // Go to postal worker and start task
                    
                    //acquire when worker has dinshed task
                    done.acquire();
                    
                                                       
               
                    // Release postal worker and leave post office
                    postalWorkerQueue.release();
                    customerQueue.release();
                    
                    
                    System.out.println("Customer " + id + " has finished " + taskList2[task]);
                    System.out.println("Customer " + id + " leaves post office");
                    
                    System.out.println("Joined Customer " + id);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

      

class PostalWorker implements Runnable {
    int id;
    int task;
    int custid;
    
    
    
    

    PostalWorker(int id) {
        this.id = id;
        
        
    }

    public void run() {
        try {
            while (true) {
               
                
                customerReady.acquire();
                
                getFromQueue.acquire();
                task = queue.poll();
                custid = custList.poll();
                getFromQueue.release();
                System.out.println("Postal worker " + id + " serving customer " + custid);
                workerList.add(id);
                workerReady.release();
                
                
                
                if (task == 2) { // Scale needed for package
                        scales.acquire();
                        System.out.println("Scales in use by postal worker " + id);
                        //System.out.println("Postal worker " + id % 3 + " weighing package for customer " + id);
                        Thread.sleep((long) (taskTable[task]* 1000.0));
                        //System.out.println("Postal worker " + id % 3 + " finished weighing package for customer " + id);
                        System.out.println("Scales released by postal worker " + id);
                        scales.release();
                        System.out.println("Postal worker " + id + " finished serving customer " + custid);
                        
                        
                    } else {
                        Thread.sleep((long) (taskTable[task] * 1000));
                        System.out.println("Postal worker " + id + " finished serving customer " + custid);
                        
                    }
                
                
                done.release();
                
                
            }
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


        for (int i = 0; i < 3; i++) {
            Thread postalWorker = new Thread(new PostalWorker(i));
            postalWorker.setDaemon(true);
            System.out.println("Postal Worker " + i + " created");
            postalWorker.start();
        }
        // Create and start threads
        for (int i = 0; i < 50; i++) {
            Thread customer = new Thread(new Customer(i));
            
            System.out.println("Customer " + i + " created");
            
            customer.start();
            
        }
        
        
    }
}