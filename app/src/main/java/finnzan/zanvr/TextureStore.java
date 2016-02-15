package finnzan.zanvr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.support.v4.util.Pair;
import android.test.suitebuilder.annotation.MediumTest;

import java.io.InputStream;
import java.security.KeyPair;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import finnzan.util.CommonTools;

/**
 * Created by finnb on 2/14/2016.
 */
public class TextureStore {

    private ArrayList<Pair<String, Integer>> mTextures = new  ArrayList<Pair<String, Integer>>();

    public TextureStore(){

    }

    public int GetTexture(Context context, GL10 gl, String file) {
        try {
            for (Pair<String, Integer> p : mTextures) {
                if (p.first.equals(file)) {
                    return p.second;
                }
            }

            int id = mTextures.size();
            Bitmap bmp = BitmapFactory.decodeStream(context.getAssets().open(file));

            gl.glBindTexture(GL10.GL_TEXTURE_2D, id);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);

            mTextures.add(new Pair<String, Integer>(file, id));
            CommonTools.Log("Texture [" + file + "] [" + id + "] added.");
            return id;
        } catch (Exception ex) {
            CommonTools.HandleException(ex);
            return -1;
        }
    }
}
