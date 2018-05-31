package com.rscdaemon.io;

import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rscdaemon.core.Point2F;
import com.rscdaemon.core.Point3F;
import com.rscdaemon.core.Tuple2F;
import com.rscdaemon.core.Vector3F;
import com.rscdaemon.core.VertexAttributes;
import com.rscdaemon.internal.ResourceLoader;

/**
 * An extremely ugly monster.  This should never see the light of day.
 * <br>
 * <br>
 * Converts the proprietary "OB3" format to "RSCD Intermediate" format.
 * <br>
 * <br>
 * <strong>This is an internal tool and will not be documented.</strong>
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 * 
 */
class OB3Converter
{

	static class TriangulatorImpl
	{
		private static final float CONCAVE = 1;
		private static final float CONVEX = -1;

		private float mConcaveVertexCount;

		public List<Tuple2F> computeTriangles(final List<Tuple2F> pVertices)
		{
			final ArrayList<Tuple2F> triangles = new ArrayList<Tuple2F>();
			final ArrayList<Tuple2F> vertices = new ArrayList<Tuple2F>(
					pVertices.size());
			vertices.addAll(pVertices);

			if (vertices.size() == 3)
			{
				triangles.addAll(vertices);
				return triangles;
			}

			while (vertices.size() >= 3)
			{
				final float vertexTypes[] = this.classifyVertices(vertices);

				final int vertexCount = vertices.size();
				for (int index = 0; index < vertexCount; index++)
				{
					if (this.isEarTip(vertices, index, vertexTypes))
					{
						this.cutEarTip(vertices, index, triangles);
						break;
					}
				}
			}

			return triangles;
		}

		private static boolean areVerticesClockwise(
				final ArrayList<Tuple2F> pVertices)
		{
			final float vertexCount = pVertices.size();

			float area = 0;
			for (int i = 0; i < vertexCount; i++)
			{
				final Tuple2F p1 = pVertices.get(i);
				final Tuple2F p2 = pVertices.get(TriangulatorImpl
						.computeNextIndex(pVertices, i));
				area += p1.getX() * p2.getY() - p2.getX() * p1.getY();
			}

			if (area < 0)
			{
				return true;
			} else
			{
				return false;
			}
		}

		private float[] classifyVertices(final ArrayList<Tuple2F> pVertices)
		{
			final int vertexCount = pVertices.size();

			final float[] vertexTypes = new float[vertexCount];
			this.mConcaveVertexCount = 0;

			if (!TriangulatorImpl.areVerticesClockwise(pVertices))
			{
				Collections.reverse(pVertices);
			}

			for (int index = 0; index < vertexCount; index++)
			{
				final int previousIndex = TriangulatorImpl
						.computePreviousIndex(pVertices, index);
				final int nextIndex = TriangulatorImpl.computeNextIndex(
						pVertices, index);

				final Tuple2F previousVertex = pVertices.get(previousIndex);
				final Tuple2F currentVertex = pVertices.get(index);
				final Tuple2F nextVertex = pVertices.get(nextIndex);

				if (TriangulatorImpl.isTriangleConvex(previousVertex.getX(),
						previousVertex.getY(), currentVertex.getX(),
						currentVertex.getY(), nextVertex.getX(),
						nextVertex.getY()))
				{
					vertexTypes[index] = CONVEX;
				} else
				{
					vertexTypes[index] = CONCAVE;
					this.mConcaveVertexCount++;
				}
			}

			return vertexTypes;
		}

		private static boolean isTriangleConvex(final float pX1,
				final float pY1, final float pX2, final float pY2,
				final float pX3, final float pY3)
		{
			if (TriangulatorImpl.computeSpannedAreaSign(pX1, pY1, pX2, pY2,
					pX3, pY3) < 0)
			{
				return false;
			} else
			{
				return true;
			}
		}

		private static float computeSpannedAreaSign(final float pX1,
				final float pY1, final float pX2, final float pY2,
				final float pX3, final float pY3)
		{
			float area = 0;

			area += pX1 * (pY3 - pY2);
			area += pX2 * (pY1 - pY3);
			area += pX3 * (pY2 - pY1);

			return (float) Math.signum(area);
		}

