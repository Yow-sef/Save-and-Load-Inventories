package com.natamus.saveandloadinventories.cmds;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.natamus.collective.functions.MessageFunctions;
import com.natamus.collective.functions.PlayerFunctions;
import com.natamus.collective.functions.StringFunctions;
import com.natamus.saveandloadinventories.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public class CommandSaveinventory {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("saveinventory").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
			.then(Commands.argument("inventory-name", StringArgumentType.word())
			.executes((command) -> {
				return saveinventory(command);
			}))
		);
		dispatcher.register(Commands.literal("si").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
			.then(Commands.argument("inventory-name", StringArgumentType.word())
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
			MessageFunctions.sendTranslatableMessage(source, "collective.shared.message.playeronly", ChatFormatting.RED);
			return 1;
		}
		
		String inventoryname = StringArgumentType.getString(command, "inventory-name").toLowerCase();
		if (inventoryname.trim() == "") {
			MessageFunctions.sendTranslatableMessage(source, "collective.saveandloadinventories.message.inventorynameinvalid", ChatFormatting.RED, inventoryname);
			return 0;
		}
		
		String gearstring = PlayerFunctions.getPlayerGearString(player);
		if (StringFunctions.sequenceCount(gearstring, "\n") < 40) {
			MessageFunctions.sendTranslatableMessage(source, "collective.saveandloadinventories.message.somethingwentwrongwhilegenerating", ChatFormatting.RED);
			return 0;					
		}
		
		if (!Util.writeGearStringToFile(inventoryname, gearstring)) {
			MessageFunctions.sendTranslatableMessage(source, "collective.saveandloadinventories.message.somethingwentwrongwhile", ChatFormatting.RED, inventoryname);
			return 0;							
		}
		
		MessageFunctions.sendTranslatableMessage(source, "collective.saveandloadinventories.message.successfullysavedinventory", ChatFormatting.DARK_GREEN, inventoryname);
		MessageFunctions.sendTranslatableMessage(source, "collective.saveandloadinventories.message.loadcommandloadinventory", ChatFormatting.DARK_GREEN, inventoryname);
		return 1;
	}
}
