// Copyright 2012 Mitchell Kember. Subject to the MIT license.

package com.mitchellkember.mycraft;

/**
 * Classes that listen and respond to changes in the GameState must implement
 * this interface. This allows them to be notified when Chunks are modified.
 * 
 * @author Michell Kember
 * @since 10/12/2011
 */
interface GameStateListener {
    void gameStateChunkChanged(Chunk chunk);
}
