package com.svamp.spacetrash;

/*
 * Vec3.java
 *
 * Created: 10-Nov-2009
 *
 * Author: Hj. Malthaner
 * Email:  h_malthaner@users.sourceforge.net
 *
 * See license.txt for license details
 */


/**
 * Simple 3D vector type (the math one)
 *
 * @author Hj. Malthaner
 */
public class Vec3
{

    public double x, y, z;

    /**
     * @returns length^2
     * @author Hj. Malthaner
     */
    double length2() {
        return x * x + y * y + z * z;
    }

    public void sub(Vec3 other)
    {
        x -= other.x;
        y -= other.y;
        z -= other.z;
    }

    public void add(Vec3 other)
    {
        x += other.x;
        y += other.y;
        z += other.z;
    }

    public Vec3()
    {
        x = y = z = 0.0;
    }

    public Vec3(Vec3 v)
    {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    public String toString()
    {

        String text = "X: " + x + " Y: " + y + " Z: " + z;
        return text;
    }
}
