package com.grobe.techrebirth;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<String> PROGRESS_BAR_HIGHLIGHT_COLOR;
    public static final ModConfigSpec.ConfigValue<Boolean> PROGRESS_BAR_HIGHLIGHT_ENABLED;


    public static final ModConfigSpec.ConfigValue<Boolean> AUTO_SMELT_ENABLED;
    public static final ModConfigSpec.ConfigValue<Boolean> AUTO_SMELT_APPLY_FORTUNE;


    static{
        BUILDER.push("UI Settings");

        PROGRESS_BAR_HIGHLIGHT_ENABLED = BUILDER
                .comment("Enable progress bar highlight on hover")
                .define("progressBarHighlightEnabled", true);
        PROGRESS_BAR_HIGHLIGHT_COLOR = BUILDER
                .comment("Progress bar highlight color in hex format (e.g. #FFD700 for gold)"
                , "Popular colors: #FFD700 (Gold), #00FF00 (Green), #FF4500 (Orange Red),"
                ,"#1E90FF (Dodger Blue), #FF69B4 (Hot Pink), #32CD32 (Lime Green)")
                .define("progressBarHighlightColor", "#FFD7000");

        BUILDER.pop();
        //SPEC = BUILDER.build();

        BUILDER.push("Gameplay Settings");

        AUTO_SMELT_ENABLED = BUILDER
                .comment("Enable automatic smelting in mod on some tools")
                .define("autoSmeltEnabled", true);
        AUTO_SMELT_APPLY_FORTUNE = BUILDER
                .comment("Should fortune work with ore drops and auto smelt if enabled")
                        .define("autoSmeltFortune", false);

        BUILDER.pop();
        SPEC = BUILDER.build();

    }
    public static int getHighlightColor(){
        try {
            String colorHex = PROGRESS_BAR_HIGHLIGHT_COLOR.get().replace("#", "");
            // ✅ DODAJ OVO: Osiguraj da je boja u full ARGB formatu
            if (colorHex.length() == 6) {
                colorHex = "FF" + colorHex; // Dodaj alpha channel (255 = fully opaque)
            }
            else if(colorHex.length() == 8){

            }else{
                System.out.println("Invalid color format: " + colorHex + " - using fallback");
                return 0xFFFFD700; // fallback
            }
            long colorLong = Long.parseLong(colorHex, 16);
            return (int) colorLong;
        } catch (NumberFormatException e) {
            System.out.println("Invalid color format in config: " + PROGRESS_BAR_HIGHLIGHT_COLOR.get());
            return 0xFFFFD700; // fallback to gold
        }
    }
    public static boolean isHighlightEnabled(){
        return PROGRESS_BAR_HIGHLIGHT_ENABLED.get();
    }


    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