		private static boolean isAnyVertexfloatriangle(
				final ArrayList<Tuple2F> pVertices, final float[] pVertexTypes,
				final float pX1, final float pY1, final float pX2,
				final float pY2, final float pX3, final float pY3)
		{
			int i = 0;

			final float vertexCount = pVertices.size();
			while (i < vertexCount - 1)
			{
				if ((pVertexTypes[i] == CONCAVE))
				{
					final Tuple2F currentVertex = pVertices.get(i);

					final float currentVertexX = currentVertex.getX();
					final float currentVertexY = currentVertex.getY();
					final float areaSign1 = TriangulatorImpl
							.computeSpannedAreaSign(pX1, pY1, pX2, pY2,
									currentVertexX, currentVertexY);
					final float areaSign2 = TriangulatorImpl
							.computeSpannedAreaSign(pX2, pY2, pX3, pY3,
									currentVertexX, currentVertexY);
					final float areaSign3 = TriangulatorImpl
							.computeSpannedAreaSign(pX3, pY3, pX1, pY1,
									currentVertexX, currentVertexY);

					if (areaSign1 > 0 && areaSign2 > 0 && areaSign3 > 0)
					{
						return true;
					} else if (areaSign1 <= 0 && areaSign2 <= 0
							&& areaSign3 <= 0)
					{
						return true;
					}
				}
				i++;
			}
			return false;
		}

		private boolean isEarTip(final ArrayList<Tuple2F> pVertices,
				final int pEarTipIndex, final float[] pVertexTypes)
		{
			if (this.mConcaveVertexCount != 0)
			{
				final Tuple2F previousVertex = pVertices.get(TriangulatorImpl
						.computePreviousIndex(pVertices, pEarTipIndex));
				final Tuple2F currentVertex = pVertices.get(pEarTipIndex);
				final Tuple2F nextVertex = pVertices.get(TriangulatorImpl
						.computeNextIndex(pVertices, pEarTipIndex));

				if (TriangulatorImpl.isAnyVertexfloatriangle(pVertices,
						pVertexTypes, previousVertex.getX(),
						previousVertex.getY(), currentVertex.getX(),
						currentVertex.getY(), nextVertex.getX(),
						nextVertex.getY()))
				{
					return false;
				} else
				{
					return true;
				}
			} else
			{
				return true;
			}
		}

		private void cutEarTip(final ArrayList<Tuple2F> pVertices,
				final int pEarTipIndex, final ArrayList<Tuple2F> pTriangles)
		{
			final int previousIndex = TriangulatorImpl.computePreviousIndex(
					pVertices, pEarTipIndex);
			final int nextIndex = TriangulatorImpl.computeNextIndex(pVertices,
					pEarTipIndex);

			if (!TriangulatorImpl.isCollinear(pVertices, previousIndex,
					pEarTipIndex, nextIndex))
			{
				pTriangles.add(new Tuple2F(pVertices.get(previousIndex)));
				pTriangles.add(new Tuple2F(pVertices.get(pEarTipIndex)));
				pTriangles.add(new Tuple2F(pVertices.get(nextIndex)));
			}

			pVertices.remove(pEarTipIndex);
			if (pVertices.size() >= 3)
			{
				TriangulatorImpl
						.removeCollinearNeighborEarsAfterRemovingEarTip(
								pVertices, pEarTipIndex);
			}
		}

		private static void removeCollinearNeighborEarsAfterRemovingEarTip(
				final ArrayList<Tuple2F> pVertices, final int pEarTipCutIndex)
		{
			final int collinearityCheckNextIndex = pEarTipCutIndex
					% pVertices.size();
			int collinearCheckPreviousIndex = TriangulatorImpl
					.computePreviousIndex(pVertices, collinearityCheckNextIndex);

			if (TriangulatorImpl.isCollinear(pVertices,
					collinearityCheckNextIndex))
			{
				pVertices.remove(collinearityCheckNextIndex);

				if (pVertices.size() > 3)
				{
					/* Update */
					collinearCheckPreviousIndex = TriangulatorImpl
							.computePreviousIndex(pVertices,
									collinearityCheckNextIndex);
					if (TriangulatorImpl.isCollinear(pVertices,
							collinearCheckPreviousIndex))
					{
						pVertices.remove(collinearCheckPreviousIndex);
					}
				}
			} else if (TriangulatorImpl.isCollinear(pVertices,
					collinearCheckPreviousIndex))
			{
				pVertices.remove(collinearCheckPreviousIndex);
			}
		}

