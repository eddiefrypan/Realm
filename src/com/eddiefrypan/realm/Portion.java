package com.eddiefrypan.realm;

import java.util.ArrayList;

public class Portion extends CoordArray {
    public final int ID;
    private String name;
    public ArrayList<String> trusts;

    public Portion(int ID, String name) {
        this.ID = ID;
        this.name = name;
        trusts = new ArrayList<String>();
    }

    public String name() {
        return name;
    }
}
