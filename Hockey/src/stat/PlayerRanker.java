package stat;

import java.util.ArrayList;
import java.util.List;

import stat.prim.*;

public class PlayerRanker {
	public static int rankPlayer(Player p) {
		int gameNum = 0, winNum = 0, totNum = 0;
		long gameTime, winTime, curTime;
		float winPerc, gamePerc, perc;
		
		
		//# games, # winning games, contribution to winning games, # goals
		//
		
		int id = p.getID();
		
		List<Season> seasonList = EventController.getSeasonList();
		
		for(Season curSeason : seasonList)
			for(Event curEvent : curSeason.getEventList()) {
				totNum++;
				
				for(PlayerTime curPT : curEvent.getTimeList())
					if(curPT.getID() == id) {
						gameNum++;
						
						if(curEvent.getWin())
							winNum++;
						
						break;
					}
			}
		
		if(gameNum == 0)
			return 0;
		
		
		
		winPerc = (float) winNum/gameNum;
		gamePerc = (float) gameNum/totNum;
		
		perc = .5f*(winPerc + gamePerc);
		
				
		return (int) (1000*perc);	
	}
}
