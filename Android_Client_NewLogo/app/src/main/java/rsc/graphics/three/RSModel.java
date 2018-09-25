package rsc.graphics.three;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import rsc.MiscFunctions;
import rsc.buffers.RSBufferUtils;
import rsc.util.FastMath;
import rsc.util.GenUtil;

public final class RSModel {
	private int appliedTransform;
	private int diffuseDirX = 180;
	private int diffuseDirY = 155;
	private int diffuseDirZ = 95;
	private int diffuseMag = 256;
	int diffuseParam1 = 32;
	private int diffuseParam2 = 512;
	private boolean dontComputeDiffuse = false;
	private int faceCount;
	public int[] faceDiffuseLight;
	public int faceHead;
	public int[] faceIndexCount;
	public int[][] faceIndices;
	private int[] faceMaxX;
	private int[] faceMaxY;
	private int[] faceMaxZ;
	private int[] faceMinX;
	private int[] faceMinY;
	private int[] faceMinZ;
	private int[] faceNormX;
	private int[] faceNormY;
	private int[] faceNormZ;
	private int[][] faceParam1;
	int[] scenePolyNormalShift;
	int[] scenePolyNormalMagnitude;
	int[] faceTextureBack;
	int[] faceTextureFront;
	public int key = -1;
	private boolean m_b = false;
	private boolean m_c = false;
	public boolean m_cb = false;
	boolean m_db = false;
	boolean m_dc = true;
	public int[] facePickIndex;
	private int m_hb;
	int m_hc = 0;
	boolean m_Kb = false;
	private boolean m_v = false;
	private final int m_Vb = 12345678;
	int m_Yb = 1;
	byte[] m_zb;
	private int maxFaceDimension = 12345678;
	private int maxX;
	private int maxY;
	private int maxZ;
	private int minX;
	private int minY;
	private int minZ;
	private int rot256X;
	private int rot256Y;
	private int rot256Z;
	private int rotM_xToY;
	private int rotM_xToZ;
	private int rotM_yToX;
	private int rotM_yToZ;
	private int rotM_zToX;
	private int rotM_zToY;
	private int scaleX;
	private int scaleY;
	private int scaleZ;
	private int translateX;
	private int translateY;
	private int translateZ;
	int[] vertDiffuseLight;
	private int vertexCount2;
	int[] vertexParam2;
	int[] vertexParam6;
	byte[] vertLightOther;
	int vertHead;
	int[] vertX;
	int[] vertXRot;
	private int[] vertXTransform;
	private int[] vertY;
	int[] vertYRot;
	private int[] vertYTransform;
	int[] vertZ;
	int[] vertZRot;
	private int[] vertZTransform;

