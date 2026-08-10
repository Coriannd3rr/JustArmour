package com.coriander.justarmour;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public class TransparentConfigScreen extends Screen {

    private Button durabilityBarButton;
    private Button heldItemButton;
    private Button offhandItemButton;
    private Button showAllHeldItemsButton;
    private Button durabilityPositionButton;
    private Button showMaxDamageButton;
    private Button disableColorsButton;
    private Button showShadowButton;
    private Button hideDurabilityButton;
    private Button hudEditorButton;
    private SpacingSlider spacingSlider;
    private ScaleSlider scaleSlider;

    protected TransparentConfigScreen() {
        super(Component.literal("JustArmour Config"));
    }

    @Override
    protected void init() {
        super.init();

        int buttonWidth = 150;
        int buttonHeight = 20;
        int spacing = 24;

        // Two columns layout
        int leftColumnX = this.width / 2 - buttonWidth - 10;
        int rightColumnX = this.width / 2 + 10;
        int startY = 40;

        int leftY = startY;
        int rightY = startY;

        // LEFT COLUMN

        // Durability Bar toggle
        this.durabilityBarButton = Button.builder(
                Component.literal("Durability Bar: " + (JustArmourClient.config.showDurabilityBar ? "ON" : "OFF")),
                button -> {
                    JustArmourClient.config.showDurabilityBar = !JustArmourClient.config.showDurabilityBar;
                    button.setMessage(Component.literal("Durability Bar: " + (JustArmourClient.config.showDurabilityBar ? "ON" : "OFF")));
                    JustArmourClient.saveConfig();
                }
        ).bounds(leftColumnX, leftY, buttonWidth, buttonHeight).build();
        this.addRenderableWidget(this.durabilityBarButton);
        leftY += spacing;

        // Held Item toggle
        this.heldItemButton = Button.builder(
                Component.literal("Show Held Item: " + (JustArmourClient.config.showHeldItem ? "ON" : "OFF")),
                button -> {
                    JustArmourClient.config.showHeldItem = !JustArmourClient.config.showHeldItem;
                    button.setMessage(Component.literal("Show Held Item: " + (JustArmourClient.config.showHeldItem ? "ON" : "OFF")));
                    JustArmourClient.saveConfig();
                }
        ).bounds(leftColumnX, leftY, buttonWidth, buttonHeight).build();
        this.addRenderableWidget(this.heldItemButton);
        leftY += spacing;

        // Offhand Item toggle
        this.offhandItemButton = Button.builder(
                Component.literal("Show Offhand: " + (JustArmourClient.config.showOffhandItem ? "ON" : "OFF")),
                button -> {
                    JustArmourClient.config.showOffhandItem = !JustArmourClient.config.showOffhandItem;
                    button.setMessage(Component.literal("Show Offhand: " + (JustArmourClient.config.showOffhandItem ? "ON" : "OFF")));
                    JustArmourClient.saveConfig();
                }
        ).bounds(leftColumnX, leftY, buttonWidth, buttonHeight).build();
        this.addRenderableWidget(this.offhandItemButton);
        leftY += spacing;

        // Show All Held Items toggle
        this.showAllHeldItemsButton = Button.builder(
                Component.literal("Show All Items: " + (JustArmourClient.config.showAllHeldItems ? "ON" : "OFF")),
                button -> {
                    JustArmourClient.config.showAllHeldItems = !JustArmourClient.config.showAllHeldItems;
                    button.setMessage(Component.literal("Show All Items: " + (JustArmourClient.config.showAllHeldItems ? "ON" : "OFF")));
                    JustArmourClient.saveConfig();
                }
        ).bounds(leftColumnX, leftY, buttonWidth, buttonHeight).build();
        this.addRenderableWidget(this.showAllHeldItemsButton);
        leftY += spacing;

        // Durability Position toggle
        this.durabilityPositionButton = Button.builder(
                Component.literal("Durability: " + (JustArmourClient.config.durabilityOnRight ? "RIGHT" : "LEFT")),
                button -> {
                    JustArmourClient.config.durabilityOnRight = !JustArmourClient.config.durabilityOnRight;
                    button.setMessage(Component.literal("Durability: " + (JustArmourClient.config.durabilityOnRight ? "RIGHT" : "LEFT")));
                    JustArmourClient.saveConfig();
                }
        ).bounds(leftColumnX, leftY, buttonWidth, buttonHeight).build();
        this.addRenderableWidget(this.durabilityPositionButton);

        // RIGHT COLUMN

        // Show Max Damage toggle
        this.showMaxDamageButton = Button.builder(
                Component.literal("Max Damage: " + (JustArmourClient.config.showMaxDamage ? "ON" : "OFF")),
                button -> {
                    JustArmourClient.config.showMaxDamage = !JustArmourClient.config.showMaxDamage;
                    button.setMessage(Component.literal("Max Damage: " + (JustArmourClient.config.showMaxDamage ? "ON" : "OFF")));
                    JustArmourClient.saveConfig();
                }
        ).bounds(rightColumnX, rightY, buttonWidth, buttonHeight).build();
        this.addRenderableWidget(this.showMaxDamageButton);
        rightY += spacing;

        // Disable Colors toggle
        this.disableColorsButton = Button.builder(
                Component.literal("Colors: " + (JustArmourClient.config.disableColors ? "OFF" : "ON")),
                button -> {
                    JustArmourClient.config.disableColors = !JustArmourClient.config.disableColors;
                    button.setMessage(Component.literal("Colors: " + (JustArmourClient.config.disableColors ? "OFF" : "ON")));
                    JustArmourClient.saveConfig();
                }
        ).bounds(rightColumnX, rightY, buttonWidth, buttonHeight).build();
        this.addRenderableWidget(this.disableColorsButton);
        rightY += spacing;

        // Shadow toggle
        this.showShadowButton = Button.builder(
                Component.literal("Shadow: " + (JustArmourClient.config.showShadow ? "ON" : "OFF")),
                button -> {
                    JustArmourClient.config.showShadow = !JustArmourClient.config.showShadow;
                    button.setMessage(Component.literal("Shadow: " + (JustArmourClient.config.showShadow ? "ON" : "OFF")));
                    JustArmourClient.saveConfig();
                }
        ).bounds(rightColumnX, rightY, buttonWidth, buttonHeight).build();
        this.addRenderableWidget(this.showShadowButton);
        rightY += spacing;

        // Hide Durability Numbers toggle
        this.hideDurabilityButton = Button.builder(
                Component.literal("Dura Numbers: " + (JustArmourClient.config.hideDurabilityNumbers ? "OFF" : "ON")),
                button -> {
                    JustArmourClient.config.hideDurabilityNumbers = !JustArmourClient.config.hideDurabilityNumbers;
                    button.setMessage(Component.literal("Dura Numbers: " + (JustArmourClient.config.hideDurabilityNumbers ? "OFF" : "ON")));
                    JustArmourClient.saveConfig();
                }
        ).bounds(rightColumnX, rightY, buttonWidth, buttonHeight).build();
        this.addRenderableWidget(this.hideDurabilityButton);

        // Sliders centered below buttons
        int sliderWidth = 200;
        int sliderX = this.width / 2 - sliderWidth / 2;
        int sliderY = Math.max(leftY, rightY) + spacing + 10;

        // Spacing Slider
        this.spacingSlider = new SpacingSlider(sliderX, sliderY, sliderWidth, buttonHeight);
        this.addRenderableWidget(this.spacingSlider);
        sliderY += spacing;

        // Scale Slider
        this.scaleSlider = new ScaleSlider(sliderX, sliderY, sliderWidth, buttonHeight);
        this.addRenderableWidget(this.scaleSlider);

        // HUD Editor button in top right corner
        int editorButtonWidth = 120;
        int editorButtonX = this.width - editorButtonWidth - 10;
        int editorButtonY = 10;
        this.hudEditorButton = Button.builder(
                Component.literal("HUD Editor"),
                button -> {
                    if (this.minecraft != null) {
                        this.minecraft.gui.setScreen(new HudEditorScreen());
                    }
                }
        ).bounds(editorButtonX, editorButtonY, editorButtonWidth, buttonHeight).build();
        this.addRenderableWidget(this.hudEditorButton);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        // Transparent dark background
        graphics.fillGradient(0, 0, this.width, this.height, 0x60000000, 0x60000000);

        // Render the HUD in the background so user can see changes live
        JustArmourClient.renderArmorHUD(graphics, JustArmourClient.config.hudX, JustArmourClient.config.hudY);
        JustArmourClient.renderHeldItemHUD(graphics);
        JustArmourClient.renderOffhandItemHUD(graphics);

        super.extractRenderState(graphics, mouseX, mouseY, delta);

        // Title
        graphics.centeredText(this.font, "JustArmour Settings", this.width / 2, 20, 0xFFFFFFFF);
        graphics.centeredText(this.font, "Press ESC to close", this.width / 2, this.height - 10, 0xFFAAAAAA);
    }

    @Override
    public void onClose() {
        super.onClose();
        JustArmourClient.saveConfig();
    }

    // Spacing slider (14-20)
    private static class SpacingSlider extends AbstractSliderButton {
        private static final int MIN_SPACING = 14;
        private static final int MAX_SPACING = 20;

        public SpacingSlider(int x, int y, int width, int height) {
            super(x, y, width, height, Component.literal("Spacing: " + JustArmourClient.config.spacing),
                    (double)(JustArmourClient.config.spacing - MIN_SPACING) / (MAX_SPACING - MIN_SPACING));
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            int currentSpacing = MIN_SPACING + (int)(this.value * (MAX_SPACING - MIN_SPACING));
            this.setMessage(Component.literal("Spacing: " + currentSpacing));
        }

        @Override
        protected void applyValue() {
            int newSpacing = MIN_SPACING + (int)(this.value * (MAX_SPACING - MIN_SPACING));
            JustArmourClient.config.spacing = newSpacing;
            JustArmourClient.saveConfig();
        }
    }

    // Scale/Thickness slider
    private static class ScaleSlider extends AbstractSliderButton {
        private static final float MIN_SCALE = 0.5f;
        private static final float MAX_SCALE = 2.0f;

        public ScaleSlider(int x, int y, int width, int height) {
            super(x, y, width, height, Component.literal("Scale: " + String.format("%.1f", JustArmourClient.config.scale)),
                    (double)(JustArmourClient.config.scale - MIN_SCALE) / (MAX_SCALE - MIN_SCALE));
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            float currentScale = MIN_SCALE + (float)(this.value * (MAX_SCALE - MIN_SCALE));
            this.setMessage(Component.literal("Scale: " + String.format("%.1f", currentScale) + "x"));
        }

        @Override
        protected void applyValue() {
            float newScale = MIN_SCALE + (float)(this.value * (MAX_SCALE - MIN_SCALE));
            JustArmourClient.config.scale = newScale;
            JustArmourClient.saveConfig();
        }
    }
}