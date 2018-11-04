package eventScheduler;

import java.util.Scanner;

public class Main {
	public static void main(String args[]) {
		Scanner sc = new Scanner(System.in);
		System.out.println("1] FCFS");
		System.out.println("2] HRRN");
		System.out.println("3] SRTF");
		System.out.println("4] RR(quantum = .02)");
		System.out.println("5] RR(quantum = .01)");
		System.out.println("Enter the scheduler you wish to use: ");
		int choice = sc.nextInt();
		
		switch(choice) {
			case 1:  
				for(int i = 1; i < 31; i++) {
					FCFS.getLambda(i);
					FCFS.main(args);
			}
				break;
			case 2:
				for(int i = 1; i < 31; i++) {
					HRRNext.getLambda(i);
					HRRNext.main(args);
			}
				break;
			case 3: 
				for(int i = 1; i < 31; i++) {
					SRTF.getLambda(i);
					SRTF.main(args);
				}
				break;
			case 4: 
				for(int i = 1; i < 31; i++) {
					RR.getLambda(i);
					RR.main(args);
				}
				break;
			case 5:
				for(int i = 1; i < 31; i++) {
					RR1.getLambda(i);
					RR1.main(args);
				}
				break;
			default: System.out.println("ERROR...");
					 System.out.println("You must enter an integer between 1 and 4");
					
		}
	}
}
