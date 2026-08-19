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

public class CommandLoadinventory {
	private static final SuggestionProvider<CommandSourceStack> INVENTORY_SUGGESTIONS = (context, builder) ->
		SharedSuggestionProvider.suggest(Util.getSavedInventoryNames(), builder);

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("loadinventory").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
			.then(Commands.argument("inventory-name", StringArgumentType.word())
			.suggests(INVENTORY_SUGGESTIONS)
			.executes((command) -> {
				return loadInventory(command);
			})
			.then(Commands.argument("player-name", StringArgumentType.word())
			.executes((command) -> {
				return loadInventoryForPlayerName(command);
			})))
		);
		dispatcher.register(Commands.literal("li").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
			.then(Commands.argument("inventory-name", StringArgumentType.word())
			.suggests(INVENTORY_SUGGESTIONS)
			.executes((command) -> {
				return loadInventory(command);
			})
			.then(Commands.argument("player-name", StringArgumentType.word())
			.executes((command) -> {
				return loadInventoryForPlayerName(command);
			})))
		);
	}
	
	private static int loadInventory(CommandContext<CommandSourceStack> command) {
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
		
		String gearstring = Util.getGearStringFromFile(inventoryname);
		if (gearstring.isEmpty()) {
			Util.sendMessage(source, "Unable to load inventory content for: " + inventoryname, ChatFormatting.RED);
			return 0;					
		}
		
		Util.setPlayerGearFromString(player, gearstring);
		Util.sendMessage(source, "Loaded own inventory '" + inventoryname + "'!", ChatFormatting.DARK_GREEN);
		return 1;
	}
	
	private static int loadInventoryForPlayerName(CommandContext<CommandSourceStack> command) {
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
		
		String targetname = StringArgumentType.getString(command, "player-name").toLowerCase();
		Player target = Util.matchPlayer(player, targetname);
		if (target == null) {
			Util.sendMessage(source, "Unable to find online player with name: " + targetname, ChatFormatting.RED);
			return 0;			
		}
		
		String gearstring = Util.getGearStringFromFile(inventoryname);
		if (gearstring.isEmpty()) {
			Util.sendMessage(source, "Unable to load inventory content for: " + inventoryname, ChatFormatting.RED);
			return 0;					
		}
		
		Util.setPlayerGearFromString(target, gearstring);
		Util.sendMessage(source, "Successfully loaded inventory '" + inventoryname + "' for " + target.getName().getString() + ".", ChatFormatting.DARK_GREEN);
		Util.sendMessage(target, "Your inventory has been replaced with preset '" + inventoryname + "'.", ChatFormatting.DARK_GREEN);
		return 1;
	}
}
