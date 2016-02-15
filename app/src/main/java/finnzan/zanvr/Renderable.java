package finnzan.zanvr;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

import finnzan.util.CommonTools;
import finnzan.zanvr.mesh.IndexMeshBuffer;
import finnzan.zanvr.mesh.Mesh;

public class Renderable {

    public Mesh Mesh = null;
    public String Texture;
    public float Scale = 1;
    public float Translation[] = {0, 0, 0};
    public boolean IsCullFace = true;

    public Renderable(InputStream fs_mesh, String diffuse_map){

        try {
            IndexMeshBuffer ims = new IndexMeshBuffer();
            ims.Load(fs_mesh);
            Mesh = new Mesh(ims);

            Texture = diffuse_map;
        }catch (Exception ex){
            CommonTools.HandleException(ex);
        }
    }
}
