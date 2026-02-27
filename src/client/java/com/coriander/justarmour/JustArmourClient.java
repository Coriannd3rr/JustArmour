package com.coriander.justarmour;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JustArmourClient implements ClientModInitializer {

	public static JustArmourConfigData config;
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static final File configFile = new File(MinecraftClient.getInstance().runDirectory, "config/justarmour_config.json");

	private static KeyBinding toggleHudKeybind;
	private static KeyBinding openConfigScreenKeybind;

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
		MinecraftClient client = MinecraftClient.getInstance();
		int screenWidth = client.getWindow().getScaledWidth();
		int screenHeight = client.getWindow().getScaledHeight();

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
		toggleHudKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"justarmourtoggleKey",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_G,
				"justarmour"
		));

		// Config Screen keybind (J)
		openConfigScreenKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"justarmourconfigKey",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_J,
				"justarmour"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// Toggle HUD
			while (toggleHudKeybind.wasPressed()) {
				config.hudEnabled = !config.hudEnabled;
				saveConfig();

				if (client.player != null) {
					String message = "Armor HUD " + (config.hudEnabled ? "on" : "off");
					client.player.sendMessage(Text.literal(message), false);
				}
			}

			// Open Config Screen
			while (openConfigScreenKeybind.wasPressed()) {
				if (client.player != null) {
					client.setScreen(new TransparentConfigScreen());
				}
			}
		});
	}

	public static void renderArmorHUD(DrawContext context, int baseX, int baseY) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.options.hudHidden) return;

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
			ItemStack stack = client.player.getEquippedStack(slot);
			if (!stack.isEmpty()) {
				renderArmorPiece(context, stack, baseX, y);
			}
			y -= (int)(config.spacing * config.scale);
		}
	}

	public static void renderHeldItemHUD(DrawContext context) {
		if (!config.showHeldItem) return;

		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.options.hudHidden) return;

		ItemStack heldItem = client.player.getMainHandStack();
		if (heldItem.isEmpty()) return;

		// Show all items if enabled, otherwise only damageable
		if (!config.showAllHeldItems && !heldItem.isDamageable()) return;

		renderArmorPiece(context, heldItem, config.heldItemX, config.heldItemY);
	}

	public static void renderOffhandItemHUD(DrawContext context) {
		if (!config.showOffhandItem) return;

		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.options.hudHidden) return;

		ItemStack offhandItem = client.player.getOffHandStack();
		if (offhandItem.isEmpty()) return;

		// Show all items if enabled, otherwise only damageable
		if (!config.showAllHeldItems && !offhandItem.isDamageable()) return;

		renderArmorPiece(context, offhandItem, config.offhandItemX, config.offhandItemY);
	}

	private static void renderArmorPiece(DrawContext context, ItemStack stack, int x, int y) {
		MinecraftClient client = MinecraftClient.getInstance();

		// Calculate positions with scale
		int iconX = config.durabilityOnRight ? x - (int)(10 * config.scale) : x + (int)(10 * config.scale);

		// SCALE ITEMS AND TEXT using 1.21.8 matrix method
		context.getMatrices().pushMatrix();
		context.getMatrices().scale(config.scale, config.scale);

		int scaledIconX = (int)(iconX / config.scale);
		int scaledY = (int)(y / config.scale);

		// Draw item with vanilla durability bar if enabled
		if (config.showDurabilityBar) {
			context.drawItem(stack, scaledIconX, scaledY);
			context.drawStackOverlay(client.textRenderer, stack, scaledIconX, scaledY);
		} else {
			// Draw item without bar
			context.drawItem(stack, scaledIconX, scaledY);
		}

		// Don't render durability text if hidden or item isn't damageable
		if (!config.hideDurabilityNumbers && stack.isDamageable()) {
			int durability = stack.getMaxDamage() - stack.getDamage();
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
			int textWidth = client.textRenderer.getWidth(text);

			int textX;
			if (config.durabilityOnRight) {
				textX = (int)((x + 10) / config.scale);
			} else {
				textX = (int)((x - textWidth * config.scale - 10) / config.scale) + 16;
			}

			int textY = scaledY + 5;

			context.drawText(client.textRenderer, text, textX, textY, color, config.showShadow);
		}

		context.getMatrices().popMatrix();
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