/*
 * Copyright 2012 Benjamin Glatzel <benjamin.glatzel@me.com>
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
package terasology.boomLibary;

import org.terasology.entitySystem.systems.ComponentSystem;




/**
 * @author Esa-Petri Tirkkonen <esereja@yahoo.co.uk>
 */
public class Commands implements ComponentSystem{

	//TODO add multiplayer versions of commands
	
	/* @Command(shortDescription = "Restores your health to max")
    public void health() {
        LocalPlayer localPlayer = CoreRegistry.get(LocalPlayer.class);
        HealthComponent health = localPlayer.getCharacterEntity().getComponent(HealthComponent.class);
        health.currentHealth = health.maxHealth;
        localPlayer.getCharacterEntity().send(new FullHealthEvent(localPlayer.getCharacterEntity()));
        localPlayer.getCharacterEntity().saveComponent(health);
    }

    @Command(shortDescription = "Restores your health by an amount")
    public void health(@CommandParam("amount") int amount) {
        LocalPlayer localPlayer = CoreRegistry.get(LocalPlayer.class);
        HealthComponent health = localPlayer.getCharacterEntity().getComponent(HealthComponent.class);
        health.currentHealth = amount;
        if (health.currentHealth >= health.maxHealth) {
            health.currentHealth = health.maxHealth;
            localPlayer.getCharacterEntity().send(new FullHealthEvent(localPlayer.getCharacterEntity()));
        } else if (health.currentHealth <= 0) {
            health.currentHealth = 0;
            localPlayer.getCharacterEntity().send(new NoHealthEvent(localPlayer.getCharacterEntity(), null));
        } else {
            localPlayer.getCharacterEntity().send(new HealthChangedEvent(localPlayer.getCharacterEntity(), health.currentHealth));
        }

        localPlayer.getCharacterEntity().saveComponent(health);
    }

    @Command(shortDescription = "Set max health")
    public void setMaxHealth(@CommandParam("max") int max) {
        LocalPlayer localPlayer = CoreRegistry.get(LocalPlayer.class);
        HealthComponent health = localPlayer.getCharacterEntity().getComponent(HealthComponent.class);
        health.maxHealth = max;
        localPlayer.getCharacterEntity().saveComponent(health);
    }

    @Command(shortDescription = "Set regen rate")
    public void setRegenRaterate(@CommandParam("rate") float rate) {
        LocalPlayer localPlayer = CoreRegistry.get(LocalPlayer.class);
        HealthComponent health = localPlayer.getCharacterEntity().getComponent(HealthComponent.class);
        health.regenRate = rate;
        localPlayer.getCharacterEntity().saveComponent(health);
    }

    @Command(shortDescription = "Show your health")
    public String showHealth() {
        LocalPlayer localPlayer = CoreRegistry.get(LocalPlayer.class);
        HealthComponent health = localPlayer.getCharacterEntity().getComponent(HealthComponent.class);
        return new String("Your health:" + health.currentHealth + " max:" + health.maxHealth + " regen:" + health.regenRate );
    }

    @Command(shortDescription = "Set ground friction")
    public void setGroundFriction(@CommandParam("amount") float amount) {
        LocalPlayer localPlayer = CoreRegistry.get(LocalPlayer.class);
        CharacterMovementComponent move = localPlayer.getCharacterEntity().getComponent(CharacterMovementComponent.class);
        move.groundFriction = amount;
        localPlayer.getCharacterEntity().saveComponent(move);
    }

    @Command(shortDescription = "Set max ground speed", helpText = "Set maxGroundSpeed")
    public void setMaxGroundSpeed(@CommandParam("amount") float amount) {
        LocalPlayer localPlayer = CoreRegistry.get(LocalPlayer.class);
        CharacterMovementComponent move = localPlayer.getCharacterEntity().getComponent(CharacterMovementComponent.class);
        move.maxGroundSpeed = amount;
        localPlayer.getCharacterEntity().saveComponent(move);
    }

    @Command(shortDescription = "Set max ghost speed")
    public void setMaxGhostSpeed(@CommandParam("amount") float amount) {
        LocalPlayer localPlayer = CoreRegistry.get(LocalPlayer.class);
        CharacterMovementComponent move = localPlayer.getCharacterEntity().getComponent(CharacterMovementComponent.class);
        move.maxGhostSpeed = amount;
        localPlayer.getCharacterEntity().saveComponent(move);
    }

    @Command(shortDescription = "Set jump speed")
    public void setJumpSpeed(@CommandParam("amount") float amount) {
        LocalPlayer localPlayer = CoreRegistry.get(LocalPlayer.class);
        CharacterMovementComponent move = localPlayer.getCharacterEntity().getComponent(CharacterMovementComponent.class);
        move.jumpSpeed = amount;
        localPlayer.getCharacterEntity().saveComponent(move);
    }

    @Command(shortDescription = "Show your Movement stats")
    public String showMovement() {
        LocalPlayer localPlayer = CoreRegistry.get(LocalPlayer.class);
        CharacterMovementComponent move = localPlayer.getCharacterEntity().getComponent(CharacterMovementComponent.class);
       return new String("Your groundFriction:" + move.groundFriction + " maxGroudspeed:" + move.maxGroundSpeed + " JumpSpeed:"
                + move.jumpSpeed + " maxWaterSpeed:" + move.maxWaterSpeed + " maxGhostSpeed:" + move.maxGhostSpeed + " SlopeFactor:"
                + move.slopeFactor + " runFactor:" + move.runFactor);
    }

    @Command(shortDescription = "Go really fast")
    public void hspeed() {
        LocalPlayer localPlayer = CoreRegistry.get(LocalPlayer.class);
        CharacterMovementComponent move = localPlayer.getCharacterEntity().getComponent(CharacterMovementComponent.class);
        move.maxGhostSpeed = 50f;
        move.jumpSpeed = 24f;
        move.maxGroundSpeed = 20f;
        move.maxWaterSpeed = 12f;
        localPlayer.getCharacterEntity().saveComponent(move);
    }

    @Command(shortDescription = "Jump really high")
    public void hjump() {
        LocalPlayer localPlayer = CoreRegistry.get(LocalPlayer.class);
        HealthComponent health = localPlayer.getCharacterEntity().getComponent(HealthComponent.class);
        CharacterMovementComponent move = localPlayer.getCharacterEntity().getComponent(CharacterMovementComponent.class);
        move.jumpSpeed = 75f;
        health.fallingDamageSpeedThreshold = 85f;
        health.excessSpeedDamageMultiplier = 2f;
        localPlayer.getCharacterEntity().saveComponent(health);
        localPlayer.getCharacterEntity().saveComponent(move);
    }

    @Command(shortDescription = "Restore normal speed values")
    public void restoreSpeed() {
        LocalPlayer localPlayer = CoreRegistry.get(LocalPlayer.class);
        CharacterMovementComponent move = localPlayer.getCharacterEntity().getComponent(CharacterMovementComponent.class);
        move.maxGhostSpeed = 3f;
        move.jumpSpeed = 12f;
        move.maxGroundSpeed = 5f;
        move.maxWaterSpeed = 2f;
        move.runFactor = 1.5f;
        move.stepHeight = 0.35f;
        move.slopeFactor = 0.6f;
        move.groundFriction = 8.0f;
        move.distanceBetweenFootsteps = 1f;
        localPlayer.getCharacterEntity().saveComponent(move);
    }

    @Command(shortDescription = "Reduce the player's health by an amount")
    public void damage(@CommandParam("amount") int amount) {
        LocalPlayer localPlayer = CoreRegistry.get(LocalPlayer.class);
        HealthComponent health = localPlayer.getCharacterEntity().getComponent(HealthComponent.class);
        health.currentHealth -= amount;
        if (health.currentHealth >= health.maxHealth) {
            health.currentHealth = health.maxHealth;
            localPlayer.getCharacterEntity().send(new FullHealthEvent(localPlayer.getCharacterEntity()));
        } else if (health.currentHealth <= 0) {
            health.currentHealth = 0;
            localPlayer.getCharacterEntity().send(new NoHealthEvent(localPlayer.getCharacterEntity(), null));
        } else {
            localPlayer.getCharacterEntity().send(new HealthChangedEvent(localPlayer.getCharacterEntity(), health.currentHealth));
        }

        localPlayer.getCharacterEntity().saveComponent(health);
    }*/

	@Override
	public void initialise() {
	}

	@Override
	public void shutdown() {
	}
    
	//TODO Add commands to kick player from server, and ban him
    //TODO command to see coordinates of player
}
