package terasology.boomLibary;

import java.util.List;

import org.terasology.entitySystem.Component;

import com.google.common.collect.Lists;

public class GunComponent implements Component {

    /** Types of Projectiles this ProjectileSpawner can spawn */
    public List<String> types = Lists.newArrayList();
    
    /**
     * launch force in Newtons
     */
    public float launchForce=50;
    
}