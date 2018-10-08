namespace Assets.RSC.Models.Formats.Ob3
{
	using System.Collections.Generic;

	public partial class Ob3Model
	{

		/// <summary>
		/// 
		/// </summary>
		/// <param name="outputIndicesVertexColored"></param>
		/// <param name="outputIndicesVertexTextured"></param>
		/// <param name="textureId"></param>
		/// <param name="isColored"></param>
		/// <param name="index"></param>
		public void AddIndex(
			ref List<int> outputIndicesVertexColored,
			ref Dictionary<int, List<int>> outputIndicesVertexTextured,
			int textureId,
			bool isColored,
			int index)
		{
			if (isColored) outputIndicesVertexColored.Add(index);
			else
			{
				if (outputIndicesVertexTextured.ContainsKey(textureId))
				{
					outputIndicesVertexTextured[textureId].Add(index);
				}
				else
				{
					outputIndicesVertexTextured.Add(textureId, new List<int>(index));
				}
			}
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="inputIndices"></param>
		/// <param name="outputIndicesVertexColored"></param>
		/// <param name="outputIndicesVertexTextured"></param>
		/// <param name="textureId"></param>
		/// <param name="isColored"></param>
		/// <param name="indexCount"></param>
		public void AddIndices(
			List<int> inputIndices,
			ref List<int> outputIndicesVertexColored,
			ref Dictionary<int, List<int>> outputIndicesVertexTextured,
			int textureId,
			bool isColored,
			int indexCount)
		{
			for (int j = inputIndices.Count - indexCount; j < inputIndices.Count; j++)
			{
				AddIndex(ref outputIndicesVertexColored, ref outputIndicesVertexTextured, textureId, isColored, inputIndices[j]);
			}
		}

		/// <summary>
		/// 
		/// </summary>
		/// <returns></returns>
		public int[] CreateIndices()
		{
			var outputIndices = new List<int>();
			var faces = ModelData.getFaces();

			var outputIndicesVertexColored = new List<int>();
			var outputIndicesVertexTextured = new Dictionary<int, List<int>>();

			foreach (var i in faces)
			{
				var indexes = i.GetPoints();
				var indexCount = i.GetIndices().Length;
				var textureId = i.GetTextureIndex();
				var isColored = textureId <= 0;

				if (indexCount == 3)
				{ //triangle face (1 triangle)
					outputIndices.AddRange(indexes);
					outputIndices.Reverse();
					AddIndices(outputIndices, ref outputIndicesVertexColored, ref outputIndicesVertexTextured, textureId, isColored, indexCount);
				}
				else if (indexCount == 4)
				{ //QuadEntity face (2 triangles)

					int one = indexes[0];
					int two = indexes[1];
					int three = indexes[2];
					int four = indexes[3];

					outputIndices.Add(one); outputIndices.Add(two); outputIndices.Add(three);
					outputIndices.Add(three); outputIndices.Add(four); outputIndices.Add(one);
					outputIndices.Reverse();
					AddIndices(outputIndices, ref outputIndicesVertexColored, ref outputIndicesVertexTextured, textureId, isColored, indexCount);
				}
				else if (indexCount == 5)
				{ // penta (pentagon) face (3 triangles)

					int one = indexes[0];
					int two = indexes[1];
					int three = indexes[2];
					int four = indexes[3];
					int five = indexes[4];

					outputIndices.Add(one); outputIndices.Add(two); outputIndices.Add(five);
					outputIndices.Add(two); outputIndices.Add(three); outputIndices.Add(four);
					outputIndices.Add(two); outputIndices.Add(four); outputIndices.Add(five);
					outputIndices.Reverse();
					AddIndices(outputIndices, ref outputIndicesVertexColored, ref outputIndicesVertexTextured, textureId, isColored, indexCount);
				}
				else if (indexCount == 6)
				{
					int one = indexes[0];
					int two = indexes[1];
					int three = indexes[2];
					int four = indexes[3];
					int five = indexes[4];
					int six = indexes[5];

					outputIndices.Add(one); outputIndices.Add(two); outputIndices.Add(six);
					outputIndices.Add(two); outputIndices.Add(five); outputIndices.Add(six);
					outputIndices.Add(two); outputIndices.Add(three); outputIndices.Add(five);
					outputIndices.Add(three); outputIndices.Add(four); outputIndices.Add(five);
					outputIndices.Reverse();
					AddIndices(outputIndices, ref outputIndicesVertexColored, ref outputIndicesVertexTextured, textureId, isColored, indexCount);
				}
				else if (indexCount == 8)
				{
					int one = indexes[0];
					int two = indexes[1];
					int three = indexes[2];
					int four = indexes[3];
					int five = indexes[4];
					int six = indexes[5];
					int seven = indexes[6];
					int eight = indexes[7];

					outputIndices.Add(one); outputIndices.Add(two); outputIndices.Add(three);
					outputIndices.Add(one); outputIndices.Add(three); outputIndices.Add(four);
					outputIndices.Add(one); outputIndices.Add(four); outputIndices.Add(eight);
					outputIndices.Add(eight); outputIndices.Add(four); outputIndices.Add(five);
					outputIndices.Add(eight); outputIndices.Add(five); outputIndices.Add(seven);
					outputIndices.Add(five); outputIndices.Add(six); outputIndices.Add(seven);
					outputIndices.Reverse();
					AddIndices(outputIndices, ref outputIndicesVertexColored, ref outputIndicesVertexTextured, textureId, isColored, indexCount);
				}
				else if (indexCount == 7)
				{
					int one = indexes[0];
					int two = indexes[1];
					int three = indexes[2];
					int four = indexes[3];
					int five = indexes[4];
					int six = indexes[5];
					int seven = indexes[6];


					outputIndices.Add(one); outputIndices.Add(two); outputIndices.Add(three);
					outputIndices.Add(one); outputIndices.Add(three); outputIndices.Add(seven);
					outputIndices.Add(three); outputIndices.Add(four); outputIndices.Add(seven);
					outputIndices.Add(four); outputIndices.Add(six); outputIndices.Add(seven);
					outputIndices.Add(four); outputIndices.Add(five); outputIndices.Add(six);
					outputIndices.Reverse();

					AddIndices(outputIndices, ref outputIndicesVertexColored, ref outputIndicesVertexTextured, textureId, isColored, indexCount);
				}
				else if (indexCount == 12)
				{
					int one = indexes[0];
					int two = indexes[1];
					int three = indexes[2];
					int four = indexes[3];
					int five = indexes[4];
					int six = indexes[5];
					int seven = indexes[6];
					int eight = indexes[7];
					int nine = indexes[8];
					int ten = indexes[9];
					int eleven = indexes[10];
					int twelve = indexes[11];

					outputIndices.Add(six); outputIndices.Add(seven); outputIndices.Add(eight);
					outputIndices.Add(eight); outputIndices.Add(nine); outputIndices.Add(six);
					outputIndices.Add(nine); outputIndices.Add(ten); outputIndices.Add(six);
					outputIndices.Add(ten); outputIndices.Add(eleven); outputIndices.Add(six);
					outputIndices.Add(eleven); outputIndices.Add(twelve); outputIndices.Add(six);
					outputIndices.Add(twelve); outputIndices.Add(one); outputIndices.Add(six);
					outputIndices.Add(one); outputIndices.Add(two); outputIndices.Add(six);
					outputIndices.Add(two); outputIndices.Add(three); outputIndices.Add(six);
					outputIndices.Add(three); outputIndices.Add(four); outputIndices.Add(six);
					outputIndices.Add(four); outputIndices.Add(five); outputIndices.Add(six);
					
					outputIndices.Reverse();
					
					AddIndices(outputIndices, ref outputIndicesVertexColored, ref outputIndicesVertexTextured, textureId, isColored, indexCount);
				}
				else if (indexCount == 9)
				{
					int one = indexes[0];
					int two = indexes[1];
					int three = indexes[2];
					int four = indexes[3];
					int five = indexes[4];
					int six = indexes[5];
					int seven = indexes[6];
					int eight = indexes[7];
					int nine = indexes[8];

					outputIndices.Add(one); outputIndices.Add(two); outputIndices.Add(three);
					outputIndices.Add(one); outputIndices.Add(three); outputIndices.Add(four);
					outputIndices.Add(one); outputIndices.Add(four); outputIndices.Add(nine);
					outputIndices.Add(nine); outputIndices.Add(four); outputIndices.Add(five);
					outputIndices.Add(nine); outputIndices.Add(five); outputIndices.Add(eight);
					outputIndices.Add(eight); outputIndices.Add(six); outputIndices.Add(seven);

					outputIndices.Reverse();

					AddIndices(outputIndices, ref outputIndicesVertexColored, ref outputIndicesVertexTextured, textureId, isColored, indexCount);
				}
				else
				{

					// since cutting some of the ears fuck up. lets skip them.
					//	continue;



					// Ugly hack for cutting ears, creating indices this way if we are not able to do it any other way.
					// if (indexCount < 14)

					// outputIndices.AddRange(indexes);
					// outputIndices.AddRange(CutEars(indexes));

					if (indexes.Length % 3 == 0)
					{
						outputIndices.AddRange(indexes);

						outputIndices.Reverse();

						AddIndices(
							outputIndices, ref outputIndicesVertexColored, ref outputIndicesVertexTextured, textureId, isColored, indexCount);
					}
					else
					{
						int totalC = 0;
						for (int j = 0; j < indexes.Length; j += 3)
							if (j <= indexes.Length - 3)
								totalC += 3;

						for (int j = 0; j < totalC; j++)
							outputIndices.Add(indexes[j]);

						outputIndices.Reverse();

						AddIndices(
							outputIndices, ref outputIndicesVertexColored, ref outputIndicesVertexTextured, textureId, isColored, totalC);
					}
				}
			}


			// We are done, so lets create our meshes!

			// CreateModelMeshes(outputIndicesVertexColored, outputIndicesVertexTextured);

			// returns the total index buffer for this Ob3Model, used by our "legacy" code.
			// if something goes wrong, we will use this.
			return outputIndices.ToArray();
		}


	}
}
