package dataTypes;

public class ROIParameters {
	private int roiWidth;
	private int roiHeight;
	private int shiftX;
	private int shiftY;
	private int posX;
	private int posY;
	
	public ROIParameters(int roiWidth, int roiHeight, int shiftX, int shiftY, int posX, int posY){
		this.roiWidth = roiWidth;
		this.roiHeight = roiHeight;
		this.shiftX = shiftX;
		this.shiftY = shiftY;
		this.posX = posX;
		this.posY = posY;
	}
	public int getPosY() {
		return posY;
	}
	public void setPosY(int posY) {
		this.posY = posY;
	}
	public int getPosX() {
		return posX;
	}
	public void setPosX(int posX) {
		this.posX = posX;
	}
	public int getShiftY() {
		return shiftY;
	}
	public void setShiftY(int shiftY) {
		this.shiftY = shiftY;
	}
	public int getShiftX() {
		return shiftX;
	}
	public void setShiftX(int shiftX) {
		this.shiftX = shiftX;
	}
	public int getRoiHeight() {
		return roiHeight;
	}
	public void setRoiHeight(int roiHeight) {
		this.roiHeight = roiHeight;
	}
	public int getRoiWidth() {
		return roiWidth;
	}
	public void setRoiWidth(int roiWidth) {
		this.roiWidth = roiWidth;
	}

}
