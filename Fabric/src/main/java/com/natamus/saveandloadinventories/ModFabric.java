package com.natamus.saveandloadinventories;

import com.natamus.saveandloadinventories.cmds.CommandListinventories;
import com.natamus.saveandloadinventories.cmds.CommandLoadinventory;
import com.natamus.saveandloadinventories.cmds.CommandSaveinventory;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModFabric implements ModInitializer {
	
	@Override
	public void onInitialize() {
		setGlobalConstants();
		ModCommon.init();

		loadEvents();
	}

	private void loadEvents() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			CommandListinventories.register(dispatcher);
			CommandLoadinventory.register(dispatcher);
			CommandSaveinventory.register(dispatcher);
		});
	}

	private static void setGlobalConstants() {

	}
}
