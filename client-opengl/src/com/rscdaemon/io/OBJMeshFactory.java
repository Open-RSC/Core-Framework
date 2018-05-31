package com.rscdaemon.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.rscdaemon.core.Color3F;
import com.rscdaemon.core.Mesh;
import com.rscdaemon.core.Point2F;
import com.rscdaemon.core.Point3F;
import com.rscdaemon.core.Vector3F;
import com.rscdaemon.core.VertexAttributes;
import com.rscdaemon.internal.ResourceLoader;

/**
 * Quick test for directly loading OBJ meshes.
 * 
 * @author Zilent
 *
 */
final class OBJMeshFactory
	extends
		MeshFactory
{

	@Override
	public final Mesh loadMesh0(String resource)
		throws
			IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(ResourceLoader.loadResource(resource)));
		List<Point3F> vertices = new ArrayList<Point3F>();
		List<Vector3F> normals = new ArrayList<Vector3F>();
		List<Color3F> colors = new ArrayList<Color3F>();
		List<Point2F> texCoords = new ArrayList<Point2F>();
		List<VertexAttributes> attributes = new ArrayList<VertexAttributes>();
		
		String line = null;
		while((line = reader.readLine()) != null)
		{
			String[] tokens = line.split(" ");
			switch(tokens[0])
			{
			case "v":
				vertices.add(
					new Point3F(
						Float.parseFloat(tokens[1]),
						Float.parseFloat(tokens[2]),
						Float.parseFloat(tokens[3])));
				break;
				
			case "vn":
				normals.add(new Vector3F(Float.parseFloat(tokens[1]),
						Float.parseFloat(tokens[2]),
						Float.parseFloat(tokens[3])));
				break;
			case "c":
				colors.add(new Color3F(Float.parseFloat(tokens[1]), 
							           Float.parseFloat(tokens[2]), 
							           Float.parseFloat(tokens[3])));
				break;
			case "t":
				texCoords.add(new Point2F(Float.parseFloat(tokens[1]),
										  Float.parseFloat(tokens[2])));
				break;
			case "f":
				for(int i = 1; i < tokens.length; ++i)
				{
					String[] subtokens = tokens[i].split("/");
					int point = Integer.parseInt(subtokens[0]) - 1;
					int normal = 0;
					int color = 0;
					int texCoord = 0;
					if(subtokens.length > 1)
					{
						color = Integer.parseInt(subtokens[1]) - 1;
						if(subtokens.length > 2)
						{
							normal = Integer.parseInt(subtokens[2]) - 1;
							if(subtokens.length > 3)
							{
								texCoord = Integer.parseInt(subtokens[3]) - 1;
							}
						}
					}
					attributes.add(new VertexAttributes(point, normal, color, texCoord));
				}
				break;
			default:
				
			}
		}
		return new Mesh(vertices, normals, colors, texCoords, attributes);
	}
	
}
