package finnzan.zanvr.mesh;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import android.opengl.GLES20;

import finnzan.zanvr.util.CommonTools;

public class MeshES2 {

    private final String vertexShaderCode =
        "uniform mat4 uMVPMatrix;" +
        "attribute vec4 vPosition;" +
        "void main() {" +
        "  gl_Position = vPosition * uMVPMatrix;" +
        "}";

    private final String fragmentShaderCode =
        "precision mediump float;" +
        "uniform vec4 vColor;" +
        "void main() {" +
        "  gl_FragColor = vColor;" +
        "}";
    
    private float vertices[];
	private short indices[];
	private float textureCoordinates[];

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer indexBuffer;
    private final FloatBuffer texCoordBuf;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public MeshES2(IndexMeshBuffer m) {
    	
        // initialize vertex byte buffer for shape coordinates
    	vertices = m.GetVertices();
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
        		vertices.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
        
        indices = m.GetIndices();
        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);
		
		this.textureCoordinates = m.GetTexCoords();
		ByteBuffer tbb = ByteBuffer.allocateDirect(textureCoordinates.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		texCoordBuf = tbb.asFloatBuffer();
		texCoordBuf.put(textureCoordinates);
		texCoordBuf.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,  vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
    }

    public void draw(float[] mvpMatrix) {
    	try {
	        GLES20.glUseProgram(mProgram);
	        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
	        GLES20.glEnableVertexAttribArray(mPositionHandle);
	        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
	                                     GLES20.GL_FLOAT, false,
	                                     COORDS_PER_VERTEX * 4, vertexBuffer);
	        
	        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
	        GLES20.glUniform4fv(mColorHandle, 1, color, 0);        
	        
	        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
	        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
	        
	        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
	        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.length / COORDS_PER_VERTEX);
	        GLES20.glDisableVertexAttribArray(mPositionHandle);
    	}catch(Exception ex){
    		CommonTools.HandleException(ex);
    	}
    }
    
    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
    
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
        	CommonTools.Log(glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
}