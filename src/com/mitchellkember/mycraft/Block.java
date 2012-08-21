// Copyright 2012 Mitchell Kember. Subject to the MIT license.

package com.mitchellkember.mycraft;

/**
 * Block represents a single block in the Mycraft world. Nothing more than a
 * container for the x, y and z indices.
 * 
 * @author Mitchell Kember
 */
public class Block {
    
    final int x;
    final int y;
    final int z;
    
    Block(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
