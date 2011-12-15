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
 * GameState is the model in the Model-View-Controller (MVC) design architecture
 * for this application. GameState is responsible for simulating the MyCraft world.
 * It is a snapshot of a MyCraft world at any given time.
 * 
 * @author Mitchell Kember
 * @since 07/12/2011
 */
final class GameState {
    
    /**
     * The length of the Player's arm; how far away from the Player a block
     * can be placed.
     */
    private static final float ARM_LENGTH = 6;
    
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
     * The currently selected block.
     */
    private Block selectedBlock;
    
    /**
     * The block of air which will be replaced with a solid block if the Player
     * chooses to.
     */
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
        
        calculateSelectedBlock(chunk);
        
        if (selectedBlock != null && newBlock != null) {
            if (input.breakBlock) {
                chunk.setBlockType(selectedBlock, (byte)0);
                listener.gameStateChunkChanged(chunk);
            } else if (input.placeBlock) {
                chunk.setBlockType(newBlock, (byte)1);
                listener.gameStateChunkChanged(chunk);
            }
        }
    }
    
    /**
     * 
     * 
     * @param chunk 
     */
    void calculateSelectedBlock(Chunk chunk) {
        Vector position = player.getCamera().getPosition();
        Vector sight = player.getCamera().getSight();
        Vector ray;
        Vector step;
        
        selectedBlock = null;
        newBlock = null;
        
        // XY plane (front and back faces)
        float frontBackDistSquared = Float.MAX_VALUE;
        if (sight.z != 0) {
            if (sight.z > 0) ray = position.plus(sight.scaled((float)(Math.ceil(position.z) - position.z) / sight.z));
            else ray = position.plus(sight.scaled((float)(Math.floor(position.z) - position.z) / sight.z));
            step = sight.scaled(Math.abs(1.f / sight.z));
            
            if (ray.z == 8) ray.add(step);
            
            while (ray.x >= 0 && ray.x < 8
                    && ray.y >= 0 && ray.y < 8
                    && ray.z >= 0 && ray.z < 8) {
                float distSquared = ray.minus(position).magnitudeSquared();
                if (distSquared > ARM_LENGTH * ARM_LENGTH) break;
                
                if (sight.z > 0) {
                     if (chunk.getBlockType(new Block((int)ray.x, (int)ray.y, (int)ray.z)) != 0) {
                         selectedBlock = new Block((int)ray.x, (int)ray.y, (int)ray.z);
                         if (selectedBlock.z-1 >= 0) {
                             newBlock = new Block(selectedBlock.x, selectedBlock.y, selectedBlock.z-1);
                             if (chunk.getBlockType(newBlock) != 0) newBlock = null;
                         }
                         
                         frontBackDistSquared = distSquared;
                         break;
                     }
                } else {
                    if (ray.z-1 >= 0 && chunk.getBlockType(new Block((int)ray.x, (int)ray.y, (int)ray.z-1)) != 0) {
                        selectedBlock = new Block((int)ray.x, (int)ray.y, (int)ray.z-1);
                        if (selectedBlock.z+1 < 8) {
                            newBlock = new Block(selectedBlock.x, selectedBlock.y, selectedBlock.z+1);
                            if (chunk.getBlockType(newBlock) != 0) newBlock = null;
                        }
                        
                        frontBackDistSquared = distSquared;
                        break;
                    }
                }
                ray.add(step);
            }
        }
        
        // YZ plane (left and right faces)
        float leftRightDistSquared = Float.MAX_VALUE;
        if (sight.x != 0) {
            if (sight.x > 0) ray = position.plus(sight.scaled((float)(Math.ceil(position.x) - position.x) / sight.x));
            else ray = position.plus(sight.scaled((float)(Math.floor(position.x) - position.x) / sight.x));
            step = sight.scaled(Math.abs(1.f / sight.x));
            
            if (ray.x == 8) ray.add(step);
            
            while (ray.x >= 0 && ray.x < 8
                    && ray.y >= 0 && ray.y < 8
                    && ray.z >= 0 && ray.z < 8) {
                float distSquared = ray.minus(position).magnitudeSquared();
                if (distSquared > ARM_LENGTH * ARM_LENGTH || distSquared > frontBackDistSquared) break;
                
                if (sight.x > 0) {
                     if (chunk.getBlockType(new Block((int)ray.x, (int)ray.y, (int)ray.z)) != 0) {
                         selectedBlock = new Block((int)ray.x, (int)ray.y, (int)ray.z);
                         if (selectedBlock.x-1 >= 0) {
                             newBlock = new Block(selectedBlock.x-1, selectedBlock.y, selectedBlock.z);
                             if (chunk.getBlockType(newBlock) != 0) newBlock = null;
                         }
                         
                         leftRightDistSquared = distSquared;
                         break;
                     }
                } else {
                    if (ray.x-1 >= 0 && chunk.getBlockType(new Block((int)ray.x-1, (int)ray.y, (int)ray.z)) != 0) {
                        selectedBlock = new Block((int)ray.x-1, (int)ray.y, (int)ray.z);
                        if (selectedBlock.x+1 < 8) {
                            newBlock = new Block(selectedBlock.x+1, selectedBlock.y, selectedBlock.z);
                            if (chunk.getBlockType(newBlock) != 0) newBlock = null;
                        }
                        
                        leftRightDistSquared = distSquared;
                        break;
                    }
                }
                ray.add(step);
            }
        }
        
        // XZ plane (bottom and top faces)
        float bottomTopDistSquared = Float.MAX_VALUE;
        if (sight.y != 0) {
            if (sight.y > 0) ray = position.plus(sight.scaled((float)(Math.ceil(position.y) - position.y) / sight.y));
            else ray = position.plus(sight.scaled((float)(Math.floor(position.y) - position.y) / sight.y));
            step = sight.scaled(Math.abs(1.f / sight.y));
            
            if (ray.y == 8) ray.add(step);
            
            while (ray.x >= 0 && ray.x < 8
                    && ray.y >= 0 && ray.y < 8
                    && ray.z >= 0 && ray.z < 8) {
                float distSquared = ray.minus(position).magnitudeSquared();
                if (distSquared > ARM_LENGTH * ARM_LENGTH || distSquared > frontBackDistSquared || distSquared > leftRightDistSquared) break;
                
                if (sight.y > 0) {
                     if (chunk.getBlockType(new Block((int)ray.x, (int)ray.y, (int)ray.z)) != 0) {
                         selectedBlock = new Block((int)ray.x, (int)ray.y, (int)ray.z);
                         if (selectedBlock.y-1 >= 0) {
                             newBlock = new Block(selectedBlock.x, selectedBlock.y-1, selectedBlock.z);
                             if (chunk.getBlockType(newBlock) != 0) newBlock = null;
                         }
                         
                         bottomTopDistSquared = distSquared;
                         break;
                     }
                } else {
                    if (ray.y-1 >= 0 && chunk.getBlockType(new Block((int)ray.x, (int)ray.y-1, (int)ray.z)) != 0) {
                        selectedBlock = new Block((int)ray.x, (int)ray.y-1, (int)ray.z);
                        if (selectedBlock.y+1 < 8) {
                            newBlock = new Block(selectedBlock.x, selectedBlock.y+1, selectedBlock.z);
                            if (chunk.getBlockType(newBlock) != 0) newBlock = null;
                        }
                        
                        bottomTopDistSquared = distSquared;
                        break;
                    }
                }
                ray.add(step);
            }
        }
        
        
        //debug
        //selectedBlock = new Block((int)Math.round(position.x), (int)(position.y-1.5f-1), (int)(position.z));
    }
    
    /** PROBLEM: camera class in state, but uses RHS system 
     block class might abstract away
     passing around blocks (Vector float...)
     borderline chunks
     playerv vs gamestate responsibility
     isolate rendering and state (camera .. ) 
     player position and camera position ?
     * 
     * cant place blocks when at back corner (z=8 in state)
     * http://www.matrix44.net/cms/notes/opengl-3d-graphics/coordinate-systems-in-opengl
     
     **/
    
    /**
     * Determines whether a block is currently selected or not.
     * 
     * @return 
     */
    boolean isBlockSelected() {
        return selectedBlock != null;
    }
    
    /**
     * 
     * @return 
     */
    Block getSelectedBlock() {
        return selectedBlock;
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
