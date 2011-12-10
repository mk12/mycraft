//  
//  Player.java
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
 * Player represents the user in the MyCraft world. It is essentially a view
 * into the world because the Player cannot see himself or herself, but the
 * Player class also manages a Player's movement and physics.
 * 
 * @author Mitchell Kember
 * @since 09/12/2011
 */
public class Player {
    
    /**
     * Speed in units per 60 FPS frame for this Player's movement.
     */
    static final float MOVE_SPEED = 0.2f;
    
    private static final float GRAVITY = -0.015f;
    private static final float INITAL_JUMP_VELOCITY = 0.35f;
    
    /**
     * The number of units above this Player's feet that the head or Camera
     * is stationed.
     */
    private static final float CAMERA_HEIGHT = 1.5f;
    
    private float GROUND = 0;
    
    /**
     * The view of this Player into the world.
     */
    private Camera camera;
    
    private boolean isJumping = false;
    private float yPosition = 0;
    private float yVelocity = 0;
    
    /**
     * Creates a new player at the default position.
     */
    Player() {
        camera = new Camera();
        camera.move(new Vector(1, yPosition+CAMERA_HEIGHT, 10));
    }
    
    /**
     * Causes this Player to jump if this Player is on the ground.
     */
    void jump() {
        if (isJumping) return;
        
        isJumping = true;
        yVelocity = INITAL_JUMP_VELOCITY;
    }
    
    /**
     * Updates this Player's Y position if in the air, otherwise does nothing.
     * This should be called every frame.
     * 
     * @param multiplier the FPS divided by 60 
     */
    void update(float multiplier) {
        if (isJumping) {
            yPosition += yVelocity * multiplier;
            yVelocity += GRAVITY * multiplier;
            
            if (yPosition < GROUND) {
                yPosition = GROUND;
                yVelocity = 0;
                isJumping = false;
            }
            
            camera.setPositionY(yPosition+CAMERA_HEIGHT);
        }
    }
    
    /**
     * Gets this Player's Camera object.
     * 
     * @return the camera
     */
    Camera getCamera() {
        return camera;
    }
}