		private static boolean isCollinear(final ArrayList<Tuple2F> pVertices,
				final int pIndex)
		{
			final int previousIndex = TriangulatorImpl.computePreviousIndex(
					pVertices, pIndex);
			final int nextIndex = TriangulatorImpl.computeNextIndex(pVertices,
					pIndex);

			return TriangulatorImpl.isCollinear(pVertices, previousIndex,
					pIndex, nextIndex);
		}

		private static boolean isCollinear(final ArrayList<Tuple2F> pVertices,
				final int pPreviousIndex, final int pIndex, final int pNextIndex)
		{
			final Tuple2F previousVertex = pVertices.get(pPreviousIndex);
			final Tuple2F vertex = pVertices.get(pIndex);
			final Tuple2F nextVertex = pVertices.get(pNextIndex);

			return TriangulatorImpl.computeSpannedAreaSign(
					previousVertex.getX(), previousVertex.getY(),
					vertex.getX(), vertex.getY(), nextVertex.getX(),
					nextVertex.getY()) == 0;
		}

		private static int computePreviousIndex(final List<Tuple2F> pVertices,
				final int pIndex)
		{
			return pIndex == 0 ? pVertices.size() - 1 : pIndex - 1;
		}

		private static int computeNextIndex(final List<Tuple2F> pVertices,
				final int pIndex)
		{
			return pIndex == pVertices.size() - 1 ? 0 : pIndex + 1;
		}
	}

	private static class BoundingBox
	{
		Point2F min, max;

		BoundingBox(Point2F min, Point2F max)
		{
			this.min = min;
			this.max = max;
		}

		@Override
		public String toString()
		{
			return "(" + xWidth() + ", " + yWidth() + ")";
		}

		float xWidth()
		{
			return max.getX() - min.getX();
		}

		float yWidth()
		{
			return max.getY() - min.getY();
		}
	}

	private class Polygon
	{
		int[] vertices;
		Vector3F normal;
		int color;
		int texture;

		Polygon(int[] vertices, int color, int texture)
		{
			assert vertices.length > 2;

			this.vertices = vertices;
			this.color = color;
			this.texture = texture;

			normal = generateNormal(OB3Converter.this.vertices[vertices[0]],
					OB3Converter.this.vertices[vertices[1]],
					OB3Converter.this.vertices[vertices[2]]);
		}
	}

	private static Vector3F generateNormal(Point3F a, Point3F b, Point3F c)
	{
		Point3F v0 = new Point3F(b);
		v0.subtract(a);
		Point3F v1 = new Point3F(c);
		v1.subtract(a);

		float x0 = v0.getX(), y0 = v0.getY(), z0 = v0.getZ(), x1 = v1.getX(), y1 = v1
				.getY(), z1 = v1.getZ();

		Vector3F rv = new Vector3F(y0 * z1 - z0 * y1, x1 * z0 - z1 * x0, x0
				* y1 - y0 * x1);
		rv.normalize();
		return rv;
	}

	private Point3F[] vertices;

	private Polygon[] polygons;

	private List<Triangle> triangles = new ArrayList<Triangle>();

	private String file;
	
