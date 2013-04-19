package fu.powerprototype;



import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.LoopEntityModifier;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.anddev.andengine.entity.modifier.PathModifier.Path;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.modifier.ease.EaseSineInOut;

import android.media.MediaPlayer;

public class PowerPrototypeActivity extends BaseGameActivity{

	private Camera mCamera;
	private Texture mTexture;
	private Texture mTexture2;
	private Texture mTexture3;
	
	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;
	
	private TextureRegion mBulbOnTextureRegion;
	private TextureRegion mBulbOffTextureRegion;
	private TextureRegion mSocketTextureRegion;
	private TextureRegion mPlugTextureRegion;
	private TextureRegion mShurikenTextureRegion;
	
	//sound used in game
	private MediaPlayer soundOnCollision;
	private MediaPlayer backgroundMusic;
	
	@Override
	public Engine onLoadEngine() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
				new RatioResolutionPolicy(CAMERA_WIDTH,CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
		TextureRegionFactory.setAssetBasePath("gfx/");
		
		this.mTexture = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mTexture2 = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mTexture3 = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mBulbOnTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, this, "BulbOn.png", 0, 0);
		this.mBulbOffTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, this, "BulbOff.png", 128, 0);
		this.mSocketTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture2, this, "socket.png", 0, 0);
		this.mPlugTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture2, this, "plug.png", 128, 0);
		this.mShurikenTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture3, this, "shuriken.png",0,0);			
		
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
		this.mEngine.getTextureManager().loadTexture(this.mTexture2);
		this.mEngine.getTextureManager().loadTexture(mTexture3);				
		
		soundOnCollision = MediaPlayer.create(this, R.raw.explosion);
		backgroundMusic = MediaPlayer.create(this, R.raw.nhacnen);		
	}

	@Override
	public Scene onLoadScene() {
		Thread musicThread = new Thread(new Runnable() {
	        public void run() {
	            backgroundMusic.start();
	        }
	    });		
		
		this.mEngine.registerUpdateHandler(new FPSLogger());
		
		final Scene scene = new Scene();
		scene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));		
		final Sprite bulbOn = new Sprite(0, 0, this.mBulbOnTextureRegion);
		final Sprite bulbOff = new Sprite(0, 0, this.mBulbOffTextureRegion);
		final Sprite shuriken = new Sprite(200,200, this.mShurikenTextureRegion);
		final Sprite socket = new Sprite(CAMERA_WIDTH - 128, 130, this.mSocketTextureRegion);
		final Sprite plug = new Sprite(0, 130, this.mPlugTextureRegion) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, 
					pSceneTouchEvent.getY() - this.getHeight() / 2);
				if(pSceneTouchEvent.isActionUp()){
					this.setPosition(0, 130);
				}
				return true;
			}			
		};				
		
//		final Sprite face = new Sprite(centerX, centerY, this.mFaceTextureRegion) {
//			@Override
//			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
//				this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
//				return true;
//			}
//		};
		
		final PhysicsHandler physicsHandler = new PhysicsHandler(plug);
		plug.registerUpdateHandler(physicsHandler);
		
		scene.attachChild(bulbOn);
		scene.attachChild(bulbOff);
		scene.attachChild(socket);
		scene.attachChild(plug);
		
		
		scene.registerTouchArea(plug);
		scene.setTouchAreaBindingEnabled(true);
		
		//moving shuriken		
		final Path shurikenPath = new Path(3).to(shuriken.getX(), shuriken.getY()).to(shuriken.getX(), shuriken.getY()-60).to(shuriken.getX(), shuriken.getY());
		shuriken.registerEntityModifier(new LoopEntityModifier(new PathModifier(3, shurikenPath, null, new IPathModifierListener() {
			
			@Override
			public void onWaypointPassed(PathModifier pPathModifier, IEntity pEntity,
					int pWaypointIndex) {
				// TODO Auto-generated method stub
				
			}
		}, EaseSineInOut.getInstance())));		
		scene.attachChild(shuriken);
		musicThread.start();
		
		
		scene.registerUpdateHandler(new IUpdateHandler() {

			@Override
			public void onUpdate(float pSecondsElapsed) {
				if(plug.collidesWith(socket)) {
					bulbOff.setVisible(false);
					bulbOn.setVisible(true);
				} else {
					bulbOff.setVisible(true);
					bulbOn.setVisible(false);
				}
				
				if(collidesWithCircle(plug,shuriken)) {
					scene.setTouchAreaBindingEnabled(false);
					plug.setPosition(0, 130);
										
					soundOnCollision.start();
					scene.setTouchAreaBindingEnabled(true);
				}				
				
				if(!backgroundMusic.isPlaying()){
					backgroundMusic.start();
				}
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
			
		});
		return scene;
	}

	@Override
	public void onPause()
	{
	   super.onPause();	   
	   this.finish();
	}

	@Override
	public void onStop()
	{
		super.onStop();
		if(backgroundMusic.isPlaying())
			backgroundMusic.stop();
		else
			return;
	}

	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub
		
	}
	
	boolean collidesWithCircle(Sprite object, Sprite circle) {
	    float x1 = object.getX();
	    float y1 = object.getY();
	    float x2 = circle.getX();
	    float y2 = circle.getY();
	    double a = x1 - x2;
	    double b = y1 - y2;
	    double c = (a * a) + (b * b);

	    if (c <= object.getWidth()*object.getWidth())
	        return true;
	    else return false;
	}

}
