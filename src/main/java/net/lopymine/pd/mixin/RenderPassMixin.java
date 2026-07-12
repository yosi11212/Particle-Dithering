package net.lopymine.pd.mixin;

//? if >=26.1 {


import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.*;
import net.irisshaders.iris.pipeline.*;
import net.lopymine.dl.compat.IrisAPI;
import net.lopymine.dl.dithering.vanilla.*;
import net.lopymine.dl.thing.RenderingMarker;
import net.lopymine.dl.utils.IrisDitheringMarker;
import net.lopymine.pd.config.ParticleDitheringConfig;
import net.lopymine.pd.thing.ParticleRenderingMarker;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPass.class)
public class RenderPassMixin {

	@Shadow
	@Final
	private RenderPassBackend backend;

	@Unique
	private boolean particleDithering$addedDitheringUniform = false;

	@Inject(at = @At("HEAD"), method = "setPipeline", cancellable = true)
	private void swapPipeline(RenderPipeline pipeline, CallbackInfo ci) {
		if (!ParticleRenderingMarker.RENDERING_PARTICLE.get().isEnabled()) {
			return;
		}
		boolean shaders = IrisAPI.isShaderPackInUse();
		boolean active = ParticleDitheringConfig.getInstance().isModEnabled();
		((IrisDitheringMarker) pipeline).ditheringLib$setDithering(active && shaders);
		if (!active) {
			return;
		}
		if (shaders) {
			return;
		}
		RenderPipeline ditheringPipeline = VanillaDitheringShaderManager.getDitheringPipeline(pipeline);
		if (ditheringPipeline != null) {
			this.backend.setPipeline(ditheringPipeline);
			ci.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "setUniform(Ljava/lang/String;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V")
	private void addDitheringUniform(String name, GpuBufferSlice value, CallbackInfo ci) {
		if (!ParticleDitheringConfig.getInstance().isModEnabled()) {
			return;
		}
		if (!ParticleRenderingMarker.RENDERING_PARTICLE.get().isEnabled()) {
			return;
		}
		if (this.particleDithering$addedDitheringUniform) {
			return;
		}
		if (!name.equals("DynamicTransforms")) {
			return;
		}
		this.backend.setUniform("DitheringLibData", VanillaDitheringDataBuffer.getBuffer());
		this.particleDithering$addedDitheringUniform = true;
	}

}

//?}
