package com.rscdaemon.io;

import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.IOException;

import com.rscdaemon.core.Mesh;
import com.rscdaemon.internal.ResourceLoader;

/**
 * Quick test for directly loading OB3 meshes.
 * 
 * @author Zilent
 *
 */
final class OB3MeshFactory
	extends
		MeshFactory
{

	public static void OB3_2_OBJ(String file)
		throws
			IOException
	{
		DataInputStream bais = new DataInputStream(ResourceLoader.loadResource(file));
		int vertexCount = bais.readShort();
		int faceCount = bais.readShort();
		int[] x = new int[vertexCount], y = new int[vertexCount], z = new int[vertexCount];
		int[][] indices = new int[faceCount][];
		for(int i = 0; i < vertexCount; ++i) x[i] = bais.readShort();
		for(int i = 0; i < vertexCount; ++i) y[i] = bais.readShort();
		for(int i = 0; i < vertexCount; ++i) z[i] = bais.readShort();
		
		int[] faceSizes = new int[faceCount];
		for (int i = 0; i < faceCount; i++) faceSizes[i] = bais.readByte() & 0xFF;
		for (int l1 = 0; l1 < faceCount; l1++) {
			bais.readShort(); // frontface tex/col TODO
		}
		for (int l1 = 0; l1 < faceCount; l1++) {
			bais.readShort(); // backface tex/col TODO
		}
		for (int l1 = 0; l1 < faceCount; l1++) {
			bais.readByte(); // Lighting TODO
		}
		
		for (int i = 0; i < faceCount; i++)
		{
			indices[i] = new int[faceSizes[i]];
			for (int vertexPtr = 0; vertexPtr < faceSizes[i]; vertexPtr++)
			{
				if (vertexCount < 256)
				{
					indices[i][vertexPtr] = bais.readByte() & 0xFF;
				} else
				{
					indices[i][vertexPtr] = bais.readShort();
				}
			}
		}
		
		FileWriter fout = new FileWriter("models/obj/"+file.substring(file.lastIndexOf("/")));
		for(int i = 0; i < vertexCount; ++i)
		{
			fout.write("v " + -x[i] + " " + -y[i] + " " + -z[i] + "\n");
		}
		fout.write("\n");
		for(int i = 0; i < indices.length; ++i)
		{
			fout.write("f");
			for(int j = 0; j < indices[i].length; ++j)
			{
				fout.write(" " + (indices[i][j] + 1));
			}
			fout.write("\n");
		}
		fout.close();
	}
	
	public static void main(String[] $)
		throws
			Throwable
	{
		OB3_2_OBJ("models/ob3/table.ob3");
	}
	
	@Override
	public Mesh loadMesh0(String resource)
		throws
			IOException
	{
/*		DataInputStream bais = new DataInputStream(ResourceLoader.loadResource(resource));
		Point3F[] vertices = new Point3F[bais.readShort()];
		int faceCount = bais.readShort();
		List<Face> faces = new ArrayList<Face>();

		for(int i = 0; i < vertices.length; ++i)
		{
			vertices[i] = new Point3F();
			vertices[i].setX(bais.readShort());
		}
		for(int i = 0; i < vertices.length; ++i)
		{
			vertices[i].setY(-bais.readShort());
		}
		for(int i = 0; i < vertices.length; ++i)
		{
			vertices[i].setZ(bais.readShort());
		}
		
		int[] faceSizes = new int[faceCount];
		for (int l = 0; l < faceCount; l++)
		{
			faceSizes[l] = bais.readByte() & 0xFF;;
		}

		for (int l1 = 0; l1 < faceCount; l1++) {
			bais.readShort(); // frontface tex/col TODO
		}
		for (int l1 = 0; l1 < faceCount; l1++) {
			bais.readShort(); // backface tex/col TODO
		}
		for (int l1 = 0; l1 < faceCount; l1++) {
			bais.readByte(); // Lighting TODO
		}
		
		int[][] faceVertices = new int[faceCount][];
		for (int l2 = 0; l2 < faceCount; l2++)
		{
			faceVertices[l2] = new int[faceSizes[l2]];
			for (int i3 = 0; i3 < faceSizes[l2]; i3++)
			{
				if (vertices.length < 256) {
					faceVertices[l2][i3] = bais.readByte() & 0xFF;
				} else {
					faceVertices[l2][i3] = bais.readShort();
				}
			}
			if(faceVertices[l2].length == 3)
			{
				faces.add(new Face(new Tuple3I(faceVertices[l2][0], faceVertices[l2][1], faceVertices[l2][2])));
			}
			else if(faceVertices[l2].length == 4)
			{
				faces.add(new Face(new Tuple3I(faceVertices[l2][0], faceVertices[l2][1], faceVertices[l2][2])));
				faces.add(new Face(new Tuple3I(faceVertices[l2][2], faceVertices[l2][3], faceVertices[l2][0])));
			}
			else
			{
				System.out.println("PANIC!");
			}
		}
		return new Mesh(Arrays.asList(vertices), faces);*/
		return null;
	}

}
