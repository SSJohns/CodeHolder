package stat.prim;


import java.util.List;

public class Roster {
	private List<Integer> idList;
	
	public Roster(List <Integer> idList) {
		this.idList = idList;
	}
	
	public List<Integer> getIDList() {
		return idList;
	}
}