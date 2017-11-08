package dataTypes;

public class CameraParameters {
	private double exposureTime;
	private int emGain;
	private int nbrFrames;
	private boolean shutterOpen;
	
	public CameraParameters(){
		
	}
	public CameraParameters(double exposureTime, int emGain, int nbrFrames, boolean shutterOpen){
		this.exposureTime = exposureTime;
		this.emGain = emGain;
		this.nbrFrames = nbrFrames;
		this.shutterOpen = shutterOpen;
	}
	
	public double getExposureTime() {
		return exposureTime;
	}
	public void setExposureTime(double exposureTime) {
		this.exposureTime = exposureTime;
	}
	public int getEmGain() {
		return emGain;
	}
	public void setEmGain(int emGain) {
		this.emGain = emGain;
	}
	public int getNbrFrames() {
		return nbrFrames;
	}
	public void setNbrFrames(int nbrFrames) {
		this.nbrFrames = nbrFrames;
	}
	public boolean isShutterOpen() {
		return shutterOpen;
	}
	public void setShutterOpen(boolean shutterOpen) {
		this.shutterOpen = shutterOpen;
	}
}
