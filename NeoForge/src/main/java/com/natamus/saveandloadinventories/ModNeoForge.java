package com.natamus.saveandloadinventories;

import com.natamus.saveandloadinventories.neoforge.events.NeoForgeCommandRegisterEvent;
import com.natamus.saveandloadinventories.util.Reference;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod(Reference.MOD_ID)
public class ModNeoForge {
	
	public ModNeoForge(IEventBus modEventBus) {
		modEventBus.addListener(this::loadComplete);

		setGlobalConstants();
		ModCommon.init();
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		NeoForge.EVENT_BUS.register(NeoForgeCommandRegisterEvent.class);
	}

	private static void setGlobalConstants() {

	}
}