package microscopeControl;
import java.util.EventObject;

//Event which is sent whenever the intensity of a laser needs to be incremented
public class LaserIntensityIncrementNeededEvent extends EventObject{
	
	private double increment;
	private String laserName;
	
	public LaserIntensityIncrementNeededEvent(Object source,String laserName, double increment) {
		super(source);
		this.increment = increment;
		this.laserName = laserName;
	}
	
	public double getIncrement(){
		return increment;
	}
	public String getName(){
		return laserName;
	}

}
