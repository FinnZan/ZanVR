package finnzan.zanvr;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import finnzan.zanvr.mesh.Line;

public class SceneRenderer implements Renderer {
	private Context mContext;

	private ArrayList<Renderable> mRenderables = new ArrayList<Renderable>();

	private TextureStore mTextureStore = null;

	private  int mWidth = 0;
	private  int mHeight = 0;

	private float mAngle = 0;

	private Renderable mCockpit = null;


	public SceneRenderer(Context context) {
		mContext = context;

		try {

			mTextureStore = new TextureStore();

			Log.d("GL", "Load resources...");
			AssetManager assets = context.getAssets();

			mCockpit = new Renderable(assets.open("F1.ims"), "F1.png");
			mCockpit.Scale = 1;

			Renderable f1 = new Renderable(assets.open("F1.ims"), "F1.png");
			f1.Scale = 1;
			mRenderables.add(f1);

			Renderable room = new Renderable(assets.open("room.ims"), "room.png");
			room.Scale = 1;
			room.IsCullFace = false;
			mRenderables.add(room);

			Renderable beach_house = new Renderable(assets.open("beach_house.ims"), "beach_house.png");
			beach_house.IsCullFace = false;
			beach_house.Scale = 2;
			beach_house.Translation[0] = -50;
			beach_house.Translation[2] = 500;
			mRenderables.add(beach_house);

		} catch (Exception ex) {
			Log.d("GL", ex.toString());
		}
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		try {
			Log.d("GL", "Surface created ...");

			gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			gl.glShadeModel(GL10.GL_SMOOTH);
			gl.glClearDepthf(1.0f);
			gl.glEnable(GL10.GL_DEPTH_TEST);
			gl.glDepthFunc(GL10.GL_LEQUAL);
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

			gl.glFrontFace(GL10.GL_CCW);

			FloatBuffer ambient = FloatBuffer.allocate(4);
			ambient.put(0.5f);
			ambient.put(0.5f);
			ambient.put(0.5f);
			ambient.put(1.0f);

			FloatBuffer position = FloatBuffer.allocate(4);
			position.put(100f);
			position.put(100f);
			position.put(100f);
			position.put(1f);

			float[] mycolor = {0.8f, 0.7f, 0.6f, 1.0f};
			gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_AMBIENT_AND_DIFFUSE, mycolor, 0);
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glEnable(GL10.GL_ALPHA_TEST);
			gl.glAlphaFunc(GL10.GL_GREATER, 0.1f);
			gl.glEnable(GL10.GL_BLEND);
			gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);

			float[] fogColor = { 0, 0, 0, 0 };
			gl.glFogx(GL10.GL_FOG_MODE, GL10.GL_EXP2);
			gl.glFogfv(GL10.GL_FOG_COLOR, FloatBuffer.wrap(fogColor));
			gl.glHint(GL10.GL_FOG_HINT, GL10.GL_LINE_SMOOTH_HINT);
			gl.glFogf(GL10.GL_FOG_DENSITY, 0.01f);
			gl.glFogf(GL10.GL_FOG_START, 0.0f);
			gl.glFogf(GL10.GL_FOG_END, 1.0f);
			// gl.glEnable(GL10.GL_FOG);
		}catch(Exception ex) {
			Log.d("GL", ex.toString());
		}
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		mAngle += 0.25;

		float[] pos = Global.Observer.getPosition();
		float[] epos = Global.Observer.getEyePosition();
		float[] eye = Global.Observer.getEyeVect();

		float cX = pos[0];
		float cY = pos[1];
		float cZ = pos[2];

		float eX = eye[0];
		float eY = eye[1];
		float eZ = eye[2];

		float epX = epos[0];
		float epY = epos[1];
		float epZ = epos[2];

		float br = -(float)(Global.Observer.getBodyRotation()[1]/Math.PI * 180);

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		if(Global.IS_VR_MODE) {

			//left
			gl.glViewport(0, 0, mWidth / 2 - 1, mHeight);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			GLU.gluPerspective(gl, 60.0f, (float) mWidth / 2 / (float) mHeight, Global.NEAR_CLIP, Global.FAR_CLIP);

			drawScene(gl, Global.EYE_SPACING, cX, cY, cZ, eX, eY, eZ, epX, epY, epZ,br);

			//right
			gl.glViewport(mWidth / 2 + 1, 0, mWidth / 2 - 1, mHeight);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			GLU.gluPerspective(gl, 60.0f, (float) mWidth / 2 / (float) mHeight, Global.NEAR_CLIP, Global.FAR_CLIP);

			drawScene(gl, -Global.EYE_SPACING, cX, cY, cZ, eX, eY, eZ, epX, epY, epZ,br);

		}else{
			gl.glViewport(0, 0, mWidth, mHeight);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			GLU.gluPerspective(gl, 45.0f, (float) mWidth / (float) mHeight, Global.NEAR_CLIP, Global.FAR_CLIP);

			drawScene(gl, 0, cX, cY, cZ, eX, eY, eZ, epX, epY, epZ,br);
		}
	}

	private void drawScene(GL10 gl, float shift, float cX, float cY, float cZ, float eX, float eY, float eZ, float epX, float epY, float epZ, float br) {
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glTranslatef(shift, 0, 0);

		gl.glTranslatef(-epX, -epY, -epZ);

		GLU.gluLookAt(gl, 0, 0, 0, eX, eY, eZ, 0f, 1f, 0f);

		if(!Global.Observer.IsWalkMode) {
			gl.glPushMatrix();
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureStore.GetTexture(mContext, gl, mCockpit.Texture));
			gl.glRotatef(br, 0, 1, 0);
			gl.glScalef(mCockpit.Scale, mCockpit.Scale, mCockpit.Scale);
			gl.glDisable(GL10.GL_CULL_FACE);
			mCockpit.Mesh.Draw(gl);
			gl.glPopMatrix();
		}

		gl.glTranslatef(-cX, -cY, -cZ);

		float gird_space = 100;
		int grid_size = 20;
		for(float i=-grid_size/2;i<=grid_size/2;i+=1) {
			Line.draw(gl, gird_space* i, -10, -gird_space*grid_size/2, gird_space* i, -10, gird_space*grid_size/2);
			Line.draw(gl, -gird_space*grid_size/2, -10, gird_space* i, gird_space*grid_size/2, -10, gird_space*i);
		}

		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

		for (Renderable r: mRenderables) {
			gl.glPushMatrix();
			gl.glTranslatef(r.Translation[0], r.Translation[1], r.Translation[2]);
			gl.glScalef(r.Scale, r.Scale, r.Scale);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureStore.GetTexture(mContext, gl, r.Texture));

			if(r.IsCullFace) {
				gl.glEnable(GL10.GL_CULL_FACE);
			}else {
				gl.glDisable(GL10.GL_CULL_FACE);
			}
			r.Mesh.Draw(gl);
			gl.glPopMatrix();
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d("GL", "Surface changed ...");
		mWidth = width;
		mHeight = height;
	}


}