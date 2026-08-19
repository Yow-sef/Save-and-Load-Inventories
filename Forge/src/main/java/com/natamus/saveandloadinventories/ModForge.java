package com.natamus.saveandloadinventories;

import com.natamus.saveandloadinventories.forge.events.ForgeCommandRegisterEvent;
import com.natamus.saveandloadinventories.util.Reference;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Reference.MOD_ID)
public class ModForge {
	
	public ModForge(FMLJavaModLoadingContext modLoadingContext) {
		BusGroup busGroup = modLoadingContext.getModBusGroup();
		FMLLoadCompleteEvent.getBus(busGroup).addListener(this::loadComplete);

		setGlobalConstants();
		ModCommon.init();
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		ForgeCommandRegisterEvent.registerEventsInBus();
	}

	private static void setGlobalConstants() {

	}
}