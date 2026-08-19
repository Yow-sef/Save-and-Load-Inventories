package com.natamus.saveandloadinventories.cmds;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.natamus.collective.functions.PlayerFunctions;
import com.natamus.collective.functions.MessageFunctions;
import com.natamus.saveandloadinventories.util.Util;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public class CommandLoadinventory {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("loadinventory").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
			.then(Commands.argument("inventory-name", StringArgumentType.word())
			.executes((command) -> {
				return loadInventory(command);
			}))
			.then(Commands.argument("inventory-name", StringArgumentType.word())
			.then(Commands.argument("player-name", StringArgumentType.word())
			.executes((command) -> {
				return loadInventoryForPlayerName(command);
			})))
		);
		dispatcher.register(Commands.literal("li").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
			.then(Commands.argument("inventory-name", StringArgumentType.word())
			.executes((command) -> {
				return loadInventory(command);
			}))
			.then(Commands.argument("inventory-name", StringArgumentType.word())
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
			MessageFunctions.sendTranslatableMessage(source, "collective.shared.message.playeronly", ChatFormatting.RED);
			return 1;
		}
		
		String inventoryname = StringArgumentType.getString(command, "inventory-name").toLowerCase();
		if (inventoryname.trim() == "") {
			MessageFunctions.sendTranslatableMessage(source, "collective.saveandloadinventories.message.inventorynameinvalid", ChatFormatting.RED, inventoryname);
			return 0;
		}
		
		String gearstring = Util.getGearStringFromFile(inventoryname);
		if (gearstring == "") {
			MessageFunctions.sendTranslatableMessage(source, "collective.saveandloadinventories.message.unableloadcontent", ChatFormatting.RED, inventoryname);
			return 0;					
		}
		
		PlayerFunctions.setPlayerGearFromString(player, gearstring);
		MessageFunctions.sendTranslatableMessage(source, "collective.saveandloadinventories.message.loadedowninventory", ChatFormatting.DARK_GREEN, inventoryname);
		return 1;
	}
	
	private static int loadInventoryForPlayerName(CommandContext<CommandSourceStack> command) {
		CommandSourceStack source = command.getSource();
		
		Player player;
		try {
			player = source.getPlayerOrException();
		}
		catch (CommandSyntaxException ex) {
			MessageFunctions.sendTranslatableMessage(source, "collective.shared.message.playeronly", ChatFormatting.RED);
			return 1;
		}
		
		String inventoryname = StringArgumentType.getString(command, "inventory-name").toLowerCase();
		if (inventoryname.trim() == "") {
			MessageFunctions.sendTranslatableMessage(source, "collective.saveandloadinventories.message.inventorynameinvalid", ChatFormatting.RED, inventoryname);
			return 0;
		}
		
		String targetname = StringArgumentType.getString(command, "player-name").toLowerCase();
		Player target = PlayerFunctions.matchPlayer(player, targetname.toLowerCase());
		if (target == null) {
			MessageFunctions.sendTranslatableMessage(source, "collective.saveandloadinventories.message.unablefindonline", ChatFormatting.RED, targetname);
			return 0;			
		}
		
		String gearstring = Util.getGearStringFromFile(inventoryname);
		if (gearstring == "") {
			MessageFunctions.sendTranslatableMessage(source, "collective.saveandloadinventories.message.unableloadcontent", ChatFormatting.RED, inventoryname);
			return 0;					
		}
		
		PlayerFunctions.setPlayerGearFromString(target, gearstring);
		MessageFunctions.sendTranslatableMessage(source, "collective.saveandloadinventories.message.successfullyloadedinventory", ChatFormatting.DARK_GREEN, inventoryname, target.getName().getString());
		MessageFunctions.sendTranslatableMessage(target, "collective.saveandloadinventories.message.inventoryreplacedpreset", ChatFormatting.DARK_GREEN, inventoryname);
		return 1;
	}
}
