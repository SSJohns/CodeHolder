package prog.gui;

public class Modifiable<K> {
	private K value;
	
	
	public Modifiable(K value) {
		this.value = value;
	}
	
	public void set(K value) {
		this.value = value;
	}
	
	public K get() {
		return value;
	}
}
