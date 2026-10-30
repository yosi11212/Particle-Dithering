package net.lopymine.pd.mixin;

//? if >=26.2 {
//? if >=26.3 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.*;
import com.mojang.renderpearl.frontend.FrontendRenderPipeline;
import net.lopymine.dl.compat.IrisAPI;
import net.lopymine.dl.dithering.vanilla.VanillaDitheringShaderManager;
import net.lopymine.dl.utils.IrisDitheringMarker;
*///?}
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

	// Since 26.3 RenderPass is an interface taking compiled pipelines, so swap the pipeline before it gets compiled
	//? if >=26.3 {
	/*// Buffers can't be written inside the render pass, so write the particle dithering data while preparing
	@Inject(at = @At("HEAD"), method = "prepareGroup")
	private void stageDitheringData(CallbackInfo ci) {
		if (ParticleDitheringConfig.getInstance().isModEnabled()) {
			VanillaDitheringDataBuffer.stage(DitheringManager.createData());
		}
	}

	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/renderpearl/api/commands/RenderPass;setUniform(Ljava/lang/String;Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;)V",
					shift = At.Shift.AFTER
			),
			method = "executeGroup"
	)
	private void addDitheringUniform(CallbackInfo ci, @Local(argsOnly = true) RenderPass pass) {
		if (!ParticleDitheringConfig.getInstance().isModEnabled()) {
			return;
		}
		GpuBufferSlice slice = VanillaDitheringDataBuffer.getSlice();
		if (slice != null) {
			pass.setUniform("DitheringLibData", slice);
		}
	}

	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/RenderSystem;getCompiledPipeline(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;)Lcom/mojang/renderpearl/api/pipeline/CompiledRenderPipeline;"
			),
			method = "drawLayers"
	)
	private static CompiledRenderPipeline swapPipeline(RenderPipeline pipeline, Operation<CompiledRenderPipeline> original) {
		boolean shaders = IrisAPI.isShaderPackInUse();
		boolean active = ParticleDitheringConfig.getInstance().isModEnabled();
		((IrisDitheringMarker) pipeline).ditheringLib$setDithering(active && shaders);

		RenderPipeline chosen = pipeline;
		if (active && !shaders) {
			RenderPipeline ditheringPipeline = VanillaDitheringShaderManager.getDitheringPipeline(pipeline);
			if (ditheringPipeline != null) {
				chosen = ditheringPipeline;
			}
		}

		CompiledRenderPipeline compiled = original.call(chosen);
		// The GL pipeline no longer references its RenderPipeline, so pass the Iris dithering mark to it directly
		if (compiled instanceof FrontendRenderPipeline frontend && frontend.backendRenderPipeline() instanceof IrisDitheringMarker marker) {
			marker.ditheringLib$setDithering(active && shaders);
		}
		return compiled;
	}
	*///?}
}
//?}
