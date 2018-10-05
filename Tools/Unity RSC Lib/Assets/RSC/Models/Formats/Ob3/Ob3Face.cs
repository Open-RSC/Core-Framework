namespace Assets.RSC.Models.Formats.Ob3
{
	using UnityEngine;

	public class Ob3Face
	{
		int[] _indices;
		Color _faceColor;		
		int _textureId=-1;		

		public Ob3Face(Color c, int[] indices)
		{
			_indices = indices;
			_faceColor = c;			
		}
		
		public Ob3Face(int textureId, Color color, int[] indices)
		{
			_indices = indices;
			_textureId = textureId;
			_faceColor = color;
		}
		
		public Ob3Face(int[] indices)
		{
			_indices = indices;
			_faceColor = Color.red;
		}		

		public int[] GetIndices()
		{
			return _indices;
		}

		public Color GetFaceColor()
		{
			return _faceColor;
		}

		public int GetTextureIndex()
		{
			return _textureId;
		}

		public int[] GetPoints()
		{
			return _indices;
		}

	}
}

