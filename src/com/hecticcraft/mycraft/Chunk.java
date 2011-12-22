//  
//  Chunk.java
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
 * Chunk represents a chunk of 16 by 16 by 16 blocks in the MyCraft world.
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
