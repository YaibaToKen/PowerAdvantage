package com.mcmoddev.poweradvantage.api.simple;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import com.mcmoddev.poweradvantage.api.ConduitBlock;
import com.mcmoddev.poweradvantage.api.ConduitType;
import com.mcmoddev.poweradvantage.api.PowerConnectorContext;
import com.mcmoddev.poweradvantage.util.PowerHelper;
/**
 * <p>
 * This block class implements the com.mcmoddev.poweradvantage.api.ConduitBlock 
 * class and renders as a pipe. You will need to have the appropriate blockstate 
 * and block model json files for it to render as pipes.
 * </p><p>
 * Extend this class to create your own power conduits with minimal effort.
 * </p>
 * @author DrCyano
 *
 */
public abstract class BlockSimplePowerConduit extends ConduitBlock {

	/**
	 * power type identifier
	 */
	private final ConduitType[] types;
	/**
	 * radius of the pipe model, in meters (0.0625 per pixel)
	 */
	private final float pipeRadius; // in fraction of a block (aka meters)
	/**
	 * Blockstate property
	 */
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	/**
	 * Blockstate property
	 */
	public static final PropertyBool NORTH = PropertyBool.create("north");
	/**
	 * Blockstate property
	 */
	public static final PropertyBool EAST = PropertyBool.create("east");
	/**
	 * Blockstate property
	 */
	public static final PropertyBool WEST = PropertyBool.create("west");
	/**
	 * Blockstate property
	 */
	public static final PropertyBool UP = PropertyBool.create("up");
	/**
	 * Blockstate property
	 */
	public static final PropertyBool DOWN = PropertyBool.create("down");

