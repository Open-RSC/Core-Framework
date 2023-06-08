package com.openrsc.server.model.world.region;

import com.openrsc.server.util.rsc.CollisionFlag;

public class TileValue {
	public byte traversalMask = CollisionFlag.FULL_BLOCK;
	public short diagWallVal = 0;
	public byte horizontalWallVal = 0;
	public byte overlay = 0;
	public byte verticalWallVal = 0;
	public byte elevation = 0;
	public boolean projectileAllowed = false;
	public boolean originalProjectileAllowed = false;

	@Override
	public String toString() {
		return "TileValue{" +
			"traversalMask=" + traversalMask +
			", diagWallVal=" + diagWallVal +
			", horizontalWallVal=" + horizontalWallVal +
			", overlay=" + overlay +
			", verticalWallVal=" + verticalWallVal +
			", elevation=" + elevation +
			", projectileAllowed=" + projectileAllowed +
			", originalProjectileAllowed=" + originalProjectileAllowed +
			'}';
	}

	public boolean equals(final TileValue other) {
		return 	this.traversalMask == other.traversalMask &&
				this.diagWallVal == other.diagWallVal &&
				this.horizontalWallVal == other.horizontalWallVal &&
				this.overlay == other.overlay &&
				this.verticalWallVal == other.verticalWallVal &&
				this.elevation == other.elevation &&
				this.projectileAllowed == other.projectileAllowed &&
				this.originalProjectileAllowed == other.originalProjectileAllowed;
	}
}
