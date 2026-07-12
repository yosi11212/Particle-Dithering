package net.lopymine.pd.mixin;

//? if <=1.21.11 {

import com.mojang.blaze3d.buffers.*;
import com.mojang.blaze3d.opengl.GlRenderPass;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import net.lopymine.dl.compat.IrisAPI;
import net.lopymine.dl.dithering.vanilla.*;
import net.lopymine.dl.utils.IrisDitheringMarker;
import net.lopymine.pd.config.ParticleDitheringConfig;
import net.lopymine.pd.thing.ParticleRenderingMarker;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlRenderPass.class)
public abstract class GlRenderPassMixin {

	@Shadow public abstract void setUniform(String name, GpuBuffer value);

	@Unique
	private boolean particleDithering$addedDitheringUniform = false;

	@Unique
	private boolean particleDithering$Skip = false;

	@ModifyVariable(at = @At("HEAD"), method = "setPipeline", ordinal = 0, argsOnly = true)
	private RenderPipeline swapPipeline(RenderPipeline original) {
		if (!ParticleRenderingMarker.RENDERING_PARTICLE.get().isEnabled()) {
			return original;
		}
		boolean shaders = IrisAPI.isShaderPackInUse();
		boolean active = ParticleDitheringConfig.getInstance().isModEnabled();
		((IrisDitheringMarker) original).ditheringLib$setDithering(active && shaders);
		if (!active) {
			return original;
		}
		if (shaders) {
			return original;
		}
		RenderPipeline ditheringPipeline = VanillaDitheringShaderManager.getDitheringPipeline(original);
		if (ditheringPipeline != null) {
			return ditheringPipeline;
		}
		return original;
	}

	@Inject(at = @At("HEAD"), method = "setUniform(Ljava/lang/String;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V")
	private void addDitheringUniform(String name, GpuBufferSlice value, CallbackInfo ci) {
		if (this.particleDithering$Skip) {
			return;
		}
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
		this.particleDithering$Skip = true;
		this.setUniform("DitheringLibData", VanillaDitheringDataBuffer.getBuffer());
		this.particleDithering$Skip = false;
		this.particleDithering$addedDitheringUniform = true;
	}

}
//?}
