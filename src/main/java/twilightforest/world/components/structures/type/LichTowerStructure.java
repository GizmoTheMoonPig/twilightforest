package twilightforest.world.components.structures.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import org.jetbrains.annotations.Nullable;
import twilightforest.TwilightForestMod;
import twilightforest.data.tags.BiomeTagGenerator;
import twilightforest.init.TFEntities;
import twilightforest.init.TFMapDecorations;
import twilightforest.init.TFStructureTypes;
import twilightforest.util.jigsaw.JigsawPlaceContext;
import twilightforest.world.components.structures.lichtower.TowerMainComponent;
import twilightforest.world.components.structures.lichtowerrevamp.LichTowerFoyer;
import twilightforest.world.components.structures.lichtowerrevamp.LichYardBox;
import twilightforest.world.components.structures.util.ControlledSpawningStructure;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LichTowerStructure extends ControlledSpawningStructure {
	public static final MapCodec<LichTowerStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
		controlledSpawningCodec(instance).apply(instance, LichTowerStructure::new)
	);

	public LichTowerStructure(ControlledSpawningConfig controlledSpawningConfig, AdvancementLockConfig advancementLockConfig, HintConfig hintConfig, DecorationConfig decorationConfig, boolean centerInChunk, Optional<Holder<MapDecorationType>> structureIcon, StructureSettings structureSettings) {
		super(controlledSpawningConfig, advancementLockConfig, hintConfig, decorationConfig, centerInChunk, structureIcon, structureSettings);
	}

	@Override
	protected StructurePiece getFirstPiece(GenerationContext context, RandomSource random, ChunkPos chunkPos, int x, int y, int z) {
		return new TowerMainComponent(random, 0, x, y, z);
		// return makeFoyer(context, random, x, y + 1, z);
	}

	@Nullable
	private static LichTowerFoyer makeFoyer(GenerationContext context, RandomSource random, int x, int y, int z) {
		Direction direction = Rotation.getRandom(random).rotate(Direction.SOUTH);
		BlockPos placePos = new BlockPos(x, y, z).relative(direction, -7); // Shift to re-align yard with grass-clearing zone
		FrontAndTop oriented = FrontAndTop.fromFrontAndTop(Direction.UP, direction);

		JigsawPlaceContext placeContext = JigsawPlaceContext.pickPlaceableJunction(placePos, BlockPos.ZERO, oriented, context.structureTemplateManager(), TwilightForestMod.prefix("lich_tower/tower_foyer"), "twilightforest:lich_tower/vestibule", random);

		return placeContext == null ? null : new LichTowerFoyer(context.structureTemplateManager(), placeContext, random.nextBoolean(), random.nextBoolean());
	}

	@Override
	protected void generateFromStartingPiece(StructurePiece startingPiece, GenerationContext context, StructurePiecesBuilder structurePiecesBuilder) {
		super.generateFromStartingPiece(startingPiece, context, structurePiecesBuilder);

		if (startingPiece instanceof LichTowerFoyer foyerPiece) {
			LichYardBox.beginYard(foyerPiece, context, structurePiecesBuilder);
		}
	}

	@Override
	public StructureType<?> type() {
		return TFStructureTypes.LICH_TOWER.get();
	}

	public static LichTowerStructure buildLichTowerConfig(BootstrapContext<Structure> context) {
		return new LichTowerStructure(
			ControlledSpawningConfig.firstIndexMonsters(
				new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 10, 1, 2),
				new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 10, 1, 2),
				new MobSpawnSettings.SpawnerData(EntityType.CREEPER, 1, 1, 1),
				new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 1, 1, 2),
				new MobSpawnSettings.SpawnerData(TFEntities.DEATH_TOME.get(), 10, 2, 3),
				new MobSpawnSettings.SpawnerData(EntityType.WITCH, 1, 1, 1)
			),
			new AdvancementLockConfig(List.of(TwilightForestMod.prefix("progress_naga"))),
			new HintConfig(HintConfig.book("lichtower", 4), TFEntities.KOBOLD.get()),
			new DecorationConfig(2.5f, false, true, false, true),
			true, Optional.of(TFMapDecorations.LICH_TOWER),
			new StructureSettings(
				context.lookup(Registries.BIOME).getOrThrow(BiomeTagGenerator.VALID_LICH_TOWER_BIOMES),
				Arrays.stream(MobCategory.values()).collect(Collectors.toMap(category -> category, category -> new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create()))), // Landmarks have Controlled Mob spawning
				GenerationStep.Decoration.SURFACE_STRUCTURES,
				TerrainAdjustment.BEARD_THIN
			)
		);
	}
}
