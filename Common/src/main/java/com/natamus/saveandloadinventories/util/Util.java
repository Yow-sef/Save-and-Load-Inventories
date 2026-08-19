package com.natamus.saveandloadinventories.util;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class Util {
	private static final String dirpath = "config" + File.separator + "saveandloadinventories";

	public static boolean writeGearStringToFile(String filename, String gearstring) {
		File dir = new File(dirpath);
		dir.mkdirs();

		try (PrintWriter writer = new PrintWriter(dirpath + File.separator + filename + ".txt", StandardCharsets.UTF_8)) {
			writer.println(gearstring);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static String getGearStringFromFile(String filename) {
		File dir = new File(dirpath);
		File file = new File(dirpath + File.separator + filename + ".txt");

		String gearstring = "";
		if (dir.isDirectory() && file.isFile()) {
			try {
				gearstring = new String(Files.readAllBytes(Paths.get(dirpath + File.separator + filename + ".txt")));
			} catch (IOException ignored) {}
		}

		return gearstring;
	}

	public static java.util.List<String> getSavedInventoryNames() {
		java.util.List<String> names = new java.util.ArrayList<>();
		File folder = new File(dirpath);
		if (!folder.isDirectory()) {
			return names;
		}

		File[] listOfFiles = folder.listFiles();
		if (listOfFiles != null) {
			for (File listOfFile : listOfFiles) {
				if (listOfFile.isFile() && listOfFile.getName().endsWith(".txt")) {
					names.add(listOfFile.getName().replace(".txt", ""));
				}
			}
		}

		return names;
	}

	public static String getListOfInventories() {
		StringBuilder inventories = new StringBuilder();

		File folder = new File(dirpath);
		if (!folder.isDirectory()) {
			return inventories.toString();
		}

		File[] listOfFiles = folder.listFiles();
		if (listOfFiles != null) {
			for (File listOfFile : listOfFiles) {
				if (listOfFile.isFile() && listOfFile.getName().endsWith(".txt")) {
					if (!inventories.toString().isEmpty()) {
						inventories.append(", ");
					}
					inventories.append(listOfFile.getName().replace(".txt", ""));
				}
			}
		}

		return inventories.toString();
	}

	public static String getPlayerGearString(Player player) {
		RegistryOps<Tag> registryOps = player.level().registryAccess().createSerializationContext(NbtOps.INSTANCE);
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 36; i++) {
			ItemStack stack = player.getInventory().getItem(i);
			sb.append(i).append(": '").append(encodeItemStack(stack, registryOps)).append("'\n");
		}

		sb.append("head: '").append(encodeItemStack(player.getItemBySlot(EquipmentSlot.HEAD), registryOps)).append("'\n");
		sb.append("chest: '").append(encodeItemStack(player.getItemBySlot(EquipmentSlot.CHEST), registryOps)).append("'\n");
		sb.append("legs: '").append(encodeItemStack(player.getItemBySlot(EquipmentSlot.LEGS), registryOps)).append("'\n");
		sb.append("feet: '").append(encodeItemStack(player.getItemBySlot(EquipmentSlot.FEET), registryOps)).append("'\n");
		sb.append("offhand: '").append(encodeItemStack(player.getItemBySlot(EquipmentSlot.OFFHAND), registryOps)).append("'\n");

		return sb.toString();
	}

	private static String encodeItemStack(ItemStack stack, RegistryOps<Tag> registryOps) {
		if (stack == null || stack.isEmpty()) {
			return "";
		}
		try {
			Optional<Tag> tag = ItemStack.CODEC.encodeStart(registryOps, stack).result();
			return tag.map(Object::toString).orElse("");
		} catch (Exception e) {
			return "";
		}
	}

	public static void setPlayerGearFromString(Player player, String gearstring) {
		if (gearstring == null || gearstring.trim().isEmpty()) {
			return;
		}

		RegistryOps<Tag> registryOps = player.level().registryAccess().createSerializationContext(NbtOps.INSTANCE);
		boolean cleared = false;
		String[] lines = gearstring.split("\n");

		for (String rawLine : lines) {
			String line = rawLine.trim();
			if (line.isEmpty() || !line.contains(": '")) {
				continue;
			}

			int firstColon = line.indexOf(": '");
			String slotName = line.substring(0, firstColon).trim();
			String itemNbt = line.substring(firstColon + 3);
			if (itemNbt.endsWith("'")) {
				itemNbt = itemNbt.substring(0, itemNbt.length() - 1);
			}

			ItemStack itemStack = ItemStack.EMPTY;
			if (!itemNbt.isEmpty()) {
				try {
					CompoundTag tag = TagParser.parseCompoundFully(itemNbt);
					itemStack = ItemStack.CODEC.parse(registryOps, tag).result().orElse(ItemStack.EMPTY);
				} catch (Exception ignored) {}
			}

			if (!cleared) {
				cleared = true;
				player.getInventory().clearContent();
			}

			try {
				int slot = Integer.parseInt(slotName);
				if (slot >= 0 && slot < 36) {
					player.getInventory().setItem(slot, itemStack);
				}
			} catch (NumberFormatException e) {
				switch (slotName.toLowerCase()) {
					case "head" -> player.setItemSlot(EquipmentSlot.HEAD, itemStack);
					case "chest" -> player.setItemSlot(EquipmentSlot.CHEST, itemStack);
					case "legs" -> player.setItemSlot(EquipmentSlot.LEGS, itemStack);
					case "feet" -> player.setItemSlot(EquipmentSlot.FEET, itemStack);
					case "offhand" -> player.setItemSlot(EquipmentSlot.OFFHAND, itemStack);
				}
			}
		}
	}

	public static Player matchPlayer(Player player, String targetName) {
		if (player.level().getServer() == null) {
			return null;
		}
		return player.level().getServer().getPlayerList().getPlayerByName(targetName);
	}

	public static void sendMessage(CommandSourceStack source, String message, ChatFormatting formatting) {
		source.sendSuccess(() -> Component.literal(message).withStyle(formatting), false);
	}

	public static void sendMessage(Player player, String message, ChatFormatting formatting) {
		player.sendSystemMessage(Component.literal(message).withStyle(formatting));
	}
}