package com.mcmoddev.mineralogy.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RockSlab extends net.minecraft.block.Block {

	public static final PropertyDirection FACING = PropertyDirection.create("facing");

	private static final float THICKNESS = 0.5f;

	private static final AxisAlignedBB[] BOXES = new AxisAlignedBB[EnumFacing.values().length];
	static {
		for (int i = 0; i < EnumFacing.values().length; i++) {
			EnumFacing orientation = EnumFacing.values()[i];
			float x1 = 0;
			float x2 = 1;
			float y1 = 0;
			float y2 = 1;
			float z1 = 0;
			float z2 = 1;
			switch (orientation) {
				case DOWN:
					y1 = 1f - THICKNESS;
					break;
				case SOUTH:
					z2 = THICKNESS;
					break;
				case NORTH:
					z1 = 1f - THICKNESS;
					break;
				case EAST:
					x2 = THICKNESS;
					break;
				case WEST:
					x1 = 1f - THICKNESS;
					break;
				case UP:
				default:
					y2 = THICKNESS;
					break;
			}
			BOXES[orientation.ordinal()] = new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
		}
	}

	public RockSlab(float hardness, float blastResistance, int toolHardnessLevel, SoundType sound) {
		super(Material.ROCK);
		this.setHardness((float) hardness); // dirt is 0.5, grass is 0.6, stone is 1.5,iron ore is 3, obsidian is 50
		this.setResistance((float) blastResistance); // dirt is 0, iron ore is 5, stone is 10, obsidian is 2000
		this.setSoundType(sound); // sound for stone
		this.setHarvestLevel("pickaxe", toolHardnessLevel);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
		this.useNeighborBrightness = true;
	}

	@Deprecated
	@Override
	public boolean isOpaqueCube(IBlockState bs) {
		return false;
	}

	@Deprecated
	@Override
	public boolean isFullCube(IBlockState bs) {
		return false;
	}

	@Deprecated
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing face, float partialX,
			float partialY, float partialZ, int meta, EntityLivingBase placer) {
		IBlockState defaultState = this.getDefaultState().withProperty(FACING, face);
		// redimension to face-local up and right dimensions
		float up;
		float right;
		EnumFacing.Axis upRotationAxis;
		EnumFacing.Axis rightRotationAxis;
		switch (face) {
			case UP: // works
				up = partialZ - 0.5F;
				right = partialX - 0.5F;
				upRotationAxis = EnumFacing.Axis.X;
				rightRotationAxis = EnumFacing.Axis.Z;
				break;
			case EAST: // works
				up = partialY - 0.5F;
				right = partialZ - 0.5F;
				upRotationAxis = EnumFacing.Axis.Z;
				rightRotationAxis = EnumFacing.Axis.Y;
				break;
			case SOUTH:
				up = 0.5F - partialY;
				right = 0.5F - partialX;
				upRotationAxis = EnumFacing.Axis.X;
				rightRotationAxis = EnumFacing.Axis.Y;
				break;
			case DOWN:
				up = 0.5F - partialZ;
				right = 0.5F - partialX;
				upRotationAxis = EnumFacing.Axis.X;
				rightRotationAxis = EnumFacing.Axis.Z;
				break;
			case WEST:
				up = 0.5F - partialY;
				right = 0.5F - partialZ;
				upRotationAxis = EnumFacing.Axis.Z;
				rightRotationAxis = EnumFacing.Axis.Y;
				break;
			case NORTH: // works
				up = partialY - 0.5F;
				right = partialX - 0.5F;
				upRotationAxis = EnumFacing.Axis.X;
				rightRotationAxis = EnumFacing.Axis.Y;
				break;
			default:
				return defaultState;
		}
		if (Math.abs(up) < 0.25F && Math.abs(right) < 0.25F) {
			// no rotation
			return defaultState;
		}
		boolean upOrRight = up + right > 0;
		boolean upOrLeft = up - right > 0;
		if (upOrRight) {
			// up or right
			if (upOrLeft) {
				// up
				return defaultState.withProperty(FACING, face.rotateAround(upRotationAxis));
			} else {
				// right
				return defaultState.withProperty(FACING, face.rotateAround(rightRotationAxis).getOpposite());
			}
		} else {
			// down or left
			if (upOrLeft) {
				// left
				return defaultState.withProperty(FACING, face.rotateAround(rightRotationAxis));
			} else {
				// down
				return defaultState.withProperty(FACING, face.rotateAround(upRotationAxis).getOpposite());
			}
		}
	}

	@Deprecated
	@Override
	public IBlockState getStateFromMeta(final int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
	}

	@Override
	public int getMetaFromState(final IBlockState bs) {
		return ((EnumFacing) bs.getValue(FACING)).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}

	@Deprecated
	@Override
	public AxisAlignedBB getBoundingBox(final IBlockState bs, final IBlockAccess world, final BlockPos coord) {
		final EnumFacing orientation = bs.getValue(FACING);
		return BOXES[orientation.ordinal()];
	}

	@Deprecated
	@Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
    		List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
		final EnumFacing orientation = worldIn.getBlockState(pos).getValue(FACING);
		super.addCollisionBoxToList(pos, entityBox, collidingBoxes, BOXES[orientation.ordinal()]);
	}
}
