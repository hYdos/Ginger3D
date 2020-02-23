package com.github.hydos.ginger.engine.api;

import com.github.hydos.ginger.engine.font.TextMaster;
import com.github.hydos.ginger.engine.io.Window;
import com.github.hydos.ginger.engine.particle.ParticleMaster;
import com.github.hydos.ginger.engine.postprocessing.*;
import com.github.hydos.ginger.engine.render.MasterRenderer;
import com.github.hydos.ginger.engine.render.tools.MousePicker;
import com.github.hydos.ginger.engine.utils.Loader;
import com.github.hydos.ginger.main.GingerMain;

public class Ginger {
	
	public MasterRenderer masterRenderer;
	
	MousePicker picker;
	
	public Fbo contrastFbo;
	
	public void setup(MasterRenderer masterRenderer, GameData data) {
		contrastFbo = new Fbo();
		this.masterRenderer = masterRenderer;
		picker = new MousePicker(data.camera, masterRenderer.getProjectionMatrix(), null);
		PostProcessing.init();
        ParticleMaster.init(masterRenderer.getProjectionMatrix());
	}
	
	public void update(GameData data) {
		data.camera.move();
		data.player.move(null);
		Window.update();
		GingerMain.update();
		picker.update();
		ParticleMaster.update(data.camera);
	}
	
	public void render(Game game) {
		GingerMain.preRenderScene(masterRenderer);
		contrastFbo.bindFBO();
		masterRenderer.renderScene(game.data.entities, game.data.normalMapEntities, game.data.flatTerrains, game.data.lights, game.data.camera, game.data.clippingPlane);
		ParticleMaster.renderParticles(game.data.camera);
		contrastFbo.unbindFBO();
		PostProcessing.doPostProcessing(contrastFbo.colorTexture);
		if(game.data.handleGuis) {
			renderOverlays(game);
		}
	}
	
	public void renderWithoutTerrain(Game game) {
		GingerMain.preRenderScene(masterRenderer);
		contrastFbo.bindFBO();
		masterRenderer.renderSceneNoTerrain(game.data.entities, game.data.normalMapEntities, game.data.lights, game.data.camera, game.data.clippingPlane);
		ParticleMaster.renderParticles(game.data.camera);
		contrastFbo.unbindFBO();
		PostProcessing.doPostProcessing(contrastFbo.colorTexture);
		if(game.data.handleGuis) {
			renderOverlays(game);
		}
	}
	
	public void renderOverlays(Game game) {
		masterRenderer.renderGuis(game.data.guis);
		TextMaster.render();
	}
	
	public void postRender() {
		Window.swapBuffers();
	}
	
	public void cleanup() {
		Window.stop();
		PostProcessing.cleanUp();
		ParticleMaster.cleanUp();
		masterRenderer.cleanUp();
		TextMaster.cleanUp();
		Loader.cleanUp();
	}
	
}
