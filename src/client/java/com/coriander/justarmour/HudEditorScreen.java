package com.coriander.justarmour;

import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.item.ItemStack;

public class HudEditorScreen extends Screen {
    private int armorDragOffsetX, armorDragOffsetY;
    private int heldDragOffsetX, heldDragOffsetY;
    private int offhandDragOffsetX, offhandDragOffsetY;
    private boolean draggingArmor = false;
    private boolean draggingHeldItem = false;
    private boolean draggingOffhandItem = false;

    protected HudEditorScreen() {
        super(Text.literal("HUD Editor"));
    }

    @Override
    protected void init() {
        super.init();

        // Register mouse event handlers using Fabric API
        ScreenMouseEvents.beforeMouseClick(this).register((screen, click) -> {
            double mouseX = click.x();
            double mouseY = click.y();
            int button = click.button();

            if (button == 0) {
                // Check offhand item box first
                if (shouldShowOffhandItem() && isInsideOffhandItemBox(mouseX, mouseY)) {
                    draggingOffhandItem = true;
                    offhandDragOffsetX = (int) mouseX - JustArmourClient.config.offhandItemX;
                    offhandDragOffsetY = (int) mouseY - JustArmourClient.config.offhandItemY;
                    return;
                }

                // Check held item box
                if (shouldShowHeldItem() && isInsideHeldItemBox(mouseX, mouseY)) {
                    draggingHeldItem = true;
                    heldDragOffsetX = (int) mouseX - JustArmourClient.config.heldItemX;
                    heldDragOffsetY = (int) mouseY - JustArmourClient.config.heldItemY;
                    return;
                }

                // Check armor box
                if (isInsideArmorBox(mouseX, mouseY)) {
                    draggingArmor = true;
                    armorDragOffsetX = (int) mouseX - JustArmourClient.config.hudX;
                    armorDragOffsetY = (int) mouseY - JustArmourClient.config.hudY;
                }
            }
        });

        ScreenMouseEvents.beforeMouseRelease(this).register((screen, click) -> {
            int button = click.button();
            if (button == 0 && (draggingArmor || draggingHeldItem || draggingOffhandItem)) {
                draggingArmor = false;
                draggingHeldItem = false;
                draggingOffhandItem = false;
                JustArmourClient.saveConfig();
            }
        });
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Handle dragging
        if (draggingArmor) {
            JustArmourClient.config.hudX = (int)(mouseX - armorDragOffsetX);
            JustArmourClient.config.hudY = (int)(mouseY - armorDragOffsetY);
        } else if (draggingHeldItem) {
            JustArmourClient.config.heldItemX = (int)(mouseX - heldDragOffsetX);
            JustArmourClient.config.heldItemY = (int)(mouseY - heldDragOffsetY);
        } else if (draggingOffhandItem) {
            JustArmourClient.config.offhandItemX = (int)(mouseX - offhandDragOffsetX);
            JustArmourClient.config.offhandItemY = (int)(mouseY - offhandDragOffsetY);
        }

        // Draw box around armor HUD area
        drawArmorBox(context);

        // Draw box around held item if visible
        if (shouldShowHeldItem()) {
            drawHeldItemBox(context);
        }

        // Draw box around offhand item if visible
        if (shouldShowOffhandItem()) {
            drawOffhandItemBox(context);
        }

        // Render the armor HUD
        JustArmourClient.renderArmorHUD(context, JustArmourClient.config.hudX, JustArmourClient.config.hudY);

        // Render held item HUD
        JustArmourClient.renderHeldItemHUD(context);

        // Render offhand item HUD
        JustArmourClient.renderOffhandItemHUD(context);
    }

    private boolean shouldShowHeldItem() {
        if (!JustArmourClient.config.showHeldItem) return false;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return false;

        ItemStack heldItem = client.player.getMainHandStack();
        if (heldItem.isEmpty()) return false;

        return JustArmourClient.config.showAllHeldItems || heldItem.isDamageable();
    }

    private boolean shouldShowOffhandItem() {
        if (!JustArmourClient.config.showOffhandItem) return false;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return false;

        ItemStack offhandItem = client.player.getOffHandStack();
        if (offhandItem.isEmpty()) return false;

        return JustArmourClient.config.showAllHeldItems || offhandItem.isDamageable();
    }

    private void drawArmorBox(DrawContext context) {
        int hudX = JustArmourClient.config.hudX;
        int hudY = JustArmourClient.config.hudY;
        int spacing = JustArmourClient.config.spacing;
        float scale = JustArmourClient.config.scale;

        // Calculate bounds with scale
        int armorSlots = 4;
        int scaledSpacing = (int)(spacing * scale);
        int topY = hudY - (scaledSpacing * (armorSlots - 1)) - (int)(4 * scale);
        int bottomY = hudY + (int)(20 * scale);

        int leftX, rightX;
        if (JustArmourClient.config.durabilityOnRight) {
            leftX = hudX - (int)(18 * scale);
            rightX = hudX + (int)(64 * scale);
        } else {
            leftX = hudX - (int)(44 * scale);
            rightX = hudX + (int)(30 * scale);
        }

        // Draw semi-transparent background
        int bgColor = 0x80000000;
        context.fill(leftX, topY, rightX, bottomY, bgColor);

        // Draw border
        int borderColor = 0xFF404040;
        context.fill(leftX, topY, rightX, topY + 1, borderColor);
        context.fill(leftX, bottomY - 1, rightX, bottomY, borderColor);
        context.fill(leftX, topY, leftX + 1, bottomY, borderColor);
        context.fill(rightX - 1, topY, rightX, bottomY, borderColor);
    }

