package twilightforest.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;
import twilightforest.entity.projectile.MoonwormShot;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFEntities;
import twilightforest.init.TFSounds;
import twilightforest.util.TFItemStackUtils;

import javax.annotation.Nonnull;
import java.util.Objects;

public class MoonwormQueenItem extends Item {

	public static final int FIRING_TIME = 12;

	public MoonwormQueenItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (stack.getDamageValue() == this.getMaxDamage(stack)) {
			return InteractionResult.FAIL;
		} else {
			player.startUsingItem(hand);
			return InteractionResult.SUCCESS;
		}
	}

	//	[VanillaCopy] ItemBlock.onItemUse, hardcoding the block
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		Player player = context.getPlayer();
		BlockPlaceContext blockItemUseContext = new BlockPlaceContext(context);

		if (!state.canBeReplaced()) {
			pos = pos.relative(context.getClickedFace());
		}

		if (player != null) {
			ItemStack itemstack = player.getItemInHand(context.getHand());

			if (itemstack.getDamageValue() < itemstack.getMaxDamage() && player.mayUseItemAt(pos, context.getClickedFace(), itemstack) && level.isUnobstructed(TFBlocks.MOONWORM.get().defaultBlockState(), pos, CollisionContext.empty())) {
				if (this.tryPlace(blockItemUseContext).consumesAction()) {
					SoundType soundtype = level.getBlockState(pos).getBlock().getSoundType(level.getBlockState(pos), level, pos, player);
					level.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
					// TF - damage stack instead of shrinking
					player.stopUsingItem();
				}

				return InteractionResult.SUCCESS;
			} else {
				return InteractionResult.FAIL;
			}
		}
		return InteractionResult.FAIL;
	}


	@Override
	public boolean releaseUsing(ItemStack stack, Level level, LivingEntity living, int useRemaining) {
		int useTime = this.getUseDuration(stack, living) - useRemaining;

		if (!level.isClientSide() && useTime > FIRING_TIME && (stack.getDamageValue() + 1) < stack.getMaxDamage()) {

			if (level.addFreshEntity(new MoonwormShot(TFEntities.MOONWORM_SHOT.get(), level, living))) {
				if (living instanceof Player player && !player.getAbilities().instabuild) TFItemStackUtils.hurtButDontBreak(stack, 2, (ServerLevel) level, player);

				level.playSound(null, living.getX(), living.getY(), living.getZ(), TFSounds.MOONWORM_SQUISH.get(), living instanceof Player ? SoundSource.PLAYERS : SoundSource.NEUTRAL, 1.0F, 1.0F);
				return true;
			}
		}

		return false;
	}

	@Nonnull
	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity user) {
		return 72000;
	}

	//everything from this point on is a [VanillaCopy] of BlockItem, since extending the class doesnt work for this
	public InteractionResult tryPlace(BlockPlaceContext context) {
		if (!context.canPlace()) {
			return InteractionResult.FAIL;
		} else {
			BlockPlaceContext blockitemusecontext = this.getBlockItemUseContext(context);
			if (blockitemusecontext == null) {
				return InteractionResult.FAIL;
			} else {
				BlockState blockstate = this.getStateForPlacement(blockitemusecontext);
				if (blockstate == null) {
					return InteractionResult.FAIL;
				} else if (!this.placeBlock(blockitemusecontext, blockstate)) {
					return InteractionResult.FAIL;
				} else {
					BlockPos blockpos = blockitemusecontext.getClickedPos();
					Level level = blockitemusecontext.getLevel();
					Player playerentity = blockitemusecontext.getPlayer();
					ItemStack itemstack = blockitemusecontext.getItemInHand();
					BlockState blockstate1 = level.getBlockState(blockpos);
					Block block = blockstate1.getBlock();
					if (block == blockstate.getBlock()) {
						blockstate1 = this.updateBlockStateFromTag(blockpos, level, itemstack, blockstate1);
						this.onBlockPlaced(blockpos, level, playerentity, itemstack);
						block.setPlacedBy(level, blockpos, blockstate1, playerentity, itemstack);
						if (playerentity instanceof ServerPlayer) {
							CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) playerentity, blockpos, itemstack);
						}
					}

					SoundType soundtype = blockstate1.getSoundType(level, blockpos, context.getPlayer());
					level.playSound(playerentity, blockpos, this.getPlaceSound(blockstate1, level, blockpos, Objects.requireNonNull(context.getPlayer())), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
					if (level instanceof ServerLevel server && !playerentity.getAbilities().instabuild) {
						TFItemStackUtils.hurtButDontBreak(itemstack, 1, server, playerentity);
					}

					return InteractionResult.SUCCESS;
				}
			}
		}
	}

	protected SoundEvent getPlaceSound(BlockState state, Level level, BlockPos pos, Player entity) {
		return state.getSoundType(level, pos, entity).getPlaceSound();
	}

	@Nullable
	public BlockPlaceContext getBlockItemUseContext(BlockPlaceContext context) {
		return context;
	}

	protected boolean onBlockPlaced(BlockPos pos, Level level, @Nullable Player player, ItemStack stack) {
		return BlockItem.updateCustomBlockEntityTag(level, player, pos, stack);
	}

	@Nullable
	protected BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockstate = TFBlocks.MOONWORM.get().getStateForPlacement(context);
		return blockstate != null && this.canPlace(context, blockstate) ? blockstate : null;
	}

	protected boolean canPlace(BlockPlaceContext context, BlockState state) {
		Player player = context.getPlayer();
		CollisionContext collision = player == null ? CollisionContext.empty() : CollisionContext.of(player);
		return (state.canSurvive(context.getLevel(), context.getClickedPos())) && context.getLevel().isUnobstructed(state, context.getClickedPos(), collision);
	}

	private BlockState updateBlockStateFromTag(BlockPos pos, Level level, ItemStack stack, BlockState state) {
		BlockItemStateProperties blockitemstateproperties = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
		if (blockitemstateproperties.isEmpty()) {
			return state;
		} else {
			BlockState blockstate = blockitemstateproperties.apply(state);
			if (blockstate != state) {
				level.setBlock(pos, blockstate, Block.UPDATE_CLIENTS);
			}

			return blockstate;
		}
	}

	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		return context.getLevel().setBlock(context.getClickedPos(), state, Block.UPDATE_ALL_IMMEDIATE);
	}
}