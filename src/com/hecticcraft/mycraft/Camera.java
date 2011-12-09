//  
//  Camera.java
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

import static org.lwjgl.util.glu.GLU.gluLookAt;

/**
 * Camera manages a camera in 3D spaceL. It calculates the
 * necessary matrix transformations to orient the camera, and
 * provides a simplified mechanism for moving the camera via
 * move and rotate methods.
 * 
 * @author Mitchell Kember
 * @since 08/12/2011
 */
class Camera {
    
    private static final float DEG_TO_RAD = (float)Math.PI / 180.f;
    
    /**
     * The position, in global coordinates.
     */
    private Vector position;
    
    /**
     * Represents which direction "up" is, relative to this Camera.
     */
    private Vector upVector;
    
    /**
     * Represents which direction "right" is, relative to this Camera.
     */
    private Vector rightVector;
    
    /**
     * The location this Camera is looking at, relative to this Camera.
     */
    private Vector lookAt;
    
    /**
     * Creates a new Camera with the specified initial Vectors.
     * 
     * @param position position Vector of the Camera
     * @param upVector up direction Vector
     * @param rightVector right direction Vector
     * @param lookAt relative Vector to look at
     */
    Camera(Vector position, Vector upVector, Vector rightVector, Vector lookAt) {
        this.position = position;
        this.upVector = upVector;
        this.rightVector = rightVector;
        this.lookAt = lookAt;
    }
    
    /**
     * Creates a new Camera with default upVector (y = 1) and rightVector (x = 1) Vectors.
     * 
     * @param position position Vector of the Camera
     * @param lookAt relative Vector to look at
     */
    Camera(Vector position, Vector lookAt) {
        this(position, new Vector(0, 1, 0), new Vector(1, 0, 0), lookAt);
    }
    
    /**
     * Creates a new Camera with default Vectors, which place the Camera
     * right-side-up at the origin, looking towards negative Z.
     */
    Camera() {
        this(new Vector(0, 0, 0), new Vector(0, 0, -1));
    }
    
    /**
     * Updates the OpenGL matrix stack for this Camera's view. Call after
     * any Camera transformations, before rendering.
     */
    void updateMatrix() {
        Vector globalLookAt = position.plus(lookAt);
        
        gluLookAt((float)position.x,     (float)position.y,     (float)position.z,
                  (float)globalLookAt.x, (float)globalLookAt.y, (float)globalLookAt.z,
                  (float)upVector.x,     (float)upVector.y,     (float)upVector.z);
    }
    
    /**
     * Moves this Camera by global vector {@code vec}.
     * 
     * @param vec the movement Vector
     */
    void move(Vector vec) {
        position.add(vec);
    }
    
    /**
     * Moves this Camera forward in the direction it is facing. Pass a negative
     * {@code distance} to move backwards.
     * 
     * @param distance the distance to move forward by
     */
    void moveForward(float distance) {
        position.add(lookAt.scaled(distance));
    }
    
    /**
     * Moves this Camera upward while facing the same direction. Pass a negative
     * {@code distance} to move downwards.
     * 
     * @param distance the distance to move upward by
     */
    void moveUpward(float distance) {
        position.add(upVector.scaled(distance));
    }
    
    /**
     * Moves this Camera to the right while facing the same direction. Pass a negative
     * {@code distance} to move to the left.
     * 
     * @param distance the distance to move to the right by
     */
    void strafeRight(float distance) {
        position.add(rightVector.scaled(distance));
    }
    
    /**
     * Rotates this Camera about the X axis by {@code angle} degrees.
     * 
     * @param angle degrees to rotate by
     */
    void rotateX(float angle) {
        lookAt = lookAt.scaled((float)Math.cos(angle*DEG_TO_RAD)).plus(upVector.scaled((float)Math.sin(angle*DEG_TO_RAD))).normalized();
        upVector = Vector.cross(lookAt, rightVector).scaled(-1);
    }
    
    /**
     * Rotates this Camera about the Y axis by {@code angle} degrees.
     * 
     * @param angle degrees to rotate by
     */
    void rotateY(float angle) {
        lookAt = lookAt.scaled((float)Math.cos(angle*DEG_TO_RAD)).minus(rightVector.scaled((float)Math.sin(angle*DEG_TO_RAD))).normalized();
        rightVector = Vector.cross(lookAt, upVector);
    }
    
    /**
     * Rotates this Camera about the Z axis by {@code angle} degrees.
     * 
     * @param angle degrees to rotate by
     */
    void rotateZ(float angle) {
        rightVector = rightVector.scaled((float)Math.cos(angle*DEG_TO_RAD)).plus(upVector.scaled((float)Math.sin(angle*DEG_TO_RAD))).normalized();
        upVector = Vector.cross(lookAt, rightVector).scaled(-1);
    } 
}
