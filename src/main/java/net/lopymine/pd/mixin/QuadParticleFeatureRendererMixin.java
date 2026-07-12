package net.lopymine.pd.mixin;

//? if >=26.2 {
import java.util.List;
import net.lopymine.dl.dithering.DitheringData;
import net.lopymine.dl.dithering.vanilla.VanillaDitheringDataBuffer;
import net.lopymine.pd.config.ParticleDitheringConfig;
import net.lopymine.pd.dithering.DitheringManager;
import net.lopymine.pd.thing.ParticleRenderingMarker;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.*;
import net.minecraft.client.renderer.feature.QuadParticleFeatureRenderer.Submit;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(QuadParticleFeatureRenderer.class)
public class QuadParticleFeatureRendererMixin {

	@Unique
	@Nullable
	private DitheringData backup;
	@Unique
	private boolean restore;

	@Inject(at = @At("HEAD"), method = "executeGroup")
	private void markParticle1(CallbackInfo ci) {
		this.restore = false;
		ParticleRenderingMarker.RENDERING_PARTICLE.get().setEnabled(true);
		if (ParticleDitheringConfig.getInstance().isModEnabled()) {
			this.backup  = DitheringData.CURRENT_DITHERING_DATA.get();
			this.restore = true;
			DitheringManager.updateBuffer();
		}
	}

	@Inject(at = @At("RETURN"), method = "executeGroup")
	private void markParticle2(CallbackInfo ci) {
		ParticleRenderingMarker.RENDERING_PARTICLE.get().setEnabled(false);
		if (this.restore) {
			this.restore = false;
			DitheringData.CURRENT_DITHERING_DATA.set(this.backup);
			if (this.backup != null) {
				VanillaDitheringDataBuffer.update(this.backup);
			}
		}
	}
}
//?}
