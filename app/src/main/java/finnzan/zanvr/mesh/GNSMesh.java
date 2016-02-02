package finnzan.zanvr.mesh;

public class GNSMesh {
	protected int numvertices; /* number of vertices in model */
	protected float vertices[]; /* array of vertices */

	protected int numnormals; /* number of normals in model */
	protected float normals[]; /* array of normals */

	protected int numcolors;
	protected byte colors[];

	protected int numtexcoords; /* number of texcoords in model */
	protected float texcoords[]; /* array of texture coordinates */

	protected int numtriangles; /* number of triangles in model */
	protected GNSMtriangle triangles[]; /* array of triangles */

	public GNSMesh() {

	}

	public IndexMeshBuffer GetIndexedBuffer() {
		float tVertices[] = new float[this.numtriangles * 3 * 3];
		for (int i = 0; i < this.numtriangles; i++) {
			for (int j = 0; j < 3; j++) {
				tVertices[i * 9 + (j * 3 + 0)] = this.vertices[(3 * triangles[i].vindices[j]) + 0];
				tVertices[i * 9 + (j * 3 + 1)] = this.vertices[(3 * triangles[i].vindices[j]) + 1];
				tVertices[i * 9 + (j * 3 + 2)] = this.vertices[(3 * triangles[i].vindices[j]) + 2];
			}
		}

		float tTexcoords[] = new float[this.numtriangles * 3 * 2];
		for (int i = 0; i < this.numtriangles; i++) {
			for (int j = 0; j < 3; j++) {
				tTexcoords[i * 6 + (j * 2 + 0)] = this.texcoords[(2 * triangles[i].tindices[j]) + 0];
				tTexcoords[i * 6 + (j * 2 + 1)] = this.texcoords[(2 * triangles[i].tindices[j]) + 1];
			}
		}

		short tIndices[] = new short[this.numtriangles * 3];
		for (int i = 0; i < this.numtriangles; i++) {
			tIndices[i * 3] = (short) (i * 3);
			tIndices[i * 3 + 1] = (short) (i * 3 + 1);
			tIndices[i * 3 + 2] = (short) (i * 3 + 2);
		}

		IndexMeshBuffer ims = new IndexMeshBuffer(tVertices, tTexcoords,
				tIndices);

		return ims;
	}

	// GNSMtriangle ============================================
	public class GNSMtriangle {
		public GNSMtriangle() {
			this.vindices = new short[3]; /* array of triangle vertex indices */
			this.nindices = new short[3]; /* array of triangle vertex indices */
			this.cindices = new short[3]; /* array of triangle vertex indices */
			this.tindices = new short[3]; /* array of triangle vertex indices */
		}

		public short vindices[]; /* array of triangle vertex indices */
		public short nindices[]; /* array of triangle normal indices */
		public short cindices[]; /* array of triangle color indices */
		public short tindices[]; /* array of triangle texcoord indices */
	};
}
