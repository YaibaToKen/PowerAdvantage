package com.mcmoddev.poweradvantage.init;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Triple;

import java.util.Arrays;

import com.mcmoddev.lib.events.MMDLibRegisterBlocks;
import com.mcmoddev.lib.init.Materials;
import com.mcmoddev.poweradvantage.PowerAdvantage;
import com.mcmoddev.poweradvantage.api.ConduitType;
import com.mcmoddev.poweradvantage.api.GUIBlock;
import com.mcmoddev.poweradvantage.api.fluid.InteractiveFluidBlock;
import com.mcmoddev.poweradvantage.blocks.BlockFrame;
import com.mcmoddev.poweradvantage.blocks.BlockPowerSwitch;
import com.mcmoddev.poweradvantage.machines.conveyors.BlockConveyor;
import com.mcmoddev.poweradvantage.machines.conveyors.BlockConveyorFilter;
import com.mcmoddev.poweradvantage.machines.conveyors.TileEntityBlockFilter;
import com.mcmoddev.poweradvantage.machines.conveyors.TileEntityFoodFilter;
import com.mcmoddev.poweradvantage.machines.conveyors.TileEntityFuelFilter;
import com.mcmoddev.poweradvantage.machines.conveyors.TileEntityInventoryFilter;
import com.mcmoddev.poweradvantage.machines.conveyors.TileEntityOreFilter;
import com.mcmoddev.poweradvantage.machines.conveyors.TileEntityOverflowFilter;
import com.mcmoddev.poweradvantage.machines.conveyors.TileEntityPlantFilter;
import com.mcmoddev.poweradvantage.machines.conveyors.TileEntitySmeltableFilter;
import com.mcmoddev.poweradvantage.machines.creative.InfiniteEnergyBlock;
import com.mcmoddev.poweradvantage.machines.fluidmachines.FluidDischargeBlock;
import com.mcmoddev.poweradvantage.machines.fluidmachines.FluidDrainBlock;
import com.mcmoddev.poweradvantage.machines.fluidmachines.FluidPipeBlock;
import com.mcmoddev.poweradvantage.machines.fluidmachines.MetalTankBlock;
import com.mcmoddev.poweradvantage.machines.fluidmachines.StillBlock;
import com.mcmoddev.poweradvantage.machines.fluidmachines.StorageTankBlock;
import com.mcmoddev.poweradvantage.machines.fluidmachines.modsupport.TerminalFluidPipeBlock;
import com.mojang.realmsclient.util.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Blocks extends com.mcmoddev.lib.init.Blocks {
	@SubscribeEvent
	public static void blockRegistrationEvent(final MMDLibRegisterBlocks event) {
		List<GUIBlock> mySpecialGuiBlocks = Arrays.asList(Pair.of(FluidDrainBlock.class, "fluid_drain"), Pair.of(FluidDischargeBlock.class, "fluid_discharge"), Pair.of(StorageTankBlock.class, "fluid_storage_tank"), Pair.of(MetalTankBlock.class, "metal_fluid_tank"), Pair.of(StillBlock.class, "still"))
				.stream().map( p -> {
					Class<? extends Block> clazz = p.first();
					GUIBlock rv = null;
					Constructor<?> ctor = null;
					try {
						ctor = clazz.getConstructor();
						rv = (GUIBlock)ctor.newInstance();
					} catch( NoSuchMethodException | SecurityException ex) {
						FMLLog.severe("Exception trying to get block class constructor: %s", ex);
					} catch( IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException | ExceptionInInitializerError ex) {
						FMLLog.severe("Exception trying to instantiate block: %s", ex);
					}
					if (rv != null) {
						rv.setRegistryName(PowerAdvantage.MODID, p.second());
					}
					return rv;
				})
				.filter( b -> b != null)
				.collect(Collectors.toList());

		List<GUIBlock> jumbleOBlocks = Arrays.asList( Triple.of(BlockConveyor.class, "item_conveyor", null), 
				Triple.of(BlockConveyorFilter.class, "item_filter_block", TileEntityBlockFilter.class),
				Triple.of(BlockConveyorFilter.class, "item_filter_food", TileEntityFoodFilter.class),
				Triple.of(BlockConveyorFilter.class, "item_filter_fuel", TileEntityFuelFilter.class),
				Triple.of(BlockConveyorFilter.class, "item_filter_inventory", TileEntityInventoryFilter.class),
				Triple.of(BlockConveyorFilter.class, "item_filter_ore", TileEntityOreFilter.class),
				Triple.of(BlockConveyorFilter.class, "item_filter_plant", TileEntityPlantFilter.class),
				Triple.of(BlockConveyorFilter.class, "item_filter_smeltable", TileEntitySmeltableFilter.class),
				Triple.of(BlockConveyorFilter.class, "item_filter_overflow", TileEntityOverflowFilter.class))
				.stream().map( trip -> {
					Class<?> teClazz = (Class<?>)trip.getRight();
					Class<? extends Block> clazz = trip.getLeft();
					String name = trip.getMiddle();
					GUIBlock rv = null;
					Constructor<?> ctor = null;
					try {
						if (teClazz != null) {
							ctor = clazz.getConstructor(Material.class, java.lang.Float.TYPE, teClazz);
							rv = (GUIBlock) ctor.newInstance(Material.PISTON, 0.75f, teClazz);
						} else {
							ctor = clazz.getConstructor(Material.class, java.lang.Float.TYPE);
							rv = (GUIBlock) ctor.newInstance(Material.PISTON, 0.75f);
						}
					} catch( NoSuchMethodException | SecurityException ex) {
						FMLLog.severe("Exception trying to get block class constructor: %s", ex);
					} catch( IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException | ExceptionInInitializerError ex) {
						FMLLog.severe("Exception trying to instantiate block: %s", ex);
					}
					if (rv != null) {
						rv.setRegistryName(PowerAdvantage.MODID, name);
					}
					return rv;
				})
				.filter( b -> b != null)
				.collect(Collectors.toList());
		
		List<Block> basicBlocks = new LinkedList<>();
		basicBlocks.add(new FluidPipeBlock().setRegistryName("fluid_pipe"));
		basicBlocks.add(new TerminalFluidPipeBlock().setRegistryName("fluid_pipe_terminal").setTranslationKey("fluid_pipe").setCreativeTab(null));
		basicBlocks.add(new BlockPowerSwitch(Fluids.fluidConduit_general).setRegistryName("fluid_switch"));
		basicBlocks.add(new BlockFrame(Material.PISTON).setResistance(Materials.getMaterialByName("steel").getBlastResistance()).setHardness(0.75f).setRegistryName("steel_frame"));
		List<GUIBlock> infinites = Arrays.asList("steam", "electricity", "quantum")
				.stream().map( name -> (GUIBlock) new InfiniteEnergyBlock(new ConduitType(name)).setRegistryName("infinite_"+name))
				.collect(Collectors.toList());
		
		mySpecialGuiBlocks.stream().forEachOrdered( bl -> addBlock(bl, bl.getRegistryName().getPath(), Materials.DEFAULT, bl.getCreativeTab()));
		jumbleOBlocks.stream().forEachOrdered( bl -> addBlock(bl, bl.getRegistryName().getPath(), Materials.DEFAULT, bl.getCreativeTab()));
		basicBlocks.stream().forEachOrdered( bl -> addBlock(bl, bl.getRegistryName().getPath(), Materials.DEFAULT, bl.getCreativeTab()));
		infinites.stream().forEachOrdered( bl -> addBlock(bl, bl.getRegistryName().getPath(), Materials.DEFAULT, bl.getCreativeTab()));
	}

	/*

	public static BlockFluidBase crude_oil_block;
	public static BlockFluidBase refined_oil_block;

		OreDictionary.registerOre("pipe", fluid_pipe);
		OreDictionary.registerOre("frameSteel", steel_frame);

		refined_oil_block = (BlockFluidBase) addBlock(new InteractiveFluidBlock(Fluids.refined_oil, true, (World w, EntityLivingBase e) -> {
			e.addPotionEffect(new PotionEffect(Potion.REGISTRY.getObject(new ResourceLocation("nausea")), 200));
		}), "refined_oil");

		crude_oil_block = (BlockFluidBase) addBlock(new InteractiveFluidBlock(Fluids.crude_oil, true, (World w, EntityLivingBase e) -> {
			e.addPotionEffect(new PotionEffect(Potion.REGISTRY.getObject(new ResourceLocation("slowness")), 200, 2));
		}), "crude_oil");
	 */
}