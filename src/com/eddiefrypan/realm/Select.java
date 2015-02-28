package com.eddiefrypan.realm;

public class Select {
    public static CoordArray createSelection(Coord first,
                                             Coord last) {
        int xDelta = Math.abs(last.getX() - first.getX()) + 1;
        int yDelta = Math.abs(last.getFirstZ() - first.getFirstZ()) + 1;
        if (xDelta <= 100 && yDelta <= 100) {
            Coord[] coords = new Coord[xDelta];
            for (int i = 0; i < coords.length; i++) {
                coords[i] = new Coord(last.getX()
                        + (last.getX() < first.getX() ? i
                        : last.getX() > first.getX() ? i * -1 : 0),
                        (last.getFirstZ() <= first.getFirstZ() ? last
                                .getFirstZ() : first.getFirstZ()),
                        (last.getLastZ() >= first.getLastZ() ? last.getLastZ()
                                : first.getLastZ()));
            }
            return new CoordArray(coords);
        }
        return null;
    }
}
