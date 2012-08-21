// Copyright 2012 Mitchell Kember. Subject to the MIT license.

package com.mitchellkember.mycraft;

/**
 * Chunk represents a chunk of 16 by 16 by 16 blocks in the Mycraft world.
 * Each block uses one byte to represent its type, totaling 4 kilobytes to
 * store the information for one Chunk.
 * 
 * @author Mitchell Kember
 * @since 09/12/2011
 */
final class Chunk {
    
    /**
     * Not used yet since there is only one Chunk.
     */
    //private Vector position;
    
    /**
     * This 3D array stores all the types of the blocks in this Chunk. It is in
     * the following format:
     * 
     * {@code data[x][y][z]}
     */
    private byte[][][] data = new byte[16][16][16];
    
    {
        // Place a ground layer of blocks
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                data[x][0][z] = 1;
            }
        }
    }
    
    /**
     * Gets this Chunk's data array.
     * 
     * @return the data
     */
    byte[][][] getData() {
        return data;
    }
    
    /**
     * Set a block's type.
     * 
     * @param block the location of the block
     * @param type its new type id
     */
    void setBlockType(Block block, byte type) {
        data[block.x][block.y][block.z] = type;
    }
    
    /**
     * Get a block's type.
     * 
     * @param block the location of the block.
     * @return its type id
     */
    byte getBlockType(Block block) {
        return data[block.x][block.y][block.z];
    }
}
