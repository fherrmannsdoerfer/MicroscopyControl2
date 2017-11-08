package dataTypes;

public class XYStagePosition {
	private double xPos;
	private double yPos;
	
	public XYStagePosition(double xPos, double yPos){
		this.xPos = xPos;
		this.yPos = yPos;
	}
		
	public double getxPos() {
		return xPos;
	}

	public void setxPos(double xPos) {
		this.xPos = xPos;
	}

	public double getyPos() {
		return yPos;
	}

	public void setyPos(double yPos) {
		this.yPos = yPos;
	}

	@Override
	public String toString(){
		return "X: "+xPos+ " Y: "+ yPos;
	}
}
