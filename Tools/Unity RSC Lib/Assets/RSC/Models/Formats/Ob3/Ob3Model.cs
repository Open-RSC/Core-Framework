namespace Assets.RSC.Models.Formats.Ob3
{
	using System.Collections.Generic;
	using System.Linq;

	using UnityEngine;

	public partial class Ob3Model
	{
		int VertexCount;
		int FaceCount;

		public Ob3ModelData ModelData { get; set; }
		public sbyte[] ModelDataBuffer { get; set; }
		public int Offset { get; set; }

		public bool IsLoaded { get; set; }
		public bool IsGiantCrystal { get; set; }
		public string ModelName { get; set; }

		public Ob3Model(string modelName, sbyte[] modelData, int offset, bool isLoaded)
		{
			// TODO: Complete member initialization
			//this.Textures = new List<TextureData>();
			this.ModelDataBuffer = modelData;
			this.Offset = offset;
			this.IsLoaded = isLoaded;
			this.ModelName = modelName;
			this.ModelData = new Ob3ModelData();
			// Meshes = new List<Ob3ModelMesh>();

			UnpackModelData(ModelDataBuffer, offset);
		}

		public Ob3Model(string modelName, int vertexCount, int faceCount)
		{
			// TODO: Complete member initialization
			//	this.Textures = new List<TextureData>();
			this.VertexCount = vertexCount;
			this.FaceCount = faceCount;
			this.ModelName = modelName;

			//	Meshes = new List<Ob3ModelMesh>();

			this.ModelData = new Ob3ModelData();
		}

		private void UnpackModelData(sbyte[] modelData, int offset)
		{
			var mReader = new Ob3ModelReader();
			mReader.ReadValues(modelData, offset);

			this.ModelData = mReader.BuildRscModel();
			this.VertexCount = this.ModelData.getVertices().Count;
			this.FaceCount = this.ModelData.getFaces().Count;
		}



		public bool IsMeshBuilt = false;

		private int[] _indices;
		public int[] GetIndices()
		{
			if (_indices == null)
			{
				_indices = CreateIndices();

				IsMeshBuilt = true;

				_vertices = CreateVertices();
				_indices = CreateIndices();

				Normalize(ref _vertices, ref _indices);
			}

			return _indices ?? (_indices = this.CreateIndices());
		}

		private void Normalize(ref VertexPositionColorTextureNormal[] vertices, ref int[] indices)
		{
			for (int i = 0; i < vertices.Length; i++)
				vertices[i].Normal = new Vector3(0, 0, 0);

			for (int i = 0; i < indices.Length / 3; i++)
			{
				int index1 = indices[i * 3];
				int index2 = indices[i * 3 + 1];
				int index3 = indices[i * 3 + 2];

				Vector3 side1 = vertices[index1].Position - vertices[index3].Position;
				Vector3 side2 = vertices[index1].Position - vertices[index2].Position;
				Vector3 normal = Vector3.Cross(side1, side2);

				vertices[index1].Normal += normal;
				vertices[index2].Normal += normal;
				vertices[index3].Normal += normal;
			}

			for (int i = 0; i < vertices.Length; i++)
				vertices[i].Normal.Normalize();
		}


		public int GetPrimitiveCount()
		{
			return GetIndices().Length / 3;
		}

		private VertexPositionColorTextureNormal[] _vertices;
		public VertexPositionColorTextureNormal[] GetVertices()
		{
			return _vertices ?? (_vertices = CreateVertices());
		}

		private VertexPositionColorTextureNormal[] CreateVertices()
		{
			var verts = new List<VertexPositionColorTextureNormal>();
			var faces = ModelData.getFaces();
			var vertices = ModelData.getVertices();

			int x = 0;
			foreach (var vert in vertices)
			{
				Color c = Color.white;
				var v = new VertexPositionColorTextureNormal();
				v.Position = vert;

				var matchingFace = faces.FirstOrDefault(j => j.GetPoints().Any(p => p == x));
				if (matchingFace != null)
				{
					var textureIndex = matchingFace.GetTextureIndex();
					c = matchingFace.GetFaceColor();

					if (textureIndex > 0)
					{
						// var mName = this.ModelName;

						bool useTransparency = false;
						// fugly
						if (textureIndex == 4) textureIndex = 3;
						if (textureIndex == 18)
						{
							textureIndex = 17;
							useTransparency = true;
						}

					/*	var t = Game1.TextureCache.GetByIndex(textureIndex);

						if (t != null && !this.Textures.Contains(t))
						{
							this.Textures.Add(t);
						}

						if (t != null)
						{
							Color[] colors = new Color[t.Texture.Width * t.Texture.Height];
							t.Texture.GetData(colors);

							var newCol = colors.FirstOrDefault(col => col != Color.Transparent && col != Color.Black);
							if (newCol != Color.Transparent || c == Color.FromNonPremultiplied(0, 0, 0, 0))
							{
								c = newCol;
								if (useTransparency)
									c = new Color(c.R, c.G, c.B, 100);
								//v.Color = c;
							}
						}

						*/
						// load texture
					}

					//if (c == Color.FromNonPremultiplied(0, 0, 0, 0))
					//{
					//	//	c = Color.White;

					//}

					v.Color = c;



				}
				//v.Color = c;// Color.Red;
				if (c == new Color(0, 0, 0, 0))
				{

				}
				verts.Add(v);
				x++;
			}

			var ve = verts.ToArray();

			ApplyTextureCoordinates(ref ve);

			return ve;
		}

		private void ApplyTextureCoordinates(ref VertexPositionColorTextureNormal[] vertices)
		{
			var lastSize = 0;
			var faces = ModelData.getFaces();

			var textCoords = new Vector2[4]; // 4
			textCoords[0] = new Vector2(0, 0);
			textCoords[1] = new Vector2(1, 0);
			textCoords[2] = new Vector2(0, 1);
			textCoords[3] = new Vector2(1, 1);

			var index = 0;

			for (int j = 0; j < faces.Count; j++)
			{
				var points = faces[j].GetPoints();
				var pointLen = points.Length;
				for (int x = 0; x < pointLen; x++)
				{
					var color = faces[j].GetFaceColor();
					var text = faces[j].GetTextureIndex();

					if (text == -1)
						vertices[points[x]].Color = color;
					else
					{
						var texturedVertices = points.Length;

						//if (texturedVertices == 4)
						//{
						//	vertices[points[0]].TextureCoordinate = new Vector2(1, 0);
						//	vertices[points[1]].TextureCoordinate = new Vector2(0, 1);
						//	vertices[points[2]].TextureCoordinate = new Vector2(1, 1);
						//	vertices[points[3]].TextureCoordinate = new Vector2(0, 0);
						//	break;
						//}
						//if (texturedVertices == 3)
						//{
						//	if (lastSize == texturedVertices)
						//	{
						//		vertices[points[0]].TextureCoordinate = new Vector2(1, 0);
						//		vertices[points[1]].TextureCoordinate = new Vector2(0, 1);
						//		vertices[points[2]].TextureCoordinate = new Vector2(1, 1);
						//	}
						//	else
						//	{
						//		vertices[points[0]].TextureCoordinate = new Vector2(0, 0);
						//		vertices[points[1]].TextureCoordinate = new Vector2(1, 0);
						//		vertices[points[2]].TextureCoordinate = new Vector2(0, 1);
						//	}
						//	break;
						//}

						if (texturedVertices > 0)
						{
							for (int i = 0; i < texturedVertices; i++)
							{
								vertices[points[i]].Texture = textCoords[index];
								index = (index + 1) % textCoords.Length;
							}
							break;
						}
						else
						{
							vertices[points[x]].Texture = new Vector2(0,0);
							//vertices[points[x]].TextureCoordinate = new Vector2(0, 0);
						}

						// apply texture?

					}
				}
				lastSize = faces[j].GetPoints().Length;
			}
			//				throw new NotImplementedException();
		}

	}
}

