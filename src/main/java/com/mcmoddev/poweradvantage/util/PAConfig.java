package com.mcmoddev.poweradvantage.util;

import java.io.File;
import java.util.Locale;

import com.mcmoddev.lib.util.Config;
import com.mcmoddev.poweradvantage.PowerAdvantage;
import com.mcmoddev.poweradvantage.RecipeMode;
import com.mcmoddev.poweradvantage.api.ConduitType;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PAConfig extends Config {

	private static Configuration configuration;
	private static final String CONFIG_FILE = "config/PowerAdvantage.cfg";
	private static final String GENERAL_CAT = "General";
	private static String mode;
	private static final String[] presets = {"NORMAL", "TECH_PROGRESSION", "APOCALYPTIC"};
	
	@SubscribeEvent
	public void onConfigChange(final ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(PowerAdvantage.MODID)) {
			init();
		}
	}

	public static class Options extends com.mcmoddev.lib.util.Config.Options {
		private static RecipeMode recipeMode;
		private static float chestLootFactor;
		private static boolean plasticIsRubber;
		private static boolean useOtherFluids;
		
		public static RecipeMode getRecipeMode() {
			return recipeMode;
		}
		
		public static void setRecipeModeFromString(String mode) {
			switch (mode){
			case "NORMAL":
				setRecipeMode(RecipeMode.NORMAL);
				break;

			case "APOCALYPTIC":
				setRecipeMode(RecipeMode.APOCALYPTIC);
				break;

			case "TECH_PROGRESSION":
				setRecipeMode(RecipeMode.TECH_PROGRESSION);
				break;

			default:
				FMLLog.severe(PowerAdvantage.MODID+" does not recognize recipe_mode '"+mode+"'");
				throw new IllegalArgumentException("'"+mode+"' is not valid for config option 'recipe_mode'. Valid options are: NORMAL, APOCALYPTIC, or TECH_PROGRESSION");
			}
		}
		
		public static void setRecipeMode(RecipeMode mode) {
			recipeMode = mode;
		}
		
		public static float getChestLootFactor() {
			return chestLootFactor;
		}
		
		public static void setChestLootFactor(float factor) {
			chestLootFactor = factor;
		}
		
		public static boolean isPlasticAlsoRubber() {
			return plasticIsRubber;
		}
		
		public static void setPlasticIsRubber(boolean isIt) {
			plasticIsRubber = isIt;
		}
		
		public static boolean getUseOtherFluids() {
			return useOtherFluids;
		}
		
		public static void setUseOtherFluids(boolean truth) {
			useOtherFluids = truth;
		}
	}
	
	public static void init() {
		if (configuration == null) {
			configuration = new Configuration(new File(CONFIG_FILE));
			MinecraftForge.EVENT_BUS.register(new PAConfig());
		}
		
		Options.setRecipeModeFromString(configuration.getString("recipe_mode", "options", "NORMAL", "NORMAL, APOCALYPTIC, or TECH_PROGRESSION. \n"
				+ "Sets the style of recipes used in your game. \n"
				+ "In NORMAL mode, everything needed is craftable from vanilla items and the machines are \n"
				+ "available pretty much as soon as the player returns from their first mining expedition. \n"
				+ "In APOCALYPTIC mode, some important items are not craftable, but can be found in \n"
				+ "treasure chests, requiring the players to pillage for their machines. \n"
				+ "In TECH_PROGRESSION mode, important items are very complicated to craft using vanilla \n"
				+ "items, but are easy to duplicate once they are made. This gives the players a sense of \n"
				+ "invention and rising throught the ages from stone-age to space-age.",presets).toUpperCase(Locale.US).trim());
		Options.setChestLootFactor(configuration.getFloat("treasure_chest_loot_factor", "options", 0.5f, 0.0f, 1000.0f, 
				"Controls the rarity of items from this mod being found in treasure chests relative to \n"
						+  "the frequency of other chest loot items. Set to 0 to disable metal ingots from \n"
						+  "appearing in treasure chests."));
		
		Options.setPlasticIsRubber(configuration.getBoolean("plastic_equals_rubber", "options", true, 
				"If true, then plastic will be useable in recipes as if it were rubber (for cross-mod compatibility)"));

		Options.setUseOtherFluids(configuration.getBoolean("use_other_fluids", "Other Power Mods", true, 
				"If true, then Power Advantage will use existing fluids added by other mods where possible"));
		configuration.save();
	}
}
