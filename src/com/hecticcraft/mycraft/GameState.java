//  
//  GameState.java
//  MyCraft
//  
//  Created on 07/12/2011.
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
    
    GameStateListener listener;
    
    /**
     * The one and only Player.
     */
    private Player player = new Player();
    
    private Chunk chunk;
    
    /**
     * Creates a new GameState with the specified class implementing
     * GamStateListener to listen for state changes.
     * 
     * @param listener the object to receive state change events
     */
    GameState(GameStateListener listener) {
        this.listener = listener;
        chunk = new Chunk();
        listener.gameStateChunkChanged(chunk);
    }
    
    /**
     * Updates the GameState, responding to user input through {@code GameStateInputData}.
     * This should be called every frame.
     * 
     * @param input user input that should modify the state or move the player
     * @param deltaTime time passed since the last call in milliseconds
     * @see GameStateInputData
     */
    void update(GameStateInputData input, float deltaTime) {
        float multiplier = deltaTime / (100.f/6.f);
        player.update(input, multiplier);
        
        if (input.placeBlock) {
            Vector position = player.getPosition();
            Vector lookAt = player.getCamera().getLookAt().normalized();
            
            float distXY;
            // XY plane faces (front and back)
            int closestZ = (int)Math.floor(position.z);
            Vector XY = position.plus(lookAt.scaled((closestZ-position.z)/lookAt.z));
            
            Vector add = lookAt.scaled(-1.f/lookAt.z);
            
            while (XY.x > 0 && XY.x < 8
                    && XY.y > 0 && XY.y < 8
                    && XY.z < 0 && XY.z > -8) {
                
                if (chunk.getBlockType((int)XY.x, (int)XY.y, -(int)XY.z) != 0) {
                    distXY = (float)Math.sqrt((XY.x-position.x)*(XY.x-position.x) + (XY.y-position.y)*(XY.y-position.y) + (XY.z-position.z)*(XY.z-position.z));
                    break;
                }
                
                XY.add(add);
            }
            
            if (XY.x < 8 && XY.y < 8 && XY.z > -8 && XY.x > 0 && XY.y > 0 && XY.z < 0) {
                chunk.setBlockType((int)(XY.x), (int)(XY.y), -(int)(XY.z)-1, (byte)1);
                listener.gameStateChunkChanged(chunk);
            }
        }
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
