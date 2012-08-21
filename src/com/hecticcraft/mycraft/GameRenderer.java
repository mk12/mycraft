// Copyright 2012 Mitchell Kember. Subject to the MIT license.

package com.hecticcraft.mycraft;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.IntBuffer;
import java.util.logging.Level;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ARBVertexBufferObject;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 * GameRenderer is responsible for managing the application's window and
 * rendering an instance of GameState into it.
 * 
 * Note: sometimes the Java VM will crash when placing a block. It doesn't happen
 * often, but I have no idea why this happens.
 * 
 * @author Mitchell Kember
 * @since 07/12/2011
 */
final class GameRenderer implements GameStateListener {
    
    private static final int DISPLAY_WIDTH = 800;
    private static final int DISPLAY_HEIGHT = 600;
    private static final int DEPTH_BUFFER_BITS = 24;
    private static final int DESIRED_SAMPLES = 8;
    
    private static final String WINDOW_TITLE = "Mycraft";
    
    /**
     * The width and height of the cross hairs in the middle of the screen.
     */
    private static final float CROSSHAIR_SIZE = 0.025f;
    
    /**
     * The furthest away from this Camera that objects will be rendered.
     */
    private float renderDistance = 50;
    
    /**
     * The ID for the Vertex Buffer Object (VBO).
     */
    private int bufferObjectID;
    
    /**
     * A simple 16 by 16 dirt texture.
     */
    private Texture dirtTexture;
    
    /**
     * The number of vertices last uploaded to the VBO.
     */
    private int numVerts;
    
    /**
     * Gets the vertices to use for rendering a block (inverts the z axis).
     * 
     * @param block the block's location
     * @return its rendering coordinates
     */
    static Vector openGLCoordinatesForBlock(Block block) {
        return new Vector(block.x, block.y, -block.z);
    }
    
    /**
     * Creates a new GameRenderer and sets up the LWJGL window.
     * 
     * @throws LWJGLException if there is an error setting up the window
     */
    GameRenderer() throws LWJGLException {
        Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
        Display.setFullscreen(false);
        Display.setTitle(WINDOW_TITLE);
        
        // Try using oversampling for smooth edges.
        try {
            Display.create(new PixelFormat(0, DEPTH_BUFFER_BITS, 0, DESIRED_SAMPLES));
        } catch (LWJGLException lwjgle) {
            // Replace this with text on screen
            System.out.println("Could not enable anti-aliasing. Brace yourself for jaggies.");
            Display.create(new PixelFormat(0, DEPTH_BUFFER_BITS, 0, 0));
        }
        
        // Get ready
        prepareOpenGL();
        resizeOpenGL();
        initializeData();
        loadTextures();
    }
    
    /**
     * Enables and Disables various OpenGL states. This should be called once when
     * the GameRenderer is created, before any rendering.
     */
    private void prepareOpenGL() {
        glEnable(GL_CULL_FACE);  // back face culling
        glEnable(GL_DEPTH_TEST); // z-buffer
        glEnable(GL_TEXTURE_2D); // textures
        
        // We don't need these
        glDisable(GL_ALPHA_TEST);
        glDisable(GL_STENCIL_TEST);
        glDisable(GL_DITHER);
        glDisable(GL_LIGHTING);
        
        // Cross hair and selected block hilighting
        glLineWidth(2.f);
        
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        
        // Background colour
        glClearColor(0.8f, 0.9f, 1.f, 0.0f);
    }
    
