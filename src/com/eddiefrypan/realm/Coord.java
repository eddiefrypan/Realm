package com.eddiefrypan.realm;

public class Coord {
    private final int x;
    private int firstZ, lastZ;

    public Coord(int x, int firstZ, int lastZ) {
        this.x = x;
        this.firstZ = firstZ;
        this.lastZ = lastZ;
    }

    public boolean containsCoord(Coord coord) {
        return coord.x == x && coord.firstZ >= firstZ && coord.lastZ <= lastZ;
    }

    public boolean touchesCoord(Coord coord) {
        return coord.x == x && ((coord.firstZ <= lastZ && coord.firstZ >= firstZ) || (coord.lastZ <= lastZ && coord.lastZ >= firstZ));
    }

    public boolean nextToCoord(Coord coord) {
        return coord.x == x && ((coord.firstZ <= lastZ + 1 && coord.firstZ >= firstZ - 1)
                || (coord.lastZ <= lastZ + 1 && coord.lastZ >= firstZ - 1));
    }

    public boolean addCoord(Coord coord) {
        if (coord.x != x)
            return false;
        else if (nextToCoord(coord)) {
            if (coord.firstZ < firstZ) firstZ = coord.firstZ;
            if (coord.lastZ > lastZ) lastZ = coord.lastZ;
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


        //TODO: fix this
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

    public Coord overLap(Coord coord) {
        if (touchesCoord(coord))
            return new Coord(x, firstZ > coord.firstZ ? firstZ : coord.firstZ, lastZ < coord.lastZ ? lastZ : coord.lastZ);
        return null;
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

    public int getSize() {
        return lastZ - firstZ + 1;
    }

    public String getString() {
        return x + "#" + firstZ + "#" + lastZ;
    }
}
