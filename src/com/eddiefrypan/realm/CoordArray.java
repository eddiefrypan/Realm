package com.eddiefrypan.realm;

import java.util.ArrayList;
import java.util.Arrays;

public class CoordArray {
	private Coord[] coords;

	public CoordArray() {
		coords = new Coord[0];
	}

	public CoordArray(Coord[] coords) {
		this.coords = coords;
	}

	public CoordArray(Coord coord) {
		coords = new Coord[1];
		coords[0] = coord;
	}

	public Coord[] getCoords() {
		return coords.clone();
	}

	public boolean containsCoord(Coord coord) {
		boolean found = false;
		for (int i = 0; i < coords.length; i++)
			if (coords[i].containsCoord(coord))
				found = true;
		return found;
	}

	public boolean touchesCoords(Coord[] coordArray) {
		boolean touches = false;
		for (int i = 0; i < coordArray.length; i++) {
			for (int x = 0; x < coords.length; x++)
				if (coordArray[i].touchesCoord(coords[x])) {
					touches = true;
					break;
				}
			if (touches)
				break;
		}
		return touches;
	}

	public boolean touchesCoords(CoordArray coordArray) {
		return touchesCoords(coordArray.coords.clone());
	}

	public void addCoord(Coord coord) {
		boolean found = false;
		for (int i = 0; i < coords.length; i++) {
			found = coords[i].addCoord(coord);
			if (found)
				break;
		}

		if (!found) {
			Coord[] copy = new Coord[coords.length + 1];
			for (int i = 0; i < coords.length; i++) {
				copy[i] = coords[i];
			}
			copy[copy.length - 1] = coord;
			coords = copy;
		}
	}

	public void addCoords(Coord[] coords) {
		for (int i = 0; i < coords.length; i++) {
			addCoord(coords[i]);
		}
	}

	public void addCoords(CoordArray coordArray) {
		addCoords(coordArray.coords.clone());
	}

	public void removeCoord(Coord coord) {
		ArrayList<Coord> revised = new ArrayList<Coord>(Arrays.asList(coords));
		for (int i = 0; i < revised.size(); i++) {
			Coord[] result = revised.get(i).removeCoord(coord);
			if (result.length == 1) {
				if (result[0] == null) {
					revised.remove(i);
				} else {
					revised.set(i, result[0]);
				}
			} else {
				revised.set(i, result[0]);
				if (result[1] != null)
					revised.add(result[1]);
			}
		}

		coords = (Coord[]) revised.toArray();
	}

	public void removeCoords(Coord[] coords) {
		for (int i = 0; i < coords.length; i++) {
			removeCoord(coords[i]);
		}
	}

	public void removeCoords(CoordArray coordArray) {
		removeCoords(coordArray.coords.clone());
	}

	public Coord getCoord(int index) {
		return coords[index];
	}

	public String getCoordString() {
		String string = "";
		for (int i = 0; i < coords.length; i++) {
			string += coords[i].getString();
			string += ";";
		}
		return string;
	}
}
