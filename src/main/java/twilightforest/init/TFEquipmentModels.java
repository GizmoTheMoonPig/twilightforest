package twilightforest.init;

import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import twilightforest.TwilightForestMod;

import java.util.function.BiConsumer;

public class TFEquipmentModels {

	public static final ResourceKey<EquipmentAsset> IRONWOOD = createId("ironwood");
	public static final ResourceKey<EquipmentAsset> STEELEAF = createId("steeleaf");
	public static final ResourceKey<EquipmentAsset> NAGA = createId("naga");
	public static final ResourceKey<EquipmentAsset> FIERY = createId("fiery");
	public static final ResourceKey<EquipmentAsset> KNIGHTMETAL = createId("knightmetal");
	public static final ResourceKey<EquipmentAsset> PHANTOM = createId("phantom");
	public static final ResourceKey<EquipmentAsset> ARCTIC = createId("arctic");
	public static final ResourceKey<EquipmentAsset> YETI = createId("yeti");

	static ResourceKey<EquipmentAsset> createId(String name) {
		return ResourceKey.create(EquipmentAssets.ROOT_ID, TwilightForestMod.prefix(name));
	}

	public static void bootstrap(BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> consumer) {
		consumer.accept(IRONWOOD, EquipmentClientInfo.builder().addHumanoidLayers(TwilightForestMod.prefix("ironwood"), false).build());
		consumer.accept(STEELEAF, EquipmentClientInfo.builder().addHumanoidLayers(TwilightForestMod.prefix("steeleaf"), false).build());
		consumer.accept(NAGA, EquipmentClientInfo.builder().addHumanoidLayers(TwilightForestMod.prefix("naga"), false).build());
		consumer.accept(FIERY, EquipmentClientInfo.builder().addHumanoidLayers(TwilightForestMod.prefix("fiery"), false).build());
		consumer.accept(KNIGHTMETAL, EquipmentClientInfo.builder().addHumanoidLayers(TwilightForestMod.prefix("knightmetal"), false).build());
		consumer.accept(PHANTOM, EquipmentClientInfo.builder().addHumanoidLayers(TwilightForestMod.prefix("phantom"), false).build());
		consumer.accept(ARCTIC, EquipmentClientInfo.builder()
			.addHumanoidLayers(TwilightForestMod.prefix("arctic_dyed"), true)
			.addHumanoidLayers(TwilightForestMod.prefix("arctic_overlay"), false)
			.build());
		consumer.accept(YETI, EquipmentClientInfo.builder().addHumanoidLayers(TwilightForestMod.prefix("yeti"), false).build());
	}
}
