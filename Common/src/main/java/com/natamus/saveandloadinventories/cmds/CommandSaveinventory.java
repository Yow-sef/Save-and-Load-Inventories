package com.natamus.saveandloadinventories.cmds;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.natamus.saveandloadinventories.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.entity.player.Player;

public class CommandSaveinventory {
	private static final SuggestionProvider<CommandSourceStack> INVENTORY_SUGGESTIONS = (context, builder) ->
		SharedSuggestionProvider.suggest(Util.getSavedInventoryNames(), builder);

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("saveinventory").requires(Commands.hasPermission(Commands.LEVEL_ALL))
			.then(Commands.argument("inventory-name", StringArgumentType.word())
			.suggests(INVENTORY_SUGGESTIONS)
			.executes((command) -> {
				return saveinventory(command);
			}))
		);
		dispatcher.register(Commands.literal("si").requires(Commands.hasPermission(Commands.LEVEL_ALL))
			.then(Commands.argument("inventory-name", StringArgumentType.word())
			.suggests(INVENTORY_SUGGESTIONS)
			.executes((command) -> {
				return saveinventory(command);
			}))
		);
	}
	
	private static int saveinventory(CommandContext<CommandSourceStack> command) {
		CommandSourceStack source = command.getSource();
		Player player;
		try {
			player = source.getPlayerOrException();
		}
		catch (CommandSyntaxException ex) {
			Util.sendMessage(source, "This command can only be executed by a player.", ChatFormatting.RED);
			return 1;
		}
		
		String inventoryname = StringArgumentType.getString(command, "inventory-name").toLowerCase();
		if (inventoryname.trim().isEmpty()) {
			Util.sendMessage(source, "Inventory name '" + inventoryname + "' is invalid.", ChatFormatting.RED);
			return 0;
		}
		
		String gearstring = Util.getPlayerGearString(player);
		if (gearstring.isEmpty()) {
			Util.sendMessage(source, "Something went wrong while generating inventory string.", ChatFormatting.RED);
			return 0;					
		}
		
		if (!Util.writeGearStringToFile(inventoryname, gearstring)) {
			Util.sendMessage(source, "Something went wrong while saving inventory to file.", ChatFormatting.RED);
			return 0;							
		}
		
		Util.sendMessage(source, "Successfully saved inventory with name '" + inventoryname + "'!", ChatFormatting.DARK_GREEN);
		Util.sendMessage(source, "Load it with /loadinventory " + inventoryname, ChatFormatting.DARK_GREEN);
		return 1;
	}
}
