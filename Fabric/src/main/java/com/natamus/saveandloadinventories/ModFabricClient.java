package com.natamus.saveandloadinventories;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.natamus.saveandloadinventories.util.Util;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public class ModFabricClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			// /saveinventory and /si
			dispatcher.register(ClientCommands.literal("saveinventory")
				.then(ClientCommands.argument("inventory-name", StringArgumentType.word())
				.executes(ModFabricClient::saveInventoryClient))
			);
			dispatcher.register(ClientCommands.literal("si")
				.then(ClientCommands.argument("inventory-name", StringArgumentType.word())
				.executes(ModFabricClient::saveInventoryClient))
			);

			// /listinventories
			dispatcher.register(ClientCommands.literal("listinventories")
				.executes(ModFabricClient::listInventoriesClient)
			);

			// /loadinventory and /li
			dispatcher.register(ClientCommands.literal("loadinventory")
				.then(ClientCommands.argument("inventory-name", StringArgumentType.word())
				.executes(ModFabricClient::loadInventoryClient))
			);
			dispatcher.register(ClientCommands.literal("li")
				.then(ClientCommands.argument("inventory-name", StringArgumentType.word())
				.executes(ModFabricClient::loadInventoryClient))
			);
		});
	}

	private static int saveInventoryClient(CommandContext<FabricClientCommandSource> command) {
		FabricClientCommandSource source = command.getSource();
		LocalPlayer player = source.getPlayer();
		if (player == null) {
			return 0;
		}

		String inventoryname = StringArgumentType.getString(command, "inventory-name").toLowerCase();
		if (inventoryname.trim().isEmpty()) {
			source.sendError(Component.literal("Inventory name is invalid: " + inventoryname).withStyle(ChatFormatting.RED));
			return 0;
		}

		String gearstring = Util.getPlayerGearString(player);
		if (gearstring.isEmpty()) {
			source.sendError(Component.literal("Something went wrong while generating inventory string.").withStyle(ChatFormatting.RED));
			return 0;
		}

		if (!Util.writeGearStringToFile(inventoryname, gearstring)) {
			source.sendError(Component.literal("Something went wrong while saving inventory to file.").withStyle(ChatFormatting.RED));
			return 0;
		}

		source.sendFeedback(Component.literal("Successfully saved inventory with name '" + inventoryname + "'!").withStyle(ChatFormatting.DARK_GREEN));
		source.sendFeedback(Component.literal("Load it with /loadinventory " + inventoryname).withStyle(ChatFormatting.DARK_GREEN));
		return 1;
	}

	private static int listInventoriesClient(CommandContext<FabricClientCommandSource> command) {
		FabricClientCommandSource source = command.getSource();
		String list = Util.getListOfInventories();
		source.sendFeedback(Component.literal("Saved inventories: " + list + ".").withStyle(ChatFormatting.DARK_GREEN));
		return 1;
	}

	private static int loadInventoryClient(CommandContext<FabricClientCommandSource> command) {
		FabricClientCommandSource source = command.getSource();
		LocalPlayer player = source.getPlayer();
		if (player == null) {
			return 0;
		}

		String inventoryname = StringArgumentType.getString(command, "inventory-name").toLowerCase();
		if (inventoryname.trim().isEmpty()) {
			source.sendError(Component.literal("Inventory name is invalid: " + inventoryname).withStyle(ChatFormatting.RED));
			return 0;
		}

		String gearstring = Util.getGearStringFromFile(inventoryname);
		if (gearstring.isEmpty()) {
			source.sendError(Component.literal("Unable to load inventory content for: " + inventoryname).withStyle(ChatFormatting.RED));
			return 0;
		}

		Util.setPlayerGearFromString(player, gearstring);
		if (player.isCreative() && Minecraft.getInstance().gameMode != null) {
			for (int i = 0; i < player.inventoryMenu.slots.size(); i++) {
				Minecraft.getInstance().gameMode.handleCreativeModeItemAdd(player.inventoryMenu.slots.get(i).getItem(), i);
			}
		}
		source.sendFeedback(Component.literal("Loaded inventory '" + inventoryname + "'!").withStyle(ChatFormatting.DARK_GREEN));
		return 1;
	}
}
