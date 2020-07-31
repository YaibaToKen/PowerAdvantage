package com.mcmoddev.poweradvantage;

import com.mcmoddev.lib.events.MMDLibRegisterBlocks;
import com.mcmoddev.lib.events.MMDLibRegisterBlockTypes;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LibEvents {
	@SubscribeEvent
	public static void blockTypeRegistrationEvent(MMDLibRegisterBlockTypes ev) {
		//IRegAPI<Block> api = ev.getApi();
		// what can be done here ?
	}

	public LibEvents() {
		// TODO Auto-generated constructor stub
	}

}
