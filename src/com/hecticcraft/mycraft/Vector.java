//  
//  Vector.java
//  MyCraft
//  
//  Created on 06/12/2011.
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
 * an existing one (e.g. add/plus, sub/minus, scale/scaled).
 *
 * @author Mitchell Kember
 * @since 08/12/2011
 */
class Vector {
    float x;
    float y;
    float z;
    
    /**
     * Creates a new Vector with the specified components.
     * 
     * @param x the x component
     * @param y the y component
     * @param z the z component
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
     * does not  modify this Vector.
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
}
