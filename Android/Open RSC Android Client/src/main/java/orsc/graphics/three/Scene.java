package orsc.graphics.three;

import orsc.MiscFunctions;
import orsc.buffers.BufferStack;
import orsc.graphics.two.GraphicsController;
import orsc.util.FastMath;
import orsc.util.GenUtil;

public final class Scene {
	static final int TRANSPARENT = 12345678;
	private final RSModel[] m_Ab;
	private final int[] m_B = new int[40];
	private final int m_db;
	private final int polyNormalScale;
	private final int m_ib = 50;
	private final int[][] m_Ib;
	private final int[] m_J = new int[40];
	private final int[] m_qb;
	private final int[] m_Qb = new int[40];
	private final int[] m_r;
	private final boolean m_Ub;
	private final int[] m_v;
	private final int[] m_Vb = new int[40];
	private final int[] m_yb;
	private final int rot1024_zTop = 5;
	public int fogSmoothingStartDistance = 10;
	public int fogZFalloff = 20;
	public int fogLandscapeDistance;
	public RSModel m_T;
	public int fogEntityDistance;
	private int[] m_a;
	private int m_A;
	private int m_cb;
	private int m_Cb;
	private int m_cc = 0;
	private long[] m_D;
	private GraphicsController graphics;
	private int m_e;
	private int m_eb;
	private int[] m_Eb;
	private int[][] m_ec;
	private boolean m_f = false;
	private int[] m_Fb;
	private byte[][] m_g;
	private int[] m_gb;
	private int[] m_H;
	private int[] m_Hb;
	private int[][] m_i;
	private int m_j;
	private int[] m_jb;
	private boolean m_K;
	private int[][] resourceDatabase;
	private int[][] m_L;
	private int m_n;
	private int m_Nb;
	private int[] m_ob;
	private int[] m_Ob;
	private int[] pixelData;
	private int[] m_Q;
	private boolean[] m_S;
	private int m_u;
	private int m_vb;
	private int m_wb = 192;
	private int m_Wb;
	private Scanline[] m_x;
	private int m_Xb;
	private Polygon[] polygons;
	private int m_zb;
	private int m_Zb = 256;
	private int modelCount;
	private RSModel[] models;
	private int rot1024_off_x;
	private int rot1024_off_y;
	private int rot1024_off_z;
	private int rot1024_vp_src;
	private int cameraProjX;
	private int cameraProjY;
	private int cameraProjZ;

