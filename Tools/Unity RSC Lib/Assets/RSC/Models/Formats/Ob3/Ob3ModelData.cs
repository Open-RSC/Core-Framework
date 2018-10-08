namespace Assets.RSC.Models.Formats.Ob3
{
	using System.Collections.Generic;

	using UnityEngine;

	public class Ob3ModelData
	{
		private List<Vector3> vertices;
		private List<Ob3Face> faces;
		private float xRot;
		private float yRot;
		private float zRot;
		private float xScale;
		private float yScale;
		private float zScale;
		private float xTranslate;
		private float yTranslate;
		private float zTranslate;
		private int numTextures = 0;

		public Ob3ModelData(List<Vector3> vertices, List<Ob3Face> faces)
		{
			this.vertices = vertices;// new ClonableStack<Vertex>();
			this.faces = faces;// new Vector();
			// this.faces = faces;
			// this.vertices = vertices;
			xRot = yRot = zRot = 0.0F;
			xScale = yScale = zScale = 1.0F;
			xTranslate = yTranslate = zTranslate = 0.0F;
		}

		public Ob3ModelData(List<Vector3> vertices, List<Ob3Face> faces, List<Vector3> vertices1, List<Ob3Face> faces1)
		{
			this.vertices = vertices; //new Vector();
			this.faces = faces;//new Vector();
			//this.faces = faces;
			//this.vertices = vertices;
			xRot = yRot = zRot = 0.0F;
			xScale = yScale = zScale = 1.0F;
			xTranslate = yTranslate = zTranslate = 0.0F;
		}

		public Ob3ModelData()
		{
			vertices = new List<Vector3>();
			faces = new List<Ob3Face>();
			// faces = new Vector();
			// vertices = new Vector();
			xRot = yRot = zRot = 0.0F;
			xScale = yScale = zScale = 1.0F;
			xTranslate = yTranslate = zTranslate = 0.0F;
		}

		public void addFace(Ob3Face ob3Face)
		{
			faces.Add(ob3Face);
		}

		public Ob3Face getFace(int i)
		{
			return faces[i];
		}

		public Ob3Face removeFace(int i)
		{
			var f = faces[i];
			faces.Remove(f);
			return f;
		}

		public List<Ob3Face> getFaces()
		{
			return faces;
		}

		public void addVert(Vector3 vertex)
		{
			vertices.Add(vertex);
		}

		public Vector3 removeVert(int i)
		{
			var f = vertices[i];
			vertices.Remove(f);
			return f;
		}

		public Vector3 getVert(int i)
		{
			return vertices[i];
		}

		public List<Vector3> getVertices()
		{
			return vertices;
		}

		public void setVertices(List<Vector3> vertices)
		{
			this.vertices = vertices;
		}

		public void setFaces(List<Ob3Face> faces)
		{
			this.faces = faces;
		}

		public float getXRot()
		{
			return xRot;
		}

		public void setXRot(float xRot)
		{
			if (xRot > 360F)
			{
				xRot -= 360F;
			}
			else
				if (xRot < -360F)
				{
					xRot += 360F;
				}
			this.xRot = xRot;
		}

		public float getYRot()
		{
			return yRot;
		}

		public void setYRot(float yRot)
		{
			if (yRot > 360F)
			{
				yRot -= 360F;
			}
			else
				if (yRot < -360F)
				{
					yRot += 360F;
				}
			this.yRot = yRot;
		}

		public float getZRot()
		{
			return zRot;
		}

		public void setZRot(float zRot)
		{
			if (zRot > 360F)
			{
				zRot -= 360F;
			}
			else
				if (zRot < -360F)
				{
					zRot += 360F;
				}
			this.zRot = zRot;
		}

		public float getXScale()
		{
			return xScale;
		}

		public void setXScale(float xScale)
		{
			this.xScale = xScale;
		}

		public float getYScale()
		{
			return yScale;
		}

		public void setYScale(float yScale)
		{
			this.yScale = yScale;
		}

		public float getZScale()
		{
			return zScale;
		}

		public void setZScale(float zScale)
		{
			this.zScale = zScale;
		}

		public void setScale(float scale)
		{
			setXScale(scale);
			setYScale(scale);
			setZScale(scale);
		}

		public float getXTranslate()
		{
			return xTranslate;
		}

		public void setXTranslate(float xTranslate)
		{
			this.xTranslate = xTranslate;
		}

		public float getYTranslate()
		{
			return yTranslate;
		}

		public void setYTranslate(float yTranslate)
		{
			this.yTranslate = yTranslate;
		}

		public float getZTranslate()
		{
			return zTranslate;
		}

		public void setZTranslate(float zTranslate)
		{
			this.zTranslate = zTranslate;
		}

		public void setNumTextures(int numTextures)
		{
			this.numTextures = numTextures;
		}

		public int getNumTextures()
		{
			return numTextures;
		}

		public int FaceCount { get; set; }

		public int[] FaceVertCount { get; set; }
	}
}

