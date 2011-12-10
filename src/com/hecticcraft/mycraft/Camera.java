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
 * Camera manages a typical first person camera in 3D space. It calculates
 * the necessary matrix transformations to orient the camera, and provides
 * a simplified mechanism for moving the camera via movement and rotation methods.
 * 
 * @author Mitchell Kember
 * @since 08/12/2011
 */
final class Camera {
    
    private static final float DEG_TO_RAD = (float)Math.PI / 180.f;
    
    /**
     * This Vector will always point skyward. There is no need for a changing
     * "up" vector because the horizon should always be level.
     */
    private static final Vector skyVector = new Vector(0, 1, 0);
    
    /**
     * The position, in global coordinates.
     */
    private Vector position = new Vector(0, 0, 0);
    
    /**
     * Normalized Vector pointing to the right of this Camera.
     */
    private Vector rightVector = new Vector(1, 0, 0);
    
    /**
     * The location this Camera is looking at, relative to this Camera.
     */
    private Vector lookAt = new Vector(0, 0, -1);
    
    private float rotationX = 0;
    private float rotationY = 0;
    
    /**
     * Updates the OpenGL matrix stack for this Camera's view. Call after
     * any Camera transformations, before rendering.
     */
    void updateMatrix() {
        Vector globalLookAt = position.plus(lookAt);
        
        gluLookAt((float)position.x,      (float)position.y,      (float)position.z,
                  (float)globalLookAt.x,  (float)globalLookAt.y,  (float)globalLookAt.z,
                  (float)skyVector.x,     (float)skyVector.y,     (float)skyVector.z);
    }
    
    /**
     * Moves this Camera by adding {@code vec} to its position.
     * 
     * @param vec the movement Vector
     */
    void move(Vector vec) {
        position.add(vec);
    }
    
    /**
     * Moves this Camera forward in the direction it is facing. Pass a negative
     * {@code distance} to move backwards. This will never move the camera along
     * the global Y axis, only along the global XZ plane.
     * 
     * @param distance the distance to move forward by
     */
    void moveForward(float distance) {
        position.add(new Vector(lookAt.x, 0, lookAt.z).normalized().scaled(distance));
    }
    
    /**
     * Moves this Camera to the right while facing the same direction. Pass a
     * negative {@code distance} to move to the left. This will never move the
     * camera along the global Y axis, only along the global XZ plane.
     * 
     * @param distance the distance to move to the right by
     */
    void strafeRight(float distance) {
        position.add(rightVector.scaled(distance));
    }
    
    /**
     * Rotates this Camera about the X axis by {@code angle} degrees. This will
     * not pitch below -90 degrees or above +90 degrees.
     * 
     * @param angle degrees to rotate by
     */
    void pitch(float angle) {
        rotationY += angle;
        
        if (rotationX + angle < -89.5f || rotationX + angle > 89.5f) return;
        rotationX += angle;
        lookAt = Vector.axisRotation(lookAt, rightVector, angle*DEG_TO_RAD);
    }
    
    /**
     * Rotates this Camera about the Y axis by {@code angle} degrees.
     * 
     * Note: This does not rotate about the Y axis relative to this Camera,
     * but rather about the global Y axis so that the horizon will always
     * stay level through this Camera's view. Technically, this is more
     * of a "swivel" than a yaw.
     * 
     * @param angle degrees to rotate by
     */
    void yaw(float angle) {
        lookAt = Vector.axisRotation(lookAt, skyVector, angle*DEG_TO_RAD);
        rightVector = Vector.cross(lookAt, skyVector).normalized();
    }
}