	/**
	 * Standard constructor. Must be called in the first line of code in the
	 * constructor of extending classes.
	 *
	 * @param blockMaterial This is the material for the block. Typically is set
	 *                      to net.minecraft.block.material.Material.piston, though any material can
	 *                      be used.
	 * @param hardness      This affects how long it takes to break the block. 0.5 is
	 *                      a good value if you want it to be easy to break.
	 * @param pipeRadius    This is used to determine the size of the hit-boxes
	 *                      around the conduit pipes. For each pixel of radius, add 0.0625 (1/16).
	 *                      For example, if your block model files use
	 *                      <i>poweradvantage:block/pipe3_*</i> as the parent model, then the radius
	 *                      of the model will be 3 pixels (6 pixels wide) and you will want to use
	 *                      0.1875 (3/16) as the pipeRadius. A pixel in Minecraft is defines as 1/16
	 *                      of a block, regardless of the texture resolution.
	 * @param energyType    This is the energy type for this block. This block will
	 *                      automatically connect to neighboring blocks of the same energy type.
	 */
	public BlockSimplePowerConduit(Material blockMaterial, float hardness, float pipeRadius, ConduitType... energyType) {
		super(blockMaterial);
		this.types = energyType;
		super.setHardness(hardness);
		this.pipeRadius = pipeRadius;
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(WEST, false)
				.withProperty(DOWN, false)
				.withProperty(SOUTH, false)
				.withProperty(EAST, false)
				.withProperty(UP, false)
				.withProperty(NORTH, false));
	}

	/**
	 * Gets the energy type for this block.
	 *
	 * @return The type of energy/power for this block
	 */
	@Override
	public ConduitType[] getTypes() {
		return types;
	}


	/**
	 * Determines whether this conduit is compatible with an adjacent one
	 *
	 * @param connection A context object that provides the power type and block direction information
	 * @return True if power should be allowed to flow through this connection, false otherwise
	 */
	@Override
	public boolean canAcceptConnection(PowerConnectorContext connection) {
		ConduitType[] myTypes = getTypes();
		for (int i = 0; i < myTypes.length; i++) {
			if (ConduitType.areSameType(myTypes[i], connection.powerType)) return true;
		}
		return false;
	}

	/**
	 * Determines whether this block/entity should receive energy
	 *
	 * @return true if this block/entity should receive energy
	 */
	public boolean isPowerSink() {
		return false;
	}

	/**
	 * Determines whether this block/entity can provide energy
	 *
	 * @return true if this block/entity can provide energy
	 */
	public boolean isPowerSource() {
		return false;
	}

	/**
	 * Creates a blockstate instance.
	 */
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{WEST, DOWN, SOUTH, EAST, UP, NORTH});
	}

	/**
	 * Sets the data values of a BlockState instance to represent this block
	 */
	@Override
	public IBlockState getActualState(final IBlockState bs, final IBlockAccess world, final BlockPos coord) {
		return bs
				.withProperty(WEST, this.canConnectTo(world, coord, bs, EnumFacing.WEST, coord.west()))
				.withProperty(DOWN, this.canConnectTo(world, coord, bs, EnumFacing.DOWN, coord.down()))
				.withProperty(SOUTH, this.canConnectTo(world, coord, bs, EnumFacing.SOUTH, coord.south()))
				.withProperty(EAST, this.canConnectTo(world, coord, bs, EnumFacing.EAST, coord.east()))
				.withProperty(UP, this.canConnectTo(world, coord, bs, EnumFacing.UP, coord.up()))
				.withProperty(NORTH, this.canConnectTo(world, coord, bs, EnumFacing.NORTH, coord.north()));
	}


	/**
	 * Calculates the collision boxes for this block.
	 */
	@Override
	public AxisAlignedBB getBoundingBox(final IBlockState bs, final IBlockAccess world, final BlockPos coord) {
		IBlockState oldBS = bs;
		final boolean connectNorth = this.canConnectTo(world, coord, oldBS, EnumFacing.NORTH, coord.north());
		final boolean connectSouth = this.canConnectTo(world, coord, oldBS, EnumFacing.SOUTH, coord.south());
		final boolean connectWest = this.canConnectTo(world, coord, oldBS, EnumFacing.WEST, coord.west());
		final boolean connectEast = this.canConnectTo(world, coord, oldBS, EnumFacing.EAST, coord.east());
		final boolean connectUp = this.canConnectTo(world, coord, oldBS, EnumFacing.UP, coord.up());
		final boolean connectDown = this.canConnectTo(world, coord, oldBS, EnumFacing.DOWN, coord.down());
		final boolean allFalse = !(connectNorth || connectSouth || connectWest || connectEast || connectUp || connectDown);

		float radius = pipeRadius;
		float rminus = 0.5f - radius;
		float rplus = 0.5f + radius;

		float x1 = rminus;
		float x2 = rplus;
		float y1 = rminus;
		float y2 = rplus;
		float z1 = rminus;
		float z2 = rplus;
		if (connectNorth) {
			z1 = 0.0f;
		}
		if (connectSouth) {
			z2 = 1.0f;
		}
		if (connectWest) {
			x1 = 0.0f;
		}
		if (connectEast) {
			x2 = 1.0f;
		}
		if (connectDown) {
			y1 = 0.0f;
		}
		if (connectUp) {
			y2 = 1.0f;
		}
		if (allFalse) { // Horizontal '+' when making no connections
			z1 = 0.0f;
			z2 = 1.0f;
			x1 = 0.0f;
			x2 = 1.0f;
		}

		return new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
	}

	/**
	 * Calculates the collision boxes for this block.
	 */
	@Override
	public void addCollisionBoxToList(IBlockState bs, World world, BlockPos coord, AxisAlignedBB box,
			List<AxisAlignedBB> collisionBoxList, Entity entity, boolean p_185477_7_) {
		IBlockState oldBS = bs;
		final boolean connectNorth = this.canConnectTo(world, coord, oldBS, EnumFacing.NORTH, coord.north());
		final boolean connectSouth = this.canConnectTo(world, coord, oldBS, EnumFacing.SOUTH, coord.south());
		final boolean connectWest = this.canConnectTo(world, coord, oldBS, EnumFacing.WEST, coord.west());
		final boolean connectEast = this.canConnectTo(world, coord, oldBS, EnumFacing.EAST, coord.east());
		final boolean connectUp = this.canConnectTo(world, coord, oldBS, EnumFacing.UP, coord.up());
		final boolean connectDown = this.canConnectTo(world, coord, oldBS, EnumFacing.DOWN, coord.down());
		final boolean allFalse = !(connectNorth || connectSouth || connectWest || connectEast || connectUp || connectDown);
// Horizontal '+' when making no connections

		float radius = pipeRadius;
		float rminus = 0.5f - radius;
		float rplus = 0.5f + radius;

		AxisAlignedBB newBox;
		newBox = new AxisAlignedBB(rminus, rminus, rminus, rplus, rplus, rplus);
		super.addCollisionBoxToList(coord, box, collisionBoxList, newBox);

		if (connectUp) {
			newBox = new AxisAlignedBB(rminus, rminus, rminus, rplus, 1f, rplus);
			super.addCollisionBoxToList(coord, box, collisionBoxList, newBox);
		}
		if (connectDown) {
			newBox = new AxisAlignedBB(rminus, 0f, rminus, rplus, rplus, rplus);
			super.addCollisionBoxToList(coord, box, collisionBoxList, newBox);
		}
		if (allFalse || connectEast) {
			newBox = new AxisAlignedBB(rminus, rminus, rminus, 1f, rplus, rplus);
			super.addCollisionBoxToList(coord, box, collisionBoxList, newBox);
		}
		if (allFalse || connectWest) {
			newBox = new AxisAlignedBB(0f, rminus, rminus, rplus, rplus, rplus);
			super.addCollisionBoxToList(coord, box, collisionBoxList, newBox);
		}
		if (allFalse || connectSouth) {
			newBox = new AxisAlignedBB(rminus, rminus, rminus, rplus, rplus, 1f);
			super.addCollisionBoxToList(coord, box, collisionBoxList, newBox);
		}
		if (allFalse || connectNorth) {
			newBox = new AxisAlignedBB(rminus, rminus, 0f, rplus, rplus, rplus);
			super.addCollisionBoxToList(coord, box, collisionBoxList, newBox);
		}
	}

	/**
	 * This method determines whether to connect to a neighboring block.
	 * Override this method to change block connection behavior.
	 *
	 * @param w          World instance
	 * @param thisBlock  The block that is checking its neighbor
	 * @param bs         Block state of this block
	 * @param face       The face on the first block through which the connection would happen
	 * @param otherBlock Coordinate of neighboring block
	 * @return Default implementation: true if the neighboring block implements
	 * ITypedConductor and has the same energy type as this block. Overriding
	 * the canConnectTo(ConductorType) method will change the results of this
	 * method.
	 */
	protected boolean canConnectTo(IBlockAccess w, BlockPos thisBlock, IBlockState bs, EnumFacing face, BlockPos otherBlock) {
		w.getBlockState(otherBlock);
		ConduitType[] types = getTypes();
		for (int i = 0; i < types.length; i++) {
			if (PowerHelper.areConnectable(new PowerConnectorContext(types[i], w, bs, thisBlock, face))) return true;
		}
		return false;
	}


	/**
	 * Override of default block behavior
	 */
	@Override
	public boolean isOpaqueCube(IBlockState bs) {
		return false;
	}

	/**
	 * Override of default block behavior
	 */
	@Override
	public boolean isFullCube(IBlockState bs) {
		return false;
	}


	/**
	 * Metadata not used.
	 */
	@Override
	public int getMetaFromState(final IBlockState bs) {
		return 0;
	}

	/**
	 * Checks wheter this block consumes or produces power
	 *
	 * @param conduitType power type
	 * @return Returns false because this block neither consumes nor produces power
	 */
	@Override
	public boolean isPowerSink(ConduitType conduitType) {
		return false;
	}

	/**
	 * Checks wheter this block consumes or produces power
	 *
	 * @param conduitType power type
	 * @return Returns false because this block neither consumes nor produces power
	 */
	@Override
	public boolean isPowerSource(ConduitType conduitType) {
		return false;
	}

	/**
	 * Override of default block behavior
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockState bs, final IBlockAccess world, final BlockPos coord, final EnumFacing face) {
		return true;
	}
}