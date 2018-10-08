namespace Assets.RSC
{
	using System.IO;

	using UnityEngine;

	using global::RSC.Data;

	class Utils
	{
		public static void UnpackData(string arg0, ref sbyte[] data)
		{
			sbyte[] abyte0 = link.getFile(arg0);
			// Debug.Log("Unpacking data.. " + arg0+" File loaded: "+(abyte0 != null));
			if (abyte0 != null)
			{
				int l = ((abyte0[0] & 0xff) << 16) + ((abyte0[1] & 0xff) << 8) + (abyte0[2] & 0xff);
				int i1 = ((abyte0[3] & 0xff) << 16) + ((abyte0[4] & 0xff) << 8) + (abyte0[5] & 0xff);
				sbyte[] abyte1 = new sbyte[abyte0.Length - 6];
				for (int j1 = 0; j1 < abyte0.Length - 6; j1++) abyte1[j1] = abyte0[j1 + 6];

				if (i1 != l)
				{
					//if (!isMembers)
					//{
					data = new sbyte[l];
					DataFileDecrypter.unpackData(data, l, abyte1, i1, 0);
					//}
					//else
					//{
					//	M_LandscapeDataBuffer = new sbyte[l];
					//	DataFileDecrypter.unpackData(M_LandscapeDataBuffer, l, abyte1, i1, 0);
					//}

					/* if (OnContentLoaded != null)
					{
						OnContentLoaded(this, new ContentLoadedEventArgs("Unpacking " + arg1, arg2));
					} */
				}
				else
				{
					data = abyte1;
				}
			}
			else
			{

				data = unpackData2(arg0);
			}
		}

		public static sbyte[] unpackData2(string filename)
		{
			int i = 0;
			int k = 0;
			sbyte[] abyte0 = link.getFile(filename);
			if (abyte0 == null)
			{
				try
				{
					//Console.WriteLine("Loading " + fileTitle + " - 0%");
					//drawLoadingBarText(startPercentage, "Loading " + fileTitle + " - 0%");
					var inputstream = new BinaryReader(DataOperations.openInputStream(filename));
					//DataInputStream datainputstream = new DataInputStream(inputstream);
					sbyte[] abyte2 = new sbyte[6] {
                        inputstream.ReadSByte(),inputstream.ReadSByte(),inputstream.ReadSByte(),
                        inputstream.ReadSByte(),inputstream.ReadSByte(),inputstream.ReadSByte()
                    };

					//inputstream.Read(abyte2, 0, 6);
					i = ((abyte2[0] & 0xff) << 16) + ((abyte2[1] & 0xff) << 8) + (abyte2[2] & 0xff);
					k = ((abyte2[3] & 0xff) << 16) + ((abyte2[4] & 0xff) << 8) + (abyte2[5] & 0xff);



					//	Console.WriteLine("Loading " + fileTitle + " - 5%");
					//		drawLoadingBarText(startPercentage, "Loading " + fileTitle + " - 5%");
#warning this could break stuff
					// int l = 0;
					int l = 0; //6
					abyte0 = new sbyte[k];
					while (l < k)
					{
						int i1 = k - l;
						if (i1 > 1000)
							i1 = 1000;

						for (int t = 0; t < i1; t++)
							abyte0[l + t] = inputstream.ReadSByte();

						// inputstream.Read(abyte0, l, i1);

						l += i1;
						//	Console.WriteLine("Loading " + fileTitle + " - " + (5 + (l * 95) / k) + "%");
						//	drawLoadingBarText(startPercentage, "Loading " + fileTitle + " - " + (5 + (l * 95) / k) + "%");
					}

					inputstream.Close();
				}
				catch (IOException _ex) { }
			}
			//	Console.WriteLine("Unpacking " + fileTitle);
			//	drawLoadingBarText(startPercentage, "Unpacking " + fileTitle);
			if (k != i)
			{
				sbyte[] abyte1 = new sbyte[i];
				DataFileDecrypter.unpackData(abyte1, i, abyte0, k, 0);
				return abyte1;
			}
			else
			{
				//  return unpackData(filename, fileTitle, startPercentage); // abyte0;
				return abyte0;
			}
		}

		public static int ColorToInt(Color color)
		{
			//return BitConverter.ToInt16(new byte[] { color.B, color.G, color.R }, 0);
			return ((byte)(color.r * 255) << 16) + ((byte)(color.g * 255) << 8) + (byte)(color.b * 255);

		}

		public static Color UIntToColor(uint color)
		{
			byte a = (byte)(color >> 24);
			byte r = (byte)(color >> 16);
			byte g = (byte)(color >> 8);
			byte b = (byte)(color >> 0);
			return new Color((float)r / 255f, (float)g / 255f, (float)b / 255f, (float)a / 255f);
		}

		public static Color GetColorStandardTexture(int color)
		{
			int blue = color >> 16 & 0xff;
			int green = color >> 8 & 0xff;
			int red = color & 0xff;
			return new Color(blue, green, red, 255);
		}

		public static Color GetColorStandard(int color)
		{
			int blue = color >> 16 & 0xff;
			int green = color >> 8 & 0xff;
			int red = color & 0xff;
			return new Color((float)red / 255f, (float)green / 255f, (float)blue / 255f, 1f);
		}

		public static Color GetTextureColor(int rscColor)
		{
			rscColor = -1 - rscColor;
			int r = (rscColor >> 10 & 0x1f) * 8;
			int g = (rscColor >> 5 & 0x1f) * 8;
			int b = (rscColor & 0x1f) * 8;
			int rgbValue = 0;
			float r2 = 0f, g2 = 0f, b2 = 0f;
			for (int j4 = 0; j4 < 256; j4++)
			{
				int j6 = j4 * j4;
				int red = (r * j6) / 0x10000;
				int green = (g * j6) / 0x10000;
				int blue = (b * j6) / 0x10000;
				rgbValue = ((red << 16) + (green << 8) + blue);
				r2 = red;// red;	// Actually: Blue
				g2 = green;//green;
				b2 = blue; //blue;	// Actually: Red
			}
			return new Color((float)r2 / 255f, g2 / 255f, b2 / 255f, 1f);
		}

		public static Color GetColor(int rscColor)
		{
			rscColor = -1 - rscColor;
			int k2 = (rscColor >> 10 & 0x1f) * 8;
			int j3 = (rscColor >> 5 & 0x1f) * 8;
			int l3 = (rscColor & 0x1f) * 8;
			int rgbValue = 0;
			float r = 0f, g = 0f, b = 0f;
			for (int j4 = 0; j4 < 256; j4++)
			{
				int j6 = j4 * j4;
				int red = (k2 * j6) / 0x10000;
				int green = (j3 * j6) / 0x10000;
				int blue = (l3 * j6) / 0x10000;
				rgbValue = ((red << 16) + (green << 8) + blue);
				r = red;// red;	// Actually: Blue
				g = green;//green;
				b = blue; //blue;	// Actually: Red
			}
			return new Color(k2 / 255f, j3 / 255f, l3 / 255f, 1f);
		}

	}
}
