package stat.prim;
import java.util.ArrayList;
import java.util.List;

public class Season {
		private static List<Season> seasonList = new ArrayList<Season>();
	
		private int year;
		private List<Event> eventList;
		private Roster roster;
		private boolean isOpen = false;
		
		
		
		public Season(int year, List<Event> eventList, Roster roster) {
			this.year = year;
			this.eventList = eventList;
			this.roster = roster;
			
			seasonList.add(this);
		}
		
		
		public void setEventList(List<Event> eventList) {
			clearEventList();
			
			this.eventList = eventList;
		}
		public void addEvent(String name, String date, boolean didWin, long totalTime, List<PlayerTime> timeList, boolean isReal) {
			Event e = new Event(this, name, date, didWin, totalTime, timeList, true, isReal);
			
			eventList.add(e);
			e.include();
		}
		public void removeEvent(Event event) {
			eventList.remove(this);
		}
		
		public void clearEventList() {
			if(eventList == null)
				return;
			
			List<Event> destroyList = new ArrayList<Event>();
			
			for(Event e : eventList)
				if(!e.getIsReal())
					destroyList.add(e);
			
			for(Event e : destroyList)
				e.destroy();
		}
		
		//ACCESSOR/MUTATOR FUNCTIONS
			public int getYear() {
				return year;
			}
			public List<Event> getEventList() {
				return eventList;
			}
			public Roster getRoster() {
				return roster;
			}
			public void setOpen(boolean isOpen) {
				this.isOpen = isOpen;
			}
			public boolean getOpen() {
				return isOpen;
			}

			
			
		//GLOBAL
			public static Season getRandom() {
				int index;
				index = (int) Math.min((Math.random()*seasonList.size()), seasonList.size());
				
				return seasonList.get(index);
			}
		
		
	}