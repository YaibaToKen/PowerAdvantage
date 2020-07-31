package cyano.poweradvantage.machines.conveyors;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import cyano.poweradvantage.api.GUIBlock;
import cyano.poweradvantage.init.ItemGroups;

public class BlockConveyor extends GUIBlock {

	private final Class<? extends TileEntityConveyor> tileEntityClass;

	public BlockConveyor(Material m, float hardness) {
		super(m);
		this.setHardness(hardness);
		this.tileEntityClass = TileEntityConveyor.class;
		this.setSoundType(SoundType.METAL);
		this.setCreativeTab(ItemGroups.tab_powerAdvantage);
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(FACING, EnumFacing.NORTH));
	}


	/**
	 * Blockstate property
	 */
	public static final PropertyDirection FACING = PropertyDirection.create("facing");

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
	}

	@Override
	public TileEntity createNewTileEntity(World w, int m) {
		try {
			return tileEntityClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			FMLLog.severe("Failed to create instance of class " + tileEntityClass.getName()
					+ "! Did you forget to give it a no-arg constructor?");
			return null;
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState bs) {
		return false;
	}


	@Override
	public boolean isFullCube(IBlockState bs) {
		return false;
	}

	@Override
	public void onBlockPlacedBy(World w, BlockPos coord, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		//TODO No longer an EnumFacing param
		w.setBlockState(coord, this.getDefaultState().withProperty(FACING,
			EnumFacing.getFacingFromVector((float)(placer.posX - coord.getX()), 0f, (float)(placer.posZ - coord.getZ())).getOpposite()),
			2);
		super.onBlockPlacedBy(w, coord, state, placer, stack);
	}

	@Override
	public IBlockState getStateFromMeta(final int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
	}

	@Override
	public int getMetaFromState(final IBlockState bs) {
		int i = ((EnumFacing) bs.getValue(FACING)).getIndex();
		return i;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{FACING});
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState bs) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState bs, World world, BlockPos coord) {
		TileEntity te = world.getTileEntity(coord);
		if (te != null && te instanceof TileEntityConveyor) {
			if (((TileEntityConveyor) te).getStackInSlot(0) == null) {
				return 0;
			} else {
				return 7;
			}
		}
		return 0;
	}

	@Override
	public void breakBlock(final World w, final BlockPos coord, final IBlockState bs) {
		final TileEntity tileEntity = w.getTileEntity(coord);
		if (tileEntity instanceof TileEntityConveyor) {
			InventoryHelper.dropInventoryItems(w, coord, (IInventory) tileEntity);
			w.updateComparatorOutputLevel(coord, this);
		}
		super.breakBlock(w, coord, bs);
	}

}
