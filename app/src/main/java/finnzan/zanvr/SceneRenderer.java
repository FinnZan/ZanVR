package finnzan.zanvr;

import java.io.InputStream;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import finnzan.zanvr.mesh.IndexMeshBuffer;
import finnzan.zanvr.mesh.Mesh;
import finnzan.zanvr.mesh.MeshPrimitive;

public class SceneRenderer implements Renderer {
	private Mesh mesh;

	private int[] textures;
	private Bitmap bmpTex;
	private Bitmap bmpTile;

	private float mShift = 0;

	public SceneRenderer(Context context, float shift) {
		try {

			mShift = shift;

			Log.d("GL", "Load resources...");
			AssetManager assets = context.getAssets();
			bmpTex = BitmapFactory.decodeStream(assets.open("texture.png"));		        
			bmpTile = BitmapFactory.decodeStream(assets.open("tile.png"));	

			InputStream is = assets.open("out.ims");
			IndexMeshBuffer ims = new IndexMeshBuffer();
			ims.Load(is);
			this.mesh = new Mesh(ims);

			MeshPrimitive mp = new MeshPrimitive();
			mp.LoadQuad(500);

		} catch (Exception ex) {
			Log.d("GL", ex.toString());
		}
	}

	@Override
	public void onDrawFrame(GL10 gl) {

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		if (Global.SCENE_ELEVATION > 80)
			Global.SCENE_ELEVATION  = 80;
		if (Global.SCENE_ELEVATION  < 20)
			Global.SCENE_ELEVATION = 20;
		gl.glTranslatef(mShift, -Global.SCENE_ELEVATION, Global.TRANSLATE_Z);

		gl.glRotatef(Global.ROTATE_X, 1, 0, 0);
		gl.glRotatef(Global.ROTATE_Y, 0, 1, 0);
		
		gl.glScalef(Global.SCENE_SCALE, Global.SCENE_SCALE, Global.SCENE_SCALE);

		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		this.mesh.Draw(gl);

		gl.glDisable(GL10.GL_CULL_FACE);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d("GL", "Surface changed ...");
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 5.0f, 500.0f);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		try {
			Log.d("GL", "Surface created ...");
			
			gl.glClearColor(0.0f, 0.1f, 0.2f, 1.0f);
			gl.glShadeModel(GL10.GL_SMOOTH);
			gl.glClearDepthf(1.0f);
			gl.glEnable(GL10.GL_DEPTH_TEST);
			gl.glDepthFunc(GL10.GL_LEQUAL);
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
	
			gl.glFrontFace(GL10.GL_CCW);
			gl.glEnable(GL10.GL_CULL_FACE);
			gl.glEnable(GL10.GL_CULL_FACE);
	
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
	
			float[] mycolor = { 0.8f, 0.7f, 0.6f, 1.0f };
			gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_AMBIENT_AND_DIFFUSE, mycolor, 0);	
			gl.glEnable(GL10.GL_TEXTURE_2D);	
			gl.glEnable(GL10.GL_ALPHA_TEST);
			gl.glAlphaFunc(GL10.GL_GREATER, 0.1f);	
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);	
			gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
	
			textures = new int[2];
			gl.glGenTextures(2, textures, 0);
	
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmpTex, 0);
	
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[1]);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,GL10.GL_REPEAT);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmpTile, 0);
	
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
}