package net.lopymine.pd.mixin;

//? if <=26.1 {
/*import com.llamalad7.mixinextras.sugar.Local;
import net.lopymine.dl.dithering.DitheringData;
import net.lopymine.dl.dithering.vanilla.VanillaDitheringDataBuffer;
import net.lopymine.pd.config.ParticleDitheringConfig;
import net.lopymine.pd.dithering.DitheringManager;
import net.lopymine.pd.thing.ParticleRenderingMarker;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.feature.ParticleFeatureRenderer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(ParticleFeatureRenderer.class)
public class ParticleFeatureRendererMixin {

	@Unique
	@Nullable
	private DitheringData backup;
	@Unique
	private boolean restore;

	@Inject(at = @At("HEAD"), method = "render")
	private void markParticle1(CallbackInfo ci, @Local(argsOnly = true) SubmitNodeCollection collection) {
		if (collection.getParticleGroupRenderers().isEmpty()) {
			return;
		}
		this.restore = false;
		ParticleRenderingMarker.RENDERING_PARTICLE.get().setEnabled(true);
		if (ParticleDitheringConfig.getInstance().isModEnabled()) {
			this.backup  = DitheringData.CURRENT_DITHERING_DATA.get();
			this.restore = true;
			DitheringManager.updateBuffer();
		}
	}

	@Inject(at = @At("RETURN"), method = "render")
	private void markParticle2(CallbackInfo ci, @Local(argsOnly = true) SubmitNodeCollection collection) {
		if (collection.getParticleGroupRenderers().isEmpty()) {
			return;
		}
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
*///?}
