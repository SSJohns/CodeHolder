package math;

public final class Math2D {
	private Math2D() {
	}
	
	public static boolean isEven(int n) {
		return (n % 2 == 0);
	}
	
	public static double sqr(double n) {
		return n*n;
	}
	
	public static double lengthdirX(double dis, double dir) {
		return dis*Math.cos(dir/180*Math.PI);
	}
	public static double lengthdirY(double dis, double dir) {
		return -dis*Math.sin(dir/180*Math.PI);
	}
	
	public static double pointDirection(double x1, double y1, double x2, double y2) {
		return Math.atan2(y2-y1,x2-x1)/Math.PI*180;
	}
	
	public static double pointDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(sqr(x2-x1) + sqr(y2-y1));
	}
	
	public static boolean isInsideBox(int x, int y, int x1, int y1, int x2, int y2) {
		return (x > x1 && y > y1 && x < x2 && y < y2);
	}
	
	public static int roundToNearest(double num, int otherNum) {
		return (int) (otherNum*Math.round(num/otherNum));
	}
}
