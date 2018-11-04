package eventScheduler;

public class ProcessSRTF extends Process implements Comparable<ProcessSRTF>{
	double responseRatio;
	double waitingTime;
	int type;
	boolean completed;
	int place;
	double remainingTime;
	
	public int compareTo(ProcessSRTF e) {
		return this.remainingTime > e.remainingTime ? 1 : this.remainingTime < e.remainingTime ? -1 : 0;
	}
}
