package eventScheduler;

import java.io.FileWriter;
import java.util.*; 

public class HRRNext {
	//used to hold generated events
	public static Queue<ProcessHRRN> EventQueue;
	//used to hold events that are ready to be processed
	public static Queue<ProcessHRRN> readyQueue;
	//used to generate random arrival times in an increading order
	public static double newArrivalTime;
	//used to hold the current time
	public static double currentTime;
	//used to flag if the server is busy or not
	public static boolean serverBusy;
	//used to calculate the average service time
	public static double averageServiceTime;
	//used to hold the user input lambda(1<= x <= 30)
	public static double lambda;
	//used to hold the number of events completed
	public static int eventsCompleted;
	//used to calculate the average turnaround time
	public static double turnaroundArr[];
	//used to add all of the events in Ready Queue 
	public static double totalEventsInQueue;
	//used to calculate the number of times the 'getNumberOfEventsInQueue()' method is called
	public static      int cycles;
	//used to hold the amount of time the CPU is idle
	public static double timeSpentIdle;
	
	
	//this method is used to initialize all global variables and generate the list of events
	public static void init(){
		EventQueue = new LinkedList<ProcessHRRN>();
		readyQueue = new LinkedList<ProcessHRRN>();
		newArrivalTime = 0;
		serverBusy = false;
		averageServiceTime = (double) 0.042;
		eventsCompleted = 0;
		turnaroundArr = new double [10000];
		generateEvents();
		totalEventsInQueue = 0;
		cycles = 0;
		timeSpentIdle = 0;
	}
	
	//this method is used to get a random arrival time that follows a poisson distribution
	public static double poissonRandomArrival(double lambda) {
		return (Math.log(Math.random())/-lambda);
	}
	
	//used to retrieve the amount of events in the Ready Queue
	public static double getNumberOfEventsInQueue() {
		return (readyQueue.size());

	}

	//used to generate a random service time that follows an exponential distribution with an average service time being .06
	public static double generateServiceTime(double lambda) {
		double expRandom = (double) (-lambda * Math.log(Math.random())/Math.log(2));
		return expRandom;
	}

	//used to find all events that are ready and put them in the Ready Queue
	public static void generateReadyQueue() {
		int i = 0;
		ProcessHRRN e = new ProcessHRRN();
		e = ((LinkedList<ProcessHRRN>) EventQueue).get(0);
		while(e.arrivalTime <= currentTime) {
			e = ((LinkedList<ProcessHRRN>) EventQueue).get(i);
			if(!readyQueue.contains(e)) {
				readyQueue.add(e);
				EventQueue.remove(e);
			}
			i++;
			try {
			e = ((LinkedList<ProcessHRRN>) EventQueue).get(i);
			}
			catch(IndexOutOfBoundsException ex) {
				System.out.println("Index out of bounds exception...");
				break;
			}		
		}
		if(readyQueue.isEmpty()) {
			readyQueue.add(EventQueue.peek());
			EventQueue.remove();
		}
	}
	
	//used to get lambda from the Main class
	public static void getLambda(double l){
		lambda = l;
	}
	
	//used to find the event that has the highest response ratio
	public static int findHighestR() {
		generateReadyQueue();
		int i = 0;
		ProcessHRRN highestR = ((LinkedList<ProcessHRRN>) readyQueue).get(0);
		highestR.waitingTime = (currentTime - highestR.arrivalTime); 
		highestR.responseRatio = (highestR.serviceTime + highestR.waitingTime) / highestR.serviceTime;
		for(int j =1; j < readyQueue.size()-1; j++) {
			ProcessHRRN eve = ((LinkedList<ProcessHRRN>) readyQueue).get(j);
			eve.waitingTime = (currentTime - eve.arrivalTime);
			eve.responseRatio = (eve.serviceTime + eve.waitingTime) / eve.serviceTime;
			if (eve.responseRatio > highestR.responseRatio && eve.arrivalTime <= currentTime) {
				highestR = eve;
				i = j;
			}
		}
		return i;
	}
	
	//used to run the simulator
	public static void runSim() {
		totalEventsInQueue = 0;
		while(eventsCompleted < 10000) {
			processEvent();
			totalEventsInQueue += getNumberOfEventsInQueue();
			cycles++;
		}
	}
	
	//used to process the event that currently has the highest response ratio
	public static void processEvent() {
		int index = findHighestR();
		ProcessHRRN e = ((LinkedList<ProcessHRRN>) readyQueue).get(index);
		if(currentTime < e.arrivalTime) {
			timeSpentIdle += (e.arrivalTime - currentTime);
			currentTime = e.arrivalTime;
		}
		e.completionTime = (currentTime + e.serviceTime);
		currentTime = e.completionTime;
		e.completed = true;
		turnaroundArr[eventsCompleted] = (e.completionTime - e.arrivalTime);
		((LinkedList<ProcessHRRN>) readyQueue).remove(index);
		eventsCompleted++;
	}
	
