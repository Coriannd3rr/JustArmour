package com.coriander.justarmour;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JustArmourClient implements ClientModInitializer {

	public static JustArmourConfigData config;
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static final File configFile = new File(Minecraft.getInstance().gameDirectory, "config/justarmour_config.json");

	private static KeyMapping toggleHudKeybind;
	private static KeyMapping openConfigScreenKeybind;
	private static final KeyMapping.Category JUSTARMOUR_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("justarmour", "main"));

	@Override
	public void onInitializeClient() {
		loadConfig();

		HudRenderCallback.EVENT.register((context, delta) -> {
			// Set default position on first render if not set
			if (config.hudX == -1 || config.hudY == -1) {
				setDefaultPosition();
			}

			if (config.hudEnabled) {
				renderArmorHUD(context, config.hudX, config.hudY);
				renderHeldItemHUD(context);
				renderOffhandItemHUD(context);
			}
		});

		registerKeybinds();
	}

	private void setDefaultPosition() {
		Minecraft client = Minecraft.getInstance();
		int screenWidth = client.getWindow().getGuiScaledWidth();
		int screenHeight = client.getWindow().getGuiScaledHeight();

		// Calculate perfect bottom right position for armor
		int rightMargin = config.durabilityOnRight ? 80 : 60;
		int bottomMargin = 40;

		config.hudX = screenWidth - rightMargin;
		config.hudY = screenHeight - bottomMargin;

		// Set held item position below armor
		config.heldItemX = config.hudX;
		config.heldItemY = config.hudY + config.spacing;

		// Set offhand item position to left of armor
		config.offhandItemX = config.hudX - 80;
		config.offhandItemY = config.hudY;

		saveConfig();
	}

	private void registerKeybinds() {
		// Toggle HUD keybind (G)
		toggleHudKeybind = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.justarmour.toggle",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_G,
				JUSTARMOUR_CATEGORY
		));

		// Config Screen keybind (J)
		openConfigScreenKeybind = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.justarmour.config",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_J,
				JUSTARMOUR_CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// Toggle HUD
			while (toggleHudKeybind.consumeClick()) {
				config.hudEnabled = !config.hudEnabled;
				saveConfig();

				if (client.player != null) {
					String message = "Armor HUD " + (config.hudEnabled ? "on" : "off");
					client.player.displayClientMessage(Component.literal(message), false);
				}
			}

			// Open Config Screen
			while (openConfigScreenKeybind.consumeClick()) {
				if (client.player != null) {
					client.setScreen(new TransparentConfigScreen());
				}
			}
		});
	}

	public static void renderArmorHUD(GuiGraphics context, int baseX, int baseY) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.options.hideGui) return;

		int y = baseY;

		// Get armor slots in order: boots, leggings, chestplate, helmet
		EquipmentSlot[] armorSlots = {
				EquipmentSlot.FEET,
				EquipmentSlot.LEGS,
				EquipmentSlot.CHEST,
				EquipmentSlot.HEAD
		};

		// Render armor pieces
		for (EquipmentSlot slot : armorSlots) {
			ItemStack stack = client.player.getItemBySlot(slot);
			if (!stack.isEmpty()) {
				renderArmorPiece(context, stack, baseX, y);
			}
			y -= (int)(config.spacing * config.scale);
		}
	}

	public static void renderHeldItemHUD(GuiGraphics context) {
		if (!config.showHeldItem) return;

		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.options.hideGui) return;

		ItemStack heldItem = client.player.getMainHandItem();
		if (heldItem.isEmpty()) return;

		// Show all items if enabled, otherwise only damageable
		if (!config.showAllHeldItems && !heldItem.isDamageableItem()) return;

		renderArmorPiece(context, heldItem, config.heldItemX, config.heldItemY);
	}

	public static void renderOffhandItemHUD(GuiGraphics context) {
		if (!config.showOffhandItem) return;

		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.options.hideGui) return;

		ItemStack offhandItem = client.player.getOffhandItem();
		if (offhandItem.isEmpty()) return;

		// Show all items if enabled, otherwise only damageable
		if (!config.showAllHeldItems && !offhandItem.isDamageableItem()) return;

		renderArmorPiece(context, offhandItem, config.offhandItemX, config.offhandItemY);
	}

	private static void renderArmorPiece(GuiGraphics context, ItemStack stack, int x, int y) {
		Minecraft client = Minecraft.getInstance();

		// Calculate positions with scale
		int iconX = config.durabilityOnRight ? x - (int)(10 * config.scale) : x + (int)(10 * config.scale);

		// SCALE ITEMS AND TEXT using 1.21.11 matrix method
		context.pose().pushMatrix();
		context.pose().scale(config.scale, config.scale);

		int scaledIconX = (int)(iconX / config.scale);
		int scaledY = (int)(y / config.scale);

		// Draw item with vanilla durability bar if enabled
		if (config.showDurabilityBar) {
			context.renderItem(stack, scaledIconX, scaledY);
			context.renderItemDecorations(client.font, stack, scaledIconX, scaledY);
		} else {
			// Draw item without bar
			context.renderItem(stack, scaledIconX, scaledY);
		}

		// Don't render durability text if hidden or item isn't damageable
		if (!config.hideDurabilityNumbers && stack.isDamageableItem()) {
			int durability = stack.getMaxDamage() - stack.getDamageValue();
			int max = stack.getMaxDamage();
			int color;

			// Color logic
			if (config.disableColors) {
				color = 0xFFFFFFFF;
			} else {
				if (durability == max) {
					color = 0xFF55FF55;
				} else if (durability == max - 1) {
					color = 0xFFFFFFFF;
				} else if (durability <= 71) {
					color = 0xFFFF5555;
				} else if (durability <= 149) {
					color = 0xFFFFA500;
				} else if (durability <= 281) {
					color = 0xFFFFFF55;
				} else {
					color = 0xFFFFFFFF;
				}
			}

			String text = config.showMaxDamage ? durability + "/" + max : String.valueOf(durability);
			int textWidth = client.font.width(text);

			int textX;
			if (config.durabilityOnRight) {
				textX = (int)((x + 10) / config.scale);
			} else {
				textX = (int)((x - textWidth * config.scale - 10) / config.scale) + 16;
			}

			int textY = scaledY + 5;

			context.drawString(client.font, text, textX, textY, color, config.showShadow);
		}

		context.pose().popMatrix();
	}

	public static void loadConfig() {
		try {
			if (!configFile.exists()) {
				config = new JustArmourConfigData();
				saveConfig();
				return;
			}
			FileReader reader = new FileReader(configFile);
			config = gson.fromJson(reader, JustArmourConfigData.class);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			config = new JustArmourConfigData();
		}
	}

	public static void saveConfig() {
		try {
			configFile.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(configFile);
			gson.toJson(config, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}