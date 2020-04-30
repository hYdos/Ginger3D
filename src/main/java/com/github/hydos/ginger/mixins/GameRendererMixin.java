package com.github.hydos.ginger.mixins;

import com.github.hydos.ginger.engine.common.api.GingerEngine;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(at = @At("HEAD"), method = "tick")
    public void gingerTick(CallbackInfo ci) {
        GingerEngine.getInstance().timer.tick();
    }

}
