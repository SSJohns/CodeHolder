package io;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import stat.PlayerController;
import stat.prim.Player;

public class WirelessController {
	//private List<Wireless> deviceList = new ArrayList<Wireless>();
	
	
	public WirelessController() {
		/*Timer update;
		update.schedule(new TimerTask() {
			public void run() {	
				updatePlayerTime();
			}
		}, 0, (int) (1000./20));*/
	}
	
	/*public void addDevice(Wireless device) {
		deviceList.add(device);
	}*/
	
	public void updateData() {
		List<Player> playerList = PlayerController.getPlayerList();
		Player curPlayer;
		
		//Talk to Each Device
		//for(Wireless device : deviceList) {
			//curPlayer = playerList.get(device.id);
			//curPlayer.setOnIce(device.getInput());
		 //}
	}
}
