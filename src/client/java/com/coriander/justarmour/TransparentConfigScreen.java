package com.coriander.justarmour;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class TransparentConfigScreen extends Screen {

    private ButtonWidget durabilityBarButton;
    private ButtonWidget heldItemButton;
    private ButtonWidget offhandItemButton;
    private ButtonWidget showAllHeldItemsButton;
    private ButtonWidget durabilityPositionButton;
    private ButtonWidget showMaxDamageButton;
    private ButtonWidget disableColorsButton;
    private ButtonWidget showShadowButton;
    private ButtonWidget hideDurabilityButton;
    private ButtonWidget hudEditorButton;
    private SpacingSlider spacingSlider;
    private ScaleSlider scaleSlider;

    protected TransparentConfigScreen() {
        super(Text.literal("JustArmour Config"));
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
        this.durabilityBarButton = ButtonWidget.builder(
                Text.literal("Durability Bar: " + (JustArmourClient.config.showDurabilityBar ? "ON" : "OFF")),
                button -> {
                    JustArmourClient.config.showDurabilityBar = !JustArmourClient.config.showDurabilityBar;
                    button.setMessage(Text.literal("Durability Bar: " + (JustArmourClient.config.showDurabilityBar ? "ON" : "OFF")));
                    JustArmourClient.saveConfig();
                }
        ).dimensions(leftColumnX, leftY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(this.durabilityBarButton);
        leftY += spacing;

        // Held Item toggle
        this.heldItemButton = ButtonWidget.builder(
                Text.literal("Show Held Item: " + (JustArmourClient.config.showHeldItem ? "ON" : "OFF")),
                button -> {
                    JustArmourClient.config.showHeldItem = !JustArmourClient.config.showHeldItem;
                    button.setMessage(Text.literal("Show Held Item: " + (JustArmourClient.config.showHeldItem ? "ON" : "OFF")));
                    JustArmourClient.saveConfig();
                }
        ).dimensions(leftColumnX, leftY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(this.heldItemButton);
        leftY += spacing;

        // Offhand Item toggle
        this.offhandItemButton = ButtonWidget.builder(
                Text.literal("Show Offhand: " + (JustArmourClient.config.showOffhandItem ? "ON" : "OFF")),
                button -> {
                    JustArmourClient.config.showOffhandItem = !JustArmourClient.config.showOffhandItem;
                    button.setMessage(Text.literal("Show Offhand: " + (JustArmourClient.config.showOffhandItem ? "ON" : "OFF")));
                    JustArmourClient.saveConfig();
                }
        ).dimensions(leftColumnX, leftY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(this.offhandItemButton);
        leftY += spacing;

        // Show All Held Items toggle
        this.showAllHeldItemsButton = ButtonWidget.builder(
                Text.literal("Show All Items: " + (JustArmourClient.config.showAllHeldItems ? "ON" : "OFF")),
                button -> {
                    JustArmourClient.config.showAllHeldItems = !JustArmourClient.config.showAllHeldItems;
                    button.setMessage(Text.literal("Show All Items: " + (JustArmourClient.config.showAllHeldItems ? "ON" : "OFF")));
                    JustArmourClient.saveConfig();
                }
        ).dimensions(leftColumnX, leftY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(this.showAllHeldItemsButton);
        leftY += spacing;

        // Durability Position toggle
        this.durabilityPositionButton = ButtonWidget.builder(
                Text.literal("Durability: " + (JustArmourClient.config.durabilityOnRight ? "RIGHT" : "LEFT")),
                button -> {
                    JustArmourClient.config.durabilityOnRight = !JustArmourClient.config.durabilityOnRight;
                    button.setMessage(Text.literal("Durability: " + (JustArmourClient.config.durabilityOnRight ? "RIGHT" : "LEFT")));
                    JustArmourClient.saveConfig();
                }
        ).dimensions(leftColumnX, leftY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(this.durabilityPositionButton);

        // RIGHT COLUMN

        // Show Max Damage toggle
        this.showMaxDamageButton = ButtonWidget.builder(
                Text.literal("Max Damage: " + (JustArmourClient.config.showMaxDamage ? "ON" : "OFF")),
                button -> {
                    JustArmourClient.config.showMaxDamage = !JustArmourClient.config.showMaxDamage;
                    button.setMessage(Text.literal("Max Damage: " + (JustArmourClient.config.showMaxDamage ? "ON" : "OFF")));
                    JustArmourClient.saveConfig();
                }
        ).dimensions(rightColumnX, rightY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(this.showMaxDamageButton);
        rightY += spacing;

        // Disable Colors toggle
        this.disableColorsButton = ButtonWidget.builder(
                Text.literal("Colors: " + (JustArmourClient.config.disableColors ? "OFF" : "ON")),
                button -> {
                    JustArmourClient.config.disableColors = !JustArmourClient.config.disableColors;
                    button.setMessage(Text.literal("Colors: " + (JustArmourClient.config.disableColors ? "OFF" : "ON")));
                    JustArmourClient.saveConfig();
                }
        ).dimensions(rightColumnX, rightY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(this.disableColorsButton);
        rightY += spacing;

        // Shadow toggle
        this.showShadowButton = ButtonWidget.builder(
                Text.literal("Shadow: " + (JustArmourClient.config.showShadow ? "ON" : "OFF")),
                button -> {
                    JustArmourClient.config.showShadow = !JustArmourClient.config.showShadow;
                    button.setMessage(Text.literal("Shadow: " + (JustArmourClient.config.showShadow ? "ON" : "OFF")));
                    JustArmourClient.saveConfig();
                }
        ).dimensions(rightColumnX, rightY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(this.showShadowButton);
        rightY += spacing;

        // Hide Durability Numbers toggle
        this.hideDurabilityButton = ButtonWidget.builder(
                Text.literal("Dura Numbers: " + (JustArmourClient.config.hideDurabilityNumbers ? "OFF" : "ON")),
                button -> {
                    JustArmourClient.config.hideDurabilityNumbers = !JustArmourClient.config.hideDurabilityNumbers;
                    button.setMessage(Text.literal("Dura Numbers: " + (JustArmourClient.config.hideDurabilityNumbers ? "OFF" : "ON")));
                    JustArmourClient.saveConfig();
                }
        ).dimensions(rightColumnX, rightY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(this.hideDurabilityButton);

        // Sliders centered below buttons
        int sliderWidth = 200;
        int sliderX = this.width / 2 - sliderWidth / 2;
        int sliderY = Math.max(leftY, rightY) + spacing + 10;

        // Spacing Slider
        this.spacingSlider = new SpacingSlider(sliderX, sliderY, sliderWidth, buttonHeight);
        this.addDrawableChild(this.spacingSlider);
        sliderY += spacing;

        // Scale Slider
        this.scaleSlider = new ScaleSlider(sliderX, sliderY, sliderWidth, buttonHeight);
        this.addDrawableChild(this.scaleSlider);

        // HUD Editor button in top right corner
        int editorButtonWidth = 120;
        int editorButtonX = this.width - editorButtonWidth - 10;
        int editorButtonY = 10;
        this.hudEditorButton = ButtonWidget.builder(
                Text.literal("HUD Editor"),
                button -> {
                    if (this.client != null) {
                        this.client.setScreen(new HudEditorScreen());
                    }
                }
        ).dimensions(editorButtonX, editorButtonY, editorButtonWidth, buttonHeight).build();
        this.addDrawableChild(this.hudEditorButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Transparent dark background
        context.fillGradient(0, 0, this.width, this.height, 0x60000000, 0x60000000);

        // Render the HUD in the background so user can see changes live
        JustArmourClient.renderArmorHUD(context, JustArmourClient.config.hudX, JustArmourClient.config.hudY);
        JustArmourClient.renderHeldItemHUD(context);
        JustArmourClient.renderOffhandItemHUD(context);

        super.render(context, mouseX, mouseY, delta);

        // Title
        context.drawCenteredTextWithShadow(this.textRenderer, "JustArmour Settings", this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, "Press ESC to close", this.width / 2, this.height - 10, 0xAAAAAA);
    }

    @Override
    public void close() {
        super.close();
        JustArmourClient.saveConfig();
    }

    // Spacing slider (14-20)
    private static class SpacingSlider extends SliderWidget {
        private static final int MIN_SPACING = 14;
        private static final int MAX_SPACING = 20;

        public SpacingSlider(int x, int y, int width, int height) {
            super(x, y, width, height, Text.literal("Spacing: " + JustArmourClient.config.spacing),
                    (double)(JustArmourClient.config.spacing - MIN_SPACING) / (MAX_SPACING - MIN_SPACING));
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            int currentSpacing = MIN_SPACING + (int)(this.value * (MAX_SPACING - MIN_SPACING));
            this.setMessage(Text.literal("Spacing: " + currentSpacing));
        }

        @Override
        protected void applyValue() {
            int newSpacing = MIN_SPACING + (int)(this.value * (MAX_SPACING - MIN_SPACING));
            JustArmourClient.config.spacing = newSpacing;
            JustArmourClient.saveConfig();
        }
    }

    // Scale/Thickness slider
    private static class ScaleSlider extends SliderWidget {
        private static final float MIN_SCALE = 0.5f;
        private static final float MAX_SCALE = 2.0f;

        public ScaleSlider(int x, int y, int width, int height) {
            super(x, y, width, height, Text.literal("Scale: " + String.format("%.1f", JustArmourClient.config.scale)),
                    (double)(JustArmourClient.config.scale - MIN_SCALE) / (MAX_SCALE - MIN_SCALE));
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            float currentScale = MIN_SCALE + (float)(this.value * (MAX_SCALE - MIN_SCALE));
            this.setMessage(Text.literal("Scale: " + String.format("%.1f", currentScale) + "x"));
        }

        @Override
        protected void applyValue() {
            float newScale = MIN_SCALE + (float)(this.value * (MAX_SCALE - MIN_SCALE));
            JustArmourClient.config.scale = newScale;
            JustArmourClient.saveConfig();
        }
    }
}