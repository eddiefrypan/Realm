package com.eddiefrypan.realm;

public class Coord {
	private final int x;
	private int firstZ, lastZ;

	public Coord(int x, int firstZ, int lastZ) {
		this.x = x;
		this.firstZ = firstZ;
		this.lastZ = lastZ;
	}

	public Coord(int x, int[] z) {
		this.x = x;
		this.firstZ = z[0];
		this.lastZ = z[z.length - 1];
	}

	public boolean containsCoord(Coord coord) {
		if (coord.x != x)
			return false;
		if (coord.firstZ >= firstZ && coord.lastZ <= lastZ)
			return true;
		return false;
	}

	public boolean touchesCoord(Coord coord) {
		if (coord.x != x)
			return false;
		else if ((coord.firstZ <= lastZ && coord.firstZ >= firstZ)
				|| (coord.lastZ <= lastZ && coord.lastZ >= firstZ)) {
			return true;
		}
		return false;
	}

	public boolean nextToCoord(Coord coord) {
		if (coord.x != x)
			return false;
		else if ((coord.firstZ <= lastZ + 1 && coord.firstZ >= firstZ - 1)
				|| (coord.lastZ <= lastZ + 1 && coord.lastZ >= firstZ - 1)) {
			return true;
		}
		return false;
	}

	public boolean addCoord(Coord coord) {
		if (coord.x != x) {
			return false;
		} else if (nextToCoord(coord)) {
			int newFirstZ = firstZ;
			int newLastZ = lastZ;

			if (coord.firstZ < newFirstZ)
				newFirstZ = coord.firstZ;
			if (coord.lastZ > newLastZ)
				newLastZ = coord.lastZ;
			firstZ = newFirstZ;
			lastZ = newLastZ;
			return true;
		}
		return false;
	}

	public Coord[] removeCoord(Coord coord) {
		Coord[] array = new Coord[1];
		if (coord.x != x) {
			array[0] = this;
			return array;
		}

		int firstZDiff = firstZ - coord.firstZ;
		int lastZDiff = lastZ - coord.lastZ;
		int coordSize = coord.lastZ - coord.firstZ + 1;

		if (firstZDiff >= 0 && coordSize > firstZDiff + 1) {
			array[0] = null;
		} else if (firstZDiff < 0) {
			array[0] = new Coord(x, firstZ, coord.firstZ - 1);
		}

		if (lastZDiff <= 0 && coordSize > lastZDiff) {
			if (array[0] != null) {
				Coord zero = array[0];
				array = new Coord[2];
				array[0] = zero;
				array[1] = null;
			} else {
				array[0] = null;
			}
		} else if (lastZDiff > 0) {
			if (array[0] != null) {
				Coord zero = array[0];
				array = new Coord[2];
				array[0] = zero;
				array[1] = new Coord(x, coord.lastZ + 1, lastZ);
			} else {
				array[0] = new Coord(x, coord.lastZ + 1, lastZ);
			}
		}

		return array;
	}

	public int getX() {
		return x;
	}

	public int getFirstZ() {
		return firstZ;
	}

	public int getLastZ() {
		return lastZ;
	}

	public String getString() {
		return x + "#" + firstZ + "#" + lastZ;
	}

	public int[] getZs() {
		int[] coords = new int[lastZ - firstZ + 1];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = firstZ + i;
		}
		return coords;
	}
}
