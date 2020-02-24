package com.github.hydos.ginger;

import java.util.*;

import com.github.hydos.ginger.engine.api.*;
import com.github.hydos.ginger.engine.cameras.Camera;
import com.github.hydos.ginger.engine.elements.GuiTexture;
import com.github.hydos.ginger.engine.elements.buttons.TextureButton;
import com.github.hydos.ginger.engine.elements.objects.*;
import com.github.hydos.ginger.engine.font.*;
import com.github.hydos.ginger.engine.io.Window;
import com.github.hydos.ginger.engine.math.vectors.*;
import com.github.hydos.ginger.engine.obj.ModelLoader;
import com.github.hydos.ginger.engine.obj.shapes.StaticCube;
import com.github.hydos.ginger.engine.particle.*;
import com.github.hydos.ginger.engine.render.MasterRenderer;
import com.github.hydos.ginger.engine.render.models.TexturedModel;
import com.github.hydos.ginger.engine.utils.Loader;
import com.github.hydos.ginger.main.GingerMain;
import com.github.hydos.ginger.main.settings.Constants;
import com.github.hydos.litecraft.Block;

public class Example extends Game{
	
	private Ginger ginger3D;
	
	private boolean isInWorld = false;
	
	private ParticleSystem system;
	
	public Example() {
		super();
		Constants.movementSpeed = 0.000005f;
		Constants.turnSpeed = 0.00002f;
		Constants.gravity = -0.000000000005f;
		Constants.jumpPower = 0.000005f;
		
		Window.create(1200, 800, "LiteCraft", 60);
		
		GingerMain.init();
		
        Window.setBackgroundColour(0.2f, 0.2f, 0.8f);
        
		//TODO: block register class to register blockzz
		//TODO: could also probally pull the mesh from 1 place to lower memory usage in the future
		TexturedModel dirtModel = ModelLoader.loadGenericCube("block/cubes/soil/dirt.png");
		TexturedModel grassModel = ModelLoader.loadGenericCube("block/cubes/soil/gravel.png");
		
        
        
        
        StaticCube.scaleCube(6);
		Player player = new Player(dirtModel, new Vector3f(0,0,-3),0,180f,0, new Vector3f(0.2f, 0.2f, 0.2f));
		Camera camera = new Camera(new Vector3f(0,0.1f,0), player);
		ginger3D = new Ginger();
		
		data = new GameData(player, camera);
		data.handleGuis = false;
		ginger3D.setup(new MasterRenderer(camera), this);
		
		
		float blockSpacing = 1f;
		float blockLineSpacing = 1f;
		float blockUpwardsSpacing = 1f;
		
		//TODO: rename entity class to object class because not just entities
		List<Block> chunk = new ArrayList<Block>();
		TexturedModel activeModel = dirtModel;
		for(int k = 0; k<8;k++) {
			if(k == 7) {
				activeModel = grassModel;
			}
			for(int i = 0; i<8;i++) {
				for(int j = 0; j<8;j++) {
					chunk.add(new Block(activeModel, new Vector3f(blockLineSpacing*i, blockUpwardsSpacing*k, blockSpacing*j)));
				}
			}
		}
		
		//add chunk to "entity" render list
		for(Block b: chunk) {
			data.entities.add(b);
		}

		
		
        FontType font = new FontType(Loader.loadFontAtlas("candara.png"), "candara.fnt");
        
        ginger3D.setGlobalFont(font);
        
        ginger3D.registerText("LiteCraft", 3, new Vector2f(0,0), 1f, true, "PLAYBUTTON");

        
		Light sun = new Light(new Vector3f(100,105,-100), new Vector3f(1.3f, 1.3f, 1.3f), new Vector3f(0.0001f, 0.0001f, 0.0001f));
		data.lights.add(sun);

		data.entities.add(player);	
		
		system = setupParticles();
		
		
		TextureButton playButton = ginger3D.registerButton("/textures/guis/purpur.png", new Vector2f(0, 0), new Vector2f(0.25f, 0.1f));
		playButton.show(data.guis);
		
		GuiTexture title = new GuiTexture(Loader.loadTextureDirectly("/textures/guis/title.png"), new Vector2f(0, 0.8F), new Vector2f(0.25f, 0.1f));
		data.guis.add(title);
		
		
		//start the game loop
		ginger3D.startGame();
	}


	private ParticleSystem setupParticles() {
		ParticleTexture particleTexture = new ParticleTexture(Loader.loadTexture("particles/smoke.png"), 8);
		

		system = new ParticleSystem(particleTexture, 100, 10f, 0.3f, 4, 3f);
		system.randomizeRotation();
		system.setDirection(new Vector3f(0,0.001f,0), 0.00001f);
		system.setLifeError(0);
		system.setSpeedError(0);
		system.setScaleError(1f);
		return system;
		
	}


	@Override
	public void update() {
		ginger3D.update(data);
		
		ginger3D.masterRenderer.renderShadowMap(data.entities, data.lights.get(0));
		
		data.camera.move();
		data.player.move(null);
		
		system.generateParticles(new Vector3f(0,-2,0));
		
		if(isInWorld) {
			ginger3D.renderWithoutTerrain(this);
		}
		
		GUIText text = ginger3D.gingerRegister.texts.get(0);
		
		TextureButton playButton = ginger3D.gingerRegister.guiButtons.get(0);
		
		boolean isClicked = playButton.isClicked();
		
		playButton.update();
		
		if(text == null) {System.out.println("Yes is null crusty foss time");}
		
		text.setText(isClicked + "");
		ginger3D.renderOverlays(this);
		
		if(isClicked) {
			Window.lockMouse();
			playButton.hide(data.guis);
			isInWorld = true;
		}
		
		ginger3D.postRender();
	}


	@Override
	public void exit() {
		ginger3D.cleanup();
	}
}
