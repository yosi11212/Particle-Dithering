package net.lopymine.pd.dithering;

import java.util.*;
import net.lopymine.dl.api.DitheringLibAPI;
import net.lopymine.dl.dithering.DitheringData;
import net.lopymine.dl.dithering.vanilla.VanillaDitheringDataBuffer;
import net.lopymine.pd.config.ParticleDitheringConfig;
import net.lopymine.pd.config.distance.DitheringDataConfig;
import net.minecraft.resources.Identifier;

public class DitheringManager {

	private static final Set<Identifier> VANILLA_TARGETS = Set.of(
			Identifier.fromNamespaceAndPath("minecraft", "core/particle")
	);

	private static final Set<String> IRIS_TARGETS = Set.of("particles", "particlestrans");

	public static void register() {
		DitheringLibAPI api = DitheringLibAPI.getInstance();
		api.getVanillaTargets().addAll(VANILLA_TARGETS);
		api.getIrisTargets().addAll(IRIS_TARGETS);
	}

	public static void updateBuffer() {
		DitheringData data = createData();
		data.push();
		VanillaDitheringDataBuffer.update(data);
	}

	public static DitheringData createData() {
		DitheringData data = DitheringLibAPI.getInstance().getData();
		DitheringDataConfig config = ParticleDitheringConfig.getInstance().getDitheringDataConfig();
		data.setFar((float) config.getFar());
		data.setNear((float) config.getNear());
		data.setMinValue((float) config.getMinValue());
		data.setFixedValue(1.0F);
		data.setPixelSize((float) config.getPixelSize());
		return data;
	}
}
