package com.thetoothpick.dudetheresyourcar;

import android.location.Location;

/**
 * Created by frank on 9/14/14.
 */
public interface Finder {
    public Location find();
    public Location cached();
    public Location getCurrentLocation();
    public void cache(Location location);
}
