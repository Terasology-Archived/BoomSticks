package terasology.boomLibary;

import org.terasology.engine.Time;
import org.terasology.entitySystem.systems.In;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;

@RegisterSystem(RegisterMode.AUTHORITY)
public class Sheluder implements UpdateSubscriberSystem {
	private Runnable runnable;
	private long timeToRun;
	@In
	private Time time;

	@Override
	public void initialise() {
	}

	@Override
	public void shutdown() {
	}

	@Override
	public void update(float delta) {
		if (null == this.runnable) {
			return;
		}
		if (timeToRun > time.getGameTimeInMs()) {
			Runnable runnable = this.runnable;
			this.runnable = null;
			runnable.run();
		}
	}

	public void scheduleRunnable(Runnable runnable, long afterSoManyMilliseconds) {
		this.runnable = runnable;
		this.timeToRun = afterSoManyMilliseconds + time.getGameTimeInMs();
	}
}
