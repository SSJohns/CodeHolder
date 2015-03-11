package prog.panel;

public class Stopwatch {
	private static long startTime;
	
	public static void start() {
		startTime = System.currentTimeMillis();
	}
	
	public static void stop() {
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}
}