	public OB3Converter(String inFile, String outFile) throws Throwable
	{
		file = inFile;
		DataInputStream dis = new DataInputStream(
				ResourceLoader.loadResource(inFile));
		vertices = new Point3F[dis.readShort()];
		polygons = new Polygon[dis.readShort()];
		for (int i = 0; i < vertices.length; ++i)
		{
			vertices[i] = new Point3F();
			vertices[i].setX(-dis.readShort());
		}
		for (int i = 0; i < vertices.length; ++i)
			vertices[i].setY(-dis.readShort());
		for (int i = 0; i < vertices.length; ++i)
			vertices[i].setZ(-dis.readShort());

		int[] faceSizes = new int[polygons.length];
		for (int i = 0; i < polygons.length; i++)
		{
			faceSizes[i] = dis.readByte() & 0xFF;
		}

		int[] frontOverlay = new int[polygons.length];
		for (int i = 0; i < polygons.length; i++)
		{
			frontOverlay[i] = dis.readShort();
			if (frontOverlay[i] == 32767)
			{
				frontOverlay[i] = 0;
			}
		}
		int[] backOverlay = new int[polygons.length];
		for (int i = 0; i < polygons.length; i++)
		{
			backOverlay[i] = dis.readShort();
			if (backOverlay[i] == 32767)
			{
				backOverlay[i] = 0;
			}
		}
		for (int i = 0; i < polygons.length; i++)
		{
			dis.readByte(); // We're doing our own lighting based on normals.
		}

		int[] colors = new int[polygons.length];
		int[] textures = new int[polygons.length];
		for (int i = 0; i < polygons.length; ++i)
		{
			colors[i] = frontOverlay[i];
			textures[i] = backOverlay[i];
		}

		for (int i = 0; i < polygons.length; i++)
		{
			int[] indices = new int[faceSizes[i]];
			for (int vertexPtr = 0; vertexPtr < faceSizes[i]; vertexPtr++)
			{
				if (vertices.length < 256)
				{
					indices[vertexPtr] = dis.readByte() & 0xFF;
				} else
				{
					indices[vertexPtr] = dis.readShort();
				}
			}

			// Here, each polygon has the vertex data, bounds data, overlay
			// data, normal data...
			polygons[i] = new Polygon(indices, colors[i], textures[i]);
		}

		// Front-Faces
		for (Polygon p : polygons)
		{
			List<Pair> tris = triangulateEx(p);
			for (int i = 0; i < tris.size(); i += 3)
			{
				triangles.add(new Triangle(new Pair[] { tris.get(i),
						tris.get(i + 1), tris.get(i + 2) }, p.normal, p.color,
						p.texture));

			}
		}
		FileWriter fw = new FileWriter(outFile);
		fw.write("# OB3 -> OBJ Test\n");
		List<Point3F> fv = new ArrayList<Point3F>();
		List<Vector3F> fn = new ArrayList<Vector3F>();

		for (Triangle t : triangles)
		{
			if (!fv.contains(t.a))
			{
				fv.add(t.a);
			}
			if (!fv.contains(t.b))
			{
				fv.add(t.b);
			}
			if (!fv.contains(t.c))
			{
				fv.add(t.c);
			}
			if (!fn.contains(t.normal))
			{
				fn.add(t.normal);
			}
		}

		MeshDataStruct mds = smoothNormals(triangles);
		for (Point3F v : mds.pointBuffer)
		{
			fw.write("v " + v.getX() + " " + v.getY() + " " + v.getZ() + "\n");
		}
		fw.write("\n");
		for (Vector3F n : mds.normalBuffer)
		{
			fw.write("vn " + n.getX() + " " + n.getY() + " " + n.getZ() + "\n");
		}
		fw.write("\n");

		for (Integer i : mds.colorBuffer)
		{
			float[] rgba = getRGBA(i);
			fw.write("c " + rgba[0] + " " + rgba[1] + " " + rgba[2] + "\n");
		}
		fw.write("\n");
		for (Point2F p : mds.texCoordBuffer)
		{
			fw.write("t " + p.getX() + " " + p.getY() + "\n");
		}
		fw.write("\n");
		for (int i = 0; i < mds.attributesBuffer.size(); ++i)
		{
			VertexAttributes va = mds.attributesBuffer.get(i);
			if (i % 3 == 0)
			{
				fw.write("\nf");
			}
			fw.write(" " + va.getPositionIndex() + "/" + va.getColorIndex()
					+ "/" + va.getNormalIndex() + "/" + va.getTexCoordsIndex());
		}
		fw.write("\n");
		fw.close();
	}

	static boolean epsilonEquals(float a, float b)
	{
		return Math.abs(a - b) < 0.01f;
	}

	class MeshDataStruct
	{
		List<Point3F> pointBuffer;
		List<Vector3F> normalBuffer;
		List<Integer> colorBuffer;
		List<Point2F> texCoordBuffer;
		List<VertexAttributes> attributesBuffer;

		MeshDataStruct(List<Point3F> a, List<Vector3F> b, List<Integer> c,
				List<Point2F> d, List<VertexAttributes> e)
		{
			this.pointBuffer = a;
			this.normalBuffer = b;
			this.colorBuffer = c;
			this.texCoordBuffer = d;
			this.attributesBuffer = e;
		}
	}

