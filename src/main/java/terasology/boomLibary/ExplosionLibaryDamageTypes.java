package terasology.boomLibary;

import org.terasology.asset.Assets;
import org.terasology.entitySystem.prefab.Prefab;

public enum ExplosionLibaryDamageTypes {
	
	DISINTEGRATION("explosionLibary:disintegrationDamage");
	
    private String prefabId;

    private ExplosionLibaryDamageTypes(String prefabId) {
        this.prefabId = prefabId;
    }

    public Prefab get() {
        return Assets.getPrefab(prefabId);
    }

}
