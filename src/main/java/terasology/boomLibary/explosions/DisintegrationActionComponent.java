/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package terasology.boomLibary.explosions;

import javax.vecmath.Vector3f;

import org.terasology.entitySystem.Component;
import org.terasology.logic.actions.ActionTarget;

/**
 * @author Immortius <immortius@gmail.com>
 */
public class DisintegrationActionComponent implements Component{
    public ActionTarget relativeTo = ActionTarget.Instigator;
    public boolean ParticleEffects=false;
    //time for last chec
    public long lastCheckTime;
    // update should be calculated
    public boolean running=false;
    //source of explosion
    public Vector3f origin = null;
    
    /**
     * size of disintegration circle
     */
    public float X = 15;
    public float Y = 15;
    public float Z = 15;
    
    /**
     * 
     */
    public boolean dropBlocks=false;
    
    /** 
     * 0=cube
     * 1=circle
     * 2=elipsoid
     */
    public int type=2;
    
    /**
     * damage done to entities, zero to turn off
     */
    public int damage=50;
    
    /**
     * time to wait before explosion hapens after hit or activation
     */
    public int preExplosionWait=100;
    
    
}
