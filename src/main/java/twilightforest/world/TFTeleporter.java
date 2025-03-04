package twilightforest.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import twilightforest.TwilightForestMod;
import twilightforest.block.TFPortalBlock;
import twilightforest.config.TFConfig;
import twilightforest.data.tags.BlockTagGenerator;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFDimension;
import twilightforest.item.MagicMapItem;
import twilightforest.util.landmarks.LandmarkUtil;
import twilightforest.util.landmarks.LegacyLandmarkPlacements;
import twilightforest.util.Restriction;

import java.util.*;
import java.util.function.Predicate;

public class TFTeleporter {

	public static TeleportTransition createTransition(Entity entity, ServerLevel dest, BlockPos pos, boolean forcedEntry) {
		TeleportTransition transition;
		TeleporterCache cache = TeleporterCache.get(dest);

		if ((transition = placeInExistingPortal(cache, dest, entity, pos)) == null) {
			TwilightForestMod.LOGGER.debug("Did not find existing portal, making a new one.");
			transition = createPosition(dest, entity, pos, cache, forcedEntry);
		}

		if (transition != null) return transition;
		return new TeleportTransition(dest, Vec3.atCenterOf(pos.atY(dest.getSeaLevel())), Vec3.ZERO, entity.getYRot(), entity.getXRot(), TeleportTransition.PLACE_PORTAL_TICKET);
	}

	@Nullable
	protected static TeleportTransition createPosition(ServerLevel dest, Entity entity, BlockPos destPos, TeleporterCache cache, boolean locked) {
		TeleportTransition info = moveToSafeCoords(dest, entity, destPos);
		makePortal(cache, entity, dest, info.position(), locked);
		info = placeInExistingPortal(cache, dest, entity, BlockPos.containing(info.position()));
		return info;
	}