	private MeshDataStruct smoothNormals(List<Triangle> triangles)
	{

		List<Point3F> points = new ArrayList<>();
		List<Vector3F> normals = new ArrayList<>();
		Map<Vector3F, List<Point3F>> normalsRelations = new HashMap<>();
		Map<Point3F, Vector3F> pointNormals = new HashMap<>();
		List<VertexAttributes> attribs = new ArrayList<>();
		List<Integer> colors = new ArrayList<Integer>();
		List<Point2F> texCoords = new ArrayList<Point2F>();
		for (Triangle t : triangles)
		{
			if (t.overlay1 > 0 || t.overlay2 > 0)
			{
				for (Point2F p : t.texcoords)
				{
					if (!texCoords.contains(p))
					{
						texCoords.add(p);
					}
				}
			}
			if (t.overlay1 < 0)
			{
				if (!colors.contains(t.overlay1))
				{
					colors.add(t.overlay1);
				}
			} else
			{
				if (!colors.contains(0))
				{
					colors.add(0);
				}
			}
			if (t.overlay2 < 0)
			{
				if (!colors.contains(t.overlay2))
				{
					colors.add(t.overlay2);
				}
			} else
			{
				if (!colors.contains(0))
				{
					colors.add(0);
				}
			}
			for (Point3F v : Arrays.asList(t.a, t.b, t.c))
			{
				if (!points.contains(new Point3F(v.getX(), v.getY(), v.getZ())))
				{
					points.add(new Point3F(v.getX(), v.getY(), v.getZ()));
				}
			}
		}
		// Now the vertices should be populated.
		for (Point3F point : points)
		{
			List<Point3F> localPoints = new ArrayList<>();
			List<Vector3F> localNormals = new ArrayList<>();
			for (Triangle t : triangles)
			{
				if (epsilonEquals(t.a.getX(), point.getX())
						&& epsilonEquals(t.a.getY(), point.getY())
						&& epsilonEquals(t.a.getZ(), point.getZ()))
				{
					localPoints.add(new Point3F(t.a.getX(), t.a.getY(), t.a
							.getZ()));
					localNormals.add(t.normal);
				}
				if (epsilonEquals(t.b.getX(), point.getX())
						&& epsilonEquals(t.b.getY(), point.getY())
						&& epsilonEquals(t.b.getZ(), point.getZ()))
				{
					localPoints.add(new Point3F(t.b.getX(), t.b.getY(), t.b
							.getZ()));
					localNormals.add(t.normal);
				}
				if (epsilonEquals(t.c.getX(), point.getX())
						&& epsilonEquals(t.c.getY(), point.getY())
						&& epsilonEquals(t.c.getZ(), point.getZ()))
				{
					localPoints.add(new Point3F(t.c.getX(), t.c.getY(), t.c
							.getZ()));
					localNormals.add(t.normal);
				}
			}
			Vector3F normal = average(localNormals
					.toArray(new Vector3F[localNormals.size()]));
			normalsRelations.put(normal, localPoints);
			pointNormals.put(point, normal);
		}
		for (Vector3F n : normalsRelations.keySet())
		{
			normals.add(n);
		}

		for (Triangle t : triangles)
		{
			int i = 0;
			for (Point3F p : Arrays.asList(t.a, t.b, t.c))
			{
				attribs.add(new VertexAttributes(points.indexOf(p) + 1, normals
						.indexOf(pointNormals.get(p)) + 1, colors
						.indexOf(t.overlay1 < 0 ? t.overlay1
								: (t.overlay2 < 0 ? t.overlay2 : -1)) + 1,
						(t.overlay1 > 0 || t.overlay2 > 0) ? texCoords
								.indexOf(t.texcoords[i]) + 1 : 0));
				i++;
			}
		}

		return new MeshDataStruct(points, normals, colors, texCoords, attribs);
	}

	static Vector3F average(Vector3F[] vectors)
	{
		Vector3F rv = new Vector3F();
		for (Vector3F v : vectors)
		{
			rv.add(v);
		}
		rv.scale(1.0f / vectors.length);
		return rv;
	}

	private class Triangle
	{
		Point3F a, b, c;
		Vector3F normal;
		int overlay1;
		int overlay2;
		Point2F[] texcoords;
		final static float SS_WIDTH = 1024;
		final static float T_WIDTH = 128;
		Triangle(Pair[] vertices, Vector3F normal, int color, int texture)
		{
			this.a = vertices[0].coord;
			this.b = vertices[1].coord;
			this.c = vertices[2].coord;
			this.normal = normal;
			this.overlay1 = color;
			this.overlay2 = texture;
						
			this.texcoords = new Point2F[] { vertices[0].texCoord,
					vertices[1].texCoord, vertices[2].texCoord };
			int textureID = overlay1 > 0 ? overlay1 : (overlay2 > 0 ? overlay2 : 0);
			for(Point2F tex : texcoords)
			{
				tex.setX(tex.getX() / (SS_WIDTH / T_WIDTH));
				tex.setY(tex.getY() / (SS_WIDTH / T_WIDTH));
				float xoff = ((textureID % (SS_WIDTH / T_WIDTH)) * (T_WIDTH / SS_WIDTH));
				tex.setX(tex.getX() + xoff);
				float yoff = ((textureID / (int)(SS_WIDTH / T_WIDTH)) * (T_WIDTH / SS_WIDTH));
				tex.setY(tex.getY() + yoff);
			}
		}
	}

