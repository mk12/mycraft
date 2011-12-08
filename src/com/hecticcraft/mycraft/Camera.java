//  
//  Camera.java
//  MyCraft
//  
//  Created on 06/12/2011.
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

import javax.vecmath.Vector3d;
import static org.lwjgl.util.glu.GLU.gluLookAt;


/**
 *
 * @author Mitchell Kember
 * @since 08/12/2011
 */
public class Camera {
    
    private Vector3d  eye = new Vector3d(0, 0,  0);
    private Vector3d   up = new Vector3d(0, 1,  0);
    private Vector3d side = new Vector3d(1, 0,  0);
    private Vector3d look = new Vector3d(0, 0, -1);
    
    float rotatedX = 0;
    float rotatedY = 0;
    float rotatedZ = 0;
    
    Camera() {
    }
    
    void updateMatrix() {
        Vector3d viewpoint = new Vector3d();
        viewpoint.add(eye, look);
        
        gluLookAt((float)eye.x, (float)eye.y, (float)eye.z,
                  (float)viewpoint.x, (float)viewpoint.y, (float)viewpoint.z,
                  (float)up.x, (float)up.y, (float)up.z);
    }
    
    
    void move(Vector3d vec) {
        eye.add(vec);
    }
    
    void moveForward(float distance) {
        Vector3d vec = new Vector3d();
        vec.scale(-distance, look);
        eye.add(vec);
    }
    void moveUpward(float distance) {
        
    }
    void strafeRight(float distance) {
        Vector3d vec = new Vector3d();
        vec.scale(-distance, side);
        eye.add(vec);
    }
    
    void rotateX(float angle) {
        rotatedX += angle;
        
        Vector3d vec = new Vector3d();
        Vector3d vec2 = new Vector3d();
        vec.scale(Math.cos(angle * (Math.PI/180)), look);
        vec2.scale(Math.sin(angle*(Math.PI/180)), up);
        
        look = new Vector3d();
        look.add(vec, vec2);
        look.normalize();
        
        up.cross(look, side);
        up.scale(-1);
    }
    void rotateY(float angle) {
        rotatedY += angle;
    }
    void rotateZ(float angle) {
        rotatedZ += angle;
        
        Vector3d vec = new Vector3d();
        Vector3d vec2 = new Vector3d();
        vec.scale(Math.cos(angle * (Math.PI/180)), side);
        vec2.scale(Math.sin(angle*(Math.PI/180)), up);
        
        side = new Vector3d();
        side.add(vec, vec2);
        side.normalize();
        
        up = new Vector3d();
        up.cross(look, side);
        up.scale(-1);
    }
    
}
