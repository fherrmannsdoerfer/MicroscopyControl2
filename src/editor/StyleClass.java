package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.io.Serializable;
//class that contains all colors and spaces used in the editor. All of these attributes
//are stored at this location only
public class StyleClass implements Serializable{
	private Color colorMicroscope = new Color(171,65,152);
	private Color colorStainingRobot = new Color(101,194,148);
	private Color colorLoop = new Color(88,197,199);
	private Color colorComment = new Color(100,100,100);
	private Color batchProcessing = new Color(180,180,180);
	private int widthProcessingStepsPanel = 300;
	private int heightProcessingStepsPanel = 60;
	private int leftIndent = 15;
	private int rightIndent = 15;
	private int upperIndent = 0;
	private int lowerIndent = 10;
	private int heightEditor = 1000;
	private Color removeButtonColor = new Color(62,18,22);
	private Dimension pathFields = new Dimension(400,22);
	private Dimension optionPaneWidth = new Dimension(450,999);
	private Dimension sizeEditor = new Dimension(1450, heightEditor);
	private int defaultIndentation = 35;
	private Dimension dimensionAvailableModules = new Dimension(350,heightEditor);
	private Dimension dimensionSelectedModules = new Dimension(widthProcessingStepsPanel + 300,heightEditor);
	private Dimension dimensionParameters = new Dimension(500,heightEditor);
	
	public Color getColorComment() {
		return colorComment;
	}
	public Color getColorMicroscope() {
		return colorMicroscope;
	}
	public void setColorMicroscope(Color colorMicroscope) {
		this.colorMicroscope = colorMicroscope;
	}
	public Color getColorStainingRobot() {
		return colorStainingRobot;
	}
	public void setColorStainingRobot(Color colorStainingRobot) {
		this.colorStainingRobot = colorStainingRobot;
	}
	public Color getColorLoop() {
		return colorLoop;
	}
	public void setColorLoop(Color colorLoop) {
		this.colorLoop = colorLoop;
	}
	public Color getBatchProcessingColor() {
		return batchProcessing;
	}
	public int getWidthEditorModules() {
		return widthProcessingStepsPanel;
	}
	public void setWidthProcessingStepsPanel(int widthProcessingStepsPanel) {
		this.widthProcessingStepsPanel = widthProcessingStepsPanel;
	}
	public int getHeightProcessingStepsPanel() {
		return heightProcessingStepsPanel;
	}
	public void setHeightProcessingStepsPanel(int heightProcessingStepsPanel) {
		this.heightProcessingStepsPanel = heightProcessingStepsPanel;
	}
	public int getLeftIndent() {
		return leftIndent;
	}
	public void setLeftIndent(int leftIndent) {
		this.leftIndent = leftIndent;
	}
	public int getRightIndent() {
		return rightIndent;
	}
	public void setRightIndent(int rightIndent) {
		this.rightIndent = rightIndent;
	}
	public int getUpperIndent() {
		return upperIndent;
	}
	public void setUpperIndent(int upperIndent) {
		this.upperIndent = upperIndent;
	}
	public int getLowerIndent() {
		return lowerIndent;
	}
	public void setLowerIndent(int lowerIndent) {
		this.lowerIndent = lowerIndent;
	}
	public Color getRemoveButtonColor() {
		return removeButtonColor;
	}
	public void setRemoveButtonColor(Color removeButtonColor) {
		this.removeButtonColor = removeButtonColor;
	}
	public Dimension getDimensionPathFields() {
		return pathFields;
	}
	public void setDimensionPathFields(Dimension widthPathFields) {
		this.pathFields = widthPathFields;
	}
	public Dimension getDimensionOptionPane() {
		return optionPaneWidth;
	}
	public int getDefaultIndentation() {
		return defaultIndentation;
	}
	public Dimension getDimensionAvailableModules() {
		return dimensionAvailableModules;
	}
	public void setDimensionAvailableModules(Dimension dimensionAvailableModules) {
		this.dimensionAvailableModules = dimensionAvailableModules;
	}
	public Dimension getDimensionSelectedModules() {
		return dimensionSelectedModules;
	}
	public void setDimensionSelectedModules(Dimension dimensionSelectedModules) {
		this.dimensionSelectedModules = dimensionSelectedModules;
	}
	public Dimension getDimensionParameters() {
		return dimensionParameters;
	}
	public void setDimensionParameters(Dimension dimensionParameters) {
		this.dimensionParameters = dimensionParameters;
	}
	public Dimension getSizeEditor() {
		return sizeEditor;
	}
	public void setSizeEditor(Dimension sizeEditor) {
		this.sizeEditor = sizeEditor;
	}

}
