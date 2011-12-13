//  
//  Camera.java
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

import static org.lwjgl.util.glu.GLU.gluLookAt;

/**
 * Camera manages a first person camera in 3D space. It calculates the necessary
 * matrix transformations to orient the camera, and provides a simplified
 * mechanism for orienting the camera via movement and rotation methods. It makes
 * heavy use of the Vector class.
 * <p>
 * Camera is meant to be used with OpenGL, which uses a right-handed system.
 * Camera, however, creates a transparent layer between this and functions as
 * if it was using a left-handed coordinate system (into the screen being
 * positive Z rather than negative). GameState uses a left-handed system in the
 * sense that objects only exist in positive coordinates extending forward, up
 * and right from the origin. This is by virtue of the fact that coordinates of
 * objects (which are integers) are simply stored as indices in a 3D array.
 * Because Camera's methods cater to the state's system, Camera is a part of the
 * model (MVC), not the view. This decision was made through the logic that
 * Camera represents a property of the Player (its view) rather than an aspect
 * of rendering. Internally, however, Camera is more of a mix of the model and
 * the view.
 * 
 * @author Mitchell Kember
 * @since 08/12/2011
 * @see Vector
 */
final class Camera {
    
    /**
     * Constant used  to convert from degrees to radians.
     */
    private static final float DEG_TO_RAD = (float)Math.PI / 180.f;
    
    /**
     * This Vector will always point skyward. There is no need for a changing
     * "up" vector because the horizon should always be level.
     */
    private static final Vector sky = new Vector(0, 1, 0);
    
    /**
     * The position, stored internally in OpenGL/right-handed coordinates. That is,
     * movement actions will change the Z-coordinate of {@code position} inversely.
     */
    private Vector position = new Vector(0, 0, 0);
    
    /**
     * Normalized Vector pointing to the right of this Camera.
     */
    private Vector right = new Vector(1, 0, 0);
    
    /**
     * The view or sight of this Camera, as a normalized Vector relative to
     * this Camera's position.
     * 
     * @see #position
     */
    private Vector sight = new Vector(0, 0, -1);
    
    /**
     * Keeps track of this Camera's pitch, used to avoid pitching below
     * -90 degrees or above +90 degrees.
     * 
     * @see #pitch
     */
    private float rotationX = 0;
    
    /**
     * Updates the OpenGL ModelView matrix stack for this Camera's view.
     * Call after all Camera transformations and before rendering. It is
     * assumed that the identity matrix has been loaded.
     */
    void updateMatrix() {
        // Get the absolute coordinate of the view direction
        Vector lookAt = position.plus(sight);
        
        // Multiply onto the matrix stack
        gluLookAt((float)position.x,(float)position.y,  (float)position.z,
                  (float)lookAt.x,  (float)lookAt.y,    (float)lookAt.z,
                  (float)sky.x,     (float)sky.y,       (float)sky.z);
    }
    
    /**
     * Moves this Camera by adding {@code vec} to its position.
     * 
     * @param vec the movement Vector
     */
    void move(Vector vec) {
        position.add(vec.invertedZ());
    }
    
    /**
     * Moves this Camera forward in the direction it is facing. Pass a negative
     * {@code distance} to move backwards. This will never move the camera along
     * the global Y axis, only along the global XZ plane.
     * 
     * @param distance the distance to move forward by
     */
    void moveForward(float distance) {
        position.add(new Vector(sight.x, 0, sight.z).normalized().scaled(distance));
    }
    
    /**
     * Moves this Camera to the right while facing the same direction. Pass a
     * negative {@code distance} to move to the left. This will never move the
     * camera along the global Y axis, only along the global XZ plane.
     * 
     * @param distance the distance to move to the right by
     */
    void strafeRight(float distance) {
        position.add(right.scaled(distance));
    }
    
    /**
     * Rotates this Camera about the X axis by {@code angle} degrees. This will
     * not pitch below -90 degrees or above +90 degrees.
     * 
     * @param angle degrees to rotate by
     */
    void pitch(float angle) {
        if (rotationX + angle < -89.9f || rotationX + angle > 89.9f) return;
        rotationX += angle; // keep track of angle
        
        sight = Vector.axisRotation(sight, right, angle*DEG_TO_RAD);
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
        sight = Vector.axisRotation(sight, sky, -angle*DEG_TO_RAD);
        right = Vector.cross(sight, sky).normalized();
    }
    
    /**
     * Sets this Camera's position's X-coordinate to {@code x}.
     * 
     * @param x the new position's X-coordinate
     */
    void setPositionX(float x) {
        position.x = x;
    }
    
    /**
     * Sets this Camera's position's Y-coordinate to {@code y}.
     * 
     * @param y the new position's Y-coordinate
     */
    void setPositionY(float y) {
        position.y = y;
    }
    
    /**
     * Sets this Camera's position's Z-coordinate to {@code z}.
     * 
     * @param z the new position's Z-coordinate
     */
    void setPositionZ(float z) {
        position.z = -z;
    }
    
    /**
     * Gets a copy of this Camera's position.
     * 
     * @return the position
     */
    Vector getPosition() {
        return position.invertedZ();
    }
    
    /**
     * Gets a copy of the Vector which represents the direction of this Camera's line of sight.
     */
    Vector getSight() {
        return sight.invertedZ();
    }
}
