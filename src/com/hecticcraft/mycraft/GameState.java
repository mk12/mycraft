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
    
    private static final float ARM_LENGTH_SQUARED = 6*6;
    
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
    
    private boolean isBlockSelected;
    private Block selectedBlock;
    private Block newBlock;
    
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
        
        calculateSelectedBlock(chunk);
        
        if (input.placeBlock && isBlockSelected && newBlock != null) {
            newBlock.setType(chunk, (byte)1);
            listener.gameStateChunkChanged(chunk);
        }
    }
    
    void calculateSelectedBlock(Chunk chunk) {
        Vector position = player.getCamera().getPosition();
        Vector sight = player.getCamera().getSight();
        Vector frontBack;
        Vector step;
        
        // XY plane (front and back faces)
        float frontBackDistSquared = Float.MAX_VALUE;
        if (sight.z != 0) {
            if (sight.z < 0) frontBack = position.plus(sight.scaled((float)(Math.ceil(position.z) - position.z) / sight.z));
            else frontBack = position.plus(sight.scaled((float)(Math.floor(position.z) - position.z) / sight.z));
            step = sight.scaled(Math.abs(1.f / sight.z));
            
            while (frontBack.x >= 0 && frontBack.x < 8
                    && frontBack.y >= 0 && frontBack.y < 8
                    && frontBack.z > -8 && frontBack.z <= 0) {
                float distSquared = frontBack.minus(position).magnitudeSquared();
                if (distSquared > ARM_LENGTH_SQUARED) break;
                
                if (sight.z < 0) {
                     if (Block.fromOpenGL((int)frontBack.x, (int)frontBack.y, (int)frontBack.z).getType(chunk) != 0) {
                         selectedBlock = Block.fromOpenGL((int)frontBack.x, (int)frontBack.y, (int)frontBack.z);
                         newBlock = Block.fromWorld(selectedBlock.x, selectedBlock.y, selectedBlock.z-1); // make sure its air
                         if (newBlock.getType(chunk) != 0) newBlock = null;
                         
                         frontBackDistSquared = distSquared;
                         isBlockSelected = true;
                         return;
                     }
                } else {
                    if (-(frontBack.z+1) >= 0 && Block.fromOpenGL((int)frontBack.x, (int)frontBack.y, (int)frontBack.z+1).getType(chunk) != 0) {
                        selectedBlock = Block.fromOpenGL((int)frontBack.x, (int)frontBack.y, (int)frontBack.z+1);
                        if (selectedBlock.z+1 >= 8) newBlock = null;
                        else {
                            newBlock = Block.fromWorld(selectedBlock.x, selectedBlock.y, selectedBlock.z+1);
                            if (newBlock.getType(chunk) != 0) newBlock = null;
                        }
                        
                        frontBackDistSquared = distSquared;
                        isBlockSelected = true;
                        return;
                    }
                }
                frontBack.add(step);
            }
        }
        
        isBlockSelected = false;
    }
    
    /** PROBLEM: camera class in state, but uses RHS system 
     block class might abstract away
     passing around blocks (Vector float...)
     borderline chunks
     playerv vs gamestate responsibility
     isolate rendering and state (camera .. ) 
     player position and camera position ?
     * 
     * http://www.matrix44.net/cms/notes/opengl-3d-graphics/coordinate-systems-in-opengl
     
     **/
    
    /**
     * Determines whether a block is currently selected or not.
     * 
     * @return 
     */
    boolean isBlockSelected() {
        return isBlockSelected;
    }
    
    Vector getSelectedBlock() {
        return selectedBlock.getOpenGLCoordinates();
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
