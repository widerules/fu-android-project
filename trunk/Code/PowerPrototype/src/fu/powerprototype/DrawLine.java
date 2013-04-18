package fu.powerprototype;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Line;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.ui.activity.BaseGameActivity;

public class DrawLine extends BaseGameActivity {

	private Camera mCamera;
	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;
	
	@Override
	public Engine onLoadEngine() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return mEngine = new Engine( new EngineOptions(true, ScreenOrientation.LANDSCAPE,new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
		
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		Scene scene = new Scene();
		float x1,x2,y1,y2;
		x1= 0;
		x2 = CAMERA_WIDTH;
		y1 = 0;
		y2 = CAMERA_HEIGHT;
		Line line;
		line = new Line(x1, x2, y1, y2);
		line.setColor(1, 0, 0);
		scene.attachChild(line);
		return null;
		
	}

	@Override
	public void onLoadComplete() {
		
		
	}

}
