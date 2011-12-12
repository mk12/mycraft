//  
//  Player.java
//  MyCraft
//  
//  Created on 09/12/2011.
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
 * Player represents the user in the MyCraft world. A Player is primarily a view
 * into the MyCraft world, and so much of the work is done by the Camera which
 * the Player owns. This class also manages a Player's movements and physics.
 * 
 * @author Mitchell Kember
 * @since 09/12/2011
 */
public class Player {
    
    static final float ARM_LENGTH = 5.f;
    
    /**
     * Speed in units per 60 FPS frame for this Player's movement.
     */
    private static final float MOVE_SPEED = 0.1f;
    
    /**
     * The pull of gravity, in units per 60 FPS frame.
     */
    private static final float GRAVITY = -0.015f;
    
    /**
     * The initial upward velocity this Player will have upon jumping.
     */
    private static final float INITAL_JUMP_VELOCITY = 0.25f;
    
    /**
     * The number of units above this Player's feet that the head or Camera
     * is stationed.
     */
    private static final float CAMERA_HEIGHT = 1.5f;
    
    private float ground = 0;
    private boolean isJumping = false;
    private float yPosition = 0;
    private float yVelocity = 0;
    
    /**
     * The view of this Player into the MyCraft world.
     */
    private Camera camera = new Camera();
    
    {
        camera.move(new Vector(0, yPosition+CAMERA_HEIGHT, 0));
    }
    
    /**
     * Updates this Player's position and Camera. Responds to user input
     * delegated from GameState through the GameStateInputData object passed
     * down. This should be called every frame.
     * 
     * @param input the user input, part of which should move the player
     * @param multiplier the FPS divided by 60 
     */
    void update(GameStateInputData input, float multiplier) {
        // Movement
        if (input.forward) {
            camera.moveForward(MOVE_SPEED * multiplier);
        }
        if (input.backward) {
            camera.moveForward(-MOVE_SPEED * multiplier);
        }
        if (input.left) {
            camera.strafeRight(-MOVE_SPEED * multiplier);
        }
        if (input.right) {
            camera.strafeRight(MOVE_SPEED * multiplier);
        }
        
        // Orient the camera
        camera.pitch(input.lookDeltaY);
        camera.yaw(input.lookDeltaX);
        
        // Boundaries
        Vector position = camera.getWorldPosition();
        if (position.x < 0) position.x = 0;
        else if (position.x > 8) position.x = 8;
        if (position.z > 0) position.z = 0;
        else if (position.z < -8) position.z = -8;
        
        // Jumping
        if (input.jump && !isJumping) {
            isJumping = true;
            yVelocity = INITAL_JUMP_VELOCITY;
        }
        
        if (isJumping) {
            yPosition += yVelocity * multiplier;
            yVelocity += GRAVITY * multiplier;
            
            if (yPosition < ground) {
                yPosition = ground;
                yVelocity = 0;
                isJumping = false;
            }
            
            camera.setPositionY(yPosition+CAMERA_HEIGHT);
        }
        
        if (input.placeBlock) {
            
        }
    }
    
    /*
     * Vector position = player.getCamera().getWorldPosition();
        Vector sight = player.getCamera().getWorldSight();
        Vector step;
        
        // XY plane (front and back faces)
        if (sight.z != 0) {
            Vector frontBack = position.plus(sight.scaled(((int)Math.round(position.z) - position.z) / sight.z));
            step = sight.scaled(Math.abs(1.f / sight.z));

            while (frontBack.isInsideSquarePrism(0, 8, 0, 8, -8, 0) && frontBack.minus(position).magnitude() < Player.ARM_LENGTH) {
                if (chunk.getBlockType((int)frontBack.x, (int)frontBack.y, (int)frontBack.z) != 0) {
                    chunk.setBlockType((int)frontBack.x, (int)frontBack.y, (int)frontBack.z+1, (byte)1);
                    listener.gameStateChunkChanged(chunk);
                    break;
                }
                frontBack.add(step);
            }
        }
     */
    
    
    /**
     * Gets this Player's Camera object.
     * 
     * @return the camera
     */
    Camera getCamera() {
        return camera;
    }
}