    /**
     * Resizes the OpenGL viewport and recalculates the projection matrix.
     */
    private void resizeOpenGL() {
        glViewport(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(45, (float)DISPLAY_WIDTH / (float)DISPLAY_HEIGHT, 0.25f, renderDistance);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }
    
    /**
     * Renders a GameState.
     * 
     * @param state the GameState to render
     */
    void render(GameState state) {
        // Clear colour and z buffers
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        // Load the identity matrix
        glLoadIdentity();
        // Let the Camera calculate the view matrix
        state.getPlayerView().updateMatrix();
        // Full brightness for textures
        glColor3b((byte)127, (byte)127, (byte)127);
        
        // Start at 1 to avoid drawing 1st degenerate vertex and messing everything else up
        glDrawArrays(GL_TRIANGLE_STRIP, 1, numVerts);
        
        // Black lines
        glColor3b((byte)-127, (byte)-127, (byte)-127);
        
        // Draw selected block outline hilight
        if (state.isBlockSelected()) {
            Vector selectedBlock = openGLCoordinatesForBlock(state.getSelectedBlock());
            
            // Just use immediate mode/fixed function pipeline
            glBegin(GL_LINE_STRIP);
            glVertex3f(selectedBlock.x, selectedBlock.y, selectedBlock.z);
            glVertex3f(selectedBlock.x+1, selectedBlock.y, selectedBlock.z);
            glVertex3f(selectedBlock.x+1, selectedBlock.y+1, selectedBlock.z);
            glVertex3f(selectedBlock.x, selectedBlock.y+1, selectedBlock.z);
            glVertex3f(selectedBlock.x, selectedBlock.y, selectedBlock.z);
            glVertex3f(selectedBlock.x, selectedBlock.y, selectedBlock.z-1);
            glVertex3f(selectedBlock.x+1, selectedBlock.y, selectedBlock.z-1);
            glVertex3f(selectedBlock.x+1, selectedBlock.y+1, selectedBlock.z-1);
            glVertex3f(selectedBlock.x, selectedBlock.y+1, selectedBlock.z-1);
            glVertex3f(selectedBlock.x, selectedBlock.y, selectedBlock.z-1);
            glEnd();
            glBegin(GL_LINES);
            glVertex3f(selectedBlock.x, selectedBlock.y+1, selectedBlock.z);
            glVertex3f(selectedBlock.x, selectedBlock.y+1, selectedBlock.z-1);

            glVertex3f(selectedBlock.x+1, selectedBlock.y+1, selectedBlock.z);
            glVertex3f(selectedBlock.x+1, selectedBlock.y+1, selectedBlock.z-1);

            glVertex3f(selectedBlock.x+1, selectedBlock.y, selectedBlock.z);
            glVertex3f(selectedBlock.x+1, selectedBlock.y, selectedBlock.z-1);
            glEnd();
        }
        
        // Reload identity matrix
        glLoadIdentity();
        // Draw crosshair
        glBegin(GL_LINES);
        glVertex3f(-CROSSHAIR_SIZE/2, 0, -0.25f); glVertex3f(CROSSHAIR_SIZE/2, 0, -0.25f);
        glVertex3f(0, -CROSSHAIR_SIZE/2, -0.25f); glVertex3f(0, CROSSHAIR_SIZE/2, -0.25f);
        glEnd();
        
        // Update
        Display.update();
        Display.sync(60);
    }
    
    /**
     * Loads textures that will be used by this GameRenderer.
     */
    private void loadTextures() {
        try {
            dirtTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/dirt.png"));
        } catch (IOException ioe) {
            Mycraft.LOGGER.log(Level.WARNING, ioe.toString(), ioe);
        }
        
        // Texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST); // Minecraft! (try using GL_LINEAR and you'll see what I mean)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        
        // It will stay bound
        dirtTexture.bind();
    }
    
    /**
     * Creates the VBO that this GameRenderer will use.
     * 
     * @throws LWJGLException if VBOs are not supported
     */
    private void initializeData() throws LWJGLException {
        if (!GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
            Mycraft.LOGGER.log(Level.SEVERE, "GL_ARB_vertex_buffer_object not supported.");
            throw new LWJGLException("GL_ARB_vertex_buffer_object not supported");
        }
        
        // Create it
        bufferObjectID = ARBVertexBufferObject.glGenBuffersARB();

        // Vertex Data interleaved format: XYZST
        final int position  = 3;
        final int texcoords = 2;
        final int sizeOfInt = 4; // 4 bytes in an int
        final int vertexDataSize = (position + texcoords) * sizeOfInt;
        
        // Bind it
        ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, bufferObjectID);
        
        // Vertex and texture pointers
        glVertexPointer(3, GL_INT, vertexDataSize, 0);
        glTexCoordPointer(2, GL_INT, vertexDataSize, position*sizeOfInt);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
    }
    
    /**
     * Calculates vertices for a cube located at ({@code x}, {@code y}, {@code z}).
     * The vertices must be rendered with GL_TRIANGLE_STRIP.
     * 
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @return the vertices in interleaved XYZST format
     */
    private int[] cubeData(int x, int y, int z) {
        return new int[]{ // 23*5 ints
            x, y, z,     0, 0, // degenerate
            
            x, y, z,     1, 0,
            x+1,y,z,     1, 1,
            
            x,y+1,z,     0, 0,
            x+1,y+1,z,   0, 1,
            
            x,y+1,z-1,   1, 0,
            x+1,y+1,z-1, 1, 1,
            
            x,y,z-1,     0, 0,
            x+1,y,z-1,   0, 1,
            
            x, y, z,     1, 0,
            x+1,y,z,     1, 1,
            
            x+1,y,z,     0, 0, // degenerate
            x+1,y,z,     0, 0, // degenerate
            
            x+1,y,z,     0, 1,
            x+1,y,z-1,   1, 1,
            
            x+1,y+1,z,   0, 0,
            x+1,y+1,z-1, 1, 0,
            
            x+1,y+1,z-1, 0, 0, // degenerate
            x,y+1,z-1,   0, 0, // degenerate
            
            x,y+1,z-1,   0, 0,
            x,y,z-1,     0, 1,
            
            x,y+1,z,     1, 0,
            x, y, z,     1, 1,
            
            x, y, z,     0, 0, // degenerate
        };
    }

    /**
     * Updates the VBO when the a {@code chunk} in the GameState has changed.
     * 
     * @param chunk the chunk that has changed
     */
    @Override
    public void gameStateChunkChanged(Chunk chunk) {
        byte[][][] data = chunk.getData();
        IntBuffer vertexData = BufferUtils.createIntBuffer(70000);
        
        try {
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z > -16; z--) {
                        if (data[x][y][-z] != 0) vertexData.put(cubeData(x, y, z));
                    }
                }
            }
        } catch (BufferOverflowException boe1) {
            // Try again with more memory
            try {
                vertexData = BufferUtils.createIntBuffer(150000);
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z > -16; z--) {
                            if (data[x][y][-z] != 0) vertexData.put(cubeData(x, y, z));
                        }
                    }
                }
            } catch (BufferOverflowException boe2) {
                // Bail out
                System.out.println("Oops! Mycraft has crashed!");
                Mycraft.LOGGER.log(Level.SEVERE, boe2.toString(), boe2);
                System.exit(1);
            }
        }
        
        numVerts = vertexData.position() / 5;
        vertexData.flip();
        
        // Upload data
        ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, vertexData, ARBVertexBufferObject.GL_DYNAMIC_DRAW_ARB);
    }
}
