package com.natamus.saveandloadinventories.cmds;

import com.mojang.brigadier.CommandDispatcher;
import com.natamus.collective.functions.MessageFunctions;
import com.natamus.saveandloadinventories.util.Util;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.ChatFormatting;

public class CommandListinventories {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("listinventories").requires(Commands.hasPermission(Commands.LEVEL_ALL))
			.executes((command) -> {
				CommandSourceStack source = command.getSource();
				
				MessageFunctions.sendTranslatableMessage(source, "collective.saveandloadinventories.message.savedinventories", ChatFormatting.DARK_GREEN, Util.getListOfInventories());
				return 1;
			})
		);
	}
}
