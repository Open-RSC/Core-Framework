using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Assets.RSC.Models.Formats.Ob3
{
	using UnityEngine;

	public class VertexPositionColorTextureNormal
	{
		public Color Color { get; set; }
		public Vector3 Position { get; set; }
		public Vector3 Normal { get; set; }
		public Vector2 Texture { get; set; }
	}
}
