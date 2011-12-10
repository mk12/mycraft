//  
//  GameState.java
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
 * GameState is responsible for simulating the MyCraft world. It alone does no
 * rendering, it simply stores data about the Player and the blocks.
 * 
 * @author Mitchell Kember
 * @since 07/12/2011
 */
final class GameState {
    
    /**
     * The one and only Player.
     */
    private Player player = new Player();
    
    /**
     * Process input to move the player. This should be called every frame.
     * 
     * @param input the GameStateInputData object
     * @see GameStateInputData
     */
    void processInput(GameStateInputData input) {
        Camera camera = player.getCamera();
        if (input.forwardKey) {
            camera.moveForward(Player.MOVE_SPEED);
        }
        if (input.backwardKey) {
            camera.moveForward(-Player.MOVE_SPEED);
        }
        if (input.leftKey) {
            camera.strafeRight(-Player.MOVE_SPEED);
        }
        if (input.rightKey) {
            camera.strafeRight(Player.MOVE_SPEED);
        }
        
        camera.pitch(input.lookDeltaY);
        camera.yaw(input.lookDeltaX);
        
        if (input.jumpKey) {
            player.jump();
        }
    }
    
    /**
     * Updates the GameState. This should be called every frame.
     * 
     * @param deltaTime time passed since the last call in milliseconds
     */
    void update(float deltaTime) {
        float multiplier = deltaTime / (100.f/6.f);
        player.update(multiplier);
    }
    
    /**
     * Gets the Player's Camera object.
     * 
     * @return the Player's Camera
     */
    Camera getPlayerView() {
        return player.getCamera();
    }
}
