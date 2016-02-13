package finnzan.zanvr.mesh;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import finnzan.util.CommonTools;

public class IndexMeshBuffer {
	private float vertices[];
	private float texCoords[];
	private short indices[];

	public IndexMeshBuffer() {
		this.vertices = new float[1];
		this.texCoords = new float[1];
		this.indices = new short[1];
	}

	public IndexMeshBuffer(float v[], float t[], short i[]) {
		this.vertices = v;
		this.texCoords = t;
		this.indices = i;
	}

	public boolean Load(InputStream is) {
		try {
			InputStreamReader inputreader = new InputStreamReader(is);
			BufferedReader buffreader = new BufferedReader(inputreader);
			String line;

			line = buffreader.readLine();
			int numTri = Integer.parseInt(line);

			this.vertices = new float[numTri * 3 * 3];
			for (int i = 0; i < numTri; i++) {
				for (int j = 0; j < 3; j++) {
					line = buffreader.readLine();
					String toks[] = line.split(" ");

					this.vertices[i * 9 + j * 3 + 0] = Float.parseFloat(toks[0]);
					this.vertices[i * 9 + j * 3 + 1] = Float.parseFloat(toks[1]);
					this.vertices[i * 9 + j * 3 + 2] = Float.parseFloat(toks[2]);
				}
			}

			this.texCoords = new float[numTri * 3 * 2];
			for (int i = 0; i < numTri; i++) {
				for (int j = 0; j < 3; j++) {
					line = buffreader.readLine();
					String toks[] = line.split(" ");

					this.texCoords[i * 6 + j * 2 + 0] = Float.parseFloat(toks[0]);
					this.texCoords[i * 6 + j * 2 + 1] = Float.parseFloat(toks[1]);
				}
			}

			this.indices = new short[numTri * 3];
			for (int i = 0; i < numTri; i++) {
				this.indices[i * 3] = (short) (i * 3);
				this.indices[i * 3 + 1] = (short) (i * 3 + 1);
				this.indices[i * 3 + 2] = (short) (i * 3 + 2);
			}
		} catch (Exception ex) {
			CommonTools.HandleException(ex);
		}

		return true;
	}

	public float[] GetVertices() {
		return this.vertices;
	}

	public float[] GetTexCoords() {
		return this.texCoords;
	}

	public short[] GetIndices() {
		return this.indices;
	}
}
