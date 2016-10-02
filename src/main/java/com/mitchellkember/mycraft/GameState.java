// Copyright 2012 Mitchell Kember. Subject to the MIT License.

package com.mitchellkember.mycraft;

/**
 * GameState is the model in the Model-View-Controller (MVC) design architecture
 * for this application. GameState is responsible for simulating the Mycraft world.
 * It is a snapshot of a Mycraft world at any given time.
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
        // Everything is simulated to look correct at 60FPS, and is multiplied
        // by this to match the real framerate.
        float multiplier = deltaTime / (100.f / 6.f);
        
        // Player movement
        player.move(input, multiplier);
        player.collision(chunk);
        if (input.jump) player.jump();
        
        // Set selectedBlock and newBlock
        calculateSelectedBlock(chunk);
        
        // Break or place a block
        if (selectedBlock != null && newBlock != null) {
            if (input.breakBlock) {
                chunk.setBlockType(selectedBlock, (byte)0);
                // Notify the listener
                listener.gameStateChunkChanged(chunk);
            } else if (input.placeBlock) {
                chunk.setBlockType(newBlock, (byte)1);
                // Notify the listener
                listener.gameStateChunkChanged(chunk);
            }
        }
    }
    
    /**
     * Calculates {@code selectedBlock} and {@code newBlock}.
     * 
     * @param chunk the chunk the Player is in
     */
    void calculateSelectedBlock(Chunk chunk) {
        Vector position = player.getCamera().getPosition();
        Vector sight = player.getCamera().getSight();
        
        Vector ray;  // Vector cast out from the players position to find a block
        Vector step; // step to increment ray by
        
        // Blocks are null unless they become assigned.
        selectedBlock = null;
        newBlock = null;
        
        // The following works, and is bug-free. That is all.
        
        // XY plane (front and back faces)
        // Start out assuming the front/back block is very far away so other blocks
        // will be chosen first, if there is no block found (if z == 0 or the ray leaves
        // its confines.
        float frontBackDistSquared = Float.MAX_VALUE;
        if (sight.z != 0) {
            // Calculate ray and step depending on look direction
            if (sight.z > 0) ray = position.plus(sight.scaled((float)(Math.ceil(position.z) - position.z) / sight.z));
            else ray = position.plus(sight.scaled((float)(Math.floor(position.z) - position.z) / sight.z));
            step = sight.scaled(Math.abs(1.f / sight.z));
            
            // Do the first step already if z == 16 to prevent an ArrayIndexOutOfBoundsException
            if (ray.z == 16) ray.add(step);
            
            while (ray.x >= 0 && ray.x < 16
                    && ray.y >= 0 && ray.y < 16
                    && ray.z >= 0 && ray.z < 16) {
                // Give up if we've extended the ray longer than the Player's arm length
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
                        if (selectedBlock.z+1 < 16) {
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
            
            if (ray.x == 16) ray.add(step);
            
            while (ray.x >= 0 && ray.x < 16
                    && ray.y >= 0 && ray.y < 16
                    && ray.z >= 0 && ray.z < 16) {
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
                        if (selectedBlock.x+1 < 16) {
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
            
            if (ray.y == 16) ray.add(step);
            
            while (ray.x >= 0 && ray.x < 16
                    && ray.y >= 0 && ray.y < 16
                    && ray.z >= 0 && ray.z < 16) {
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
                        if (selectedBlock.y+1 < 16) {
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
    }
    
    /**
     * Determines whether a block is currently selected or not.
     * 
     * @return true if a block is selected
     */
    boolean isBlockSelected() {
        return selectedBlock != null;
    }
    
    /**
     * Gets the currently selected block.
     * 
     * @return the block which is selected
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
