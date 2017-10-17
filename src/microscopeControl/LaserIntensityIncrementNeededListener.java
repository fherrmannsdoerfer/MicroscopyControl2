package microscopeControl;
import java.util.EventListener;

public interface LaserIntensityIncrementNeededListener extends EventListener{
	public void LaserIntensityIncrementNeededEventOccurred(LaserIntensityIncrementNeededEvent event);
}