	static class Pair
	{
		public Point3F coord;
		public Point2F texCoord;

		public Pair(Point3F coord, Point2F texCoord)
		{
			this.coord = coord;
			this.texCoord = texCoord;
		}
	}

	private List<Pair> triangulateEx(Polygon p)
	{
		List<Pair> rvEx = new ArrayList<>();
		Vector3F axis = new Vector3F(p.normal);
		// haaaaaaaaaaaaaaaaaaack ;) can't lose precision during calculations
		Vector3F target = new Vector3F(0.000000000000000001f,
				0.000000000000001f, 1.0f);
		axis.cross(target);
		axis.normalize();
		List<Point3F> rv = new ArrayList<>();
		float angle = (float) Math.acos(p.normal.dot(new Vector3F(0, 0, 1)));
		float s = (float) Math.sin(angle);
		float c = (float) Math.cos(angle);
		float x = axis.getX(), y = axis.getY(), z = axis.getZ();
		float[] matrix = new float[16];
		matrix[0] = x * x * (1 - c) + c;
		matrix[1] = x * y * (1 - c) - (z * s);
		matrix[2] = x * z * (1 - c) + (y * s);

		matrix[4] = y * x * (1 - c) + (z * s);
		matrix[5] = y * y * (1 - c) + c;
		matrix[6] = y * z * (1 - c) - (x * s);

		matrix[8] = x * z * (1 - c) - (y * s);
		matrix[9] = y * z * (1 - c) + (x * s);
		matrix[10] = z * z * (1 - c) + c;

		float cachedZ = 0;

		for (int i : p.vertices)
		{
			Point3F pt = new Point3F(vertices[i]);
			float nx = pt.getX() * matrix[0] + pt.getY() * matrix[1]
					+ pt.getZ() * matrix[2];
			float ny = pt.getX() * matrix[4] + pt.getY() * matrix[5]
					+ pt.getZ() * matrix[6];
			float nz = pt.getX() * matrix[8] + pt.getY() * matrix[9]
					+ pt.getZ() * matrix[10];
			pt.set(nx, ny, nz);
			cachedZ = nz;
			rv.add(pt);
		}

		x = 0;
		y = 0;
		z = 0;
		boolean bx = true, by = true, bz = true;
		boolean init = true;
		for (Point3F v : rv)
		{
			if (init)
			{
				init = false;
				x = v.getX();
				y = v.getY();
				z = v.getZ();
				continue;
			}
			if (!epsilonEquals(x, v.getX()))
				bx = false;
			if (!epsilonEquals(y, v.getY()))
				by = false;
			if (!epsilonEquals(z, v.getZ()))
				bz = false;
		}
		if (!bx && !by && !bz)
		{
			System.out.println("Plane Rotation Failed (" + file + ")");
//			System.exit(-1);
		}
		List<Tuple2F> _2dVerts = new ArrayList<>();
		for (Point3F v : rv)
		{
			_2dVerts.add(new Point2F(v.getX(), v.getY()));
		}
		_2dVerts = new TriangulatorImpl().computeTriangles(_2dVerts);

		rv.clear();
		angle = -angle;
		s = (float) Math.sin(angle);
		c = (float) Math.cos(angle);
		x = axis.getX();
		y = axis.getY();
		z = axis.getZ();
		matrix = new float[16];
		matrix[0] = x * x * (1 - c) + c;
		matrix[1] = x * y * (1 - c) - (z * s);
		matrix[2] = x * z * (1 - c) + (y * s);

		matrix[4] = y * x * (1 - c) + (z * s);
		matrix[5] = y * y * (1 - c) + c;
		matrix[6] = y * z * (1 - c) - (x * s);

		matrix[8] = x * z * (1 - c) - (y * s);
		matrix[9] = y * z * (1 - c) + (x * s);
		matrix[10] = z * z * (1 - c) + c;
		for (int i = 0; i < _2dVerts.size(); i += 3)
		{
			Point2F a2 = new Point2F(_2dVerts.get(i).getX(), _2dVerts.get(i).getY());
			Point2F b2 = new Point2F(_2dVerts.get(i + 1).getX(), _2dVerts.get(i + 1).getY());
			Point2F c2 = new Point2F(_2dVerts.get(i + 2).getX(), _2dVerts.get(i + 2).getY());
			/** Texture Gen */
			
			float minX = Float.MAX_VALUE, maxX = Float.MIN_VALUE, minY = Float.MAX_VALUE, maxY = Float.MIN_VALUE;

			for (Point2F v : Arrays.asList(a2, b2, c2))
			{
				if (v.getX() < minX)
					minX = v.getX();
				if (v.getX() > maxX)
					maxX = v.getX();

				if (v.getY() < minY)
					minY = v.getY();
				if (v.getY() > maxY)
					maxY = v.getY();
			}
			float minFactor = Float.MAX_VALUE;
			for (float val : Arrays.asList(minX, minY))
			{
				if (minFactor > val)
				{
					minFactor = val;
				}
			}
			if (minFactor < 0)
			{
				minX -= minFactor;
				minY -= minFactor;
				maxX -= minFactor;
				maxY -= minFactor;
			}
			BoundingBox bounds = new BoundingBox(new Point2F(minX, minY),
					new Point2F(maxX, maxY));
			for (Point2F p2 : Arrays.asList(a2, b2, c2))
			{
				if(minFactor < 0)
				{
					p2.setX(p2.getX() - minFactor);
					p2.setY(p2.getY() - minFactor);
				}
				p2.setX(maxX - p2.getX());
				p2.setY(maxY - p2.getY());
			
			//	p2.setX(1.0f - p2.getX());
			//	p2.setY(1.0f - p2.getY());
				if (bounds.xWidth() != 0)
					p2.setX(p2.getX() / bounds.xWidth());
				if (bounds.yWidth() != 0)
					p2.setY(p2.getY() / bounds.yWidth());
				if(p2.getX() == Float.POSITIVE_INFINITY)
				{
					p2.setX(0);
				}
				if(p2.getY() == Float.POSITIVE_INFINITY)
				{
					p2.setY(0);
				}
				p2.clamp(0.0f, 1.0f);
			}

			Point2F[] texCoords = new Point2F[] {
					new Point2F(a2.getX(), a2.getY()),
					new Point2F(b2.getX(), b2.getY()),
					new Point2F(c2.getX(), c2.getY()) };
			
			/** Texture Gen */
			for (int j = 0; j < 3; ++j)
			{
				Tuple2F t = _2dVerts.get(i + j);
				float nx = t.getX() * matrix[0] + t.getY() * matrix[1]
						+ cachedZ * matrix[2];
				float ny = t.getX() * matrix[4] + t.getY() * matrix[5]
						+ cachedZ * matrix[6];
				float nz = t.getX() * matrix[8] + t.getY() * matrix[9]
						+ cachedZ * matrix[10];
				rvEx.add(new Pair(new Point3F(nx, ny, nz), texCoords[j]));
			}
		}
		return rvEx;
	}

	private static float[] getRGBA(int rscColor)
	{
		rscColor = -1 - rscColor;
		int r = (rscColor >> 10 & 0x1f) * 8;
		int g = (rscColor >> 5 & 0x1f) * 8;
		int b = (rscColor & 0x1f) * 8;
		int rgb = 0;
		for (int i = 0; i < 256; i++)
		{
			int j6 = i * i;
			int red = (r * j6) / 0x10000;
			int green = (g * j6) / 0x10000;
			int blue = (b * j6) / 0x10000;
			rgb = ((red << 16) + (green << 8) + blue);
		}
		return new float[] { ((rgb >> 16) & 0xFF) / 255.0f,
				((rgb >> 8) & 0xFF) / 255.0f, (rgb & 0xFF) / 255.0f };
	}

	public static void main(String[] $) throws Throwable
	{
		Files.walkFileTree(
			Paths.get("models/ob3/"),
			new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
	                 throws
	                 IOException
	             {
					String in = file.toString();
					try
					{
						new OB3Converter(in, in.replaceAll("ob3", "obj"));
					} catch (Throwable e)
					{
						e.printStackTrace();
					}
	                 return FileVisitResult.CONTINUE;
	             }
			}
		);
	}

}
