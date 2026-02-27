package com.coriander.justarmour;

public class JustArmourConfigData {
    public boolean hudEnabled = true;
    public boolean durabilityOnRight = true;

    // Durability bar toggle
    public boolean showDurabilityBar = true;

    // Show held item durability
    public boolean showHeldItem = true;

    // Show offhand item
    public boolean showOffhandItem = false;

    // Show non-damageable held items (like golden apple)
    public boolean showAllHeldItems = true;

    // Show max damage format (407/407)
    public boolean showMaxDamage = false;

    // Disable colors (white only)
    public boolean disableColors = false;

    // Shadow toggle
    public boolean showShadow = true;

    // Hide durability numbers (armor only)
    public boolean hideDurabilityNumbers = false;

    // Scale/spacing
    public int spacing = 18;

    // Scale/thickness (0.5 to 2.0)
    public float scale = 1.0f;

    // HUD position (default bottom right - calculated on first launch)
    public int hudX = -1; // -1 means not set yet
    public int hudY = -1;

    // Held item position (separate from armor)
    public int heldItemX = -1;
    public int heldItemY = -1;

    // Offhand item position (separate)
    public int offhandItemX = -1;
    public int offhandItemY = -1;
}