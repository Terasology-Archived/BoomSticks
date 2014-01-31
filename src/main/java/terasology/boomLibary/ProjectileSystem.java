package terasology.boomLibary;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Vector3f;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.CoreRegistry;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.ComponentSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.characters.MovementMode;
import org.terasology.logic.characters.events.HorizontalCollisionEvent;
import org.terasology.logic.characters.events.VerticalCollisionEvent;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.WorldProvider;

public class ProjectileSystem implements ComponentSystem, UpdateSubscriberSystem {

	private static final Logger logger = LoggerFactory.getLogger(ProjectileSystem.class);
    private WorldProvider worldProvider;
    private EntityManager entityManager;
    //private Random random = new FastRandom();
    //private Time time;

	@Override
	public void initialise() {
		//TODO Remove this
		logger.info("projevctile inited");
		entityManager = CoreRegistry.get(EntityManager.class);
		//time = CoreRegistry.get(Time.class);
		worldProvider = CoreRegistry.get(WorldProvider.class);
    }

	@Override
	public void shutdown() {		
	}
	
	@Override
	public void update(float delta) {
		for (EntityRef entity : entityManager.getEntitiesWith(ProjectileComponent.class, CharacterMovementComponent.class,
                LocationComponent.class)){
			logger.info("hello world");
            ProjectileComponent projComponent=entity.getComponent(ProjectileComponent.class);
            CharacterMovementComponent movComponent=entity.getComponent(CharacterMovementComponent.class);
            LocationComponent locComponent= entity.getComponent(LocationComponent.class);
            
            Vector3f worldPos = locComponent.getWorldPosition();
            
            // Skip this if not in a loaded chunk
            if (!worldProvider.isBlockRelevant(worldPos)) {
            	//if not in loaded chunk destroy
            	logger.info("projectile out of reach, destroyed");
            	entity.destroy();
                continue;
            }
            
            //set flying
            movComponent.grounded=false;
            movComponent.footstepDelta=0;
            movComponent.groundFriction=projComponent.airFriction;
            movComponent.maxGroundSpeed=20;
            entity.saveComponent(movComponent);
            
            
            //TODO calculate gravitation effect to flying
            float yaw = (float) Math.atan2(projComponent.impulse.x, projComponent.impulse.z);
            AxisAngle4f axisAngle = new AxisAngle4f(0, 1, 0, yaw);
            locComponent.getLocalRotation().set(axisAngle);
            entity.saveComponent(locComponent);
            
            entity.send(new CharacterMoveInputEvent(0, 0, 0, projComponent.impulse, false, false));
		}
	}
	
	@ReceiveEvent(components = {ProjectileComponent.class})
    public void onBump(HorizontalCollisionEvent event, EntityRef entity) {
    	entity.send(new ActivateEvent(entity, entity) );
    }
    
    @ReceiveEvent(components = {ProjectileComponent.class})
    public void onBump(VerticalCollisionEvent event, EntityRef entity) {
    	entity.send(new ActivateEvent(entity, entity) );
    }
    
}