	public RSModel(byte[] data, int offset, boolean var3) {
		try {
			int vertexCount = RSBufferUtils.get16(offset, data);
			offset += 2;
			int faceCount = RSBufferUtils.get16(offset, data);
			offset += 2;
			this.setFaceVertexCount(faceCount, vertexCount, (int) 115);
			this.faceParam1 = new int[faceCount][1];

			int j;
			for (j = 0; vertexCount > j; ++j) {
				this.vertX[j] = RSBufferUtils.readShort(data, -1, offset);
				offset += 2;
			}

			for (j = 0; j < vertexCount; ++j) {
				this.vertY[j] = RSBufferUtils.readShort(data, -1, offset);
				offset += 2;
			}

			for (j = 0; vertexCount > j; ++j) {
				this.vertZ[j] = RSBufferUtils.readShort(data, -1, offset);
				offset += 2;
			}

			this.vertHead = vertexCount;

			for (j = 0; j < faceCount; ++j) {
				this.faceIndexCount[j] = FastMath.bitwiseAnd(255, data[offset++]);
			}

			for (j = 0; j < faceCount; ++j) {
				this.faceTextureFront[j] = RSBufferUtils.readShort(data, -1, offset);
				if (this.faceTextureFront[j] == 32767) {
					this.faceTextureFront[j] = this.m_Vb;
				}

				offset += 2;
			}

			for (j = 0; faceCount > j; ++j) {
				this.faceTextureBack[j] = RSBufferUtils.readShort(data, -1, offset);
				offset += 2;
				if (32767 == this.faceTextureBack[j]) {
					this.faceTextureBack[j] = this.m_Vb;
				}
			}

			int i;
			for (j = 0; j < faceCount; ++j) {
				i = 255 & data[offset++];
				if (i != 0) {
					this.faceDiffuseLight[j] = this.m_Vb;
				} else {
					this.faceDiffuseLight[j] = 0;
				}
			}

			for (j = 0; faceCount > j; ++j) {
				this.faceIndices[j] = new int[this.faceIndexCount[j]];

				for (i = 0; this.faceIndexCount[j] > i; ++i) {
					if (vertexCount < 256) {
						this.faceIndices[j][i] = FastMath.bitwiseAnd(255, data[offset++]);
					} else {
						this.faceIndices[j][i] = RSBufferUtils.get16(offset, data);
						offset += 2;
					}
				}
			}

			this.faceHead = faceCount;
			this.m_Yb = 1;
		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8,
					"ca.<init>(" + (data != null ? "{...}" : "null") + ',' + offset + ',' + var3 + ')');
		}
	}

	public RSModel(int vertexCount, int faceCount) {
		try {
			this.setFaceVertexCount(faceCount, vertexCount, (int) 69);
			this.faceParam1 = new int[faceCount][1];

			for (int face = 0; face < faceCount; face++) {
				this.faceParam1[face][0] = face;
			}

		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "ca.<init>(" + vertexCount + ',' + faceCount + ')');
		}
	}

	RSModel(int vertexLimit, int faceLimit, boolean var3, boolean var4, boolean var5, boolean var6, boolean var7) {
		try {
			this.m_c = var4;
			this.m_b = var7;
			this.m_db = var6;
			this.m_v = var3;
			this.dontComputeDiffuse = var5;
			this.setFaceVertexCount(faceLimit, vertexLimit, (int) 69);
		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9, "ca.<init>(" + vertexLimit + ',' + faceLimit + ',' + var3 + ',' + var4
					+ ',' + var5 + ',' + var6 + ',' + var7 + ')');
		}
	}

	private RSModel(RSModel[] models, int modelCount) {
		try {
			this.addModels(0, models, true, modelCount);
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4,
					"ca.<init>(" + (models != null ? "{...}" : "null") + ',' + modelCount + ')');
		}
	}

	private RSModel(RSModel[] var1, int var2, boolean var3, boolean var4, boolean noDiffuseCompute, boolean var6) {
		try {
			this.dontComputeDiffuse = noDiffuseCompute;
			this.m_c = var4;
			this.m_db = var6;
			this.m_v = var3;
			this.addModels(0, var1, false, var2);
		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8, "ca.<init>(" + (var1 != null ? "{...}" : "null") + ',' + var2 + ',' + var3
					+ ',' + var4 + ',' + noDiffuseCompute + ',' + var6 + ')');
		}
	}

	public RSModel(String var1) {
		try {
			byte[] var23;
			try {
				InputStream var5 = GenUtil.chooseStreamFor(var1);
				DataInputStream var6 = new DataInputStream(var5);
				var23 = new byte[3];
				int var21 = 0;
				this.m_hb = 0;

				while (true) {
					if (var21 >= 3) {
						int var22 = this.getFaceParam1((byte) 76, (byte[]) var23);
						var21 = 0;
						this.m_hb = 0;

						for (var23 = new byte[var22]; var22 > var21; var21 += var6.read(var23, var21, var22 - var21)) {
							;
						}

						var6.close();
						break;
					}

					var21 += var6.read(var23, var21, 3 - var21);
				}
			} catch (IOException var19) {
				this.faceHead = 0;
				this.vertHead = 0;
				return;
			}

			int var24 = this.getFaceParam1((byte) 76, (byte[]) var23);
			int var25 = this.getFaceParam1((byte) 76, (byte[]) var23);
			this.setFaceVertexCount(var25, var24, (int) 97);
			this.faceParam1 = new int[var25][];

			int face;
			for (face = 0; var24 > face; ++face) {
				int var7 = this.getFaceParam1((byte) 76, (byte[]) var23);
				int var8 = this.getFaceParam1((byte) 76, (byte[]) var23);
				int var9 = this.getFaceParam1((byte) 76, (byte[]) var23);
				this.insertVertex(var7, var8, var9);
			}

			for (face = 0; face < var25; ++face) {
				int var10 = this.getFaceParam1((byte) 76, (byte[]) var23);
				int var11 = this.getFaceParam1((byte) 76, (byte[]) var23);
				int var12 = this.getFaceParam1((byte) 76, (byte[]) var23);
				int var13 = this.getFaceParam1((byte) 76, (byte[]) var23);
				this.diffuseParam2 = this.getFaceParam1((byte) 76, (byte[]) var23);
				this.diffuseParam1 = this.getFaceParam1((byte) 76, (byte[]) var23);
				int var26 = this.getFaceParam1((byte) 76, (byte[]) var23);
				int[] var16 = new int[var10];

				for (int var17 = 0; var17 < var10; ++var17) {
					var16[var17] = this.getFaceParam1((byte) 76, (byte[]) var23);
				}

				int[] fP1 = new int[var13];

				for (int i = 0; var13 > i; ++i) {
					fP1[i] = this.getFaceParam1((byte) 76, (byte[]) var23);
				}

				int fID = this.insertFace(var10, var16, var11, var12, false);
				this.faceParam1[face] = fP1;
				if (var26 == 0) {
					this.faceDiffuseLight[fID] = 0;
				} else {
					this.faceDiffuseLight[fID] = this.m_Vb;
				}
			}

			this.m_Yb = 1;
		} catch (RuntimeException var20) {
			throw GenUtil.makeThrowable(var20, "ca.<init>(" + (var1 != null ? "{...}" : "null") + ')');
		}
	}

	final RSModel copyModel(boolean var1, int var2, boolean noDiffuse, boolean var4, boolean var5) {
		try {
			
			RSModel[] var6 = new RSModel[] { this };
			RSModel copy = new RSModel(var6, 1, var4, var5, noDiffuse, var1);
			copy.m_hc = this.m_hc;
			return copy;
		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9,
					"ca.Q(" + var1 + ',' + var2 + ',' + noDiffuse + ',' + var4 + ',' + var5 + ')');
		}
	}

	private final void addModels(int offset, RSModel[] models, boolean var3, int modelCount) {
		try {
			
			int faceCount = 0;
			int vertCount = 0;

			int i;
			for (i = 0; i < modelCount; ++i) {
				faceCount += models[i].faceHead;
				vertCount += models[i].vertHead;
			}

			this.setFaceVertexCount(faceCount, vertCount, (int) 88);
			if (var3) {
				this.faceParam1 = new int[faceCount][];
			}

			for (i = offset; i < modelCount; ++i) {
				RSModel model = models[i];
				model.commitTransform((byte) -28);
				this.diffuseMag = model.diffuseMag;
				this.diffuseDirX = model.diffuseDirX;
				this.diffuseDirY = model.diffuseDirY;
				this.diffuseParam2 = model.diffuseParam2;
				this.diffuseDirZ = model.diffuseDirZ;
				this.diffuseParam1 = model.diffuseParam1;

				for (int f = 0; model.faceHead > f; ++f) {
					int[] newIndices = new int[model.faceIndexCount[f]];
					int[] srcIndices = model.faceIndices[f];

					int v;
					for (v = 0; model.faceIndexCount[f] > v; ++v) {
						newIndices[v] = this.insertVertex(model.vertX[srcIndices[v]], model.vertY[srcIndices[v]],
								model.vertZ[srcIndices[v]]);
					}

					v = this.insertFace(model.faceIndexCount[f], newIndices, model.faceTextureFront[f],
							model.faceTextureBack[f], false);
					this.faceDiffuseLight[v] = model.faceDiffuseLight[f];
					this.scenePolyNormalShift[v] = model.scenePolyNormalShift[f];
					this.scenePolyNormalMagnitude[v] = model.scenePolyNormalMagnitude[f];
					if (var3) {
						int j;
						if (modelCount <= 1) {
							this.faceParam1[v] = new int[model.faceParam1[f].length];

							for (j = 0; j < model.faceParam1[f].length; ++j) {
								this.faceParam1[v][j] = model.faceParam1[f][j];
							}
						} else {
							this.faceParam1[v] = new int[model.faceParam1[f].length + 1];
							this.faceParam1[v][0] = i;

							for (j = 0; j < model.faceParam1[f].length; ++j) {
								this.faceParam1[v][1 + j] = model.faceParam1[f][j];
							}
						}
					}
				}
			}

			this.m_Yb = 1;
		} catch (RuntimeException var14) {
			throw GenUtil.makeThrowable(var14, "ca.KA(" + offset + ',' + (models != null ? "{...}" : "null") + ','
					+ var3 + ',' + modelCount + ')');
		}
	}

	public final void addRotation(int rotX, int rotY, int rotZ) {
		try {
			
			this.rot256X = rotX + this.rot256X & 255;
			this.rot256Y = rotY + this.rot256Y & 255;
			this.rot256Z = 255 & this.rot256Z + rotZ;
			this.computeAppliedTransform();
			this.m_Yb = 1;
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "ca.GA(" + rotZ + ',' + "dummy" + ',' + rotY + ',' + rotX + ')');
		}
	}

	/**
	 * 0-255 -> 0-1
	 */
	private final void applyRotMatrix(int xToZ, int zToY, int yToX, int yToZ, int zToX, int xToY, byte var7) {
		try {
			

			for (int i = 0; i < this.vertHead; ++i) {
				if (yToX != 0) {
					this.vertXTransform[i] += this.vertYTransform[i] * yToX >> 8;
				}
				if (yToZ != 0) {
					this.vertZTransform[i] += yToZ * this.vertYTransform[i] >> 8;
				}
				if (zToX != 0) {
					this.vertXTransform[i] += zToX * this.vertZTransform[i] >> 8;
				}
				if (zToY != 0) {
					this.vertYTransform[i] += zToY * this.vertZTransform[i] >> 8;
				}
				if (xToZ != 0) {
					this.vertZTransform[i] += xToZ * this.vertXTransform[i] >> 8;
				}
				if (xToY != 0) {
					this.vertYTransform[i] += this.vertXTransform[i] * xToY >> 8;
				}
			}

		} catch (RuntimeException var10) {
			throw GenUtil.makeThrowable(var10,
					"ca.E(" + xToZ + ',' + zToY + ',' + yToX + ',' + yToZ + ',' + zToX + ',' + xToY + ',' + var7 + ')');
		}
	}

	private final void calculateBoundingBoxes(int var1) {
		try {
			
			this.minX = 999999;
			this.minZ = 999999;
			this.maxZ = -999999;
			this.minY = var1;
			this.maxX = -999999;
			this.maxY = -999999;
			this.maxFaceDimension = -999999;

			for (int face = 0; face < this.faceHead; ++face) {
				int[] fIndex = this.faceIndices[face];
				int fIndexCount = this.faceIndexCount[face];
				int vID = fIndex[0];
				int minZ;
				int maxZ = minZ = this.vertZTransform[vID];
				int minY;
				int maxY = minY = this.vertYTransform[vID];
				int minX;
				int maxX = minX = this.vertXTransform[vID];

				for (int vert = 0; fIndexCount > vert; ++vert) {
					vID = fIndex[vert];
					if (this.vertZTransform[vID] >= minZ) {
						if (this.vertZTransform[vID] > maxZ) {
							maxZ = this.vertZTransform[vID];
						}
					} else {
						minZ = this.vertZTransform[vID];
					}

					if (this.vertYTransform[vID] < minY) {
						minY = this.vertYTransform[vID];
					} else if (this.vertYTransform[vID] > maxY) {
						maxY = this.vertYTransform[vID];
					}

					if (minX <= this.vertXTransform[vID]) {
						if (this.vertXTransform[vID] > maxX) {
							maxX = this.vertXTransform[vID];
						}
					} else {
						minX = this.vertXTransform[vID];
					}
				}

				if (!this.m_c) {
					this.faceMinX[face] = minX;
					this.faceMaxX[face] = maxX;
					this.faceMinY[face] = minY;
					this.faceMaxY[face] = maxY;
					this.faceMinZ[face] = minZ;
					this.faceMaxZ[face] = maxZ;
				}

				if (maxX - minX > this.maxFaceDimension) {
					this.maxFaceDimension = maxX - minX;
				}

				if (maxY - minY > this.maxFaceDimension) {
					this.maxFaceDimension = maxY - minY;
				}

				if (this.maxX < maxX) {
					this.maxX = maxX;
				}

				if (maxZ > this.maxZ) {
					this.maxZ = maxZ;
				}

				if (maxZ - minZ > this.maxFaceDimension) {
					this.maxFaceDimension = maxZ - minZ;
				}

				if (this.maxY < maxY) {
					this.maxY = maxY;
				}

				if (minX < this.minX) {
					this.minX = minX;
				}

				if (minY < this.minY) {
					this.minY = minY;
				}

				if (this.minZ > minZ) {
					this.minZ = minZ;
				}
			}

		} catch (RuntimeException var13) {
			throw GenUtil.makeThrowable(var13, "ca.C(" + var1 + ')');
		}
	}

	private final void clearRotDataAndParams26(byte var1) {
		try {
			if (var1 < 49) {
				this.setDiffuseLight(40, 102, 104, 108, -20, -89);
			}

			this.vertexParam2 = new int[this.vertHead];
			this.vertXRot = new int[this.vertHead];
			this.vertYRot = new int[this.vertHead];
			this.vertZRot = new int[this.vertHead];
			this.vertexParam6 = new int[this.vertHead];
			
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ca.H(" + var1 + ')');
		}
	}

	@Override
	public final RSModel clone() {
		try {
			
			RSModel[] var2 = new RSModel[] { this };
			RSModel var3 = new RSModel(var2, 1);
			var3.m_cb = this.m_cb;
			var3.m_hc = this.m_hc;
			return var3;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "ca.IA(" + -2 + ')');
		}
	}

	private final void commitTransform(byte var1) {
		try {
			this.resetTransformCache((int) 7972);
			

			for (int i = 0; i < this.vertHead; ++i) {
				this.vertX[i] = this.vertXTransform[i];
				this.vertY[i] = this.vertYTransform[i];
				this.vertZ[i] = this.vertZTransform[i];
			}

			this.rot256Z = 0;
			this.rot256X = 0;
			this.rotM_xToZ = 256;
			this.rotM_zToY = 256;
			this.translateY = 0;
			this.rot256Y = 0;
			this.rotM_xToY = 256;
			this.rotM_zToX = 256;
			this.scaleX = 256;
			this.appliedTransform = 0;
			this.translateX = 0;
			this.scaleY = 256;
			this.rotM_yToX = 256;
			this.scaleZ = 256;
			this.translateZ = 0;
			this.rotM_yToZ = 256;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ca.R(" + -28 + ')');
		}
	}

	private final void computeAppliedTransform() {
		try {
			
			if (this.rotM_yToX == 256 && this.rotM_yToZ == 256 && this.rotM_zToX == 256 && this.rotM_zToY == 256
					&& this.rotM_xToZ == 256 && this.rotM_xToY == 256) {
				if (this.scaleX == 256 && this.scaleY == 256 && this.scaleZ == 256) {
					if (this.rot256X == 0 && this.rot256Y == 0 && this.rot256Z == 0) {
						if (this.translateX == 0 && this.translateY == 0 && this.translateZ == 0) {
							this.appliedTransform = 0;
						} else {
							this.appliedTransform = 1;
						}
					} else {
						this.appliedTransform = 2;
					}
				} else {
					this.appliedTransform = 3;
				}
			} else {
				this.appliedTransform = 4;
			}
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ca.A(" + "dummy" + ')');
		}
	}

	private final void computeDiffuse(int var1) {
		try {
			
			if (!this.dontComputeDiffuse) {
				int diffuseDivide = this.diffuseParam2 * this.diffuseMag >> 8;

				for (int f = 0; this.faceHead > f; ++f) {
					if (this.faceDiffuseLight[f] != this.m_Vb) {
						this.faceDiffuseLight[f] = (this.faceNormY[f] * this.diffuseDirY
								+ this.faceNormX[f] * this.diffuseDirX + this.diffuseDirZ * this.faceNormZ[f])
								/ diffuseDivide;
					}
				}

				int[] tmpXNorm = new int[this.vertHead];
				int[] tmpYNorm = new int[this.vertHead];
				int[] tmpZNorm = new int[this.vertHead];
				int[] faceCount = new int[this.vertHead];

				int var7;
				for (var7 = 0; this.vertHead > var7; ++var7) {
					tmpXNorm[var7] = 0;
					tmpYNorm[var7] = 0;
					tmpZNorm[var7] = 0;
					faceCount[var7] = 0;
				}

				var7 = -16 / ((var1 + 55) / 32);

				int i;
				for (i = 0; i < this.faceHead; ++i) {
					if (this.m_Vb == this.faceDiffuseLight[i]) {
						for (int fi = 0; this.faceIndexCount[i] > fi; ++fi) {
							int fVert = this.faceIndices[i][fi];
							tmpXNorm[fVert] += this.faceNormX[i];
							tmpYNorm[fVert] += this.faceNormY[i];
							tmpZNorm[fVert] += this.faceNormZ[i];
							++faceCount[fVert];
						}
					}
				}

				for (i = 0; this.vertHead > i; ++i) {
					if (faceCount[i] > 0) {
						this.vertDiffuseLight[i] = (tmpZNorm[i] * this.diffuseDirZ + tmpXNorm[i] * this.diffuseDirX
								+ tmpYNorm[i] * this.diffuseDirY) / (diffuseDivide * faceCount[i]);
					}
				}

			}
		} catch (RuntimeException var11) {
			throw GenUtil.makeThrowable(var11, "ca.FA(" + var1 + ')');
		}
	}

	private final void computeNormals(byte var1) {
		try {
			
			if (!this.dontComputeDiffuse || !this.m_c) {
				if (var1 == 14) {
					for (int face = 0; this.faceHead > face; ++face) {
						int[] var3 = this.faceIndices[face];
						int xp = this.vertXTransform[var3[0]];
						int yp = this.vertYTransform[var3[0]];
						int zp = this.vertZTransform[var3[0]];

						int x21 = this.vertXTransform[var3[1]] - xp;
						int y21 = this.vertYTransform[var3[1]] - yp;
						int z21 = this.vertZTransform[var3[1]] - zp;

						int x31 = this.vertXTransform[var3[2]] - xp;
						int y31 = this.vertYTransform[var3[2]] - yp;
						int z31 = this.vertZTransform[var3[2]] - zp;

						int xN = z31 * y21 - z21 * y31;
						int yN = z21 * x31 - x21 * z31;
						int zN = x21 * y31 - x31 * y21;

						while (xN > 8192 || yN > 8192 || zN > 8192 || xN < -8192 || yN < -8192 || zN < -8192) {
							yN >>= 1;
							zN >>= 1;
							xN >>= 1;
						}

						int mag = (int) (Math.sqrt((double) (yN * yN + xN * xN + zN * zN)) * 256.0D);
						if (mag <= 0) {
							mag = 1;
						}

						this.faceNormX[face] = xN * 65536 / mag;
						this.faceNormY[face] = yN * 65536 / mag;
						this.faceNormZ[face] = zN * 65535 / mag;
						this.scenePolyNormalShift[face] = -1;
					}

					this.computeDiffuse(var1 ^ -85);
				}
			}
		} catch (RuntimeException var17) {
			throw GenUtil.makeThrowable(var17, "ca.K(" + var1 + ')');
		}
	}

	private final void copyFaceTo(int[] faceVert, RSModel dest, int faceVertCount, int srcFaceID, int var5) {
		try {
			
			int[] nVerts = new int[faceVertCount];

			int i;
			for (i = 0; i < faceVertCount; ++i) {
				int vID = nVerts[i] = dest.insertVertex(this.vertX[faceVert[i]], this.vertY[faceVert[i]],
						this.vertZ[faceVert[i]]);
				dest.vertDiffuseLight[vID] = this.vertDiffuseLight[faceVert[i]];
				dest.vertLightOther[vID] = this.vertLightOther[faceVert[i]];
			}

			if (var5 != 5916) {
				this.scaleX = 77;
			}

			i = dest.insertFace(faceVertCount, nVerts, this.faceTextureFront[srcFaceID],
					this.faceTextureBack[srcFaceID], false);
			if (!dest.m_db && !this.m_db) {
				dest.facePickIndex[i] = this.facePickIndex[srcFaceID];
			}

			dest.faceDiffuseLight[i] = this.faceDiffuseLight[srcFaceID];
			dest.scenePolyNormalShift[i] = this.scenePolyNormalShift[srcFaceID];
			dest.scenePolyNormalMagnitude[i] = this.scenePolyNormalMagnitude[srcFaceID];
		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9, "ca.D(" + (faceVert != null ? "{...}" : "null") + ','
					+ (dest != null ? "{...}" : "null") + ',' + faceVertCount + ',' + srcFaceID + ',' + var5 + ')');
		}
	}

	public final void copyRot256AndTranslateFrom(RSModel model, int var2) {
		try {
			if (var2 != 6029) {
				this.appliedTransform = -128;
			}

			
			this.translateX = model.translateX;
			this.translateY = model.translateY;
			this.translateZ = model.translateZ;
			this.rot256X = model.rot256X;
			this.rot256Y = model.rot256Y;
			this.rot256Z = model.rot256Z;
			this.computeAppliedTransform();
			this.m_Yb = 1;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "ca.AA(" + (model != null ? "{...}" : "null") + ',' + var2 + ')');
		}
	}

	final RSModel[] divideModelByGrid(int var1, int xDim, int zGridSize, int var4, int outModels, int limitVertCount,
			int xGridSize, boolean var8, int var9) {
		try {
			
			this.commitTransform((byte) -28);
			int[] outVertCount = new int[outModels];
			int[] outFaceCount = new int[outModels];

			for (int i = 0; outModels > i; ++i) {
				outVertCount[i] = 0;
				outFaceCount[i] = 0;
			}

			for (int i = 0; i < this.faceHead; ++i) {
				int sumX = 0;
				int sumZ = 0;
				int faceVertCount = this.faceIndexCount[i];
				int[] faceVert = this.faceIndices[i];

				for (int j = 0; faceVertCount > j; ++j) {
					sumX += this.vertX[faceVert[j]];
					sumZ += this.vertZ[faceVert[j]];
				}

				int id = sumX / (faceVertCount * xGridSize) + sumZ / (zGridSize * faceVertCount) * xDim;
				outVertCount[id] += faceVertCount;
				++outFaceCount[id];
			}

			RSModel[] output = new RSModel[outModels];

			for (int i = 0; outModels > i; ++i) {
				if (limitVertCount < outVertCount[i]) {
					outVertCount[i] = limitVertCount;
				}

				output[i] = new RSModel(outVertCount[i], outFaceCount[i], true, true, true, var8, true);
				output[i].diffuseParam2 = this.diffuseParam2;
				output[i].diffuseParam1 = this.diffuseParam1;
			}

			for (int f = 0; this.faceHead > f; ++f) {
				int sumX = 0;
				int sumZ = 0;
				int srcIndexCount = this.faceIndexCount[f];
				int[] srcIndices = this.faceIndices[f];

				for (int i = 0; i < srcIndexCount; ++i) {
					sumX += this.vertX[srcIndices[i]];
					sumZ += this.vertZ[srcIndices[i]];
				}

				int id = sumX / (srcIndexCount * xGridSize) + xDim * (sumZ / (zGridSize * srcIndexCount));
				this.copyFaceTo(srcIndices, output[id], srcIndexCount, f, 5916);
			}

			for (int sumX = 0; outModels > sumX; ++sumX) {
				output[sumX].clearRotDataAndParams26((byte) 71);
			}
			return output;
		} catch (RuntimeException var19) {
			throw GenUtil.makeThrowable(var19, "ca.J(" + 0 + ',' + xDim + ',' + zGridSize + ',' + var4 + ',' + outModels
					+ ',' + limitVertCount + ',' + xGridSize + ',' + var8 + ',' + var9 + ')');
		}
	}

	private final int getFaceParam1(byte var1, byte[] data) {
		try {
			

			while (data[this.m_hb] == 10 || data[this.m_hb] == 13) {
				++this.m_hb;
			}

			int var3 = MiscFunctions.class14_s_d[255 & data[this.m_hb++]];
			int var4 = MiscFunctions.class14_s_d[255 & data[this.m_hb++]];
			int var5 = MiscFunctions.class14_s_d[data[this.m_hb++] & 255];
			int param = var4 * 64 - 131072 + var3 * 4096 + var5;
			if (param == 123456) {
				param = this.m_Vb;
			}

			return param;
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7, "ca.JA(" + 76 + ',' + (data != null ? "{...}" : "null") + ')');
		}
	}

	public final int insertFace(int indexCount, int[] indices, int texFront, int texBack, boolean var5) {
		try {
			
			if (var5) {
				this.addRotation(23, 10, 30);
			}

			if (this.faceCount > this.faceHead) {
				this.faceIndexCount[this.faceHead] = indexCount;
				this.faceIndices[this.faceHead] = indices;
				this.faceTextureFront[this.faceHead] = texFront;
				this.faceTextureBack[this.faceHead] = texBack;
				this.m_Yb = 1;
				return this.faceHead++;
			} else {
				return -1;
			}
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7, "ca.P(" + indexCount + ',' + (indices != null ? "{...}" : "null") + ','
					+ texFront + ',' + texBack + ',' + var5 + ')');
		}
	}

	public final int insertVertex(int x, int y, int z) {
		try {
			

			for (int i = 0; this.vertHead > i; ++i) {
				if (x == this.vertX[i] && y == this.vertY[i] && z == this.vertZ[i]) {
					return i;
				}
			}

			if (this.vertHead < this.vertexCount2) {
				this.vertX[this.vertHead] = x;
				this.vertY[this.vertHead] = y;
				this.vertZ[this.vertHead] = z;
				return this.vertHead++;
			} else {
				return -1;
			}
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "ca.I(" + x + ',' + z + ',' + y + ',' + "dummy" + ')');
		}
	}

	final int insertVertex2(boolean var1, int z, int x, int y) {
		try {
			
			if (this.vertHead >= this.vertexCount2) {
				return -1;
			} else {
				this.vertX[this.vertHead] = x;
				this.vertY[this.vertHead] = y;
				this.vertZ[this.vertHead] = z;
				return this.vertHead++;
			}
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "ca.S(" + false + ',' + z + ',' + x + ',' + y + ')');
		}
	}

	final void removeFacesAndOrVerts(int deleteVerts, int var2, int deleteFaces) {
		try {
			
			this.faceHead -= deleteFaces;
			if (var2 > -110) {
				ModelFileManager.getModelFileIndex((String) null);
			}

			if (this.faceHead < 0) {
				this.faceHead = 0;
			}

			this.vertHead -= deleteVerts;
			if (this.vertHead < 0) {
				this.vertHead = 0;
			}

		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "ca.F(" + deleteVerts + ',' + var2 + ',' + deleteFaces + ')');
		}
	}

	final void resetFaceVertHead(int var1) {
		try {
			this.faceHead = 0;
			this.vertHead = 0;
			
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ca.M(" + 1 + ')');
		}
	}

	private final void resetTransformCache(int var1) {
		try {
			if (var1 != 7972) {
				this.translate2(120, 57, 20);
			}

			
			int i;
			if (this.m_Yb == 2) {
				this.m_Yb = 0;

				for (i = 0; i < this.vertHead; ++i) {
					this.vertXTransform[i] = this.vertX[i];
					this.vertYTransform[i] = this.vertY[i];
					this.vertZTransform[i] = this.vertZ[i];
				}

				this.maxX = 9999999;
				this.maxY = 9999999;
				this.minY = -9999999;
				this.maxZ = 9999999;
				this.minZ = -9999999;
				this.maxFaceDimension = 9999999;
				this.minX = -9999999;
			} else if (this.m_Yb == 1) {
				this.m_Yb = 0;

				for (i = 0; this.vertHead > i; ++i) {
					this.vertXTransform[i] = this.vertX[i];
					this.vertYTransform[i] = this.vertY[i];
					this.vertZTransform[i] = this.vertZ[i];
				}

				if (this.appliedTransform >= 2) {
					this.rotate256(-53, this.rot256X, this.rot256Z, this.rot256Y);
				}

				if (this.appliedTransform >= 3) {
					this.scale(this.scaleX, -27483, this.scaleZ, this.scaleY);
				}

				if (this.appliedTransform >= 4) {
					this.applyRotMatrix(this.rotM_xToZ, this.rotM_zToY, this.rotM_yToX, this.rotM_yToZ, this.rotM_zToX,
							this.rotM_xToY, (byte) -127);
				}

				if (this.appliedTransform >= 1) {
					this.translate(var1 - 7972, this.translateY, this.translateZ, this.translateX);
				}

				this.calculateBoundingBoxes(999999);
				this.computeNormals((byte) 14);
			}

		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ca.V(" + var1 + ')');
		}
	}

	final void rotate1024(int yOffset, int vParamSrc, int xOffset, byte var4, int zOffset, int rotY, int rotZ, int rotX,
			int zTop) {
		try {
			
			this.resetTransformCache((int) 7972);
			if (this.minZ <= MiscFunctions.netsock_s_K && this.maxZ >= MiscFunctions.rssock_facs_j
					&& this.minX <= MiscFunctions.class13_s_b && this.maxX >= MiscFunctions.pe_s_f
					&& this.minY <= MiscFunctions.cachingFile_s_y && this.maxY >= MiscFunctions.pe_s_b) {
				this.m_dc = true;
				int xy_xy = 0;
				if (var4 > -105) {
					this.clearRotDataAndParams26((byte) -103);
				}

				int xy_yy = 0;
				int yz_zy = 0;
				int yz_zz = 0;
				int xz_xz = 0;
				if (rotZ != 0) {
					xy_yy = FastMath.trigTable1024[1024 + rotZ];
					xy_xy = FastMath.trigTable1024[rotZ];
				}

				int xz_xx = 0;
				if (rotX != 0) {
					yz_zy = FastMath.trigTable1024[rotX];
					yz_zz = FastMath.trigTable1024[rotX + 1024];
				}

				if (rotY != 0) {
					xz_xz = FastMath.trigTable1024[rotY];
					xz_xx = FastMath.trigTable1024[rotY + 1024];
				}

				for (int var17 = 0; this.vertHead > var17; ++var17) {
					int x0 = this.vertXTransform[var17] - xOffset;
					int yO = this.vertYTransform[var17] - yOffset;
					int zO = this.vertZTransform[var17] - zOffset;

					int tmp;
					if (rotZ != 0) {
						tmp = yO * xy_xy + xy_yy * x0 >> 15;
						yO = yO * xy_yy - x0 * xy_xy >> 15;
						x0 = tmp;
					}

					if (rotY != 0) {
						tmp = xz_xx * x0 + zO * xz_xz >> 15;
						zO = xz_xx * zO - x0 * xz_xz >> 15;
						x0 = tmp;
					}

					if (rotX != 0) {
						tmp = yO * yz_zz - yz_zy * zO >> 15;
						zO = yz_zy * yO + yz_zz * zO >> 15;
						yO = tmp;
					}

					if (zO < zTop) {
						this.vertexParam6[var17] = x0 << vParamSrc;
					} else {
						this.vertexParam6[var17] = (x0 << vParamSrc) / zO;
					}

					if (zO < zTop) {
						this.vertexParam2[var17] = yO << vParamSrc;
					} else {
						this.vertexParam2[var17] = (yO << vParamSrc) / zO;
					}

					this.vertXRot[var17] = x0;
					this.vertYRot[var17] = yO;
					this.vertZRot[var17] = zO;
				}

			} else {
				this.m_dc = false;
			}
		} catch (RuntimeException var21) {
			throw GenUtil.makeThrowable(var21, "ca.U(" + yOffset + ',' + vParamSrc + ',' + xOffset + ',' + var4 + ','
					+ zOffset + ',' + rotY + ',' + rotZ + ',' + rotX + ',' + zTop + ')');
		}
	}

	private final void rotate256(int var1, int rotX, int rotZ, int rotY) {
		try {
			
			if (var1 >= -14) {
				this.faceNormY = (int[]) null;
			}

			for (int v = 0; v < this.vertHead; ++v) {
				int tmp;
				if (rotZ != 0) {
					int xx = FastMath.trigTable256[rotZ + 256];
					int xy = FastMath.trigTable256[rotZ];
					tmp = this.vertXTransform[v] * xx + this.vertYTransform[v] * xy >> 15;
					this.vertYTransform[v] = this.vertYTransform[v] * xx - xy * this.vertXTransform[v] >> 15;
					this.vertXTransform[v] = tmp;
				}

				if (rotX != 0) {
					int yz = FastMath.trigTable256[rotX];
					int yy = FastMath.trigTable256[256 + rotX];
					tmp = yy * this.vertYTransform[v] - yz * this.vertZTransform[v] >> 15;
					this.vertZTransform[v] = yz * this.vertYTransform[v] + yy * this.vertZTransform[v] >> 15;
					this.vertYTransform[v] = tmp;
				}

				if (rotY != 0) {
					int xz = FastMath.trigTable256[rotY];
					int xx = FastMath.trigTable256[256 + rotY];
					tmp = xz * this.vertZTransform[v] + this.vertXTransform[v] * xx >> 15;
					this.vertZTransform[v] = this.vertZTransform[v] * xx - this.vertXTransform[v] * xz >> 15;
					this.vertXTransform[v] = tmp;
				}
			}

		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9, "ca.W(" + var1 + ',' + rotX + ',' + rotZ + ',' + rotY + ')');
		}
	}

	/**
	 * 0-255 -> 0-1
	 */
	private final void scale(int xScale, int var2, int zScale, int yScale) {
		try {
			
			if (var2 != -27483) {
				this.insertVertex(-7, -31, -82);
			}

			for (int i = 0; this.vertHead > i; ++i) {
				this.vertXTransform[i] = this.vertXTransform[i] * xScale >> 8;
				this.vertYTransform[i] = this.vertYTransform[i] * yScale >> 8;
				this.vertZTransform[i] = zScale * this.vertZTransform[i] >> 8;
			}

		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "ca.BA(" + xScale + ',' + var2 + ',' + zScale + ',' + yScale + ')');
		}
	}

	final void setDiffuseDir(boolean var1, int dirX, int dirY, int dirZ) {
		try {
			if (var1) {
				this.m_Yb = 71;
			}

			
			if (!this.dontComputeDiffuse) {
				this.diffuseDirZ = dirZ;
				this.diffuseDirY = dirY;
				this.diffuseDirX = dirX;
				this.diffuseMag = (int) Math.sqrt((double) (dirZ * dirZ + dirY * dirY + dirX * dirX));
				this.computeDiffuse(52);
			}
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "ca.B(" + var1 + ',' + dirX + ',' + dirY + ',' + dirZ + ')');
		}
	}

	final void setDiffuseLight(int var1, int var2, int diffuseDirY, int var4, int diffuseDirX, int diffuseDirZ) {
		try {
			
			this.diffuseParam1 = 256 - var2 * 4;
			this.diffuseParam2 = (64 - var1) * 16 + 128;
			if (var4 > -110) {
				this.diffuseDirY = -67;
			}

			if (!this.dontComputeDiffuse) {
				this.diffuseDirX = diffuseDirX;
				this.diffuseDirZ = diffuseDirZ;
				this.diffuseDirY = diffuseDirY;
				this.diffuseMag = (int) Math.sqrt(
						(double) (diffuseDirZ * diffuseDirZ + diffuseDirY * diffuseDirY + diffuseDirX * diffuseDirX));
				this.computeDiffuse(-102);
			}
		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8, "ca.G(" + var1 + ',' + var2 + ',' + diffuseDirY + ',' + var4 + ','
					+ diffuseDirX + ',' + diffuseDirZ + ')');
		}
	}

	public final void setDiffuseLightAndColor(int dirX, int dirY, int dirZ, int p1, int p2, boolean var5, int var7) {
		try {
			this.diffuseParam2 = (64 - p2) * 16 + 128;
			this.diffuseParam1 = 256 - p1 * 4;
			
			if (!this.dontComputeDiffuse) {
				for (int i = 0; i < this.faceHead; ++i) {
					if (var5) {
						this.faceDiffuseLight[i] = this.m_Vb;
					} else {
						this.faceDiffuseLight[i] = 0;
					}
				}

				this.diffuseDirX = dirX;
				this.diffuseDirZ = dirZ;
				this.diffuseDirY = dirY;
				this.diffuseMag = (int) Math.sqrt((double) (dirY * dirY + dirX * dirX + dirZ * dirZ));
				this.computeDiffuse(-121);
			}
		} catch (RuntimeException var10) {
			throw GenUtil.makeThrowable(var10,
					"ca.N(" + dirZ + ',' + p1 + ',' + dirY + ',' + dirX + ',' + var5 + ',' + p2 + ',' + var7 + ')');
		}
	}

	private final void setFaceVertexCount(int faceCount, int vertexCount, int var3) {
		try {
			if (!this.m_db) {
				this.m_zb = new byte[faceCount];
				this.facePickIndex = new int[faceCount];
			}

			this.scenePolyNormalMagnitude = new int[faceCount];
			this.vertY = new int[vertexCount];
			this.vertLightOther = new byte[vertexCount];
			this.vertX = new int[vertexCount];
			this.scenePolyNormalShift = new int[faceCount];
			this.vertZ = new int[vertexCount];
			this.faceTextureBack = new int[faceCount];
			this.vertDiffuseLight = new int[vertexCount];
			this.faceTextureFront = new int[faceCount];
			this.faceIndices = new int[faceCount][];
			this.faceIndexCount = new int[faceCount];
			if (!this.m_b) {
				this.vertexParam2 = new int[vertexCount];
				this.vertYRot = new int[vertexCount];
				this.vertXRot = new int[vertexCount];
				this.vertZRot = new int[vertexCount];
				this.vertexParam6 = new int[vertexCount];
			}

			
			this.faceDiffuseLight = new int[faceCount];
			this.rotM_zToX = 256;
			this.rot256Y = 0;
			this.rotM_xToZ = 256;
			this.rotM_yToX = 256;
			this.scaleZ = 256;
			if (!this.m_c) {
				this.faceMaxX = new int[faceCount];
				this.faceMinY = new int[faceCount];
				this.faceMinZ = new int[faceCount];
				this.faceMaxY = new int[faceCount];
				this.faceMaxZ = new int[faceCount];
				this.faceMinX = new int[faceCount];
			}

			if (!this.dontComputeDiffuse || !this.m_c) {
				this.faceNormY = new int[faceCount];
				this.faceNormZ = new int[faceCount];
				this.faceNormX = new int[faceCount];
			}

			this.rotM_yToZ = 256;
			this.faceHead = 0;
			this.rot256X = 0;
			this.translateZ = 0;
			this.scaleX = 256;
			if (!this.m_v) {
				this.vertXTransform = new int[vertexCount];
				this.vertZTransform = new int[vertexCount];
				this.vertYTransform = new int[vertexCount];
			} else {
				this.vertXTransform = this.vertX;
				this.vertZTransform = this.vertZ;
				this.vertYTransform = this.vertY;
			}

			this.translateX = 0;
			this.vertexCount2 = vertexCount;
			this.rotM_xToY = 256;
			this.rotM_zToY = 256;
			this.translateY = 0;
			this.appliedTransform = 0;
			this.vertHead = 0;
			this.scaleY = 256;
			if (var3 <= 68) {
				this.faceIndexCount = (int[]) null;
			}

			this.faceCount = faceCount;
			this.rot256Z = 0;
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "ca.T(" + faceCount + ',' + vertexCount + ',' + var3 + ')');
		}
	}

	final void setRot256(int rotX, int rotY, int rotZ) {
		try {
			
			this.rot256X = rotX & 255;
			this.rot256Y = rotY & 255;
			this.rot256Z = rotZ & 255;
			this.computeAppliedTransform();
			this.m_Yb = 1;
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "ca.EA(" + rotZ + ',' + "dummy" + ',' + rotX + ',' + rotY + ')');
		}
	}

	public final void setTranslate(int tX, int tY, int tZ) {
		try {
			this.translateY = tY;
			this.translateX = tX;
			this.translateZ = tZ;
			
			this.computeAppliedTransform();
			this.m_Yb = 1;
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "ca.L(" + tY + ',' + "dummy" + ',' + tZ + ',' + tX + ')');
		}
	}

	final void setVertexLightOther(int id, int val) {
		try {
			
			this.vertLightOther[id] = (byte) val;
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "ca.HA(" + id + ',' + val + ',' + -61 + ')');
		}
	}

	private final void translate(int vOff, int yt, int zt, int xt) {
		try {
			

			for (int i = vOff; this.vertHead > i; ++i) {
				this.vertXTransform[i] += xt;
				this.vertYTransform[i] += yt;
				this.vertZTransform[i] += zt;
			}

		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "ca.DA(" + vOff + ',' + yt + ',' + zt + ',' + xt + ')');
		}
	}

	public final void translate2(int tX, int tY, int tZ) {
		try {
			this.translateY += tY;
			this.translateZ += tZ;
			this.translateX += tX;
			
			this.computeAppliedTransform();
			this.m_Yb = 1;
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "ca.O(" + tX + ',' + tZ + ',' + tY + ',' + true + ')');
		}
	}
}
