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
    
    private static final float ARM_LENGTH_SQUARED = 25;
    
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
        
        if (input.placeBlock && isBlockSelected) {
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
        float frontBackDistSquared = -1;
        if (sight.z != 0) {
            frontBack = position.plus(sight.scaled(((int)Math.round(position.z) - position.z) / sight.z));
            step = sight.scaled(Math.abs(1.f / sight.z));
            
            System.out.println(frontBack.x + ", " + frontBack.y + ", " + position.z);
            //System.out.println(s)
            
            while (frontBack.isInsideSquarePrism(0, 7, 0, 7, -7, 0) && frontBack.minus(position).magnitudeSquared() < ARM_LENGTH_SQUARED) {
                float distSquared = frontBack.minus(position).magnitudeSquared();
                if (distSquared > ARM_LENGTH_SQUARED) break;
                
                if (chunk.getBlockType((int)frontBack.x, (int)frontBack.y, -(int)frontBack.z) != 0) {
                    selectedBlock = new Block((int)frontBack.x, (int)frontBack.y, -(int)frontBack.z);
                    newBlock = new Block((int)frontBack.x, (int)frontBack.y, -(int)frontBack.z-1);
                    frontBackDistSquared = distSquared;
                    isBlockSelected = true;
                    return;
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
     player position and camera position ? **/
    
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
    
    boolean isBlockSelected() {
        return isBlockSelected;
    }
    
    Vector getSelectedBlock() {
        return new Vector(selectedBlock.x, selectedBlock.y, selectedBlock.z);
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
