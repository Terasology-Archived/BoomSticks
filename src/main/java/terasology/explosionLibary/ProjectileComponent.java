package terasology.explosionLibary;

import java.util.Collections;
import java.util.Set;

import javax.vecmath.Vector3f;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;

public class ProjectileComponent implements Component {

    /** What category is this spawnable. TODO: Change to a set of String "tags" */
    public String type = "undefined";
    public Set<String> tags = Collections.emptySet();
    
    public Vector3f impulse=null;
    public Vector3f location=null;

    /** Optional: If spawner is attached to an inventory and this is non-null require that item present and decrement */
    public String itemToConsume;

    /** What made this Spawnable? */
    public EntityRef parent = null;
    
    /**
     * weight in grams, setting this to zero causes gravity calculation be ignored. 
     * minus values crate anti gravitationol effect
     */
    public float weight=2;
    
    /**
     *  set how strongly friction affects projectile, negative values cause accelaration. zero disables calculation
     */
    public float airFriction=0;
    
    /**
     * if projectile accelerated by propelant set this to non zero value, minus values cause wrong direction.
     */
    public float propelantForce=0;
}