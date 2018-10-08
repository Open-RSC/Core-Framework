namespace Assets.RSC
{
	using System;
	using System.Collections.Generic;
	using System.IO;
	using System.Net.Sockets;
	using System.Threading;

	using RSCXNALib;

	using UnityEngine;

	public class link
	{
		public static sbyte[] streamToSbyte(BinaryReader stream)
		{
			List<sbyte> list = new List<sbyte>();
			{
				// int ch;
				int c = 0;
				try
				{
					while (c < stream.BaseStream.Length)
					{
						list.Add(stream.ReadSByte());
						c++;
					}
				}
				catch { }
			}
			return list.ToArray();
		}

		public static void addFile(String filename, BinaryReader reader)
		{

			link.fileName[currentFile] = filename;

			//     reader.Close();

			//  var f = Path.Combine(Config.CONF_DIR, filename);
			//var bytes = File.ReadAllBytes(f).Select(c => (char)c); ;
			//var sbytes = bytes.Select(c=>Convert.ToSByte(c)).ToArray();//c.t(sbyte[])(Array)bytes;
			link.fileData[currentFile] = streamToSbyte(reader);

			currentFile++;
		}


		public static void addFile(String fileName, sbyte[] fileData)
		{
			link.fileName[currentFile] = fileName;

			link.fileData[currentFile] = fileData;//.Cast<byte>().ToArray();

			currentFile++;
		}

		public static bool loadFile(String fileName)
		{
			try
			{
				
				// Debug.Log(Path.Combine(Config.CONF_DIR, fileName));
				//var f = new FileInfo(Path.Combine(Config.CONF_DIR, fileName));
				// if (f.Exists)
				{
					
					var bytes = (Resources.Load(fileName) as TextAsset).bytes;

					addFile(fileName, new BinaryReader(new MemoryStream(bytes)));
					return true;
				}
				return false;
			}
			catch (IOException ioe)
			{
				// ioe.printStackTrace();
				return false;
			}
		}

		public static sbyte[] getFile(String fileName)
		{
			for (int i = 0; i < currentFile; i++)
				if (link.fileName[i].Equals(fileName))
					return fileData[i];
			if (loadFile(fileName))
				return getFile(fileName);
			else
				return null;
		}


		public static TcpClient getSocket(int port)
		{
			for (link.port = port; link.port != 0; )
				try
				{
					Thread.Sleep(100);
				}
				catch (Exception _ex) { }

			return socket;
		}

		//public static void thread(Runnable runnable) {
		//    for(thread = runnable; thread != null;)
		//        try {
		//            Thread.Sleep(100);
		//        }
		//        catch(Exception _ex) { }

		//}

		public static String getAddress(String ip)
		{
			for (iplookup = ip; iplookup != null; )
				try
				{
					Thread.Sleep(100);
				}
				catch (Exception _ex) { }

			return address;
		}

		//public static Applet gameApplet;
		public static int uid;
		static int port;
		static TcpClient socket;
		//  static Runnable thread = null;
		static String iplookup = null;
		static String address;
		static int currentFile;
		private static String[] fileName = new String[50];
		private static sbyte[][] fileData = new sbyte[50][];

	}
}