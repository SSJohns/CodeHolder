package stat;

public class Header {
	String name;
	int num, w;
	float brightness = 230;
	
	
	public Header(int num, String name, int w) {
		this.num = num;
		this.name = name;
		this.w = w;
	}

	public int getNumber() {
		return num;
	}

	public int getWidth() {
		return w;
	}
}
