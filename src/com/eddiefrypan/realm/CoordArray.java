package com.eddiefrypan.realm;

import java.util.ArrayList;
import java.util.Arrays;

public class CoordArray {
    private Coord[] coords;

    public CoordArray(Coord... coords) {
        this.coords = coords;
    }

    public int size() {
        int size = 0;
        for (Coord coord : coords) size += coord.getLastZ() - coord.getFirstZ() + 1;
        return size;
    }

    public boolean containsCoord(Coord coord) {
        for (Coord own : coords)
            if (own.containsCoord(coord))
                return true;
        return false;
    }

    public boolean touchesCoord(Coord coord) {
        for (Coord own : coords) if (own.touchesCoord(coord)) return true;
        return false;
    }

    public boolean touchesCoords(CoordArray coordArray) {
        for (Coord coord : coordArray.coords)
            for (Coord own : coords)
                if (coord.touchesCoord(own)) return true;
        return false;
    }

    public void addCoord(Coord coord) {
        boolean added = false;
        for (int i = 0; i < coords.length; i++)
            if(coords[i].addCoord(coord)) {
                added = true;
                // TODO: Fix all for loops so they actually edit the arrays
            }
        if (added) return;

        Coord[] revised = new Coord[coords.length + 1];
        for (int i = 0; i < coords.length; i++) revised[i] = coords[i];
        revised[revised.length - 1] = coord;
        coords = revised;
    }

    public void addCoords(Coord... coords) {
        for (Coord coord : coords) addCoord(coord);
    }

    public void addCoords(CoordArray coordArray) {
        addCoords(coordArray.coords);
    }

    public void removeCoord(Coord coord) {
        ArrayList<Coord> revised = new ArrayList<Coord>(Arrays.asList(coords));
        //Coord[] revised = coords;
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

    public void removeCoords(Coord... coords) {
        for (Coord coord : coords) removeCoord(coord);
    }

    public void removeCoords(CoordArray coordArray) {
        removeCoords(coordArray.coords);
    }

    public void removeEntireCoord(int index) {
        Coord[] revised = coords;
        for (int i = index; i < coords.length - 1; i++)
            revised[i] = revised[i + 1];
        coords = new Coord[revised.length - 1];
        for (int i = 0; i < coords.length; i++)
            coords[i] = revised[i];
    }

    public CoordArray overLap(CoordArray coordArray) {
        for (int i = 0; i < coordArray.coords.length; i++) {
            Coord coord = coordArray.coords[i];
            boolean found = false;
            for (Coord own : coords) {
                if (own.touchesCoord(coord)) {
                    found = true;
                    coord = own.overLap(coord);
                }
            }
            if (!found) coordArray.removeEntireCoord(i);
        }
        return coordArray;
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
