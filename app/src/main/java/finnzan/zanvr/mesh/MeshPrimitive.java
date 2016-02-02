package finnzan.zanvr.mesh;

public class MeshPrimitive extends GNSMesh {
	public MeshPrimitive() {

	}

	public boolean LoadQuad(float size) {
		// Vertices
		this.numvertices = 4;
		this.vertices = new float[this.numvertices * 3];
		float v[] = { -size, 0, size, // 0, Top Left
				      -size, 0, -size, // 1, Bottom Left
				       size, 0, -size, // 2, Bottom Right
				       size, 0, size, // 3, Top Right
		};

		for (int i = 0; i < this.numvertices * 3; i++) {
			this.vertices[i] = v[i];
		}

		// TexCoords
		this.numtexcoords = 4;
		this.texcoords = new float[this.numtexcoords * 2];
		float uv[] = { 0.0f, 0.0f, 0.0f, 10.0f, 10.0f, 10.0f, 10.0f, 0.0f, };

		for (int i = 0; i < this.numtexcoords * 2; i++) {
			this.texcoords[i] = uv[i];
		}

		// Triangles
		this.numtriangles = 2;
		this.triangles = new GNSMtriangle[this.numtriangles];
		this.triangles[0] = new GNSMtriangle();
		this.triangles[0].vindices[0] = 0;
		this.triangles[0].vindices[1] = 1;
		this.triangles[0].vindices[2] = 2;
		this.triangles[0].tindices[0] = 0;
		this.triangles[0].tindices[1] = 1;
		this.triangles[0].tindices[2] = 2;

		this.triangles[1] = new GNSMtriangle();
		this.triangles[1].vindices[0] = 0;
		this.triangles[1].vindices[1] = 2;
		this.triangles[1].vindices[2] = 3;
		this.triangles[1].tindices[0] = 0;
		this.triangles[1].tindices[1] = 2;
		this.triangles[1].tindices[2] = 3;

		return true;
	}
}