	@Nullable
	protected static TeleportTransition placeInExistingPortal(TeleporterCache cache, ServerLevel destDim, Entity entity, BlockPos pos) {
		boolean flag = true;
		BlockPos blockpos;
		ColumnPos columnPos = new ColumnPos(entity.blockPosition().getX(), entity.blockPosition().getZ()); // Must be the position from the src dim

		PortalPosition portalPosition = cache.getPortalPosition(destDim.dimension().location(), columnPos);
		if (portalPosition != null) {
			blockpos = portalPosition.pos;
			portalPosition.lastUpdateTime = destDim.getGameTime();
			flag = false;
			// Validate that the Portal still exists
			TwilightForestMod.LOGGER.debug("Using cache, validating. {}", blockpos);
			if (blockpos == null || !destDim.getBlockState(blockpos).is(TFBlocks.TWILIGHT_PORTAL)) {
				// Portal was broken, we need to recreate it.
				TwilightForestMod.LOGGER.debug("Portal Invalid, recreating.");
				blockpos = null;
				cache.removeInvalidPos(destDim.dimension().location(), columnPos);
			}
		} else {
			//BlockPos blockpos3 = new BlockPos(entity);
			blockpos = getPortalPosition(destDim, pos);
		}

		if (blockpos == null) {
			return null;
		} else {
			if (flag) {
				TwilightForestMod.LOGGER.debug("Caching Src Portal Blocks to {}", blockpos);
				Map<BlockPos, Boolean> portalBlocks = new HashMap<>();
				portalBlocks.put(entity.blockPosition(), true);
				TFPortalBlock.recursivelyValidatePortal(entity.level(), entity.blockPosition(), portalBlocks, new MutableInt(0), entity.level().getBlockState(entity.blockPosition()));
				BlockPos finalBlockpos = blockpos;
				portalBlocks.forEach((blockPos, b) -> {
					if (b) {
						TwilightForestMod.LOGGER.debug("Caching {}", blockPos);
						cache.addBlockToCache(destDim.dimension().location(), new ColumnPos(blockPos.getX(), blockPos.getZ()), new PortalPosition(finalBlockpos, destDim.getGameTime()));
					}
				});
				// the last param is just an object for tracking, don't worry about it using columnPos instead of blockpos
				destDim.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(blockpos), 3, new BlockPos(columnPos.x(), blockpos.getY(), columnPos.z()));
			}

			// replace with our own placement logic
			BlockPos[] portalBorder = getBoundaryPositions(destDim, blockpos).toArray(new BlockPos[0]);
			BlockPos borderPos;
			if (portalBorder.length > 0) {
				borderPos = portalBorder[destDim.getRandom().nextInt(portalBorder.length)];
			} else {
				borderPos = blockpos;
			}

			double portalX = borderPos.getX() + 0.5;
			double portalY = borderPos.getY() + 1.0;
			double portalZ = borderPos.getZ() + 0.5;

			return makePortalInfo(destDim, entity, portalX, portalY, portalZ);
		}
	}

	@Nullable
	private static BlockPos getPortalPosition(ServerLevel destDim, BlockPos pos) {
		int i = 200; // scan radius up to 200, and also un-inline this variable back into below
		double d0 = Double.MAX_VALUE;
		BlockPos result = null;

		for (int i1 = -i; i1 <= i; ++i1) {
			BlockPos blockpos2;

			for (int j1 = -i; j1 <= i; ++j1) {

				// skip positions outside current world border (MC-114796)
				if (!destDim.getWorldBorder().isWithinBounds(pos.offset(i1, 0, j1))) {
					continue;
				}

				ChunkPos chunkPos = new ChunkPos(pos.offset(i1, 0, j1));
				LevelChunk chunk = destDim.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);

				// skip chunks that aren't generated
				if (chunk == null || chunk.getFullStatus() == FullChunkStatus.INACCESSIBLE) {
					continue;
				}

				for (BlockPos blockpos1 = pos.offset(i1, getScanHeight(destDim, pos) - pos.getY(), j1); blockpos1.getY() >= destDim.getMinY(); blockpos1 = blockpos2) {
					blockpos2 = blockpos1.below();

					// don't lookup state if inner condition would fail
					if (d0 >= 0.0D && blockpos1.distSqr(pos) >= d0) {
						continue;
					}

					// use our portal block
					if (isPortal(chunk.getBlockState(blockpos1))) {
						for (blockpos2 = blockpos1.below(); isPortal(chunk.getBlockState(blockpos2)); blockpos2 = blockpos2.below()) {
							blockpos1 = blockpos2;
						}

						float d1 = (float) blockpos1.distSqr(pos);
						if (d0 < 0.0D || d1 < d0) {
							d0 = d1;
							result = blockpos1;
							// restrict search radius to new distance
							i = Mth.ceil(Mth.sqrt(d1));
						}
					}
				}
			}
		}
		return result;
	}

	private static int getScanHeight(ServerLevel world, BlockPos pos) {
		return getScanHeight(world, pos.getX(), pos.getZ());
	}

	private static int getScanHeight(ServerLevel world, int x, int z) {
		int worldHeight = world.getMinY() - 1;
		//FIXME find an alternative to getHighestSectionPosition, its marked for removal
		@SuppressWarnings("removal")
		int chunkHeight = world.getChunk(x >> 4, z >> 4).getHighestSectionPosition() + 15;
		return Math.min(worldHeight, chunkHeight);
	}

	private static boolean isPortal(BlockState state) {
		return state.is(TFBlocks.TWILIGHT_PORTAL);
	}

	// from the start point, builds a set of all directly adjacent non-portal blocks
	private static Set<BlockPos> getBoundaryPositions(ServerLevel world, BlockPos start) {
		Set<BlockPos> result = new HashSet<>(), checked = new HashSet<>();
		checked.add(start);
		checkAdjacent(world, start, checked, result);
		return result;
	}

	private static void checkAdjacent(ServerLevel world, BlockPos pos, Set<BlockPos> checked, Set<BlockPos> result) {
		for (Direction facing : Direction.Plane.HORIZONTAL) {
			BlockPos offset = pos.relative(facing);
			if (!checked.add(offset))
				continue;
			BlockState checkState = world.getBlockState(offset);
			if (isPortal(checkState)) {
				checkAdjacent(world, offset, checked, result);
			} else {
				if (Block.isFaceFull(checkState.getCollisionShape(world, offset), Direction.UP) && world.getBlockState(offset.above()).getCollisionShape(world, offset.above()).isEmpty()) {
					result.add(offset);
				}
			}
		}
	}

	protected static boolean isPortalAt(ServerLevel world, BlockPos pos) {
		return isPortal(world.getBlockState(pos));
	}

	// Scale the coords based on the dimension type coordinate_scale
	protected static double getHorizontalScale(ServerLevel destination) {
		ServerLevel tfDim = destination.getServer().getLevel(TFDimension.DIMENSION_KEY);
		double scale = tfDim == null ? 0.125D : tfDim.dimensionType().coordinateScale();
		return destination.dimension().equals(TFDimension.DIMENSION_KEY) ? 1F / scale : scale;
	}

	protected static TeleportTransition moveToSafeCoords(ServerLevel level, Entity entity, BlockPos pos) {
		// if we're in enforced progression mode, check the biomes for safety
		boolean checkProgression = LandmarkUtil.isProgressionEnforced(level);

		if (isSafeAround(level, pos, entity, checkProgression)) {
			TwilightForestMod.LOGGER.debug("Portal destination looks safe!");
			return makePortalInfo(level, entity, Vec3.atCenterOf(pos));
		}

		TwilightForestMod.LOGGER.debug("Portal destination looks unsafe, rerouting!");

		BlockPos safeCoords = findSafeCoords(level, 200, pos, entity, checkProgression);
		if (safeCoords != null) {
			TwilightForestMod.LOGGER.debug("Safely rerouted!");
			return makePortalInfo(level, entity, safeCoords.getX(), entity.getY(), safeCoords.getZ());
		}

		TwilightForestMod.LOGGER.info("Did not find a safe portal spot first try, trying again with longer range.");
		safeCoords = findSafeCoords(level, 400, pos, entity, checkProgression);

		if (safeCoords != null) {
			TwilightForestMod.LOGGER.info("Safely rerouted to long range portal. Return trip not guaranteed.");
			return makePortalInfo(level, entity, safeCoords.getX(), entity.getY(), safeCoords.getZ());
		}

		TwilightForestMod.LOGGER.info("Did not find a safe portal spot second try, trying to move slightly towards the center between key biomes.");
		safeCoords = findSafeCoords(level, 400, moveTowardsCenter(pos, 0.5F), entity, checkProgression);

		if (safeCoords != null) {
			TwilightForestMod.LOGGER.info("Safely rerouted to slightly centered portal. Return trip not guaranteed.");
			return makePortalInfo(level, entity, safeCoords.getX(), entity.getY(), safeCoords.getZ());
		}

		TwilightForestMod.LOGGER.info("Did not find a safe portal spot third try, trying to move further towards the center between key biomes.");
		safeCoords = findSafeCoords(level, 400, moveTowardsCenter(pos, 0.9F), entity, checkProgression);

		if (safeCoords != null) {
			TwilightForestMod.LOGGER.info("Safely rerouted to very centered portal. Return trip not guaranteed.");
			return makePortalInfo(level, entity, safeCoords.getX(), entity.getY(), safeCoords.getZ());
		}

		TwilightForestMod.LOGGER.warn("Still did not find a safe portal spot.");

		return makePortalInfo(level, entity, Vec3.atCenterOf(pos));
	}

	private static BlockPos moveTowardsCenter(BlockPos pos, float lerp) {
		ColumnPos centerPos = MagicMapItem.getMagicMapCenter(pos.getX(), pos.getZ());
		float vx = centerPos.x() - pos.getX();
		float vz = centerPos.z() - pos.getZ();
		float nx = pos.getX() + vx * lerp;
		float nz = pos.getZ() + vz * lerp;
		return BlockPos.containing(nx, pos.getY(), nz);
	}

	public static boolean isSafeAround(Level world, BlockPos pos, Entity entity, boolean checkProgression) {

		if (!isSafe(world, pos, entity, checkProgression)) {
			return false;
		}

		for (Direction facing : Direction.Plane.HORIZONTAL) {
			if (!isSafe(world, pos.relative(facing, 16), entity, checkProgression)) {
				return false;
			}
		}

		return true;
	}

	private static boolean isSafe(Level world, BlockPos pos, Entity entity, boolean checkProgression) {
		return !world.dimension().equals(TFDimension.DIMENSION_KEY) || (checkPos(world, pos) && (!checkProgression || checkBiome(world, pos, entity)) && checkStructure(world, pos));
	}

	private static boolean checkPos(Level world, BlockPos pos) {
		return world.getWorldBorder().isWithinBounds(pos);
	}

	private static boolean checkStructure(Level world, BlockPos pos) {
		boolean outsideLandmarkRange = !LegacyLandmarkPlacements.blockNearLandmarkCenter(pos.getX(), pos.getZ(), 5);
		if (!outsideLandmarkRange) return false;

		Optional<StructureStart> possibleNearLandmark = LandmarkUtil.locateNearestLandmarkStart(world, SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
		return possibleNearLandmark.isEmpty() || possibleNearLandmark.get().getBoundingBox().isInside(pos);
	}

	private static boolean checkBiome(Level world, BlockPos pos, Entity entity) {
		return Restriction.isBiomeSafeFor(world.getBiome(pos).value(), entity);
	}

	@Nullable
	private static BlockPos findSafeCoords(ServerLevel world, int range, BlockPos pos, Entity entity, boolean checkProgression) {
		int attempts = range / 8;
		for (int x = 0; x < attempts; x++) {
			for (int z = 0; z < attempts; z++) {
				BlockPos dPos = new BlockPos(pos.getX() + (x * attempts) - (range / 2), 100, pos.getZ() + (z * attempts) - (range / 2));

				if (isSafeAround(world, dPos, entity, checkProgression)) {
					return dPos;
				}
			}
		}
		return null;
	}

	protected static void makePortal(TeleporterCache cache, Entity entity, ServerLevel world, Vec3 pos, boolean locked) {
		ServerLevel src = entity.level() instanceof ServerLevel serverLevel ? serverLevel : null;

		// ensure area is populated first
		loadSurroundingArea(world, pos);

		BlockPos spot = findPortalCoords(world, pos, blockPos -> isPortalAt(world, blockPos));
		String name = entity.getName().getString();

		if (spot != null) {
			TwilightForestMod.LOGGER.debug("Found existing portal for {} at {}", name, spot);
			cacheNewPortalCoords(cache, src, spot, entity.blockPosition());
			return;
		}

		spot = findPortalCoords(world, pos, blockpos -> isIdealForPortal(world, blockpos));

		if (spot != null) {
			TwilightForestMod.LOGGER.debug("Found ideal portal spot for {} at {}", name, spot);
			cacheNewPortalCoords(cache, src, makePortalAt(world, spot, locked), entity.blockPosition());
			return;
		}

		TwilightForestMod.LOGGER.debug("Did not find ideal portal spot, shooting for okay one for {}", name);
		spot = findPortalCoords(world, pos, blockPos -> isOkayForPortal(world, blockPos));

		if (spot != null) {
			TwilightForestMod.LOGGER.debug("Found okay portal spot for {} at {}", name, spot);
			cacheNewPortalCoords(cache, src, makePortalAt(world, spot, locked), entity.blockPosition());
			return;
		}

		TwilightForestMod.LOGGER.debug("Did not even find an okay portal spot, just making a fallback one for {}", name);

		spot = findPortalCoords(world, pos, blockpos -> isOkayForFallbackPortal(world, blockpos), true);
		if (spot != null) {
			TwilightForestMod.LOGGER.debug("Found fallback portal spot for {} at {}", name, spot);
			cacheNewPortalCoords(cache, src, makePortalAt(world, spot, locked), entity.blockPosition());
			return;
		}

		// well I don't think we can actually just return and fail here
		TwilightForestMod.LOGGER.debug("Did not even find a fallback portal spot, just making a random one for {}", name);

		// adjust the portal height based on what world we're traveling to
		double yFactor = getYFactor(world);

		// + 2 to make it above bedrock
		cacheNewPortalCoords(cache, src, makePortalAt(world, BlockPos.containing(entity.getX() * getHorizontalScale(world), (entity.getY() * yFactor) + 2, entity.getZ() * getHorizontalScale(world)), locked), entity.blockPosition());
	}

	protected static void loadSurroundingArea(ServerLevel world, Vec3 pos) {

		int x = Mth.floor(pos.x()) >> 4;
		int z = Mth.floor(pos.y()) >> 4;

		for (int dx = -2; dx <= 2; dx++) {
			for (int dz = -2; dz <= 2; dz++) {
				world.getChunk(x + dx, z + dz);
			}
		}
	}

	@Nullable
	protected static BlockPos findPortalCoords(ServerLevel world, Vec3 loc, Predicate<BlockPos> predicate) {
		return findPortalCoords(world, loc, predicate, false);
	}

	@Nullable
	protected static BlockPos findPortalCoords(ServerLevel world, Vec3 loc, Predicate<BlockPos> predicate, boolean makePortalInAir) {
		// adjust the height based on what world we're traveling to
		double yFactor = getYFactor(world);
		// modified copy of base Teleporter method:
		int entityX = Mth.floor(loc.x());
		int entityZ = Mth.floor(loc.z());

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		double spotWeight = -1D;
		BlockPos spot = null;

		int range = 16;
		for (int rx = entityX - range; rx <= entityX + range; rx++) {
			double xWeight = (rx + 0.5D) - loc.x();
			for (int rz = entityZ - range; rz <= entityZ + range; rz++) {
				double zWeight = (rz + 0.5D) - loc.z();

				for (int ry = getScanHeight(world, rx, rz); ry >= world.getMinY(); ry--) {


					pos.set(rx, ry, rz);
					if (!makePortalInAir && !world.isEmptyBlock(pos)) {
						continue;
					}

					if (makePortalInAir) {
						while (ry > world.getMinY() && world.isEmptyBlock(pos.set(rx, ry - 1, rz)) && predicate.test(pos)) {
							ry--;
						}
						pos.set(rx, ry, rz);
					} else {
						while (ry > world.getMinY() && world.isEmptyBlock(pos.set(rx, ry - 1, rz))) {
							ry--;
						}
					}

					double yWeight = (ry + 0.5D) - loc.y() * yFactor;
					double rPosWeight = xWeight * xWeight + yWeight * yWeight + zWeight * zWeight;

					if (spotWeight < 0.0D || rPosWeight < spotWeight) {
						// check from the "in ground" pos
						if (predicate.test(pos)) {
							spotWeight = rPosWeight;
							spot = pos.immutable();
						}
					}
				}
			}
		}

		return spot;
	}

	protected static double getYFactor(ServerLevel world) {
		return world.dimension().location().equals(Level.OVERWORLD.location()) ? 2.0 : 0.5;
	}

	private static void cacheNewPortalCoords(TeleporterCache cache, @Nullable ServerLevel srcDim, BlockPos pos, BlockPos srcPos) {
		// src/dest is backwards logic because we're caching the opposite direction
		if (srcDim == null)
			return;
		BlockPos exitPos = getPortalPosition(srcDim, srcPos);
		if (exitPos == null)
			return;
		TwilightForestMod.LOGGER.debug("Caching Dest Portal Blocks to {}", exitPos);
		cache.addBlockToCache(srcDim.dimension().location(), new ColumnPos(pos.getX(), pos.getZ()), new TFTeleporter.PortalPosition(exitPos, srcDim.getGameTime()));
		cache.addBlockToCache(srcDim.dimension().location(), new ColumnPos(pos.south().getX(), pos.south().getZ()), new TFTeleporter.PortalPosition(exitPos, srcDim.getGameTime()));
		cache.addBlockToCache(srcDim.dimension().location(), new ColumnPos(pos.east().getX(), pos.east().getZ()), new TFTeleporter.PortalPosition(exitPos, srcDim.getGameTime()));
		cache.addBlockToCache(srcDim.dimension().location(), new ColumnPos(pos.south().east().getX(), pos.south().east().getZ()), new TFTeleporter.PortalPosition(exitPos, srcDim.getGameTime()));
	}

	protected static boolean isIdealForPortal(ServerLevel world, BlockPos pos) {
		for (int potentialZ = 0; potentialZ < 4; potentialZ++) {
			for (int potentialX = 0; potentialX < 4; potentialX++) {
				for (int potentialY = 0; potentialY < 6; potentialY++) {
					BlockPos tPos = pos.offset(potentialX - 1, potentialY, potentialZ - 1);
					BlockState state = world.getBlockState(tPos);

					// all blocks mustn't be bedrock, end portal frame, etc.; and other conditions for layers >= 0
					if (state.is(BlockTags.FEATURES_CANNOT_REPLACE) || potentialY == 0 && !state.is(BlockTags.DIRT) || potentialY >= 1 && !state.canBeReplaced()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	protected static BlockPos makePortalAt(Level world, BlockPos pos, boolean locked) {
		// grass all around it
		BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();

		world.setBlockAndUpdate(pos.west().north(), grass);
		world.setBlockAndUpdate(pos.north(), grass);
		world.setBlockAndUpdate(pos.east().north(), grass);
		world.setBlockAndUpdate(pos.east(2).north(), grass);

		world.setBlockAndUpdate(pos.west(), grass);
		world.setBlockAndUpdate(pos.east(2), grass);

		world.setBlockAndUpdate(pos.west().south(), grass);
		world.setBlockAndUpdate(pos.east(2).south(), grass);

		world.setBlockAndUpdate(pos.west().south(2), grass);
		world.setBlockAndUpdate(pos.south(2), grass);
		world.setBlockAndUpdate(pos.east().south(2), grass);
		world.setBlockAndUpdate(pos.east(2).south(2), grass);

		BlockPos[] positions = new BlockPos[4];
		positions[0] = pos.below();
		positions[1] = pos.east().below();
		positions[2] = pos.south().below();
		positions[3] = pos.east().south().below();

		// dirt under it
		BlockState dirt = Blocks.DIRT.defaultBlockState();
		for (BlockPos blockpos : positions) {
			if (world.getBlockState(pos).is(BlockTags.FEATURES_CANNOT_REPLACE))
				world.setBlockAndUpdate(blockpos, dirt);
		}

		// portal in it
		BlockState portal = TFBlocks.TWILIGHT_PORTAL.get().defaultBlockState().setValue(TFPortalBlock.DISALLOW_RETURN, (locked || !TFConfig.shouldReturnPortalBeUsable));

		world.setBlock(pos, portal, Block.UPDATE_CLIENTS);
		world.setBlock(pos.east(), portal, Block.UPDATE_CLIENTS);
		world.setBlock(pos.south(), portal, Block.UPDATE_CLIENTS);
		world.setBlock(pos.east().south(), portal, Block.UPDATE_CLIENTS);

		// meh, let's just make a bunch of air over it for 4 squares
		for (int dx = -1; dx <= 2; dx++) {
			for (int dz = -1; dz <= 2; dz++) {
				for (int dy = 1; dy <= 5; dy++) {
					world.removeBlock(pos.offset(dx, dy, dz), false);
				}
			}
		}

		// finally, "nature decorations"!
		world.setBlock(pos.west().north().above(), randNatureBlock(world.getRandom()), Block.UPDATE_CLIENTS);
		world.setBlock(pos.north().above(), randNatureBlock(world.getRandom()), Block.UPDATE_CLIENTS);
		world.setBlock(pos.east().north().above(), randNatureBlock(world.getRandom()), Block.UPDATE_CLIENTS);
		world.setBlock(pos.east(2).north().above(), randNatureBlock(world.getRandom()), Block.UPDATE_CLIENTS);

		world.setBlock(pos.west().above(), randNatureBlock(world.getRandom()), Block.UPDATE_CLIENTS);
		world.setBlock(pos.east(2).above(), randNatureBlock(world.getRandom()), Block.UPDATE_CLIENTS);

		world.setBlock(pos.west().south().above(), randNatureBlock(world.getRandom()), Block.UPDATE_CLIENTS);
		world.setBlock(pos.east(2).south().above(), randNatureBlock(world.getRandom()), Block.UPDATE_CLIENTS);

		world.setBlock(pos.west().south(2).above(), randNatureBlock(world.getRandom()), Block.UPDATE_CLIENTS);
		world.setBlock(pos.south(2).above(), randNatureBlock(world.getRandom()), Block.UPDATE_CLIENTS);
		world.setBlock(pos.east().south(2).above(), randNatureBlock(world.getRandom()), Block.UPDATE_CLIENTS);
		world.setBlock(pos.east(2).south(2).above(), randNatureBlock(world.getRandom()), Block.UPDATE_CLIENTS);

		return pos;
	}

	private static BlockState randNatureBlock(RandomSource random) {
		Optional<Block> optional = BuiltInRegistries.BLOCK
			.get(BlockTagGenerator.GENERATED_PORTAL_DECO)
			.flatMap(tag -> tag.getRandomElement(random))
			.map(Holder::value);
		return optional.map(Block::defaultBlockState).orElseGet(Blocks.SHORT_GRASS::defaultBlockState);
	}

	protected static boolean isOkayForPortal(ServerLevel world, BlockPos pos) {
		for (int potentialZ = 0; potentialZ < 4; potentialZ++) {
			for (int potentialX = 0; potentialX < 4; potentialX++) {
				for (int potentialY = 0; potentialY < 6; potentialY++) {
					BlockPos tPos = pos.offset(potentialX - 1, potentialY, potentialZ - 1);
					BlockState state = world.getBlockState(tPos);

					// all blocks mustn't be bedrock, end portal frame, etc.; and other conditions for layers >= 0
					if (state.is(BlockTags.FEATURES_CANNOT_REPLACE) || potentialY == 0 && !state.isSolid() && !state.liquid() || potentialY >= 1 && !state.canBeReplaced()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	protected static boolean isOkayForFallbackPortal(ServerLevel world, BlockPos pos) {
		for (int potentialZ = 0; potentialZ < 4; potentialZ++) {
			for (int potentialX = 0; potentialX < 4; potentialX++) {
				for (int potentialY = 0; potentialY < 6; potentialY++) {
					BlockPos tPos = pos.offset(potentialX - 1, potentialY, potentialZ - 1);
					BlockState state = world.getBlockState(tPos);

					// all blocks mustn't be bedrock, end portal frame, etc.;
					if (state.is(BlockTags.FEATURES_CANNOT_REPLACE) || potentialY >= 1 && !state.canBeReplaced()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	protected static TeleportTransition makePortalInfo(ServerLevel level, Entity entity, double x, double y, double z) {
		return makePortalInfo(level, entity, new Vec3(x, y, z));
	}

	protected static TeleportTransition makePortalInfo(ServerLevel level, Entity entity, Vec3 pos) {
		return new TeleportTransition(level, pos, Vec3.ZERO, entity.getYRot(), entity.getXRot(), TeleportTransition.PLACE_PORTAL_TICKET);
	}

	static class PortalPosition {
		public final BlockPos pos;
		long lastUpdateTime;

		PortalPosition(BlockPos pos, long time) {
			this.pos = pos;
			this.lastUpdateTime = time;
		}
	}
}