	public Scene(GraphicsController var1, int var2, int maxPolygonCount, int var4) {
		this.m_Ib = new int[this.m_ib][256];
		this.m_Nb = 256;
		this.fogLandscapeDistance = 1000;
		this.m_vb = 512;
		this.m_r = new int[40];
		this.m_K = false;
		this.m_A = 256;
		this.m_cb = 0;
		this.polyNormalScale = 4;
		this.m_Ub = false;
		this.m_v = new int[this.m_ib];
		this.m_n = 0;
		this.m_yb = new int[40];
		this.fogEntityDistance = 1000;
		this.m_db = 100;
		this.rot1024_vp_src = 8;
		this.m_Ab = new RSModel[this.m_db];
		this.m_qb = new int[this.m_db];

		try {
			this.modelCount = 0;
			this.pixelData = var1.pixelData;
			this.graphics = var1;
			this.m_A = var1.width2 / 2;
			this.m_u = var2;
			this.m_wb = var1.height2 / 2;
			this.models = new RSModel[this.m_u];
			this.m_zb = 0;
			this.m_jb = new int[this.m_u];
			this.polygons = new Polygon[maxPolygonCount];

			int var5;
			for (var5 = 0; var5 < maxPolygonCount; ++var5) {
				this.polygons[var5] = new Polygon();
			}

			this.m_n = 0;
			this.m_T = new RSModel(var4 * 2, var4);
			this.m_ob = new int[var4];
			this.m_Eb = new int[var4];
			if (BufferStack.s_i == null) {
				BufferStack.s_i = new byte[17691];
			}

			this.m_Fb = new int[var4];
			this.rot1024_off_y = 0;
			this.cameraProjZ = 0;
			this.m_Ob = new int[var4];
			this.cameraProjY = 0;
			this.rot1024_off_z = 0;
			this.rot1024_off_x = 0;
			this.cameraProjX = 0;
			this.m_Q = new int[var4];
			this.m_gb = new int[var4];
			this.m_a = new int[var4];
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6,
					"lb.<init>(" + "{...}" + ',' + var2 + ',' + maxPolygonCount + ',' + var4 + ')');
		}
	}

	private boolean polygonHit1(Polygon polyA, Polygon polyB) {
		try {

			RSModel modelA = polyA.model;
			RSModel modelB = polyB.model;
			int faceA = polyA.faceID;
			int faceB = polyB.faceID;
			int[] indexA = modelA.faceIndices[faceA];
			int[] indexB = modelB.faceIndices[faceB];
			int indexCountA = modelA.faceIndexCount[faceA];
			int indexCountB = modelB.faceIndexCount[faceB];
			int bv0_x = modelB.vertXRot[indexB[0]];
			int bv0_y = modelB.vertYRot[indexB[0]];
			int bv0_z = modelB.vertZRot[indexB[0]];
			int bn_x = polyB.normalX;
			int bn_y = polyB.normalY;
			int bn_z = polyB.normalZ;
			int bf_normMag = modelB.scenePolyNormalMagnitude[faceB];
			boolean hit = false;
			int orientation = polyB.orientation;

			for (int v = 0; indexCountA > v; ++v) {
				int vID = indexA[v];
				int dot = bn_y * (bv0_y - modelA.vertYRot[vID]) + (bv0_x - modelA.vertXRot[vID]) * bn_x
						+ (bv0_z - modelA.vertZRot[vID]) * bn_z;
				if (-bf_normMag > dot && orientation < 0 || dot > bf_normMag && orientation > 0) {
					hit = true;
					break;
				}
			}

			if (!hit) {
				return true;
			} else {
				bv0_x = modelA.vertXRot[indexA[0]];
				bn_z = polyA.normalZ;
				bf_normMag = modelA.scenePolyNormalMagnitude[faceA];
				bv0_z = modelA.vertZRot[indexA[0]];
				bv0_y = modelA.vertYRot[indexA[0]];
				hit = false;
				bn_x = polyA.normalX;
				bn_y = polyA.normalY;
				orientation = polyA.orientation;

				for (int v = 0; v < indexCountB; ++v) {
					int vID = indexB[v];
					int dot = bn_x * (bv0_x - modelB.vertXRot[vID])
							- (-(bn_y * (bv0_y - modelB.vertYRot[vID])) - (bv0_z - modelB.vertZRot[vID]) * bn_z);
					if (dot < -bf_normMag && orientation > 0 || bf_normMag < dot && orientation < 0) {
						hit = true;
						break;
					}
				}

				return !hit;
			}
		} catch (RuntimeException var24) {
			throw GenUtil.makeThrowable(var24, "lb.DA(" + false + ',' + (polyA != null ? "{...}" : "null") + ','
					+ (polyB != null ? "{...}" : "null") + ')');
		}
	}

	public final int[] getQB(byte var1) {
		try {

			return this.m_qb;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "lb.C(" + var1 + ')');
		}
	}

	private boolean booleanCombinatoric(boolean var2, int var3, int var4, int var5, int var6) {
		try {

			if ((!var2 || var5 > var6) && var5 >= var6) {
				if (var5 < var4)
					return true;
				if (var3 < var6)
					return true;
				if (var3 < var4)
					return true;
				return var2;
			} else {
				if (var5 > var4)
					return true;
				if (var3 > var6)
					return true;
				if (var3 > var4)
					return true;
				return !var2;
			}
			// return (!var2 || var5 > var6) && var5 >= var6
			// ? (var5 < var4 ? true : (var3 >= var6 ? (var3 < var4 ? true :
			// var2) : true))
			// : (var5 <= var4 ? (var3 > var6 ? true : (var3 <= var4 ? !var2 :
			// true)) : true);
		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8,
					"lb.H(" + "dummy" + ',' + var2 + ',' + var3 + ',' + var4 + ',' + var5 + ',' + var6 + ')');
		}
	}

	private boolean polygonHit2(byte var1, Polygon var2, Polygon var3) {
		try {

			if (var3.minP6 >= var2.maxP6) {
				return true;
			} else {
				if (var1 > -42) {
					this.setFaceSpriteLocalPlayer(95, -1);
				}

				if (var2.minP6 >= var3.maxP6) {
					return true;
				} else if (var2.maxP2 <= var3.minP2) {
					return true;
				} else if (var3.maxP2 <= var2.minP2) {
					return true;
				} else if (var2.maxZ <= var3.minZ) {
					return true;
				} else if (var3.maxZ < var2.minZ) {
					return false;
				} else {
					RSModel var4 = var3.model;
					RSModel var5 = var2.model;
					int var6 = var3.faceID;
					int var7 = var2.faceID;
					int[] var8 = var4.faceIndices[var6];
					int[] var9 = var5.faceIndices[var7];
					int var10 = var4.faceIndexCount[var6];
					int var11 = var5.faceIndexCount[var7];
					int var15 = var5.vertXRot[var9[0]];
					int var16 = var5.vertYRot[var9[0]];
					int var17 = var5.vertZRot[var9[0]];
					int var18 = var2.normalX;
					int var19 = var2.normalY;
					int var20 = var2.normalZ;
					int var21 = var5.scenePolyNormalMagnitude[var7];
					int var22 = var2.orientation;
					boolean var14 = false;

					int var12;
					int var13;
					int var23;
					for (var23 = 0; var23 < var10; ++var23) {
						var12 = var8[var23];
						var13 = (var17 - var4.vertZRot[var12]) * var20 + (var16 - var4.vertYRot[var12]) * var19
								+ var18 * (var15 - var4.vertXRot[var12]);
						if (var13 < -var21 && var22 < 0 || var13 > var21 && var22 > 0) {
							var14 = true;
							break;
						}
					}

					if (!var14) {
						return true;
					} else {
						var14 = false;
						var22 = var3.orientation;
						var16 = var4.vertYRot[var8[0]];
						var15 = var4.vertXRot[var8[0]];
						var21 = var4.scenePolyNormalMagnitude[var6];
						var17 = var4.vertZRot[var8[0]];
						var19 = var3.normalY;
						var20 = var3.normalZ;
						var18 = var3.normalX;

						for (var23 = 0; var11 > var23; ++var23) {
							var12 = var9[var23];
							var13 = (var17 - var5.vertZRot[var12]) * var20 + (var15 - var5.vertXRot[var12]) * var18
									+ (var16 - var5.vertYRot[var12]) * var19;
							if (-var21 > var13 && var22 > 0 || var21 < var13 && var22 < 0) {
								var14 = true;
								break;
							}
						}

						if (!var14) {
							return true;
						} else {
							int[] var24;
							int var27;
							int var28;
							int[] var30;
							if (var10 != 2) {
								var30 = new int[var10];
								var24 = new int[var10];

								for (var27 = 0; var27 < var10; ++var27) {
									var28 = var8[var27];
									var30[var27] = var4.vertexParam6[var28];
									var24[var27] = var4.vertexParam2[var28];
								}
							} else {
								var30 = new int[4];
								var24 = new int[4];
								var12 = var8[1];
								var27 = var8[0];
								var30[0] = var4.vertexParam6[var27] - 20;
								var30[1] = var4.vertexParam6[var12] - 20;
								var30[2] = 20 + var4.vertexParam6[var12];
								var30[3] = var4.vertexParam6[var27] + 20;
								var24[0] = var24[3] = var4.vertexParam2[var27];
								var24[1] = var24[2] = var4.vertexParam2[var12];
							}

							int[] var25;
							int[] var26;
							if (var11 != 2) {
								var25 = new int[var11];
								var26 = new int[var11];

								for (var27 = 0; var27 < var11; ++var27) {
									var28 = var9[var27];
									var25[var27] = var5.vertexParam6[var28];
									var26[var27] = var5.vertexParam2[var28];
								}
							} else {
								var26 = new int[4];
								var25 = new int[4];
								var27 = var9[0];
								var12 = var9[1];
								var25[0] = var5.vertexParam6[var27] - 20;
								var25[1] = var5.vertexParam6[var12] - 20;
								var25[2] = var5.vertexParam6[var12] + 20;
								var25[3] = var5.vertexParam6[var27] + 20;
								var26[0] = var26[3] = var5.vertexParam2[var27];
								var26[1] = var26[2] = var5.vertexParam2[var12];
							}

							return !this.setFrustum(var25, var24, var30, var26, 1);
						}
					}
				}
			}
		} catch (RuntimeException var29) {
			throw GenUtil.makeThrowable(var29, "lb.F(" + var1 + ',' + (var2 != null ? "{...}" : "null") + ',' + "{...}" + ')');
		}
	}

	private void resetMTVertHead() {
		try {

			this.m_n = 0;
			this.m_T.resetFaceVertHead((int) 1);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "lb.Q(" + "dummy" + ')');
		}
	}

	final int resourceToColor(int resource, boolean var2) {
		try {

			if (resource == Scene.TRANSPARENT) {
				return 0;
			} else {
				this.b(resource, var2);
				if (resource >= 0) {
					return this.resourceDatabase[resource][0];
				} else {
					resource = -(resource + 1);
					int var3 = (resource & 0x7C00) >> 10;
					int var4 = (0x3E0 & resource) >> 5;
					int var5 = 0x1F & resource;
					return (var5 << 3) + (var4 << 11) + (var3 << 19);
				}
			}
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "lb.MA(" + resource + ',' + var2 + ')');
		}
	}

	private boolean booleanCombinatoric2(int var1, boolean var2, int var3, byte var4, int var5) {
		try {

			return (!var2 || var3 > var1) && var3 >= var1 ? (var5 < var1 ? true : var2) : (var1 >= var5 ? !var2 : true);
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7, "lb.CA(" + var1 + ',' + var2 + ',' + var3 + ',' + -71 + ',' + var5 + ')');
		}
	}

	private int booleanCombinatoric3(int var1, boolean var2, int var3, int var4, int var5, int var6) {
		try {

			return var4 == var1 ? var6 : (var5 - var6) * (var3 - var1) / (var4 - var1) + var6;
		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8,
					"lb.M(" + var1 + ',' + false + ',' + var3 + ',' + var4 + ',' + var5 + ',' + var6 + ')');
		}
	}

	private void setFrustum(int var1, byte var2) {
		try {

			short var3;
			if (this.m_Hb[var1] != 0) {
				var3 = 128;
			} else {
				var3 = 64;
			}

			int[] var4 = this.resourceDatabase[var1];
			int var5 = 0;

			int var6;
			int var7;
			for (var6 = 0; var3 > var6; ++var6) {
				for (var7 = 0; var3 > var7; ++var7) {
					int var8 = this.m_L[var1][this.m_g[var1][var7 + var6 * var3] & 255];
					var8 &= 16316671;
					if (var8 != 0) {
						if (var8 == 16253183) {
							this.m_S[var1] = true;
							var8 = 0;
						}
					} else {
						var8 = 1;
					}

					var4[var5++] = var8;
				}
			}

			for (var6 = 0; var6 < var5; ++var6) {
				var7 = var4[var6];
				var4[var5 + var6] = FastMath.bitwiseAnd(var7 - (var7 >>> 3), 16316671);
				var4[var6 + var5 * 2] = FastMath.bitwiseAnd(var7 - (var7 >>> 2), 16316671);
				var4[var6 + var5 * 3] = FastMath.bitwiseAnd(var7 - (var7 >>> 3) - (var7 >>> 2), 16316671);
			}

		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9, "lb.BA(" + var1 + ',' + 118 + ')');
		}
	}

	private void computePolygon(int polyID) {
		try {

			Polygon poly = this.polygons[polyID];
			RSModel model = poly.model;
			int face = poly.faceID;
			int[] index = model.faceIndices[face];
			int indexCount = model.faceIndexCount[face];
			int fParam4 = model.scenePolyNormalShift[face];
			int v0_X = model.vertXRot[index[0]];
			int v0_Y = model.vertYRot[index[0]];
			int v0_Z = model.vertZRot[index[0]];
			int v1_DX = model.vertXRot[index[1]] - v0_X;
			int v1_DY = model.vertYRot[index[1]] - v0_Y;
			int v1_DZ = model.vertZRot[index[1]] - v0_Z;
			int v2_DX = model.vertXRot[index[2]] - v0_X;
			int v2_DY = model.vertYRot[index[2]] - v0_Y;
			int v2_DZ = model.vertZRot[index[2]] - v0_Z;
			int normX = v2_DZ * v1_DY - v1_DZ * v2_DY;
			int normY = v2_DX * v1_DZ - v1_DX * v2_DZ;
			int normZ = v1_DX * v2_DY - v2_DX * v1_DY;
			if (fParam4 != -1) {
				normZ >>= fParam4;
				normX >>= fParam4;
				normY >>= fParam4;
			} else {
				fParam4 = 0;
				while (normX > 25000 || normY > 25000 || normZ > 25000 || normX < -25000 || normY < -25000
						|| normZ < -25000) {
					normX >>= 1;
					normY >>= 1;
					normZ >>= 1;
					++fParam4;
				}

				model.scenePolyNormalShift[face] = fParam4;
				model.scenePolyNormalMagnitude[face] = (int) ((double) this.polyNormalScale
						* Math.sqrt((double) (normZ * normZ + normY * normY + normX * normX)));
			}

			poly.normalX = normX;
			poly.normalY = normY;
			poly.normalZ = normZ;
			poly.orientation = (normX * v0_X) + (normY * v0_Y) + (normZ * v0_Z);

			int minZ = model.vertZRot[index[0]];
			int maxZ = minZ;
			int minP6 = model.vertexParam6[index[0]];
			int maxP6 = minP6;
			int minP2 = model.vertexParam2[index[0]];
			int maxP2 = minP2;

			for (int v = 1; indexCount > v; ++v) {
				int vv_t = model.vertZRot[index[v]];
				if (vv_t <= maxZ) {
					if (vv_t < minZ) {
						minZ = vv_t;
					}
				} else {
					maxZ = vv_t;
				}

				vv_t = model.vertexParam6[index[v]];
				if (vv_t > maxP6) {
					maxP6 = vv_t;
				} else if (vv_t < minP6) {
					minP6 = vv_t;
				}

				vv_t = model.vertexParam2[index[v]];
				if (vv_t > maxP2) {
					maxP2 = vv_t;
				} else if (minP2 > vv_t) {
					minP2 = vv_t;
				}
			}

			poly.minP6 = minP6;
			poly.maxP6 = maxP6;
			poly.maxP2 = maxP2;
			poly.maxZ = maxZ;
			poly.minP2 = minP2;
			poly.minZ = minZ;
		} catch (RuntimeException var29) {
			throw GenUtil.makeThrowable(var29, "lb.D(" + polyID + ',' + "dummy" + ')');
		}
	}

	private void setFrustum(int x, int y, int z, boolean var4) {
		try {

			int projX = 1024 - this.cameraProjX & 1023;
			int projY = 1024 - this.cameraProjY & 1023;
			int projZ = 1024 - this.cameraProjZ & 1023;
			int var8;
			int var9;
			int var10;
			if (projZ != 0) {
				var8 = FastMath.trigTable_1024[projZ];
				var9 = FastMath.trigTable_1024[1024 + projZ];
				var10 = var9 * y + var8 * z >> 15;
				z = z * var9 - var8 * y >> 15;
				y = var10;
			}

			if (projX != 0) {
				var9 = FastMath.trigTable_1024[1024 + projX];
				var8 = FastMath.trigTable_1024[projX];
				var10 = z * var9 - var8 * x >> 15;
				x = var9 * x + var8 * z >> 15;
				z = var10;
			}

			if (projY != 0) {
				var8 = FastMath.trigTable_1024[projY];
				var9 = FastMath.trigTable_1024[1024 + projY];
				var10 = var8 * x + y * var9 >> 15;
				x = var9 * x - var8 * y >> 15;
				y = var10;
			}

			if (x > MiscFunctions.frustumMinX) {
				MiscFunctions.frustumMinX = x;
			}

			if (z < MiscFunctions.frustumFarZ) {
				MiscFunctions.frustumFarZ = z;
			}

			if (y > MiscFunctions.frustumMinY) {
				MiscFunctions.frustumMinY = y;
			}

			if (z > MiscFunctions.frustumNearZ) {
				MiscFunctions.frustumNearZ = z;
			}

			if (x < MiscFunctions.frustumMaxX) {
				MiscFunctions.frustumMaxX = x;
			}

			if (y < MiscFunctions.frustumMaxY) {
				MiscFunctions.frustumMaxY = y;
			}

		} catch (RuntimeException var11) {
			throw GenUtil.makeThrowable(var11, "lb.JA(" + x + ',' + y + ',' + z + ',' + true + ')');
		}
	}

	public final void setFrustum(int var1, int var2, int var3, int var4) {
		try {
			this.m_L = new int[var4][];
			this.m_g = new byte[var4][];

			this.resourceDatabase = new int[var4][];
			this.m_i = new int[var3][];
			this.m_S = new boolean[var4];
			MiscFunctions.world_s_e = (long) var1;
			this.m_cb = var4;
			this.m_Hb = new int[var4];
			this.m_ec = new int[var2][];
			this.m_D = new long[var4];
		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "lb.U(" + var1 + ',' + var2 + ',' + var3 + ',' + var4 + ')');
		}
	}

	public final void setFrustum(int var1, int var2, int var3, int var4, int var5, int var6) {
		try {
			if (var4 == 0 && var6 == 0 && var1 == 0) {
				var4 = 32;
			}


			for (int var7 = var3; var7 < this.modelCount; ++var7) {
				this.models[var7].setDiffuseLight(var2, var5, var6, -115, var4, var1);
			}

		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8,
					"lb.KA(" + var1 + ',' + var2 + ',' + var3 + ',' + var4 + ',' + var5 + ',' + var6 + ')');
		}
	}

	private void setFrustum(int var1, int var2, int var3, Polygon[] var4) {
		try {
			if (var3 >= -50) {
				this.reduceSprites((byte) -98, (int) 32);
			}

			int var5;
			for (var5 = 0; var5 <= var1; ++var5) {
				var4[var5].m_c = false;
				var4[var5].m_f = var5;
				var4[var5].m_p = -1;
			}


			var5 = 0;

			while (true) {
				while (!var4[var5].m_c) {
					if (var5 == var1) {
						return;
					}

					Polygon var6 = var4[var5];
					var6.m_c = true;
					int var7 = var5;
					int var8 = var5 + var2;
					if (var8 >= var1) {
						var8 = var1 - 1;
					}

					for (int var9 = var8; var9 >= 1 + var7; --var9) {
						Polygon var10 = var4[var9];
						if (var10.maxP6 > var6.minP6 && var10.minP6 < var6.maxP6 && var10.maxP2 > var6.minP2
								&& var10.minP2 < var6.maxP2 && var6.m_f != var10.m_p
								&& !this.polygonHit2((byte) -84, var10, var6) && this.polygonHit1(var10, var6)) {
							this.setFrustum(var7, var4, var9, (byte) 34);
							var7 = this.m_e;
							if (var4[var9] != var10) {
								++var9;
							}

							var10.m_p = var6.m_f;
						}
					}
				}

				++var5;
			}
		} catch (RuntimeException var11) {
			throw GenUtil.makeThrowable(var11,
					"lb.I(" + var1 + ',' + var2 + ',' + var3 + ',' + (var4 != null ? "{...}" : "null") + ')');
		}
	}

	private void setFrustum(int var1, int var2, int[] var3, int var4, int var5, RSModel var6, int[] var7, int[] var8,
							int var9, int var10, int var11) {
		try {

			int var12;
			int var13;
			int var14;
			int var15;
			int var16;
			int var17;
			int var18;
			int var19;
			int var20;
			int var21;
			int var22;
			int var23;
			int var24;
			int var25;
			int var26;
			int var27;
			int var28;
			int var29;
			int var30;
			int var31;
			int var32;
			int var33;
			int var34;
			int var35;
			int var36;
			int var37;
			int var38;
			int var39;
			int var40;
			Scanline var52;
			if (var11 == 3) {
				var12 = this.m_Nb + var3[0];
				var13 = var3[1] + this.m_Nb;
				var14 = this.m_Nb + var3[2];
				var15 = var7[0];
				var16 = var7[1];
				var17 = var7[2];
				var18 = var8[0];
				var19 = var8[1];
				var20 = var8[2];
				var21 = this.m_wb + (this.m_Nb - 1);
				var22 = 0;
				var23 = 0;
				var24 = 0;
				var25 = 0;
				var26 = Scene.TRANSPARENT;
				var27 = -Scene.TRANSPARENT;
				if (var12 != var14) {
					if (var12 >= var14) {
						var24 = var20 << 8;
						var26 = var14;
						var27 = var12;
						var22 = var17 << 8;
					} else {
						var26 = var12;
						var27 = var14;
						var22 = var15 << 8;
						var24 = var18 << 8;
					}

					var25 = (var20 - var18 << 8) / (var14 - var12);
					var23 = (var17 - var15 << 8) / (var14 - var12);
					if (var26 < 0) {
						var22 -= var26 * var23;
						var24 -= var25 * var26;
						var26 = 0;
					}

					if (var27 > var21) {
						var27 = var21;
					}
				}

				var28 = 0;
				var29 = 0;
				var30 = 0;
				var31 = 0;
				var32 = Scene.TRANSPARENT;
				var33 = -Scene.TRANSPARENT;
				if (var12 != var13) {
					var29 = (var16 - var15 << 8) / (var13 - var12);
					var31 = (var19 - var18 << 8) / (var13 - var12);
					if (var13 > var12) {
						var30 = var18 << 8;
						var33 = var13;
						var28 = var15 << 8;
						var32 = var12;
					} else {
						var30 = var19 << 8;
						var32 = var13;
						var28 = var16 << 8;
						var33 = var12;
					}

					if (var33 > var21) {
						var33 = var21;
					}

					if (var32 < 0) {
						var30 -= var31 * var32;
						var28 -= var32 * var29;
						var32 = 0;
					}
				}

				var34 = 0;
				var35 = 0;
				var36 = 0;
				var37 = 0;
				var38 = Scene.TRANSPARENT;
				var39 = -Scene.TRANSPARENT;
				if (var14 != var13) {
					if (var14 > var13) {
						var34 = var16 << 8;
						var38 = var13;
						var36 = var19 << 8;
						var39 = var14;
					} else {
						var38 = var14;
						var36 = var20 << 8;
						var39 = var13;
						var34 = var17 << 8;
					}

					var37 = (var20 - var19 << 8) / (var14 - var13);
					var35 = (var17 - var16 << 8) / (var14 - var13);
					if (var38 < 0) {
						var36 -= var38 * var37;
						var34 -= var35 * var38;
						var38 = 0;
					}

					if (var21 < var39) {
						var39 = var21;
					}
				}

				this.m_Xb = var26;
				if (this.m_Xb > var32) {
					this.m_Xb = var32;
				}

				if (this.m_Xb > var38) {
					this.m_Xb = var38;
				}

				this.m_Cb = var27;
				if (var33 > this.m_Cb) {
					this.m_Cb = var33;
				}

				if (this.m_Cb < var39) {
					this.m_Cb = var39;
				}

				var40 = 0;

				for (var4 = this.m_Xb; this.m_Cb > var4; ++var4) {
					if (var4 >= var26 && var4 < var27) {
						var5 = var22;
						var1 = var22;
						var40 = var24;
						var9 = var24;
						var22 += var23;
						var24 += var25;
					} else {
						var1 = 655360;
						var5 = -655360;
					}

					if (var32 <= var4 && var4 < var33) {
						if (var5 < var28) {
							var5 = var28;
							var40 = var30;
						}

						if (var28 < var1) {
							var1 = var28;
							var9 = var30;
						}

						var30 += var31;
						var28 += var29;
					}

					if (var4 >= var38 && var39 > var4) {
						if (var34 > var5) {
							var40 = var36;
							var5 = var34;
						}

						if (var34 < var1) {
							var1 = var34;
							var9 = var36;
						}

						var36 += var37;
						var34 += var35;
					}

					Scanline var41 = this.m_x[var4];
					var41.m_e = var9;
					var41.m_l = var40;
					var41.m_d = var1;
					var41.m_k = var5;
				}

				if (this.m_Xb < this.m_Nb - this.m_wb) {
					this.m_Xb = this.m_Nb - this.m_wb;
				}
			} else if (var11 != 4) {
				this.m_Cb = this.m_Xb = var3[0] += this.m_Nb;

				for (var4 = 1; var11 > var4; ++var4) {
					if ((var12 = var3[var4] += this.m_Nb) >= this.m_Xb) {
						if (this.m_Cb < var12) {
							this.m_Cb = var12;
						}
					} else {
						this.m_Xb = var12;
					}
				}

				if (this.m_Cb >= this.m_Nb + this.m_wb) {
					this.m_Cb = this.m_Nb - 1 + this.m_wb;
				}

				if (this.m_Nb - this.m_wb > this.m_Xb) {
					this.m_Xb = this.m_Nb - this.m_wb;
				}

				if (this.m_Xb >= this.m_Cb) {
					return;
				}

				for (var4 = this.m_Xb; var4 < this.m_Cb; ++var4) {
					var52 = this.m_x[var4];
					var52.m_k = -655360;
					var52.m_d = 655360;
				}

				var12 = var11 - 1;
				var13 = var3[0];
				var14 = var3[var12];
				Scanline var53;
				if (var13 >= var14) {
					if (var14 < var13) {
						var15 = var7[var12] << 8;
						var16 = (var7[0] - var7[var12] << 8) / (var13 - var14);
						var17 = var8[var12] << 8;
						var18 = (var8[0] - var8[var12] << 8) / (var13 - var14);
						if (var13 > this.m_Cb) {
							var13 = this.m_Cb;
						}

						if (var14 < 0) {
							var17 -= var18 * var14;
							var15 -= var16 * var14;
							var14 = 0;
						}

						for (var4 = var14; var4 <= var13; ++var4) {
							var53 = this.m_x[var4];
							var53.m_d = var53.m_k = var15;
							var53.m_e = var53.m_l = var17;
							var15 += var16;
							var17 += var18;
						}
					}
				} else {
					var15 = var7[0] << 8;
					var16 = (var7[var12] - var7[0] << 8) / (var14 - var13);
					var17 = var8[0] << 8;
					var18 = (var8[var12] - var8[0] << 8) / (var14 - var13);
					if (var13 < 0) {
						var15 -= var13 * var16;
						var17 -= var13 * var18;
						var13 = 0;
					}

					if (var14 > this.m_Cb) {
						var14 = this.m_Cb;
					}

					for (var4 = var13; var4 <= var14; ++var4) {
						var53 = this.m_x[var4];
						var53.m_e = var53.m_l = var17;
						var53.m_d = var53.m_k = var15;
						var15 += var16;
						var17 += var18;
					}
				}

				for (var4 = 0; var4 < var12; ++var4) {
					var13 = var3[var4];
					var15 = var4 + 1;
					var14 = var3[var15];
					Scanline var54;
					if (var14 <= var13) {
						if (var13 > var14) {
							var16 = var7[var15] << 8;
							var17 = (var7[var4] - var7[var15] << 8) / (var13 - var14);
							var18 = var8[var15] << 8;
							var19 = (var8[var4] - var8[var15] << 8) / (var13 - var14);
							if (var14 < 0) {
								var16 -= var17 * var14;
								var18 -= var14 * var19;
								var14 = 0;
							}

							if (var13 > this.m_Cb) {
								var13 = this.m_Cb;
							}

							for (var20 = var14; var13 >= var20; ++var20) {
								var54 = this.m_x[var20];
								if (var16 < var54.m_d) {
									var54.m_e = var18;
									var54.m_d = var16;
								}

								if (var16 > var54.m_k) {
									var54.m_l = var18;
									var54.m_k = var16;
								}

								var18 += var19;
								var16 += var17;
							}
						}
					} else {
						var16 = var7[var4] << 8;
						var17 = (var7[var15] - var7[var4] << 8) / (var14 - var13);
						var18 = var8[var4] << 8;
						var19 = (var8[var15] - var8[var4] << 8) / (var14 - var13);
						if (var14 > this.m_Cb) {
							var14 = this.m_Cb;
						}

						if (var13 < 0) {
							var16 -= var13 * var17;
							var18 -= var13 * var19;
							var13 = 0;
						}

						for (var20 = var13; var14 >= var20; ++var20) {
							var54 = this.m_x[var20];
							if (var16 > var54.m_k) {
								var54.m_k = var16;
								var54.m_l = var18;
							}

							if (var16 < var54.m_d) {
								var54.m_d = var16;
								var54.m_e = var18;
							}

							var18 += var19;
							var16 += var17;
						}
					}
				}

				if (this.m_Nb - this.m_wb > this.m_Xb) {
					this.m_Xb = this.m_Nb - this.m_wb;
				}
			} else {
				var12 = var3[0] + this.m_Nb;
				var13 = this.m_Nb + var3[1];
				var14 = this.m_Nb + var3[2];
				var15 = this.m_Nb + var3[3];
				var16 = var7[0];
				var17 = var7[1];
				var18 = var7[2];
				var19 = var7[3];
				var20 = var8[0];
				var21 = var8[1];
				var22 = var8[2];
				var23 = var8[3];
				var24 = this.m_wb + this.m_Nb - 1;
				var25 = 0;
				var26 = 0;
				var27 = 0;
				var28 = 0;
				var29 = Scene.TRANSPARENT;
				var30 = -Scene.TRANSPARENT;
				if (var15 != var12) {
					var26 = (var19 - var16 << 8) / (var15 - var12);
					var28 = (var23 - var20 << 8) / (var15 - var12);
					if (var15 <= var12) {
						var29 = var15;
						var25 = var19 << 8;
						var27 = var23 << 8;
						var30 = var12;
					} else {
						var30 = var15;
						var25 = var16 << 8;
						var29 = var12;
						var27 = var20 << 8;
					}

					if (var29 < 0) {
						var27 -= var28 * var29;
						var25 -= var29 * var26;
						var29 = 0;
					}

					if (var24 < var30) {
						var30 = var24;
					}
				}

				var31 = 0;
				var32 = 0;
				var33 = 0;
				var34 = 0;
				var35 = Scene.TRANSPARENT;
				var36 = -Scene.TRANSPARENT;
				if (var12 != var13) {
					var34 = (var21 - var20 << 8) / (var13 - var12);
					if (var13 <= var12) {
						var35 = var13;
						var33 = var21 << 8;
						var36 = var12;
						var31 = var17 << 8;
					} else {
						var35 = var12;
						var36 = var13;
						var31 = var16 << 8;
						var33 = var20 << 8;
					}

					var32 = (var17 - var16 << 8) / (var13 - var12);
					if (var24 < var36) {
						var36 = var24;
					}

					if (var35 < 0) {
						var31 -= var35 * var32;
						var33 -= var34 * var35;
						var35 = 0;
					}
				}

				var37 = 0;
				var38 = 0;
				var39 = 0;
				var40 = 0;
				int var55 = Scene.TRANSPARENT;
				int var42 = -Scene.TRANSPARENT;
				if (var14 != var13) {
					var40 = (var22 - var21 << 8) / (var14 - var13);
					if (var14 <= var13) {
						var55 = var14;
						var39 = var22 << 8;
						var37 = var18 << 8;
						var42 = var13;
					} else {
						var55 = var13;
						var39 = var21 << 8;
						var42 = var14;
						var37 = var17 << 8;
					}

					var38 = (var18 - var17 << 8) / (var14 - var13);
					if (var55 < 0) {
						var39 -= var55 * var40;
						var37 -= var38 * var55;
						var55 = 0;
					}

					if (var24 < var42) {
						var42 = var24;
					}
				}

				int var43 = 0;
				int var44 = 0;
				int var45 = 0;
				int var46 = 0;
				int var47 = Scene.TRANSPARENT;
				int var48 = -Scene.TRANSPARENT;
				if (var15 != var14) {
					var46 = (var23 - var22 << 8) / (var15 - var14);
					if (var14 >= var15) {
						var48 = var14;
						var45 = var23 << 8;
						var43 = var19 << 8;
						var47 = var15;
					} else {
						var45 = var22 << 8;
						var48 = var15;
						var47 = var14;
						var43 = var18 << 8;
					}

					var44 = (var19 - var18 << 8) / (var15 - var14);
					if (var47 < 0) {
						var43 -= var47 * var44;
						var45 -= var47 * var46;
						var47 = 0;
					}

					if (var24 < var48) {
						var48 = var24;
					}
				}

				this.m_Xb = var29;
				if (this.m_Xb > var35) {
					this.m_Xb = var35;
				}

				if (var55 < this.m_Xb) {
					this.m_Xb = var55;
				}

				this.m_Cb = var30;
				if (this.m_Xb > var47) {
					this.m_Xb = var47;
				}

				if (var36 > this.m_Cb) {
					this.m_Cb = var36;
				}

				if (var42 > this.m_Cb) {
					this.m_Cb = var42;
				}

				if (this.m_Cb < var48) {
					this.m_Cb = var48;
				}

				int var49 = 0;

				for (var4 = this.m_Xb; this.m_Cb > var4; ++var4) {
					if (var4 >= var29 && var30 > var4) {
						var49 = var27;
						var9 = var27;
						var5 = var25;
						var1 = var25;
						var27 += var28;
						var25 += var26;
					} else {
						var5 = -655360;
						var1 = 655360;
					}

					if (var35 <= var4 && var36 > var4) {
						if (var31 < var1) {
							var9 = var33;
							var1 = var31;
						}

						if (var5 < var31) {
							var49 = var33;
							var5 = var31;
						}

						var31 += var32;
						var33 += var34;
					}

					if (var4 >= var55 && var4 < var42) {
						if (var37 > var5) {
							var5 = var37;
							var49 = var39;
						}

						if (var37 < var1) {
							var1 = var37;
							var9 = var39;
						}

						var37 += var38;
						var39 += var40;
					}

					if (var47 <= var4 && var48 > var4) {
						if (var43 > var5) {
							var49 = var45;
							var5 = var43;
						}

						if (var43 < var1) {
							var1 = var43;
							var9 = var45;
						}

						var45 += var46;
						var43 += var44;
					}

					Scanline var50 = this.m_x[var4];
					var50.m_e = var9;
					var50.m_d = var1;
					var50.m_k = var5;
					var50.m_l = var49;
				}

				if (this.m_Nb - this.m_wb > this.m_Xb) {
					this.m_Xb = this.m_Nb - this.m_wb;
				}
			}

			if (var10 == 5960) {
				if (this.m_K && this.m_cc < this.m_db && this.m_Wb >= this.m_Xb && this.m_Cb > this.m_Wb) {
					var52 = this.m_x[this.m_Wb];
					if (this.m_j >= var52.m_d >> 8 && this.m_j <= var52.m_k >> 8 && var52.m_k >= var52.m_d && !var6.m_db
							&& var6.m_zb[var2] == 0) {
						this.m_Ab[this.m_cc] = var6;
						this.m_qb[this.m_cc] = var2;
						++this.m_cc;
					}
				}

			}
		} catch (RuntimeException var51) {
			throw GenUtil.makeThrowable(var51,
					"lb.R(" + var1 + ',' + var2 + ',' + (var3 != null ? "{...}" : "null") + ',' + var4 + ',' + var5 + ',' + (var6 != null ? "{...}" : "null") + ',' + "{...}" + ',' + (var8 != null ? "{...}" : "null") + ',' + var9 + ',' + var10 + ',' + var11 + ')');
		}
	}

	private void setFrustum(int var1, int var2, Polygon[] var3, int var4) {
		try {

			if (var4 > var1) {
				int var5 = var1 - 1;
				int var6 = var4 + 1;
				int var7 = (var4 + var1) / 2;
				Polygon var8 = var3[var7];
				var3[var7] = var3[var1];
				var3[var1] = var8;
				int var9 = var8.m_t;

				while (var6 > var5) {
					do {
						++var5;
					} while (var3[var5].m_t > var9);

					do {
						--var6;
					} while (var9 > var3[var6].m_t);

					if (var6 > var5) {
						Polygon var10 = var3[var5];
						var3[var5] = var3[var6];
						var3[var6] = var10;
					}
				}

				this.setFrustum(var1, -1, var3, var6);
				this.setFrustum(var6 + 1, -1, var3, var4);
			}

		} catch (RuntimeException var11) {
			throw GenUtil.makeThrowable(var11,
					"lb.AA(" + var1 + ',' + -1 + ',' + "{...}" + ',' + var4 + ')');
		}
	}

	private boolean setFrustum(int var1, Polygon[] var2, int var3, byte var4) {
		try {


			while (true) {
				Polygon var6 = var2[var1];

				for (int var7 = var1 + 1; var3 >= var7; ++var7) {
					Polygon var8 = var2[var7];
					if (!this.polygonHit2((byte) -114, var6, var8)) {
						break;
					}

					var2[var1] = var8;
					var1 = var7;
					var2[var7] = var6;
					if (var3 == var7) {
						this.m_eb = var7 - 1;
						this.m_e = var7;
						return true;
					}
				}

				Polygon var11 = var2[var3];

				for (int var12 = var3 - 1; var12 >= var1; --var12) {
					Polygon var9 = var2[var12];
					if (!this.polygonHit2((byte) -46, var9, var11)) {
						break;
					}

					var2[var3] = var9;
					var2[var12] = var11;
					var3 = var12;
					if (var12 == var1) {
						this.m_eb = var12;
						this.m_e = var12 + 1;
						return true;
					}
				}

				if (var1 + 1 >= var3) {
					this.m_eb = var3;
					this.m_e = var1;
					return false;
				}

				if (!this.setFrustum(var1 + 1, var2, var3, (byte) 70)) {
					this.m_e = var1;
					return false;
				}

				var3 = this.m_eb;
			}
		} catch (RuntimeException var10) {
			throw GenUtil.makeThrowable(var10,
					"lb.FA(" + var1 + ',' + "{...}" + ',' + var3 + ',' + var4 + ')');
		}
	}

	private boolean setFrustum(int[] var1, int[] var2, int[] var3, int[] var4, int var5) {
		try {

			int var6 = var3.length;
			int var7 = var1.length;
			byte var16 = 0;
			int var8 = 0;
			int var18;
			int var20 = var18 = var2[0];
			int var10 = 0;

			int var22;
			for (var22 = 1; var6 > var22; ++var22) {
				if (var2[var22] >= var18) {
					if (var2[var22] > var20) {
						var20 = var2[var22];
					}
				} else {
					var8 = var22;
					var18 = var2[var22];
				}
			}

			int var19;
			int var21 = var19 = var4[0];

			for (var22 = var5; var7 > var22; ++var22) {
				if (var4[var22] >= var19) {
					if (var21 < var4[var22]) {
						var21 = var4[var22];
					}
				} else {
					var19 = var4[var22];
					var10 = var22;
				}
			}

			if (var19 < var20) {
				if (var21 <= var18) {
					return false;
				} else {
					int var9;
					int var11;
					int var12;
					int var13;
					int var14;
					int var15;
					boolean var17;
					if (var4[var10] > var2[var8]) {
						for (var9 = var8; var2[var8] < var4[var10]; var8 = (var8 - (1 - var6)) % var6) {
							;
						}

						while (var2[var9] < var4[var10]) {
							var9 = (1 + var9) % var6;
						}

						var12 = this.booleanCombinatoric3(var2[(var8 + 1) % var6], false, var4[var10], var2[var8],
								var3[var8], var3[(1 + var8) % var6]);
						var13 = this.booleanCombinatoric3(var2[(var6 + (var9 - 1)) % var6], false, var4[var10],
								var2[var9], var3[var9], var3[(var6 - 1 + var9) % var6]);
						var14 = var1[var10];
						var17 = var12 < var14 | var13 < var14;
						if (this.booleanCombinatoric2(var14, var17, var12, (byte) -71, var13)) {
							return true;
						}

						var11 = (var10 + 1) % var7;
						var10 = (var10 + var7 - 1) % var7;
						if (var8 == var9) {
							var16 = 1;
						}
					} else {
						for (var11 = var10; var2[var8] > var4[var10]; var10 = (var10 + var7 - 1) % var7) {
							;
						}

						for (var12 = var3[var8]; var2[var8] > var4[var11]; var11 = (var11 + 1) % var7) {
							;
						}

						var14 = this.booleanCombinatoric3(var4[(var10 + 1) % var7], false, var2[var8], var4[var10],
								var1[var10], var1[(var10 + 1) % var7]);
						var15 = this.booleanCombinatoric3(var4[(var7 + (var11 - 1)) % var7], false, var2[var8],
								var4[var11], var1[var11], var1[(var11 - 1 + var7) % var7]);
						var17 = var12 < var14 | var12 < var15;
						if (this.booleanCombinatoric2(var12, !var17, var14, (byte) -71, var15)) {
							return true;
						}

						var9 = (1 + var8) % var6;
						var8 = (var6 + (var8 - 1)) % var6;
						if (var10 == var11) {
							var16 = 2;
						}
					}

					while (var16 == 0) {
						if (var2[var8] >= var2[var9]) {
							if (var2[var9] >= var4[var10]) {
								if (var4[var10] >= var4[var11]) {
									var12 = this.booleanCombinatoric3(var2[(var8 + 1) % var6], false, var4[var11],
											var2[var8], var3[var8], var3[(1 + var8) % var6]);
									var13 = this.booleanCombinatoric3(var2[(var9 - 1 + var6) % var6], false,
											var4[var11], var2[var9], var3[var9], var3[(var6 + (var9 - 1)) % var6]);
									var14 = this.booleanCombinatoric3(var4[(1 + var10) % var7], false, var4[var11],
											var4[var10], var1[var10], var1[(var10 + 1) % var7]);
									var15 = var1[var11];
									if (this.booleanCombinatoric(var17, var13, var15, var12, var14)) {
										return true;
									}

									var11 = (1 + var11) % var7;
									if (var11 == var10) {
										var16 = 2;
									}
								} else {
									var12 = this.booleanCombinatoric3(var2[(var8 + 1) % var6], false, var4[var10],
											var2[var8], var3[var8], var3[(1 + var8) % var6]);
									var13 = this.booleanCombinatoric3(var2[(var9 + var6 - 1) % var6], false,
											var4[var10], var2[var9], var3[var9], var3[(var6 - 1 + var9) % var6]);
									var14 = var1[var10];
									var15 = this.booleanCombinatoric3(var4[(var7 + (var11 - 1)) % var7], false,
											var4[var10], var4[var11], var1[var11], var1[(var11 - 1 + var7) % var7]);
									if (this.booleanCombinatoric(var17, var13, var15, var12, var14)) {
										return true;
									}

									var10 = (var10 - 1 + var7) % var7;
									if (var11 == var10) {
										var16 = 2;
									}
								}
							} else if (var2[var9] < var4[var11]) {
								var12 = this.booleanCombinatoric3(var2[(var8 + 1) % var6], false, var2[var9],
										var2[var8], var3[var8], var3[(1 + var8) % var6]);
								var13 = var3[var9];
								var14 = this.booleanCombinatoric3(var4[(var10 + 1) % var7], false, var2[var9],
										var4[var10], var1[var10], var1[(1 + var10) % var7]);
								var15 = this.booleanCombinatoric3(var4[(var11 - 1 + var7) % var7], false, var2[var9],
										var4[var11], var1[var11], var1[(var11 - (1 - var7)) % var7]);
								if (this.booleanCombinatoric(var17, var13, var15, var12, var14)) {
									return true;
								}

								var9 = (var9 + 1) % var6;
								if (var8 == var9) {
									var16 = 1;
								}
							} else {
								var12 = this.booleanCombinatoric3(var2[(var8 + 1) % var6], false, var4[var11],
										var2[var8], var3[var8], var3[(var8 + 1) % var6]);
								var13 = this.booleanCombinatoric3(var2[(var6 + var9 - 1) % var6], false, var4[var11],
										var2[var9], var3[var9], var3[(var6 - 1 + var9) % var6]);
								var14 = this.booleanCombinatoric3(var4[(var10 + 1) % var7], false, var4[var11],
										var4[var10], var1[var10], var1[(var10 + 1) % var7]);
								var15 = var1[var11];
								if (this.booleanCombinatoric(var17, var13, var15, var12, var14)) {
									return true;
								}

								var11 = (var11 + 1) % var7;
								if (var10 == var11) {
									var16 = 2;
								}
							}
						} else if (var4[var10] > var2[var8]) {
							if (var2[var8] >= var4[var11]) {
								var12 = this.booleanCombinatoric3(var2[(1 + var8) % var6], false, var4[var11],
										var2[var8], var3[var8], var3[(1 + var8) % var6]);
								var13 = this.booleanCombinatoric3(var2[(var6 + (var9 - 1)) % var6], false, var4[var11],
										var2[var9], var3[var9], var3[(var6 + (var9 - 1)) % var6]);
								var14 = this.booleanCombinatoric3(var4[(var10 + 1) % var7], false, var4[var11],
										var4[var10], var1[var10], var1[(1 + var10) % var7]);
								var15 = var1[var11];
								if (this.booleanCombinatoric(var17, var13, var15, var12, var14)) {
									return true;
								}

								var11 = (1 + var11) % var7;
								if (var10 == var11) {
									var16 = 2;
								}
							} else {
								var12 = var3[var8];
								var13 = this.booleanCombinatoric3(var2[(var9 + (var6 - 1)) % var6], false, var2[var8],
										var2[var9], var3[var9], var3[(var9 + var6 - 1) % var6]);
								var14 = this.booleanCombinatoric3(var4[(1 + var10) % var7], false, var2[var8],
										var4[var10], var1[var10], var1[(1 + var10) % var7]);
								var15 = this.booleanCombinatoric3(var4[(var7 - 1 + var11) % var7], false, var2[var8],
										var4[var11], var1[var11], var1[(var7 + (var11 - 1)) % var7]);
								if (this.booleanCombinatoric(var17, var13, var15, var12, var14)) {
									return true;
								}

								var8 = (var6 + (var8 - 1)) % var6;
								if (var8 == var9) {
									var16 = 1;
								}
							}
						} else if (var4[var10] < var4[var11]) {
							var12 = this.booleanCombinatoric3(var2[(1 + var8) % var6], false, var4[var10], var2[var8],
									var3[var8], var3[(1 + var8) % var6]);
							var13 = this.booleanCombinatoric3(var2[(var6 + (var9 - 1)) % var6], false, var4[var10],
									var2[var9], var3[var9], var3[(var6 - 1 + var9) % var6]);
							var14 = var1[var10];
							var15 = this.booleanCombinatoric3(var4[(var7 + (var11 - 1)) % var7], false, var4[var10],
									var4[var11], var1[var11], var1[(var7 + var11 - 1) % var7]);
							if (this.booleanCombinatoric(var17, var13, var15, var12, var14)) {
								return true;
							}

							var10 = (var7 + var10 - 1) % var7;
							if (var11 == var10) {
								var16 = 2;
							}
						} else {
							var12 = this.booleanCombinatoric3(var2[(var8 + 1) % var6], false, var4[var11], var2[var8],
									var3[var8], var3[(var8 + 1) % var6]);
							var13 = this.booleanCombinatoric3(var2[(var9 + var6 - 1) % var6], false, var4[var11],
									var2[var9], var3[var9], var3[(var6 + (var9 - 1)) % var6]);
							var14 = this.booleanCombinatoric3(var4[(var10 + 1) % var7], false, var4[var11], var4[var10],
									var1[var10], var1[(1 + var10) % var7]);
							var15 = var1[var11];
							if (this.booleanCombinatoric(var17, var13, var15, var12, var14)) {
								return true;
							}

							var11 = (var11 + 1) % var7;
							if (var10 == var11) {
								var16 = 2;
							}
						}
					}

					while (var16 == 1) {
						if (~var2[var8] <= ~var4[var10]) {
							if (var4[var10] < var4[var11]) {
								var12 = this.booleanCombinatoric3(var2[(1 + var8) % var6], false, var4[var10],
										var2[var8], var3[var8], var3[(var8 + 1) % var6]);
								var13 = this.booleanCombinatoric3(var2[(var9 - 1 + var6) % var6], false, var4[var10],
										var2[var9], var3[var9], var3[(var9 - 1 + var6) % var6]);
								var14 = var1[var10];
								var15 = this.booleanCombinatoric3(var4[(var11 - 1 + var7) % var7], false, var4[var10],
										var4[var11], var1[var11], var1[(var11 + (var7 - 1)) % var7]);
								if (this.booleanCombinatoric(var17, var13, var15, var12, var14)) {
									return true;
								}

								var10 = (var10 + (var7 - 1)) % var7;
								if (var10 == var11) {
									var16 = 0;
								}
							} else {
								var12 = this.booleanCombinatoric3(var2[(1 + var8) % var6], false, var4[var11],
										var2[var8], var3[var8], var3[(1 + var8) % var6]);
								var13 = this.booleanCombinatoric3(var2[(var6 + var9 - 1) % var6], false, var4[var11],
										var2[var9], var3[var9], var3[(var9 + (var6 - 1)) % var6]);
								var14 = this.booleanCombinatoric3(var4[(var10 + 1) % var7], false, var4[var11],
										var4[var10], var1[var10], var1[(1 + var10) % var7]);
								var15 = var1[var11];
								if (this.booleanCombinatoric(var17, var13, var15, var12, var14)) {
									return true;
								}

								var11 = (1 + var11) % var7;
								if (var10 == var11) {
									var16 = 0;
								}
							}
						} else {
							if (var4[var11] > var2[var8]) {
								var12 = var3[var8];
								var14 = this.booleanCombinatoric3(var4[(var10 + 1) % var7], false, var2[var8],
										var4[var10], var1[var10], var1[(1 + var10) % var7]);
								var15 = this.booleanCombinatoric3(var4[(var11 - 1 + var7) % var7], false, var2[var8],
										var4[var11], var1[var11], var1[(var7 + (var11 - 1)) % var7]);
								if (!this.booleanCombinatoric2(var12, !var17, var14, (byte) -71, var15)) {
									return false;
								}

								return true;
							}

							var12 = this.booleanCombinatoric3(var2[(1 + var8) % var6], false, var4[var11], var2[var8],
									var3[var8], var3[(1 + var8) % var6]);
							var13 = this.booleanCombinatoric3(var2[(var9 + var6 - 1) % var6], false, var4[var11],
									var2[var9], var3[var9], var3[(var9 + var6 - 1) % var6]);
							var14 = this.booleanCombinatoric3(var4[(1 + var10) % var7], false, var4[var11], var4[var10],
									var1[var10], var1[(1 + var10) % var7]);
							var15 = var1[var11];
							if (this.booleanCombinatoric(var17, var13, var15, var12, var14)) {
								return true;
							}

							var11 = (1 + var11) % var7;
							if (var10 == var11) {
								var16 = 0;
							}
						}
					}

					while (var16 == 2) {
						if (var4[var10] < var2[var8]) {
							if (var4[var10] < var2[var9]) {
								var12 = this.booleanCombinatoric3(var2[(var8 + 1) % var6], false, var4[var10],
										var2[var8], var3[var8], var3[(var8 + 1) % var6]);
								var13 = this.booleanCombinatoric3(var2[(var9 - 1 + var6) % var6], false, var4[var10],
										var2[var9], var3[var9], var3[(var6 - 1 + var9) % var6]);
								var14 = var1[var10];
								if (!this.booleanCombinatoric2(var14, var17, var12, (byte) -71, var13)) {
									return false;
								}

								return true;
							}

							var12 = this.booleanCombinatoric3(var2[(1 + var8) % var6], false, var2[var9], var2[var8],
									var3[var8], var3[(1 + var8) % var6]);
							var13 = var3[var9];
							var14 = this.booleanCombinatoric3(var4[(1 + var10) % var7], false, var2[var9], var4[var10],
									var1[var10], var1[(var10 + 1) % var7]);
							var15 = this.booleanCombinatoric3(var4[(var11 - 1 + var7) % var7], false, var2[var9],
									var4[var11], var1[var11], var1[(var11 + var7 - 1) % var7]);
							if (this.booleanCombinatoric(var17, var13, var15, var12, var14)) {
								return true;
							}

							var9 = (1 + var9) % var6;
							if (var8 == var9) {
								var16 = 0;
							}
						} else if (var2[var8] >= var2[var9]) {
							var12 = this.booleanCombinatoric3(var2[(var8 + 1) % var6], false, var2[var9], var2[var8],
									var3[var8], var3[(1 + var8) % var6]);
							var13 = var3[var9];
							var14 = this.booleanCombinatoric3(var4[(1 + var10) % var7], false, var2[var9], var4[var10],
									var1[var10], var1[(1 + var10) % var7]);
							var15 = this.booleanCombinatoric3(var4[(var11 - (1 - var7)) % var7], false, var2[var9],
									var4[var11], var1[var11], var1[(var11 + var7 - 1) % var7]);
							if (this.booleanCombinatoric(var17, var13, var15, var12, var14)) {
								return true;
							}

							var9 = (1 + var9) % var6;
							if (var8 == var9) {
								var16 = 0;
							}
						} else {
							var12 = var3[var8];
							var13 = this.booleanCombinatoric3(var2[(var9 + var6 - 1) % var6], false, var2[var8],
									var2[var9], var3[var9], var3[(var6 + var9 - 1) % var6]);
							var14 = this.booleanCombinatoric3(var4[(var10 + 1) % var7], false, var2[var8], var4[var10],
									var1[var10], var1[(var10 + 1) % var7]);
							var15 = this.booleanCombinatoric3(var4[(var11 + (var7 - 1)) % var7], false, var2[var8],
									var4[var11], var1[var11], var1[(var7 + var11 - 1) % var7]);
							if (this.booleanCombinatoric(var17, var13, var15, var12, var14)) {
								return true;
							}

							var8 = (var6 + var8 - 1) % var6;
							if (var9 == var8) {
								var16 = 0;
							}
						}
					}

					if (var4[var10] <= var2[var8]) {
						var12 = this.booleanCombinatoric3(var2[(1 + var8) % var6], false, var4[var10], var2[var8],
								var3[var8], var3[(var8 + 1) % var6]);
						var13 = this.booleanCombinatoric3(var2[(var9 - 1 + var6) % var6], false, var4[var10],
								var2[var9], var3[var9], var3[(var6 + (var9 - 1)) % var6]);
						var14 = var1[var10];
						if (!this.booleanCombinatoric2(var14, var17, var12, (byte) -71, var13)) {
							return false;
						} else {
							return true;
						}
					} else {
						var12 = var3[var8];
						var14 = this.booleanCombinatoric3(var4[(1 + var10) % var7], false, var2[var8], var4[var10],
								var1[var10], var1[(var10 + 1) % var7]);
						var15 = this.booleanCombinatoric3(var4[(var7 + (var11 - 1)) % var7], false, var2[var8],
								var4[var11], var1[var11], var1[(var7 - 1 + var11) % var7]);
						if (this.booleanCombinatoric2(var12, !var17, var14, (byte) -71, var15)) {
							return true;
						} else {
							return false;
						}
					}
				}
			} else {
				return false;
			}
		} catch (RuntimeException var23) {
			throw GenUtil.makeThrowable(var23,
					"lb.B(" + (var1 != null ? "{...}" : "null") + ',' + (var2 != null ? "{...}" : "null") + ',' + (var3 != null ? "{...}" : "null") + ',' + "{...}" + ',' + var5 + ')');
		}
	}

	private void setFrustum(int[] var1, RSModel model, int var3, int var4, int var5, int[] var6, int[] var7, int var8,
							int var9) {
		try {

			if (var5 != -2) {
				int var10;
				int var11;
				int var12;
				int var13;
				int var14;
				int var15;
				int var16;
				int var17;
				int var18;
				int var19;
				if (var5 >= 0) {
					if (var5 >= this.m_cb) {
						var5 = 0;
					}

					this.b(var5, true);
					var10 = var7[0];
					var11 = var1[0];
					var12 = var6[0];
					var13 = var10 - var7[1];
					var14 = var11 - var1[1];
					--var4;
					var15 = var12 - var6[1];
					var16 = var7[var4] - var10;
					var17 = var1[var4] - var11;
					var18 = var6[var4] - var12;
					int var20;
					int var21;
					int var22;
					int var23;
					int var24;
					int var25;
					int var26;
					int var27;
					int var28;
					int var29;
					int var30;
					int var31;
					int var32;
					int var33;
					byte var34;
					Scanline var35;
					int var36;
					int var37;
					int var38;
					int var39;
					if (this.m_Hb[var5] == 1) {
						var19 = var11 * var16 - var17 * var10 << 12;
						var20 = var17 * var12 - var18 * var11 << 4 - this.rot1024_vp_src + 5 + 7;
						var21 = var18 * var10 - var16 * var12 << 7 - this.rot1024_vp_src + 5;
						var22 = var11 * var13 - var10 * var14 << 12;
						var23 = var14 * var12 - var15 * var11 << 5 - this.rot1024_vp_src + 11;
						var24 = var10 * var15 - var12 * var13 << 7 + (5 - this.rot1024_vp_src);
						var25 = var16 * var14 - var17 * var13 << 5;
						var26 = var15 * var17 - var14 * var18 << 4 + (5 - this.rot1024_vp_src);
						var27 = var13 * var18 - var15 * var16 >> this.rot1024_vp_src - 5;
						var28 = var20 >> 4;
						var29 = var23 >> 4;
						var30 = var26 >> 4;
						var31 = this.m_Xb - this.m_Nb;
						var32 = this.m_vb;
						var33 = var32 * this.m_Xb + this.m_Zb;
						var22 += var31 * var24;
						var34 = 1;
						var25 += var27 * var31;
						var19 += var21 * var31;
						if (this.m_f) {
							if ((this.m_Xb & 1) == 1) {
								var22 += var24;
								var33 += var32;
								var19 += var21;
								var25 += var27;
								++this.m_Xb;
							}

							var27 <<= 1;
							var24 <<= 1;
							var34 = 2;
							var21 <<= 1;
							var32 <<= 1;
						}

						if (!model.m_Kb) {
							if (this.m_S[var5]) {
								for (var9 = this.m_Xb; var9 < this.m_Cb; var9 += var34) {
									var35 = this.m_x[var9];
									var8 = var35.m_d >> 8;
									var36 = var35.m_k >> 8;
									var37 = var36 - var8;
									if (var37 <= 0) {
										var22 += var24;
										var19 += var21;
										var33 += var32;
										var25 += var27;
									} else {
										var38 = var35.m_e;
										var39 = (var35.m_l - var38) / var37;
										if (-this.m_A > var8) {
											var38 += (-var8 - this.m_A) * var39;
											var8 = -this.m_A;
											var37 = var36 - var8;
										}

										if (var36 > this.m_A) {
											var36 = this.m_A;
											var37 = var36 - var8;
										}

										Shader.shadeScanline(var23, 10, 0, 0, this.pixelData, var25 + var8 * var30, var38,
												var8 * var28 + var19, var22 + var8 * var29, var8 + var33, var26, var39,
												0, var20, this.resourceDatabase[var5], var37);
										var33 += var32;
										var22 += var24;
										var25 += var27;
										var19 += var21;
									}
								}
							} else {
								for (var9 = this.m_Xb; var9 < this.m_Cb; var9 += var34) {
									var35 = this.m_x[var9];
									var8 = var35.m_d >> 8;
									var36 = var35.m_k >> 8;
									var37 = var36 - var8;
									if (var37 <= 0) {
										var25 += var27;
										var19 += var21;
										var33 += var32;
										var22 += var24;
									} else {
										var38 = var35.m_e;
										var39 = (var35.m_l - var38) / var37;
										if (var8 < -this.m_A) {
											var38 += (-this.m_A - var8) * var39;
											var8 = -this.m_A;
											var37 = var36 - var8;
										}

										if (this.m_A < var36) {
											var36 = this.m_A;
											var37 = var36 - var8;
										}

										Shader.shadeScanline(var22 + var29 * var8, var20, (byte) 50,
												var25 + var8 * var30, var38, var39 << 2, this.resourceDatabase[var5],
												var8 + var33, var8 * var28 + var19, var26, 0, 0, this.pixelData, var23,
												var37);
										var19 += var21;
										var25 += var27;
										var33 += var32;
										var22 += var24;
									}
								}
							}
						} else {
							for (var9 = this.m_Xb; var9 < this.m_Cb; var9 += var34) {
								var35 = this.m_x[var9];
								var8 = var35.m_d >> 8;
								var36 = var35.m_k >> 8;
								var37 = var36 - var8;
								if (var37 > 0) {
									var38 = var35.m_e;
									var39 = (var35.m_l - var38) / var37;
									if (-this.m_A > var8) {
										var38 += var39 * (-var8 - this.m_A);
										var8 = -this.m_A;
										var37 = var36 - var8;
									}

									if (var36 > this.m_A) {
										var36 = this.m_A;
										var37 = var36 - var8;
									}

									Shader.shadeScanline(var33 + var8, var22 + var8 * var29, var19 + var8 * var28, 0,
											var38, var23, 0, var25 + var8 * var30, var20, var39 << 2,
											this.resourceDatabase[var5], var37, var26, this.pixelData, (byte) 119);
									var33 += var32;
									var22 += var24;
									var19 += var21;
									var25 += var27;
								} else {
									var33 += var32;
									var25 += var27;
									var19 += var21;
									var22 += var24;
								}
							}
						}
					} else {
						var19 = var16 * var11 - var10 * var17 << 11;
						var20 = var12 * var17 - var18 * var11 << 4 + 6 + (5 - this.rot1024_vp_src);
						var21 = var18 * var10 - var16 * var12 << 11 - this.rot1024_vp_src;
						var22 = var11 * var13 - var14 * var10 << 11;
						var23 = var12 * var14 - var11 * var15 << 4 - this.rot1024_vp_src + 11;
						var24 = var15 * var10 - var12 * var13 << 11 - this.rot1024_vp_src;
						var25 = var16 * var14 - var17 * var13 << 5;
						var26 = var17 * var15 - var14 * var18 << 4 + (5 - this.rot1024_vp_src);
						var27 = var18 * var13 - var16 * var15 >> this.rot1024_vp_src - 5;
						var28 = var20 >> 4;
						var29 = var23 >> 4;
						var30 = var26 >> 4;
						var31 = this.m_Xb - this.m_Nb;
						var32 = this.m_vb;
						var33 = var32 * this.m_Xb + this.m_Zb;
						var22 += var31 * var24;
						var34 = 1;
						var19 += var31 * var21;
						var25 += var27 * var31;
						if (this.m_f) {
							if ((1 & this.m_Xb) == 1) {
								var22 += var24;
								var25 += var27;
								var19 += var21;
								++this.m_Xb;
								var33 += var32;
							}

							var21 <<= 1;
							var32 <<= 1;
							var27 <<= 1;
							var34 = 2;
							var24 <<= 1;
						}

						if (model.m_Kb) {
							for (var9 = this.m_Xb; this.m_Cb > var9; var9 += var34) {
								var35 = this.m_x[var9];
								var8 = var35.m_d >> 8;
								var36 = var35.m_k >> 8;
								var37 = var36 - var8;
								if (var37 <= 0) {
									var22 += var24;
									var25 += var27;
									var33 += var32;
									var19 += var21;
								} else {
									var38 = var35.m_e;
									var39 = (var35.m_l - var38) / var37;
									if (var8 < -this.m_A) {
										var38 += var39 * (-this.m_A - var8);
										var8 = -this.m_A;
										var37 = var36 - var8;
									}

									if (var36 > this.m_A) {
										var36 = this.m_A;
										var37 = var36 - var8;
									}

									Shader.shadeScanline(this.pixelData, var23, var26, var8 * var30 + var25, var39, var38,
											var8 + var33, var37, var28 * var8 + var19, 0, this.resourceDatabase[var5],
											false, var20, var8 * var29 + var22, 0);
									var33 += var32;
									var25 += var27;
									var19 += var21;
									var22 += var24;
								}
							}
						} else if (!this.m_S[var5]) {
							for (var9 = this.m_Xb; this.m_Cb > var9; var9 += var34) {
								var35 = this.m_x[var9];
								var8 = var35.m_d >> 8;
								var36 = var35.m_k >> 8;
								var37 = var36 - var8;
								if (var37 > 0) {
									var38 = var35.m_e;
									var39 = (var35.m_l - var38) / var37;
									if (-this.m_A > var8) {
										var38 += (-this.m_A - var8) * var39;
										var8 = -this.m_A;
										var37 = var36 - var8;
									}

									if (this.m_A < var36) {
										var36 = this.m_A;
										var37 = var36 - var8;
									}

									Shader.shadeScanline(var39, 1121159302, var23, var8 * var29 + var22, var20,
											this.resourceDatabase[var5], var38, 0, var19 + var28 * var8, 0, this.pixelData,
											var33 + var8, var25 + var8 * var30, var26, var37);
									var33 += var32;
									var22 += var24;
									var19 += var21;
									var25 += var27;
								} else {
									var33 += var32;
									var25 += var27;
									var22 += var24;
									var19 += var21;
								}
							}
						} else {
							for (var9 = this.m_Xb; this.m_Cb > var9; var9 += var34) {
								var35 = this.m_x[var9];
								var8 = var35.m_d >> 8;
								var36 = var35.m_k >> 8;
								var37 = var36 - var8;
								if (var37 <= 0) {
									var25 += var27;
									var33 += var32;
									var19 += var21;
									var22 += var24;
								} else {
									var38 = var35.m_e;
									var39 = (var35.m_l - var38) / var37;
									if (var8 < -this.m_A) {
										var38 += var39 * (-this.m_A - var8);
										var8 = -this.m_A;
										var37 = var36 - var8;
									}

									if (var36 > this.m_A) {
										var36 = this.m_A;
										var37 = var36 - var8;
									}

									Shader.shadeScanline(var37, var30 * var8 + var25, 0, (byte) 25, 0, var20, var26,
											var39, this.resourceDatabase[var5], this.pixelData, var8 + var33,
											var8 * var28 + var19, 0, var23, var38, var29 * var8 + var22);
									var25 += var27;
									var22 += var24;
									var33 += var32;
									var19 += var21;
								}
							}
						}
					}
				} else {
					for (var10 = 0; var10 < this.m_ib; ++var10) {
						if (this.m_v[var10] == var5) {
							this.m_H = this.m_Ib[var10];
							break;
						}

						if (var10 == this.m_ib - 1) {
							var11 = (int) (Math.random() * (double) this.m_ib);
							this.m_v[var11] = var5;
							var5 = -1 - var5;
							var12 = ((32025 & var5) >> 10) * 8;
							var13 = ((1019 & var5) >> 5) * 8;
							var14 = (31 & var5) * 8;

							for (var15 = 0; var15 < 256; ++var15) {
								var16 = var15 * var15;
								var17 = var12 * var16 / 65536;
								var18 = var16 * var13 / 65536;
								var19 = var14 * var16 / 65536;
								this.m_Ib[var11][255 - var15] = var19 + (var18 << 8) + (var17 << 16);
							}

							this.m_H = this.m_Ib[var11];
						}
					}

					var10 = this.m_vb;
					var11 = this.m_Xb * var10 + this.m_Zb;
					byte var41 = 1;
					if (this.m_f) {
						if ((this.m_Xb & 1) == 1) {
							++this.m_Xb;
							var11 += var10;
						}

						var10 <<= 1;
						var41 = 2;
					}

					Scanline var42;
					if (model.m_cb) {
						for (var9 = this.m_Xb; this.m_Cb > var9; var9 += var41) {
							var42 = this.m_x[var9];
							var8 = var42.m_d >> 8;
							var14 = var42.m_k >> 8;
							var15 = var14 - var8;
							if (var15 > 0) {
								var16 = var42.m_e;
								var17 = (var42.m_l - var16) / var15;
								if (var8 < -this.m_A) {
									var16 += var17 * (-this.m_A - var8);
									var8 = -this.m_A;
									var15 = var14 - var8;
								}

								if (this.m_A < var14) {
									var14 = this.m_A;
									var15 = var14 - var8;
								}

								GraphicsController.a(var16, this.m_H, -var15, this.pixelData, 0, var17, var8 + var11,
										var3 - 1);
								var11 += var10;
							} else {
								var11 += var10;
							}
						}
					} else if (!this.m_Ub) {
						for (var9 = this.m_Xb; this.m_Cb > var9; var9 += var41) {
							var42 = this.m_x[var9];
							var8 = var42.m_d >> 8;
							var14 = var42.m_k >> 8;
							var15 = var14 - var8;
							if (var15 > 0) {
								var16 = var42.m_e;
								var17 = (var42.m_l - var16) / var15;
								if (var8 < -this.m_A) {
									var16 += (-var8 - this.m_A) * var17;
									var8 = -this.m_A;
									var15 = var14 - var8;
								}

								if (this.m_A < var14) {
									var14 = this.m_A;
									var15 = var14 - var8;
								}

								MiscFunctions.copyBlock16(0, var17, -var15, this.pixelData, this.m_H, var16, var11 + var8,
										418609192);
								var11 += var10;
							} else {
								var11 += var10;
							}
						}
					} else {
						for (var9 = this.m_Xb; var9 < this.m_Cb; var9 += var41) {
							var42 = this.m_x[var9];
							var8 = var42.m_d >> 8;
							var14 = var42.m_k >> 8;
							var15 = var14 - var8;
							if (var15 > 0) {
								var16 = var42.m_e;
								var17 = (var42.m_l - var16) / var15;
								if (var8 < -this.m_A) {
									var16 += (-this.m_A - var8) * var17;
									var8 = -this.m_A;
									var15 = var14 - var8;
								}

								if (this.m_A < var14) {
									var14 = this.m_A;
									var15 = var14 - var8;
								}

								MiscFunctions.copyBlock4(var17, 0, this.m_H, var16, var8 + var11, this.pixelData, -var15,
										(byte) 82);
								var11 += var10;
							} else {
								var11 += var10;
							}
						}
					}
				}

				if (var3 != 1) {
					this.polygonHit2((byte) -48, (Polygon) null, (Polygon) null);
				}

			}
		} catch (RuntimeException var40) {
			throw GenUtil.makeThrowable(var40,
					"lb.O(" + (var1 != null ? "{...}" : "null") + ',' + (model != null ? "{...}" : "null") + ',' + var3
							+ ',' + var4 + ',' + var5 + ',' + (var6 != null ? "{...}" : "null") + ','
							+ (var7 != null ? "{...}" : "null") + ',' + var8 + ',' + var9 + ')');
		}
	}

	public final void addModel(RSModel mod) {
		try {

			if (mod == null) {
				System.out.println("Warning tried to add null object!");
			}
			if (this.modelCount < this.m_u) {
				this.m_jb[this.modelCount] = 0;
				this.models[this.modelCount++] = mod;
			}

		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "lb.NA(" + "null" + ',' + 118 + ')');
		}
	}

	public final RSModel[] b(byte var1) {
		try {
			if (var1 < 95) {
				return (RSModel[]) null;
			} else {

				return this.m_Ab;
			}
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "lb.G(" + var1 + ')');
		}
	}

	public final int b(int var1) {
		try {

			if (var1 != 0) {
				this.m_S = (boolean[]) null;
			}

			return this.m_cc;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "lb.N(" + var1 + ')');
		}
	}

	private void b(int var1, boolean var2) {
		try {

			if (!var2) {
				this.m_K = false;
			}

			if (var1 >= 0) {
				this.m_D[var1] = (long) (MiscFunctions.world_s_e++);
				if (this.resourceDatabase[var1] == null) {
					int var3;
					int var5;
					int var6;
					long var8;
					if (this.m_Hb[var1] != 0) {
						for (var3 = 0; this.m_ec.length > var3; ++var3) {
							if (this.m_ec[var3] == null) {
								this.m_ec[var3] = new int[65536];
								this.resourceDatabase[var1] = this.m_ec[var3];
								this.setFrustum((int) var1, (byte) 118);
								return;
							}
						}

						var8 = 1073741824L;
						var5 = 0;

						for (var6 = 0; this.m_cb > var6; ++var6) {
							if (var1 != var6 && this.m_Hb[var6] == 1 && null != this.resourceDatabase[var6]
									&& this.m_D[var6] < var8) {
								var8 = this.m_D[var6];
								var5 = var6;
							}
						}

						this.resourceDatabase[var1] = this.resourceDatabase[var5];
						this.resourceDatabase[var5] = null;
						this.setFrustum((int) var1, (byte) 118);
					} else {
						for (var3 = 0; this.m_i.length > var3; ++var3) {
							if (null == this.m_i[var3]) {
								this.m_i[var3] = new int[16384];
								this.resourceDatabase[var1] = this.m_i[var3];
								this.setFrustum((int) var1, (byte) 118);
								return;
							}
						}

						var8 = 1073741824L;
						var5 = 0;

						for (var6 = 0; var6 < this.m_cb; ++var6) {
							if (var1 != var6 && this.m_Hb[var6] == 0 && null != this.resourceDatabase[var6]
									&& ~var8 < ~this.m_D[var6]) {
								var8 = this.m_D[var6];
								var5 = var6;
							}
						}

						this.resourceDatabase[var1] = this.resourceDatabase[var5];
						this.resourceDatabase[var5] = null;
						this.setFrustum((int) var1, (byte) 118);
					}

				}
			}
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7, "lb.T(" + var1 + ',' + var2 + ')');
		}
	}

	private void b(int var1, int var2) {
		try {

			Polygon var4 = this.polygons[var2];
			RSModel var5 = var4.model;
			int var6 = var4.faceID;
			int[] var7 = var5.faceIndices[var6];
			byte var9 = 0;
			byte var10 = 0;
			byte var11 = 1;
			int var12 = var5.vertXRot[var7[0]];
			int var13 = var5.vertYRot[var7[0]];
			int var14 = var5.vertZRot[var7[0]];
			var5.scenePolyNormalMagnitude[var6] = 1;
			var5.scenePolyNormalShift[var6] = 0;
			var4.normalY = var10;
			var4.normalX = var9;
			var4.normalZ = var11;
			var4.orientation = var14 * var11 + var12 * var9 + var13 * var10;
			int var15 = var5.vertZRot[var7[0]];
			int var16 = var15;
			int var17 = var5.vertexParam6[var7[0]];
			int var18 = var17;
			if (var5.vertexParam6[var7[1]] >= var17) {
				var18 = var5.vertexParam6[var7[1]];
			} else {
				var17 = var5.vertexParam6[var7[1]];
			}

			int var19 = var5.vertexParam2[var7[1]];
			int var20 = var5.vertexParam2[var7[0]];
			int var8 = var5.vertZRot[var7[1]];
			if (var8 <= var15) {
				if (var15 > var8) {
					var15 = var8;
				}
			} else {
				var16 = var8;
			}

			var8 = var5.vertexParam6[var7[1]];
			if (var18 >= var8) {
				if (var17 > var8) {
					var17 = var8;
				}
			} else {
				var18 = var8;
			}

			var8 = var5.vertexParam2[var7[1]];
			var4.maxP6 = var18 + 20;
			var4.minP6 = var17 - 20;
			if (var20 < var8) {
				var20 = var8;
			} else if (var8 < var19) {
				var19 = var8;
			}

			var4.maxZ = var16;
			var4.minZ = var15;
			var4.maxP2 = var20;
			var4.minP2 = var19;
		} catch (RuntimeException var21) {
			throw GenUtil.makeThrowable(var21, "lb.IA(" + var1 + ',' + var2 + ')');
		}
	}

	public final void d(int var1, int var2) {
		try {

			if (null != this.resourceDatabase[var2]) {
				int[] var3 = this.resourceDatabase[var2];

				int var5;
				int var6;
				for (int var4 = 0; var4 < 64; ++var4) {
					var5 = 4032 + var4;
					var6 = var3[var5];

					for (int var7 = 0; var7 < 63; ++var7) {
						var3[var5] = var3[var5 - 64];
						var5 -= 64;
					}

					this.resourceDatabase[var2][var5] = var6;
				}

				if (var1 != 25013) {
					this.cameraProjZ = 60;
				}

				short var9 = 4096;

				for (var5 = 0; var5 < var9; ++var5) {
					var6 = var3[var5];
					var3[var9 + var5] = FastMath.bitwiseAnd(var6 - (var6 >>> 3), 16316671);
					var3[var5 + var9 * 2] = FastMath.bitwiseAnd(16316671, var6 - (var6 >>> 2));
					var3[var5 + var9 * 3] = FastMath.bitwiseAnd(16316671, var6 - (var6 >>> 3) - (var6 >>> 2));
				}

			}
		} catch (RuntimeException var8) {
			throw GenUtil.makeThrowable(var8, "lb.OA(" + var1 + ',' + var2 + ')');
		}
	}

	public final int drawSprite(int var1, int var2, int var3, int var4, int var5, int var6, int var7, byte var8) {
		try {

			this.m_gb[this.m_n] = var1;
			this.m_Fb[this.m_n] = var4;
			this.m_a[this.m_n] = var5;
			this.m_Ob[this.m_n] = var2;
			this.m_ob[this.m_n] = var6;
			this.m_Eb[this.m_n] = var7;
			this.m_Q[this.m_n] = 0;
			int var9 = this.m_T.insertVertex2(false, var2, var4, var5);
			int var10 = this.m_T.insertVertex2(false, var2, var4, var5 - var7);
			int[] var11 = new int[]{var9, var10};
			this.m_T.insertFace(2, var11, 0, 0, false);
			this.m_T.facePickIndex[this.m_n] = var3;
			this.m_T.m_zb[this.m_n++] = 0;
			return this.m_n - 1;
		} catch (RuntimeException var12) {
			throw GenUtil.makeThrowable(var12, "lb.HA(" + var1 + ',' + var2 + ',' + var3 + ',' + var4 + ',' + var5 + ','
					+ var6 + ',' + var7 + ',' + 109 + ')');
		}
	}

	public final void endScene(int var1) {
		try {

			this.m_f = this.graphics.interlace;
			int var7 = this.m_A * this.fogLandscapeDistance >> this.rot1024_vp_src;
			MiscFunctions.frustumFarZ = 0;
			MiscFunctions.frustumNearZ = 0;
			MiscFunctions.frustumMaxX = 0;
			MiscFunctions.frustumMinX = 0;
			int var8 = this.fogLandscapeDistance * this.m_wb >> this.rot1024_vp_src;
			MiscFunctions.frustumMinY = 0;
			MiscFunctions.frustumMaxY = 0;
			this.setFrustum(this.fogLandscapeDistance, -var7, -var8, true);
			this.setFrustum(this.fogLandscapeDistance, -var7, var8, true);
			this.setFrustum(this.fogLandscapeDistance, var7, -var8, true);
			this.setFrustum(this.fogLandscapeDistance, var7, var8, true);
			this.setFrustum(0, -this.m_A, -this.m_wb, true);
			this.setFrustum(0, -this.m_A, this.m_wb, true);
			this.setFrustum(0, this.m_A, -this.m_wb, true);
			this.setFrustum(0, this.m_A, this.m_wb, true);
			MiscFunctions.frustumNearZ += this.rot1024_off_y;
			MiscFunctions.frustumMinX += this.rot1024_off_z;
			MiscFunctions.frustumFarZ += this.rot1024_off_y;
			MiscFunctions.frustumMaxY += this.rot1024_off_x;
			MiscFunctions.frustumMaxX += this.rot1024_off_z;
			MiscFunctions.frustumMinY += this.rot1024_off_x;
			this.models[this.modelCount] = this.m_T;
			this.m_T.m_Yb = 2;

			int var3;
			for (var3 = 0; this.modelCount > var3; ++var3) {
				this.models[var3].rotate1024(this.rot1024_off_y, this.rot1024_vp_src, this.rot1024_off_x, (byte) -122,
						this.rot1024_off_z, this.cameraProjY, this.cameraProjZ, this.cameraProjX, this.rot1024_zTop);
			}

			this.models[this.modelCount].rotate1024(this.rot1024_off_y, this.rot1024_vp_src, this.rot1024_off_x,
					(byte) -114, this.rot1024_off_z, this.cameraProjY, this.cameraProjZ, this.cameraProjX, this.rot1024_zTop);
			this.m_zb = 0;

			RSModel var2;
			int var6;
			int var9;
			int var10;
			int[] var11;
			int var12;
			int var13;
			int var14;
			for (var9 = 0; var9 < this.modelCount; ++var9) {
				var2 = this.models[var9];
				if (var2.m_dc) {
					for (var3 = 0; var3 < var2.faceHead; ++var3) {
						var10 = var2.faceIndexCount[var3];
						var11 = var2.faceIndices[var3];
						boolean var5 = false;

						int var4;
						for (var12 = 0; var10 > var12; ++var12) {
							var4 = var2.vertZRot[var11[var12]];
							if (this.rot1024_zTop < var4 && this.fogLandscapeDistance > var4) {
								var5 = true;
								break;
							}
						}

						if (var5) {
							int var23 = 0;

							for (var12 = 0; var12 < var10; ++var12) {
								var4 = var2.vertexParam6[var11[var12]];
								if (-this.m_A < var4) {
									var23 |= 1;
								}

								if (this.m_A > var4) {
									var23 |= 2;
								}

								if (var23 == 3) {
									break;
								}
							}

							if (var23 == 3) {
								var23 = 0;

								for (var12 = 0; var10 > var12; ++var12) {
									var4 = var2.vertexParam2[var11[var12]];
									if (-this.m_wb < var4) {
										var23 |= 1;
									}

									if (this.m_wb > var4) {
										var23 |= 2;
									}

									if (var23 == 3) {
										break;
									}
								}

								if (var23 == 3) {
									Polygon var27 = this.polygons[this.m_zb];
									var27.model = var2;
									var27.faceID = var3;
									this.computePolygon((int) this.m_zb);
									if (var27.orientation < 0) {
										var13 = var2.faceTextureFront[var3];
									} else {
										var13 = var2.faceTextureBack[var3];
									}

									if (var13 != Scene.TRANSPARENT) {
										var6 = 0;

										for (var14 = 0; var10 > var14; ++var14) {
											var6 += var2.vertZRot[var11[var14]];
										}

										var27.m_t = var2.m_hc + var6 / var10;
										++this.m_zb;
										var27.m_b = var13;
									}
								}
							}
						}
					}
				}
			}

			if (var1 > -99) {
				this.m_H = (int[]) null;
			}

			var2 = this.m_T;
			int var15;
			int var26;
			if (var2.m_dc) {
				for (var3 = 0; var2.faceHead > var3; ++var3) {
					int[] var24 = var2.faceIndices[var3];
					var10 = var24[0];
					var26 = var2.vertexParam6[var10];
					var12 = var2.vertexParam2[var10];
					var13 = var2.vertZRot[var10];
					if (this.rot1024_zTop < var13 && var13 < this.fogEntityDistance) {
						var14 = (this.m_ob[var3] << this.rot1024_vp_src) / var13;
						var15 = (this.m_Eb[var3] << this.rot1024_vp_src) / var13;
						if (this.m_A >= var26 - var14 / 2 && -this.m_A <= var26 + var14 / 2
								&& var12 - var15 <= this.m_wb && var12 >= -this.m_wb) {
							Polygon var16 = this.polygons[this.m_zb];
							var16.faceID = var3;
							var16.model = var2;
							this.b(-103, this.m_zb);
							var16.m_t = (var2.vertZRot[var24[1]] + var13) / 2;
							++this.m_zb;
						}
					}
				}
			}

			if (this.m_zb != 0) {
				this.setFrustum(0, -1, this.polygons, this.m_zb - 1);
				this.setFrustum(this.m_zb, 100, -53, this.polygons);

				for (var9 = 0; this.m_zb > var9; ++var9) {
					Polygon var25 = this.polygons[var9];
					var3 = var25.faceID;
					var2 = var25.model;
					int var17;
					int var19;
					int var28;
					if (var2 == this.m_T) {
						var11 = var2.faceIndices[var3];
						var12 = var11[0];
						var13 = var2.vertexParam6[var12];
						var14 = var2.vertexParam2[var12];
						var15 = var2.vertZRot[var12];
						var28 = (this.m_ob[var3] << this.rot1024_vp_src) / var15;
						var17 = (this.m_Eb[var3] << this.rot1024_vp_src) / var15;
						int var29 = var14 - var2.vertexParam2[var11[1]];
						var19 = var29 * (var2.vertexParam6[var11[1]] - var13) / var17;
						var19 = var2.vertexParam6[var11[1]] - var13;
						int var20 = var13 - var28 / 2;
						int var21 = this.m_Nb - (var17 - var14);
						this.graphics.drawEntity(this.m_gb[var3], var20 + this.m_Zb, var21, var28, var17,
								(256 << this.rot1024_vp_src) / var15, var19);
						if (this.m_K && this.m_db > this.m_cc) {
							var20 += (this.m_Q[var3] << this.rot1024_vp_src) / var15;
							if (var21 <= this.m_Wb && var21 + var17 >= this.m_Wb && var20 <= this.m_j
									&& this.m_j <= var20 + var28 && !var2.m_db && var2.m_zb[var3] == 0) {
								this.m_Ab[this.m_cc] = var2;
								this.m_qb[this.m_cc] = var3;
								++this.m_cc;
							}
						}
					} else {
						var14 = 0;
						var28 = 0;
						var17 = var2.faceIndexCount[var3];
						if (var2.faceDiffuseLight[var3] != Scene.TRANSPARENT) {
							if (var25.orientation < 0) {
								var28 = var2.diffuseParam1 - var2.faceDiffuseLight[var3];
							} else {
								var28 = var2.diffuseParam1 + var2.faceDiffuseLight[var3];
							}
						}

						int[] var18 = var2.faceIndices[var3];

						for (var19 = 0; var17 > var19; ++var19) {
							var6 = var18[var19];
							this.m_Qb[var19] = var2.vertXRot[var6];
							this.m_Vb[var19] = var2.vertYRot[var6];
							this.m_J[var19] = var2.vertZRot[var6];
							if (var2.faceDiffuseLight[var3] == Scene.TRANSPARENT) {
								if (var25.orientation < 0) {
									var28 = var2.diffuseParam1 + var2.vertLightOther[var6]
											- var2.vertDiffuseLight[var6];
								} else {
									var28 = var2.vertLightOther[var6] + var2.diffuseParam1
											+ var2.vertDiffuseLight[var6];
								}
							}

							if (var2.vertZRot[var6] >= this.rot1024_zTop) {
								this.m_yb[var14] = var2.vertexParam6[var6];
								this.m_B[var14] = var2.vertexParam2[var6];
								this.m_r[var14] = var28;
								if (var2.vertZRot[var6] > this.fogSmoothingStartDistance) {
									this.m_r[var14] += (var2.vertZRot[var6] - this.fogSmoothingStartDistance) / this.fogZFalloff;
								}

								++var14;
							} else {
								if (var19 != 0) {
									var15 = var18[var19 - 1];
								} else {
									var15 = var18[var17 - 1];
								}

								if (var2.vertZRot[var15] >= this.rot1024_zTop) {
									var13 = var2.vertZRot[var6] - var2.vertZRot[var15];
									var12 = var2.vertYRot[var6] - (var2.vertZRot[var6] - this.rot1024_zTop)
											* (var2.vertYRot[var6] - var2.vertYRot[var15]) / var13;
									var26 = var2.vertXRot[var6] - (var2.vertXRot[var6] - var2.vertXRot[var15])
											* (var2.vertZRot[var6] - this.rot1024_zTop) / var13;
									this.m_yb[var14] = (var26 << this.rot1024_vp_src) / this.rot1024_zTop;
									this.m_B[var14] = (var12 << this.rot1024_vp_src) / this.rot1024_zTop;
									this.m_r[var14] = var28;
									++var14;
								}

								if (var17 - 1 == var19) {
									var15 = var18[0];
								} else {
									var15 = var18[var19 + 1];
								}

								if (var2.vertZRot[var15] >= this.rot1024_zTop) {
									var13 = var2.vertZRot[var6] - var2.vertZRot[var15];
									var12 = var2.vertYRot[var6] - (var2.vertZRot[var6] - this.rot1024_zTop)
											* (var2.vertYRot[var6] - var2.vertYRot[var15]) / var13;
									var26 = var2.vertXRot[var6] - (var2.vertXRot[var6] - var2.vertXRot[var15])
											* (var2.vertZRot[var6] - this.rot1024_zTop) / var13;
									this.m_yb[var14] = (var26 << this.rot1024_vp_src) / this.rot1024_zTop;
									this.m_B[var14] = (var12 << this.rot1024_vp_src) / this.rot1024_zTop;
									this.m_r[var14] = var28;
									++var14;
								}
							}
						}

						for (var19 = 0; var19 < var17; ++var19) {
							if (0 <= this.m_r[var19]) {
								if (this.m_r[var19] > 255) {
									this.m_r[var19] = 255;
								}
							} else {
								this.m_r[var19] = 0;
							}

							if (var25.m_b >= 0) {
								if (this.m_Hb[var25.m_b] != 1) {
									this.m_r[var19] <<= 6;
								} else {
									this.m_r[var19] <<= 9;
								}
							}
						}

						this.setFrustum(0, var3, this.m_B, 0, 0, var2, this.m_yb, this.m_r, 0, 5960, var14);
						if (this.m_Xb < this.m_Cb) {
							this.setFrustum(this.m_Vb, var2, 1, var17, var25.m_b, this.m_J, this.m_Qb, 0, 0);
						}
					}
				}

				this.m_K = false;
			}
		} catch (RuntimeException var22) {
			throw GenUtil.makeThrowable(var22, "lb.P(" + var1 + ')');
		}
	}

	public final void loadTexture(int var1, int[] var3, int var4, byte[] var5) {
		try {

			this.m_g[var1] = var5;

			this.m_L[var1] = var3;
			this.m_Hb[var1] = var4;
			this.m_D[var1] = 0L;
			this.m_S[var1] = false;
			this.resourceDatabase[var1] = null;
			this.b(var1, true);
		} catch (RuntimeException var7) {
			throw GenUtil.makeThrowable(var7, "lb.E(" + var1 + ',' + (var3 != null ? "{...}" : "null")
					+ ',' + var4 + ',' + (var5 != null ? "{...}" : "null") + ')');
		}
	}

	public final void reduceSprites(byte var1, int var2) {
		try {
			if (var1 != 67) {
				this.m_cb = 31;
			}

			this.m_n -= var2;

			this.m_T.removeFacesAndOrVerts(var2 * 2, -113, var2);
			if (this.m_n < 0) {
				this.m_n = 0;
			}

		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "lb.A(" + var1 + ',' + var2 + ')');
		}
	}

	final void removeAllGameObjects(boolean var1) {
		try {

			this.resetMTVertHead();
			if (var1) {
				this.m_Xb = -11;
			}

			for (int var2 = 0; this.modelCount > var2; ++var2) {
				this.models[var2] = null;
			}

			this.modelCount = 0;
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "lb.GA(" + var1 + ')');
		}
	}

	public final void removeModel(RSModel var1) {
		try {
			for (int i = 0; i < this.modelCount; ++i) {
				if (this.models[i] == var1) {
					--this.modelCount;

					for (int j = i; j < this.modelCount; ++j) {
						this.models[j] = this.models[j + 1];
						this.m_jb[j] = this.m_jb[1 + j];
					}
				}
			}


		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "lb.W(" + (var1 != null ? "{...}" : "null") + ',' + "dummy" + ')');
		}
	}

	public final void setCamera(int centerX, int centerY, int centerZ, int xRot, int yRot, int zRot, int offset) {
		try {
			zRot &= 1023;
			xRot &= 1023;
			yRot &= 1023;

			this.cameraProjZ = 1024 - zRot & 1023;
			this.cameraProjX = 1024 - xRot & 1023;
			this.cameraProjY = 1024 - yRot & 1023;
			int offX = 0;
			int offY = 0;
			int offZ = offset;
			int sin;
			int cos;
			int tmp;
			if (xRot != 0) {
				sin = FastMath.trigTable_1024[xRot];
				cos = FastMath.trigTable_1024[xRot + 1024];
				tmp = cos * offY - sin * offset >> 15;
				offZ = sin * offY + offset * cos >> 15;
				offY = tmp;
			}

			if (yRot != 0) {
				sin = FastMath.trigTable_1024[yRot];
				cos = FastMath.trigTable_1024[yRot + 1024];
				tmp = offX * cos + offZ * sin >> 15;
				offZ = cos * offZ - sin * offX >> 15;
				offX = tmp;
			}

			if (zRot != 0) {
				cos = FastMath.trigTable_1024[zRot + 1024];
				sin = FastMath.trigTable_1024[zRot];
				tmp = offX * cos + sin * offY >> 15;
				offY = offY * cos - sin * offX >> 15;
				offX = tmp;
			}

			this.rot1024_off_z = centerZ - offZ;
			this.rot1024_off_y = centerY - offY;
			this.rot1024_off_x = centerX - offX;
		} catch (RuntimeException var15) {
			throw GenUtil.makeThrowable(var15, "lb.EA(" + centerX + ',' + centerZ + ',' + offset + ',' + xRot + ','
					+ "dummy" + ',' + yRot + ',' + centerY + ',' + zRot + ')');
		}
	}

	public final void setCombatXOffset(int var1, int var2, int var3) {
		try {
			if (var1 <= 15) {
				this.resourceDatabase = (int[][]) ((int[][]) null);
			}

			this.m_Q[var2] = var3;

		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "lb.V(" + var1 + ',' + var2 + ',' + var3 + ')');
		}
	}

	public final void setDiffuseDir(int dirZ, int dirY, boolean var3, int dirX) {
		try {

			if (dirX == 0 && dirY == 0 && dirZ == 0) {
				dirX = 32;
			}

			if (!var3) {
				this.endScene(-89);
			}

			for (int var5 = 0; var5 < this.modelCount; ++var5) {
				this.models[var5].setDiffuseDir(false, dirX, dirY, dirZ);
			}

		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "lb.L(" + dirZ + ',' + dirY + ',' + var3 + ',' + dirX + ')');
		}
	}

	public final void setFaceSpriteLocalPlayer(int var1, int var2) {
		try {

			this.m_T.m_zb[var2] = 1;
			if (var1 != '\u8000') {
				this.m_Cb = 32;
			}

		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "lb.S(" + var1 + ',' + var2 + ')');
		}
	}

	public int getX() {
		return m_j + m_Zb;
	}

	public int getY() {
		return m_Wb;
	}

	public final void setMidpoints(int var1, boolean var2, int var3, int var4, int var5, int var6, int var7) {
		try {
			this.rot1024_vp_src = var6;
			this.m_Zb = var7;
			this.m_vb = var3;
			this.m_Nb = var5;
			this.m_x = new Scanline[var1 + var5];
			this.m_wb = var1;

			this.m_A = var4;

			for (int var8 = 0; var8 < var5 + var1; ++var8) {
				this.m_x[var8] = new Scanline();
			}

			if (!var2) {
				this.m_f = false;
			}
			pixelData = graphics.pixelData;
		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9,
					"lb.K(" + var1 + ',' + var2 + ',' + var3 + ',' + var4 + ',' + var5 + ',' + var6 + ',' + var7 + ')');
		}
	}

	public final void setMouseLoc(int var1, int x, int y) {
		try {
			this.m_K = true;
			this.m_j = x - this.m_Zb;
			this.m_Wb = y;
			this.m_cc = var1;

		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "lb.J(" + var1 + ',' + x + ',' + y + ')');
		}
	}
}