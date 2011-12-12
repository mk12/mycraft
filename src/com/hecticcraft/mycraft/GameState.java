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
    
    /**
     * The object which listens to state changes (usually the renderer).
     */
    GameStateListener listener;
    
    /**
     * The one and only Player.
     */
    private Player player = new Player();
    
    /**
     * The one and only Chunk... so far.
     */
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
        float multiplier = deltaTime / (100.f / 6.f);
        
        player.move(input, multiplier);
        player.collision(chunk);
        if (input.jump) player.jump();
        player.updateHeight(multiplier);
        
        player.calculateSelectedBlock(chunk);
    }
    
    /** PROBLEM: camera class in state, but uses RHS system 
     block class might abstract away
     passing around blocks (Vector float...)
     borderline chunks
     playerv vs gamestate responsibility
     isolate rendering and state (camera .. ) 
     player position and camera position ? **/
    
    boolean isBlockSelected() {
        return false;//return player.getSelectedBlock().getType(chunk) != 0;
    }
    
    Vector getSelectedBlock() {
        Block block = player.getSelectedBlock();
        return new Vector(block.x, block.y, block.z);
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
