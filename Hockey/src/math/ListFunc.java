package math;

import java.util.List;

public final class ListFunc {
	private ListFunc() {
	}
	
	public static void sortListsWithFirst(List<Double> listNum, List listObj) {
		int s = listNum.size();
		boolean didMove;
		Double leftN, rightN;
		Object leftO, rightO;
		
		
		if(s == 0 || s == 1)
			return;
			
		
		do 
		{
			didMove = false;
			
			for(int i = 0; i < s-1; i++) {
				leftN = listNum.get(i);
				rightN = listNum.get(i+1);
				
				if(leftN.doubleValue() > rightN.doubleValue()) {
					listNum.set(i, rightN);
					listNum.set(i+1, leftN);
					
					
					leftO = listObj.get(i);
					rightO = listObj.get(i+1);
					
					listObj.set(i, leftO);
					listObj.set(i+1, rightO);
					
					didMove = true;
				}
			}
		} while(didMove);
	}
}
