namespace Assets.RSC.Models
{
	using Assets.RSC.Models.Formats.Ob3;

	using UnityEngine;

	public class RSCModel
	{
		public string ModelName { get; set; }
		public string ModelDescription { get; set; }

		private Mesh _modelMesh;
		public bool FailedToLoad { get; set; }
		public RSCModel(Ob3Model source)
		{
			FailedToLoad = true;
			var verts = source.GetVertices();
			int[] indices = source.GetIndices();

			ModelName = source.ModelName;

			if (indices.Length % 3 == 0)
			{
				_modelMesh = new Mesh();

				var vertices = new Vector3[verts.Length];
				var normals = new Vector3[verts.Length];
				var vertexColors = new Color[verts.Length];

				int index = 0;
				foreach (var vertex in verts)
				{
					vertexColors[index] = vertex.Color;
					vertices[index] = vertex.Position;
					normals[index] = vertex.Normal;
					index++;
				}

				_modelMesh.vertices = vertices;
				_modelMesh.triangles = indices;
				// _modelMesh.normals = normals;
				_modelMesh.colors = vertexColors;
				_modelMesh.uv = new Vector2[vertices.Length];
				
				_modelMesh.RecalculateNormals();

				FailedToLoad = false;
			}

		}

		public Mesh GetMesh()
		{
			return _modelMesh;
		}
	}
}

