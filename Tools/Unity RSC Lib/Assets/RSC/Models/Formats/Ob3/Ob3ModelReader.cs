namespace Assets.RSC.Models.Formats.Ob3
{
	using System.Collections.Generic;

	using Assets.RSC;

	using UnityEngine;

	using global::RSC.Data;

	public class Ob3ModelReader
	{
		public static Ob3ModelReader[] Ob3ModelReaderCache;
		
		public Ob3ModelReader()
		{
			//_useGourad = useGourad;
			_vertX = null;
			_vertY = null;
			_vertZ = null;
			_faceVertCnt = null;
			_textureFront = null;
			_textureBack = null;
			_faceShade = null;
			FaceVerts = null;
		}
		
		public Ob3ModelReader GetModel(int i)
		{
			return Ob3ModelReaderCache[i];
		}
		
		public void ReadValues(string s)
		{
			sbyte[] is2 = link.getFile(s);
			int i = 0;
			ReadValues(is2, 0);
		}

		public void ReadValues(sbyte[] buffer, int offset)
		{
			var data = buffer;
			int index = offset;

			int totalVertices = DataOperations.getShort(data, index);
			index += 2;
			int totalIndices = DataOperations.getShort(data, index);
			index += 2;
			_vertX = new int[totalVertices];
			_vertY = new int[totalVertices];
			_vertZ = new int[totalVertices];
			_faceVertCnt = new int[totalIndices];
			_textureFront = new int[totalIndices];
			_textureBack = new int[totalIndices];
			_faceShade = new int[totalIndices];
			FaceVerts = new int[totalIndices][];
			for (int l = 0; l < totalVertices; l++)
			{
				_vertX[l] = -DataOperations.getShort2(data, index); // *-1 //
				index += 2;
			}
			for (int l = 0; l < totalVertices; l++)
			{
				_vertY[l] = -DataOperations.getShort2(data, index);
				index += 2;
			}
			for (int l = 0; l < totalVertices; l++)
			{
				_vertZ[l] = DataOperations.getShort2(data, index);
				index += 2;
			}
			_vertCnt = totalVertices;

			for (int l = 0; l < totalIndices; l++)
				_faceVertCnt[l] = data[index++] & 0xff;

			for (int l1 = 0; l1 < totalIndices; l1++)
			{
				_textureFront[l1] = DataOperations.getShort2(data, index);
				index += 2;
				if (_textureFront[l1] == 32767)
					_textureFront[l1] = _useGourad;
			}
			for (int l1 = 0; l1 < totalIndices; l1++)
			{
				_textureBack[l1] = DataOperations.getShort2(data, index);
				index += 2;
				if (_textureBack[l1] == 32767)
					_textureBack[l1] = _useGourad;
			}
			for (int l1 = 0; l1 < totalIndices; l1++)
			{
				int k2 = data[index++] & 0xff;
				if (k2 == 0)
					_faceShade[l1] = 0;
				else
					_faceShade[l1] = _useGourad;
			}
			for (int l2 = 0; l2 < totalIndices; l2++)
			{
				FaceVerts[l2] = new int[_faceVertCnt[l2]];
				for (int i3 = 0; i3 < _faceVertCnt[l2]; i3++)
					if (totalVertices < 256)
					{
						FaceVerts[l2][i3] = data[index++] & 0xff;
					}
					else
					{
						FaceVerts[l2][i3] = DataOperations
								.getShort(data, index);
						index += 2;
					}
			}
			_faceCnt = totalIndices;
		}

		public Ob3ModelData BuildRscModel()
		{
			var model = new Ob3ModelData();
			var vert = new List<Vector3>();
			var faces = new List<Ob3Face>();

			model.FaceCount = _faceCnt;
			model.FaceVertCount = _faceVertCnt;

			for (int l = 0; l < _vertCnt; l++)
			{
				vert.Add(new Vector3(_vertX[l], _vertY[l], _vertZ[l]));
			}
			for (int f = 0; f < _faceCnt; f++)
			{
				var c = Color.green;// Color.FromNonPremultiplied(255,0,255,255);

// #warning transparent color instead of green for missing vertex colors.
				// c = new Color(0, 0, 0, 0);

				int textureId = -1;
				if (_textureFront[f] < 0)
				{
					int texture = _textureFront[f];
					c = Utils.GetColor(texture);
					textureId = texture;
				}
				if (_textureBack[f] < 0)
				{
					int texture = _textureBack[f];
					c = Utils.GetColor(texture);
					textureId = texture;
				}
				if (_textureFront[f] > 0)
				{
					textureId = _textureFront[f] + 1;

				}
				if (_textureBack[f] > 0)
				{
					textureId = _textureBack[f] + 1;
				}
				if (_textureBack[f] < 0 && (!(_textureFront[f] < 0)))
				{
					textureId = _textureBack[f];
				}
				if (_textureBack[f] > 0 && (!(_textureFront[f] > 0)))
				{
					textureId = _textureBack[f];
				}
				var tester = new int[_faceVertCnt[f]];
				for (int i3 = 0; i3 < _faceVertCnt[f]; i3++)
				{
					tester[i3] = FaceVerts[f][i3];
				}
				//if (textureID == -1)
				//{

				//}
				//else
				//{
				//	faces.Add(new Ob3Face(textureID, tester));
				//}
				//if (textureID < 0 && c == Color.Transparent)
				//{
				//	var t1 = texture_back[f];
				//	var t2 = texture_front[f];
				//	c = Color.Red;
				//} //textureID = -1;
				faces.Add(new Ob3Face(textureId, c, tester));
			}
			model.setVertices(vert);
			model.setFaces(faces);
			return model;
		}

		private int[] _vertX;
		private int[] _vertY;
		private int[] _vertZ;
		private int[] _faceVertCnt;
		private int[] _textureFront;
		private int[] _textureBack;
		private int[] _faceShade;
		public int[][] FaceVerts;
		private int _vertCnt;
		private int _useGourad;
		private int _faceCnt;		
	}
}

