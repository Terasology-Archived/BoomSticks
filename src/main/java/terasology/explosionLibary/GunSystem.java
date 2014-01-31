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

package terasology.explosionLibary;

import java.util.Collection;
import java.util.Set;

import javax.vecmath.Vector3f;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.CoreRegistry;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.entitySystem.systems.ComponentSystem;
import org.terasology.entitySystem.systems.In;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.logic.inventory.SlotBasedInventoryManager;
import org.terasology.logic.location.LocationComponent;
import org.terasology.monitoring.PerformanceMonitor;
import org.terasology.rendering.cameras.Camera;
import org.terasology.rendering.world.WorldRenderer;
import org.terasology.utilities.random.FastRandom;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.family.BlockFamily;

import terasology.explosionLibary.explosions.DisintegrationActionComponent;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

/**
 * @author  <immortius@gmail.com>
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class GunSystem implements ComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(GunSystem.class);

    @In
    private EntityManager entityManager;

    @In
    private BlockManager blockMan;

    @In
    private WorldProvider worldProvider;

    @In
    private SlotBasedInventoryManager invMan;

    private final FastRandom random = new FastRandom();
    private DefaultProjectileFactory factory;

    //private long tick;
    //private long classLastTick;

    /**
     * Cache containing Spawnable prefabs mapped to their spawnable "tags" - each tag may reference multiple prefabs
     * and each prefab may have multiple tags
     * */
    private SetMultimap<String, Prefab> typeLists = HashMultimap.create();

    @Override
    public void initialise() {
        factory = new DefaultProjectileFactory();
        factory.setEntityManager(entityManager);
        factory.setRandom(random);
        cacheTypes();
    }

    /**
     * Looks through all loaded prefabs and determines which are spawnable, then stores them in a local SetMultimap
     * This method should be called (or adders/removers?) whenever available spawnable prefabs change, if ever
     */
    public void cacheTypes() {
        Collection<Prefab> projectilePrefabs = CoreRegistry.get(PrefabManager.class).listPrefabs(ProjectileComponent.class);
        logger.info("Grabbed all projectile entities - got: {}", projectilePrefabs);
        for (Prefab prefab : projectilePrefabs) {
            logger.info("Prepping a projectile prefab: {}", prefab);
            ProjectileComponent projectileComponent = prefab.getComponent(ProjectileComponent.class);

            // Support multiple tags per prefab ("Goblin", "Spearman", "Goblin Spearman", "QuestMob123")
            for (String tag : projectileComponent.tags) {
                logger.info("Adding tag: {} with prefab {}", tag, prefab);
                typeLists.put(tag, prefab);
            }
        }

        logger.info("Full typeLists: {}", typeLists);
    }

    @Override
    public void shutdown() {
    }
    
    
    
    @ReceiveEvent
	public void onActivate(ActivateEvent event, EntityRef entity,
			GunComponent gunComp){
    	logger.info("Lets rock!");
    	
    	 PerformanceMonitor.startActivity("Spawn projectile");
         try {

			int projTypes = gunComp.types.size();
			if (projTypes == 0) {
				logger.warn("Spawner has no types, sad - stopping this loop iteration early :-(");
				return;
			}

			//TODO this will break soon
			//get camera to know where player is looking
            Camera playerCamera = CoreRegistry.get(WorldRenderer.class).getActiveCamera();

            Vector3f spawnPos = new Vector3f(playerCamera.getPosition().x + playerCamera.getViewingDirection().x * 1.5f,
                    playerCamera.getPosition().y + playerCamera.getViewingDirection().y * 1.5f,
                    playerCamera.getPosition().z + playerCamera.getViewingDirection().z * 1.5f
            );

            Vector3f impulseVector = new Vector3f(playerCamera.getViewingDirection());
            impulseVector.scale(gunComp.launchForce);
            
            
			// check that spawning position doesn't have block or entities in it.
            //if true activate projectile at position
			if (!(worldProvider.getBlock(
					new Vector3f(spawnPos.x, spawnPos.y, spawnPos.z)).isPenetrable() 
					&& validateSpawnPos(new Vector3f(spawnPos.x, spawnPos.y, spawnPos.z), 1, 1, 1))) {
					// TODO do damage to what ever is in
					logger.warn("projectile spawning point block was full");
				return;
			}

			// test the cache for matching prefabs
			for(int i=0;i<gunComp.types.size();i++){
				String chosenSpawnerType = gunComp.types.get(i);
				Set<Prefab> choosenType = typeLists.get(chosenSpawnerType);
				logger.info("Picked type {} which returned {} prefabs",chosenSpawnerType, choosenType.size());
				if (choosenType.size() == 0) {
					logger.warn("Type {} wasn't found, sad :-( Won't spawn anything this time",	chosenSpawnerType);
					return;
				}

				// Now actually pick one of the matching prefabs randomly and that's
				// what we'll try to spawn
				for(int a=0;a<choosenType.size();a++){
					Object[] prefabArray = choosenType.toArray();
					Prefab chosenPrefab = (Prefab) prefabArray[a];
					logger.info("Picked index {} of types {} which is a {}, to spawn at {}",a, chosenSpawnerType, chosenPrefab,spawnPos);

					// See if the chosen projectile needs an item that it consumes on
					// spawning and if the gun has it in invemtory can provide it
					String neededItem = chosenPrefab.getComponent(ProjectileComponent.class).itemToConsume;
					if (neededItem != null) {
						logger.info("This spawnable has an item demand on spawning: {} - Does its spawner have an inventory?", neededItem);
						if (entity.hasComponent(InventoryComponent.class)) {
							//InventoryComponent invComp = entity.getComponent(InventoryComponent.class);
							logger.info("Yes - it has an inventory - entity: {}", entity);

							BlockFamily neededFamily = blockMan	.getBlockFamily(neededItem);
							logger.info("Needed block family: {}", neededFamily);
							EntityRef firstSlot = invMan.getItemInSlot(entity, 0);
							logger.info("First slot {}", firstSlot);

							ItemComponent item = firstSlot.getComponent(ItemComponent.class);
							if (item != null) {
								logger.info("Got its ItemComponent: {} and its name: {}",item, item.name);
								if (neededFamily.getDisplayName().equals(item.name)) {
									logger.info("Found the item needed to spawn stuff! Decrementing by 1 then spawning");

									EntityRef result = invMan.removeItem(entity,firstSlot, 1);
									logger.info("Result from decrementing: {}", result);
								} else {
									logger.info("But that item didn't match what the spawn needed to consume. No spawn!");
								continue;
								}
							} else {
								continue;
							}

					logger.info("Successfully decremented an existing item stack - accepting item-based spawning");
				} else {
					logger.info("Nope - no inventory to source material from, cannot spawn that :-(");
					continue;
				}
			}

			// Finally create the Spawnable. Assign parentage so we can tie
			// Spawnables to their Spawner if needed
			EntityRef newSpawnableRef = factory.generate(spawnPos, chosenPrefab);
			ProjectileComponent newSpawnable = newSpawnableRef.getComponent(ProjectileComponent.class);
			newSpawnable.parent = entity;
			newSpawnable.impulse=impulseVector;
			newSpawnable.location=spawnPos;
			}
				}
         } finally {
             PerformanceMonitor.endActivity();
         }
    }

    /**
     * Validates a position as open enough to fit a Spawnable (or something else?) of the given dimensions
     *
     * @param pos position to start from
     * @param spawnableHeight height of what we want to fit into the space
     * @param spawnableDepth depth of what we want to fit into the space
     * @param spawnableWidth width of what we want to fit into the space
     * @return true if the spawn position will fit the Spawnable
     */
    private boolean validateSpawnPos(Vector3f pos, int spawnableHeight, int spawnableDepth, int spawnableWidth) {
        // TODO: Fill in with clean code or even switch to a generic utility method.
        // TODO: Could enhance this further with more suitability like ground below, water/non-water, etc. Just pass the whole prefab in
        return true;
    }
    
}
