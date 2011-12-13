//  
//  Vector.java
//  MyCraft
//  
//  Created on 08/12/2011.
//  Copyright (c) 2011 Mitchell Kember. All rights reserved.
//
//  This software is provided 'as-is', without any express or implied
//  warranty. In no event will the authors be held liable for any damages
//  arising from the use of this software.
//  
//  Permission is granted to anyone to use this software for any purpose,
//  including commercial applications, and to alter it and redistribute it
//  freely, subject to the following restrictions:
//  
//  1. The origin of this software must not be misrepresented; you must not
//  claim that you wrote the original software. If you use this software
//  in a product, an acknowledgment in the product documentation would be
//  appreciated but is not required.
//  
//  2. Altered source versions must be plainly marked as such, and must not be
//  misrepresented as being the original software.
//  
//  3. This notice may not be removed or altered from any source
//  distribution.
//  

package com.hecticcraft.mycraft;

/**
 * Vector represents a simple three-dimensional vector. In particular, it provides
 * counterparts to most normal methods, returning a new vector rather than modifying
 * an existing one (e.g. add/plus, sub/minus, scale/scaled). It also provides a
 * convenient method for axis rotations.
 *
 * @author Mitchell Kember
 * @since 08/12/2011
 */
final class Vector {
    
    /**
     * The X component of this Vector.
     */
    float x;
    
    /**
     * The Y component of this Vector.
     */
    float y;
    
    /**
     * The Z component of this Vector.
     */
    float z;
    
    /**
     * Creates a new Vector with the specified components.
     * 
     * @param x the X component
     * @param y the Y component
     * @param z the Z component
     */
    Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * Creates a new null (zero) Vector.
     */
    Vector() {
        this(0, 0, 0);
    }
    
    /**
     * Calculates the magnitude (length) of this Vector.
     * 
     * @return the length of this Vector
     */
    float magnitude() {
        return (float)Math.sqrt((x*x) + (y*y) + z*z);
    }
    
    /**
     * Calculates the magnitude (length) squared of this Vector. This is less
     * expensive to call than {@code magnitude}, because it skips the square root
     * operation. Use this when, for example, sorting by Vector length and the
     * actual magnitude is not required.
     * 
     * @return the magnitude squared
     */
    float magnitudeSquared() {
        return (x*x) + (y*y) + (z*z);
    }
    
    /**
     * Adds {@code vec} to this Vector by adding each component
     * separately.
     * 
     * @param vec the addend
     */
    void add(Vector vec) {
        x += vec.x;
        y += vec.y;
        z += vec.z;
    }
    
    /**
     * Subtracts {@code vec} from this Vector by subtracting each
     * component separately.
     * 
     * @param vec the subtrahend
     */
    void sub(Vector vec) {
        x -= vec.x;
        y -= vec.y;
        z -= vec.z;
    }
    
    /**
     * Scales this vector by the scalar value {@code s} by multiplying
     * each component separately.
     * 
     * @param s the multiplier
     */
    void scale(float s) {
        x *= s;
        y *= s;
        z *= s;
    }
    
    /**
     * Calculates the dot product of this Vector with {@code vec}.
     * 
     * @param vec another Vector
     * @return the dot product
     */
    float dot(Vector vec) {
        return x*vec.x + y*vec.y + z*vec.z;
    }
    
    /**
     * Calculates the cross product of {@code u} and {@code v} and returns
     * the result in a new Vector.
     * 
     * @param u a Vector
     * @param v another Vector
     * @return the cross product
     */
    static Vector cross(Vector u, Vector v) {
        return new Vector(u.y*v.z - u.z*v.y,
                          u.z*v.x - u.x*v.z,
                          u.x*v.y - u.y*v.x);
    }
    
    /**
     * Normalizes this Vector (i.e., makes its magnitude equal to 1).
     */
    void normalize() {
        float mag = magnitude();
        if (mag == 0) return;
        x /= mag;
        y /= mag;
        z /= mag;
    }
    
    /**
     * Calculates the sum of this Vector and {@code vec}. This does not
     * modify this Vector.
     * 
     * @param vec the addend
     * @return the sum
     */
    Vector plus(Vector vec) {
        return new Vector(x+vec.x, y+vec.y, z+vec.z);
    }
    
    /**
     * Calculates the difference of this Vector and {@code vec}. This does not
     * modify this Vector.
     * 
     * @param vec the subtrahend
     * @return the difference
     */
    Vector minus(Vector vec) {
        return new Vector(x-vec.x, y-vec.y, z-vec.z);
    }
    
    /**
     * Calculates this vector scaled by the scalar value {@code s}. This
     * does not modify this Vector.
     * 
     * @param s the multiplier
     * @return the scaled Vector
     */
    Vector scaled(float s) {
        return new Vector(x*s, y*s, z*s);
    }
    
    /**
     * Calculates the normalized version of this Vector. This does not
     * modify this Vector.
     * 
     * @return the unit Vector
     */
    Vector normalized() {
        float mag = magnitude();
        if (mag == 0) return new Vector();
        return new Vector(x/mag, y/mag, z/mag); 
    }
    
    /**
     * Rotates {@code vec} about an arbitrary axis {@code axis} by {@code angle}
     * radians. This does not modify {@code vec}.
     * 
     * @param vec the Vector to rotate
     * @param axis the arbitrary axis to rotate about
     * @param angle the angle in radians to rotate
     * @return the rotated Vector
     */
    static Vector axisRotation(Vector vec, Vector axis, float angle) {
        Vector nAxis = axis.normalized();
        
        float c = (float)Math.cos(angle);
        float s = (float)Math.sin(angle);
        
        return new Vector(nAxis.x*(nAxis.x*vec.x + nAxis.y*vec.y + nAxis.z*vec.z)*(1.f-c) + vec.x*c + (-nAxis.z*vec.y + nAxis.y*vec.z)*s,
                          nAxis.y*(nAxis.x*vec.x + nAxis.y*vec.y + nAxis.z*vec.z)*(1.f-c) + vec.y*c + ( nAxis.z*vec.x - nAxis.x*vec.z)*s,
                          nAxis.z*(nAxis.x*vec.x + nAxis.y*vec.y + nAxis.z*vec.z)*(1.f-c) + vec.z*c + (-nAxis.y*vec.x + nAxis.x*vec.y)*s);
    }
    
    // get rid of this
    /**
     * Determines if this coordinate represented by this Vector (assuming the Vector
     * is placed at the origin) is contained by the square prism defined by the
     * corner vertices ({@code x1}, {@code y1}, {@code z1}) and ({@code x2}, {@code y2}, {@code z2}).
     * 
     * Note: Lower bounds are inclusive, upper bounds are exclusive.
     * 
     * @param x1 the X-coordinate of the first corner vertex
     * @param x2 the X-coordinate of the second corner vertex
     * @param y1 the Y-coordinate of the first corner vertex
     * @param y2 the Y-coordinate of the second corner vertex
     * @param z1 the Z-coordinate of the first corner vertex
     * @param z2 the Z-coordinate of the second corner vertex
     * @return true if the the head of this vector is inside the given square prism
     */
    boolean isInsideSquarePrism(float x1, float x2, float y1, float y2, float z1, float z2) {
        return (x >= x1 && x < x2 && y >= y1 && y < y2 && z > z1 && z <= z2);
    }
}