    private void drawHeldItemBox(DrawContext context) {
        int x = JustArmourClient.config.heldItemX;
        int y = JustArmourClient.config.heldItemY;
        float scale = JustArmourClient.config.scale;

        int topY = y - (int)(4 * scale);
        int bottomY = y + (int)(20 * scale);

        int leftX, rightX;
        if (JustArmourClient.config.durabilityOnRight) {
            leftX = x - (int)(18 * scale);
            rightX = x + (int)(64 * scale);
        } else {
            leftX = x - (int)(44 * scale);
            rightX = x + (int)(30 * scale);
        }

        // Draw semi-transparent background
        int bgColor = 0x80000000;
        context.fill(leftX, topY, rightX, bottomY, bgColor);

        // Draw border
        int borderColor = 0xFF404040;
        context.fill(leftX, topY, rightX, topY + 1, borderColor);
        context.fill(leftX, bottomY - 1, rightX, bottomY, borderColor);
        context.fill(leftX, topY, leftX + 1, bottomY, borderColor);
        context.fill(rightX - 1, topY, rightX, bottomY, borderColor);
    }

    private void drawOffhandItemBox(DrawContext context) {
        int x = JustArmourClient.config.offhandItemX;
        int y = JustArmourClient.config.offhandItemY;
        float scale = JustArmourClient.config.scale;

        int topY = y - (int)(4 * scale);
        int bottomY = y + (int)(20 * scale);

        int leftX, rightX;
        if (JustArmourClient.config.durabilityOnRight) {
            leftX = x - (int)(18 * scale);
            rightX = x + (int)(64 * scale);
        } else {
            leftX = x - (int)(44 * scale);
            rightX = x + (int)(30 * scale);
        }

        // Draw semi-transparent background
        int bgColor = 0x80000000;
        context.fill(leftX, topY, rightX, bottomY, bgColor);

        // Draw border
        int borderColor = 0xFF404040;
        context.fill(leftX, topY, rightX, topY + 1, borderColor);
        context.fill(leftX, bottomY - 1, rightX, bottomY, borderColor);
        context.fill(leftX, topY, leftX + 1, bottomY, borderColor);
        context.fill(rightX - 1, topY, rightX, bottomY, borderColor);
    }

    private boolean isInsideArmorBox(double mouseX, double mouseY) {
        int hudX = JustArmourClient.config.hudX;
        int hudY = JustArmourClient.config.hudY;
        int spacing = JustArmourClient.config.spacing;
        float scale = JustArmourClient.config.scale;

        int armorSlots = 4;
        int scaledSpacing = (int)(spacing * scale);
        int topY = hudY - (scaledSpacing * (armorSlots - 1)) - (int)(4 * scale);
        int bottomY = hudY + (int)(20 * scale);

        int hitboxXStart, hitboxXEnd;
        if (JustArmourClient.config.durabilityOnRight) {
            hitboxXStart = hudX - (int)(18 * scale);
            hitboxXEnd = hudX + (int)(64 * scale);
        } else {
            hitboxXStart = hudX - (int)(44 * scale);
            hitboxXEnd = hudX + (int)(30 * scale);
        }

        return mouseX >= hitboxXStart && mouseX <= hitboxXEnd &&
                mouseY >= topY && mouseY <= bottomY;
    }

    private boolean isInsideHeldItemBox(double mouseX, double mouseY) {
        int x = JustArmourClient.config.heldItemX;
        int y = JustArmourClient.config.heldItemY;
        float scale = JustArmourClient.config.scale;

        int topY = y - (int)(4 * scale);
        int bottomY = y + (int)(20 * scale);

        int hitboxXStart, hitboxXEnd;
        if (JustArmourClient.config.durabilityOnRight) {
            hitboxXStart = x - (int)(18 * scale);
            hitboxXEnd = x + (int)(64 * scale);
        } else {
            hitboxXStart = x - (int)(44 * scale);
            hitboxXEnd = x + (int)(30 * scale);
        }

        return mouseX >= hitboxXStart && mouseX <= hitboxXEnd &&
                mouseY >= topY && mouseY <= bottomY;
    }

    private boolean isInsideOffhandItemBox(double mouseX, double mouseY) {
        int x = JustArmourClient.config.offhandItemX;
        int y = JustArmourClient.config.offhandItemY;
        float scale = JustArmourClient.config.scale;

        int topY = y - (int)(4 * scale);
        int bottomY = y + (int)(20 * scale);

        int hitboxXStart, hitboxXEnd;
        if (JustArmourClient.config.durabilityOnRight) {
            hitboxXStart = x - (int)(18 * scale);
            hitboxXEnd = x + (int)(64 * scale);
        } else {
            hitboxXStart = x - (int)(44 * scale);
            hitboxXEnd = x + (int)(30 * scale);
        }

        return mouseX >= hitboxXStart && mouseX <= hitboxXEnd &&
                mouseY >= topY && mouseY <= bottomY;
    }

    @Override
    public void close() {
        super.close();
        JustArmourClient.saveConfig();
    }
}