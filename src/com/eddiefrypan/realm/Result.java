package com.eddiefrypan.realm;

public class Result {
    public final boolean success;
    public final int[] specs;

    public Result(boolean success, int...specs) {
        this.success = success;
        this.specs = specs;
    }
}
