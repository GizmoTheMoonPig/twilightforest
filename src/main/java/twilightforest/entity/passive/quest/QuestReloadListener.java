package twilightforest.entity.passive.quest;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.ApiStatus;
import twilightforest.TwilightForestMod;
import twilightforest.entity.passive.quest.ram.QuestingRamContext;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class QuestReloadListener extends SimpleJsonResourceReloadListener<JsonElement> {

	private QuestingRamContext ram = QuestingRamContext.FALLBACK;

	public QuestReloadListener() {
		super(ExtraCodecs.JSON, FileToIdConverter.json("twilight/quests"));
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
		AtomicBoolean found = new AtomicBoolean(false);
		for (var entry : object.entrySet()) {
			if (entry.getKey().getPath().equals("questing_ram")) {
				QuestingRamContext.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).mapOrElse(context -> {
					this.ram = context;
					TwilightForestMod.LOGGER.debug("Questing Ram quest set by mod {}", entry.getKey().getNamespace());
					found.set(true);
					return DataResult.success(context);
				}, error -> {
					TwilightForestMod.LOGGER.warn("Invalid Questing Ram quest added by mod {}: {}", entry.getKey().getNamespace(), error.message());
					return error;
				});
			}
		}

		if (!found.get()) {
			TwilightForestMod.LOGGER.error("Questing Ram quest file not found. Defaulting to fallback");
			this.ram = QuestingRamContext.FALLBACK;
		}
	}

	public QuestingRamContext getQuestingRam() {
		return this.ram;
	}

	@ApiStatus.Internal
	public void setQuestsFromPacket(QuestingRamContext ram) {
		this.ram = ram;
	}
}
