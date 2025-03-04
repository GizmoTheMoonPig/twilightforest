package twilightforest.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import twilightforest.block.entity.CinderFurnaceBlockEntity;

public class CinderFurnaceBlock extends BaseEntityBlock {

	public static final BooleanProperty LIT = BooleanProperty.create("lit");
	private static final EnumProperty<Direction> FACING = TFHorizontalBlock.FACING;
	public static final MapCodec<CinderFurnaceBlock> CODEC = simpleCodec(CinderFurnaceBlock::new);

	public CinderFurnaceBlock(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	@SuppressWarnings("deprecation")
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter getter, BlockPos pos) {
		return state.getValue(LIT) ? 15 : 0;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(LIT, FACING);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CinderFurnaceBlockEntity(pos, state);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
		super.triggerEvent(state, level, pos, id, param);
		BlockEntity tileentity = level.getBlockEntity(pos);
		return tileentity != null && tileentity.triggerEvent(id, param);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof CinderFurnaceBlockEntity cinder) {
			player.openMenu(cinder);
		}

		return InteractionResult.PASS;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (state.getValue(LIT)) {
			Blocks.FURNACE.animateTick(state, level, pos, random);
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity tileentity = level.getBlockEntity(pos);
			if (tileentity instanceof CinderFurnaceBlockEntity) {
				Containers.dropContents(level, pos, (CinderFurnaceBlockEntity) tileentity);
				level.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(state, level, pos, newState, isMoving);
		}
	}
}
