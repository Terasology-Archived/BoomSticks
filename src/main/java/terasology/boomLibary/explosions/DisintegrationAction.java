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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityBuilder;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.ComponentSystem;
import org.terasology.entitySystem.systems.In;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.health.DoDamageEvent;
import org.terasology.logic.health.DoDestroyEvent;
import org.terasology.logic.health.EngineDamageTypes;
import org.terasology.logic.health.HealthComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.Vector3i;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.math.TeraMath;

import terasology.boomLibary.ExplosionLibaryDamageTypes;
import javax.vecmath.Vector3f;

/**
 * @author Esa-Petri <esereja@yahoo.co.uk>
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class DisintegrationAction implements ComponentSystem, UpdateSubscriberSystem {
	private static final Logger logger = LoggerFactory.getLogger(DisintegrationAction.class);
	
	@In
	private WorldProvider worldProvider;

	@In
	private BlockEntityRegistry blockEntityRegistry;
	
	@In
	private BlockManager  blockManager;

	@In
	private EntityManager entityManager;
	
    @In
    private Time timer;

	
	@Override
	public void initialise() {
	}

	@Override
	public void shutdown() {
	}
	
	@ReceiveEvent
	public void onActivate(ActivateEvent event, EntityRef entity,
			DisintegrationActionComponent disintegrationComp) {

		Vector3f origin = null;
		//find who activated
		switch (disintegrationComp.relativeTo) {
		case Self:
			LocationComponent loc = entity
					.getComponent(LocationComponent.class);
			if (loc != null) {
				origin = loc.getWorldPosition();
			}
			break;
		case Instigator:
			origin = event.getInstigatorLocation();
			break;
		default:
			origin = event.getTargetLocation();
			break;
		}

		//no origin stop
		if (origin == null) {
			return;
		}
		
		disintegrationComp.running=true;
		disintegrationComp.lastCheckTime=timer.getGameTimeInMs();
		disintegrationComp.origin=origin;
		//logger.info("starting explosion,comp {}", disintegrationComp.lastCheckTime);
		entity.saveComponent(disintegrationComp);
		disintegrationComp.running=true;
    	
    }

	@Override
	public void update(float delta) {
		for (EntityRef entity : entityManager.getEntitiesWith(DisintegrationActionComponent.class,
                LocationComponent.class)){

			//TODO remove useless logging
		DisintegrationActionComponent disintegrationComp = entity.getComponent(DisintegrationActionComponent.class);
		if(disintegrationComp.running==false){
			logger.info("skip, runnning is false. component id {}", entity.getId() );
			continue;
		}
		 
		//pre explosion wait
		if(disintegrationComp.preExplosionWait>(timer.getGameTimeInMs()-disintegrationComp.lastCheckTime)){
			logger.info("starting explosion,time2 {}, result {}", timer.getGameTimeInMs(),(timer.getGameTimeInMs()-disintegrationComp.lastCheckTime));
			continue;
		}
		logger.info("starting explosion,time2 {}, result {}", timer.getGameTimeInMs(),(timer.getGameTimeInMs()-disintegrationComp.lastCheckTime));
		//logger.info("starting explosion, preExplosionWait was {}", disintegrationComp.preExplosionWait);
		//logger.info("starting explosion,comp {}", disintegrationComp.lastCheckTime);
		
				
    	Vector3i blockPos = new Vector3i();
    	int x1 = (int)(disintegrationComp.X);
    	int z1 = (int)(disintegrationComp.Y);
    	int y1 = (int)(disintegrationComp.Z);
    	
    	
		//go trough 3d world as array of blocks
    	for (int ix = 0 - x1; ix < x1; ix++) {
			for (int iy = 0 - z1; iy < z1; iy++) {
				for (int iz = 0 - y1; iz < y1; iz++) {
					Vector3f target = new Vector3f(disintegrationComp.origin);

					target.x += ix;
					target.y += iy;
					target.z += iz;

					blockPos.set((int) target.x, (int) target.y, (int) target.z);
					Block currentBlock = worldProvider.getBlock(blockPos);

					float result1=0;
					switch (disintegrationComp.type) {
					case 2:// ellipsoid
						result1=((ix*ix)/(disintegrationComp.X*disintegrationComp.X)+(iy*iy)/(disintegrationComp.Y*disintegrationComp.Y)+(iz*iz)/(disintegrationComp.Z*disintegrationComp.Z));	
						if(result1>1){
							continue;
						}
					case 1:// Ball
						if ((TeraMath.fastAbs(ix) + TeraMath.fastAbs(iy) + TeraMath.fastAbs(iz) > x1)) {
							continue;
						}
					case 0:
					default:
						/* PHYSICS */
						if (currentBlock.isDestructible()) {
							
							if(disintegrationComp.dropBlocks){
							blockEntityRegistry.getEntityAt(blockPos).send(
									new DoDamageEvent(1000,
											EngineDamageTypes.EXPLOSIVE.get(),
											EntityRef.NULL));
							}else{
								blockEntityRegistry.getEntityAt(blockPos).send(new DoDestroyEvent(EntityRef.NULL,EntityRef.NULL,ExplosionLibaryDamageTypes.DISINTEGRATION.get()));
							}
						}
					}
				}
			}
		}
		
		//damage calculation to entities
		if (disintegrationComp.damage != 0) {
			for (EntityRef entity1 : entityManager.getEntitiesWith(
					HealthComponent.class, LocationComponent.class)) {
				LocationComponent location1 = entity1
						.getComponent(LocationComponent.class);
				Vector3f worldPos1 = location1.getWorldPosition();

				// Skip this if not in a loaded chunk
				if (!worldProvider.isBlockRelevant(worldPos1)) {
					continue;
				}

				Vector3f dist = new Vector3f(worldPos1);
				dist.sub(disintegrationComp.origin);

				switch (disintegrationComp.type) {
				case 2:// ellipsoid
					float result1 = 
					        ( (dist.getX() * dist.getX()) / (disintegrationComp.X * disintegrationComp.X)
							+ (dist.getY() * dist.getY()) / (disintegrationComp.Y * disintegrationComp.Y)
							+ (dist.getZ() * dist.getZ()) / (disintegrationComp.Z * disintegrationComp.Z));
					if (result1 > 1) {
						continue;
					}
				case 1:// Ball
					if ((dist.getX() + dist.getY() + dist.getZ() > x1)) {
						continue;
					}
				case 0:
				default:
				}
				entity1.send(new DoDamageEvent(disintegrationComp.damage,
						ExplosionLibaryDamageTypes.DISINTEGRATION.get(), entity));

			}
		}
		//some smoke
		if(disintegrationComp.ParticleEffects){
			EntityBuilder builder = entityManager.newBuilder("engine:smokeExplosion");
			builder.getComponent(LocationComponent.class).setWorldPosition(disintegrationComp.origin);
			builder.build();
		}
		
		//logger.info("switch off");
		disintegrationComp.running=false;
		disintegrationComp.origin=null;
		entity.saveComponent(disintegrationComp);
	}
	}
}