	//used to generate a list of 15000 events
	public static void generateEvents() {
		ProcessHRRN first = new ProcessHRRN();
		first.arrivalTime = poissonRandomArrival(lambda);
		first.place = 0;
		newArrivalTime += first.arrivalTime;
		first.serviceTime = generateServiceTime(averageServiceTime);
		EventQueue.add(first);
		readyQueue.add(first);
		first.completionTime = (first.arrivalTime + first.serviceTime);
		currentTime = first.completionTime;
		first.completed = true;
		turnaroundArr[eventsCompleted] = (first.completionTime - first.arrivalTime);
		EventQueue.remove();
		eventsCompleted++;
		for(int i = 1; i < 15000; i++) {
			ProcessHRRN e = new ProcessHRRN();
			newArrivalTime += poissonRandomArrival(lambda);
			e.place = i;
			e.arrivalTime = newArrivalTime;
			e.serviceTime = generateServiceTime(averageServiceTime);
			e.completed = false;
			EventQueue.add(e);
		}
	}
	
	//used to calculate test data to compare with the real data found
	public static double calculateUtilization(double lambda) {
		return (lambda * averageServiceTime);
	}
	
	//used to calculate test data to compare with the real data found
	public static double calculateQ(double lambda, double Tq) {
		return (lambda * Tq);
	}
	
	//used to calculate test data to compare with the real data found
	public static double calculateAverageWaitingTime(double averageTurnaroundTime) {
		return (averageTurnaroundTime - averageServiceTime);
	}

	//used to generate the report with the requested metrics and display them to the console
	public static void generateReport() {
		double total = 0;
		for(int j = 0; j < 10000; j++) {
			total += turnaroundArr[j];
		}

		double averageNumOfEventsInQueue = (double)(totalEventsInQueue / cycles);
		double averageTurnaroundRate = (total / 10000);
		double averageWaitingTime = calculateAverageWaitingTime(averageTurnaroundRate);
		double q = calculateQ(lambda, averageTurnaroundRate);
		double totalThroughput = 10000 / currentTime;
		double testRow = (lambda / (1/ averageServiceTime));
		double row = (1 - (timeSpentIdle / currentTime));
		double testQ = ((row) / (1 - row));
		
		try {
			FileWriter fw = new FileWriter("HRRN.txt", true);
			fw.write("Lambda Used: " + lambda);
			fw.write("\n");
			fw.write("* * * * * * * * *ACTUAL DATA * * * * * * * * * * * * * * * * * * * * * * * *");
			fw.write("\n");
			fw.write("AVERAGE TURNAROUND TIME: " + averageTurnaroundRate);
			fw.write("\n");
			fw.write("TOTAL THROUGHPUT: " + totalThroughput);
			fw.write("\n");
			fw.write("CPU UTILIZATION: " + row);
			fw.write("\n");
			fw.write("AVERAGE NUMBER OF PROCESSES IN READY QUEUE: " + averageNumOfEventsInQueue);
			fw.write("\n");
			fw.write("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
			fw.write("\n");
			fw.close();
		}
		catch(Exception e) {System.out.println(e);}
		
		
		//System.out.print("\n");
		System.out.println("* * * * * * * * *ACTUAL DATA * * * * * * * * * * * * * * * * * * * * * * * *");
		System.out.println("AVERAGE TURNAROUND TIME: " + averageTurnaroundRate);
		System.out.println("TOTAL THROUGHPUT: " + totalThroughput);
		System.out.println("CPU UTILIZATION: " + row);
		System.out.println("AVERAGE NUMBER OF PROCESSES IN READY QUEUE: " + averageNumOfEventsInQueue);
		System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
		System.out.print("\n");
/*		
		System.out.println("* * * * * * * * * TEST DATA* * * * * * * * * * * * * * * * * * * * * * * *");
		System.out.println("TESTING ROW: " + testRow);
		System.out.println("AVERAGE NUMBER OF PROCESSES IN QUEUE: " + q);
		System.out.println("TEST Q WITH REAL ROW: " + testQ);
		System.out.println("AVERAGE WAITING TIME: " + averageWaitingTime);
		System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
*/	
	}	
	
	//main function
	public static void main(String args[])
	{
		
		//Scanner sc = new Scanner(System.in);
		//System.out.println("Enter lambda(average arrival rate): ");
		//lambda = sc.nextDouble();
		System.out.println("Lambda Used: " + lambda);
		init();
/*
		double total = 0;
		for(int i = 0; i < EventQueue.size();i++) {
			ProcessHRRN e = new ProcessHRRN();
			e = ((LinkedList<ProcessHRRN>) EventQueue).get(i);
			total += e.serviceTime;
		}
		
		double avg = total /15000;
*/			
		runSim();
		generateReport();
		//System.out.println("Avg Service TIME: " + avg);
		serverBusy = false;
		
	}
}
	