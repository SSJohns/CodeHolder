package stat.prim;

public class PlayerTime {
	private int id;
	private long time;
	
	
	public PlayerTime(int id, long time) {
		this.id = id;
		this.time = time;
	}
	
	public int getID() {
		return id;
	}
	public void setTime(long time) {
		this.time = time;	
	}
	public long getTime() {
		return time;
	}
}

