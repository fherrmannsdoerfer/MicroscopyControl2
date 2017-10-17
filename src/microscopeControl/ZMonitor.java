package microscopeControl;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import java.util.Collections;
import java.util.Vector;

import mmcorej.CMMCore;

//Monitor that needs the name of the device to be monitored
//and will then show the updated position each second
public class ZMonitor extends JPanel {
	DrawPanel panel;
	int width; //DrawPanel width 
	int height; //
	double slope; //slope is needed to map the range of the data to the height of the panel
	double minValue; //minimal value of the visible subset of points
	double maxValue; //maximal value of the visible subset of points
	int nbrPoints; //number of points added
	int nbrVisiblePoints; // number of points that fit on the panel based on given distance between the points and the current width of the panel
	int distBetweenPoints = 2; // Distance in px between two consecutive y values in the graph
	Vector<Double> listPoints = new Vector<Double>(); // vector that contains all added points, this should be truncated from time to time
	JLabel lblYHigh; // labels for ranges
	JLabel lblYMed;
	JLabel lblYLow;
	JLabel lblXHigh;
	JLabel lblXMed;
	JLabel lblXLow;
	double roundToYAxis = 0.1;
	int timeIntervalX = 1;
	int counter = 0;
	Thread UpdateZPositionThread;
	boolean threadShouldStayRunning = true;
	MainFrame mf;
	/**
	 * Create the panel.
	 */
	
	public ZMonitor(MainFrame mf) {
		this.mf = mf;
		setBorder(new TitledBorder(null, "Z Monitor", TitledBorder.LEADING, TitledBorder.TOP, mf.getTitelFont(), null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 356, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		Box verticalBox = Box.createVerticalBox();
		GridBagConstraints gbc_verticalBox = new GridBagConstraints();
		gbc_verticalBox.fill = GridBagConstraints.VERTICAL;
		gbc_verticalBox.insets = new Insets(0, 0, 5, 5);
		gbc_verticalBox.gridx = 0;
		gbc_verticalBox.gridy = 0;
		add(verticalBox, gbc_verticalBox);
// Labels and spacer for y axis		
		lblYHigh = new JLabel("0");
		verticalBox.add(lblYHigh);		
		Component verticalGlue = Box.createVerticalGlue();
		verticalBox.add(verticalGlue);	
		lblYMed = new JLabel("0");
		verticalBox.add(lblYMed);	
		Component verticalGlue_1 = Box.createVerticalGlue();
		verticalBox.add(verticalGlue_1);	
		lblYLow = new JLabel("0");
		verticalBox.add(lblYLow);
//DrawPanel instance which draws the graph		
		panel = new DrawPanel();
		panel.setBackground(Color.black);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
			
		Box horizontalBox = Box.createHorizontalBox();
		GridBagConstraints gbc_horizontalBox = new GridBagConstraints();
		gbc_horizontalBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_horizontalBox.gridx = 1;
		gbc_horizontalBox.gridy = 1;
		add(horizontalBox, gbc_horizontalBox);
// Labels and spacer for x axis			
		lblXLow = new JLabel("0");
		horizontalBox.add(lblXLow);		
		Component horizontalGlue = Box.createHorizontalGlue();
		horizontalBox.add(horizontalGlue);		
		lblXMed = new JLabel("0");
		horizontalBox.add(lblXMed);		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		horizontalBox.add(horizontalGlue_1);		
		lblXHigh = new JLabel("0");
		horizontalBox.add(lblXHigh);
		
		UpdateZPositionThread = new Thread(new UpdateZPosition(mf));
		UpdateZPositionThread.start();
	
	}
	
	public void stopThreads(){
		threadShouldStayRunning = false;
	}
	
	void addPoint(double np) {
		listPoints.add(np);
		updateParameters();
	}
	
	void updateParameters() {
		width = panel.getWidth(); // get current dimensions 
		height = panel.getHeight();
		
		nbrVisiblePoints = width / distBetweenPoints; // calculate number of visible points based on current dimensions
		nbrPoints = listPoints.size();
		if (nbrPoints > 1) {
			minValue = 0;
			maxValue = 0;
			int xMin = 0;
			int xMax = nbrPoints;
			
			if (nbrPoints > nbrVisiblePoints) { //vector contains more points than visible
				xMin = nbrPoints - nbrVisiblePoints -1;
				minValue = Collections.min(listPoints.subList(xMin, xMax)); //get min and max y value
				maxValue = Collections.max(listPoints.subList(xMin, xMax));	
			}
			else if (nbrPoints < nbrVisiblePoints && nbrPoints > 0) { //in the beginning all entries of the vector are taken
				minValue = Collections.min(listPoints); //get min and max y value 
				maxValue = Collections.max(listPoints);
				xMin = 0;
			}
			slope = (double)(height / (maxValue - minValue));  //slope to map data range on display range
			setAxisTexts(xMin, xMax);
			panel.setValues(distBetweenPoints, height, nbrPoints, nbrVisiblePoints, //copies calculated values into the member variables of the DrawPanel to display the graph
							slope, width, maxValue, minValue, listPoints);
			
		}
	}
	
	void setAxisTexts(int xMin, int xMax) {
		String lblLow = String.format("%3.4f", minValue);
		String lblMed = String.format("%3.4f", (maxValue + minValue)/2);
		String lblHigh = String.format("%3.4f", maxValue);
		lblYLow.setText(lblLow);
		lblYMed.setText(lblMed);
		lblYHigh.setText(lblHigh);
		
		lblLow = String.format("%d", xMin*timeIntervalX);
		lblMed = String.format("%d", ((xMax + xMin)/2)*timeIntervalX);
		lblHigh = String.format("%d", xMax*timeIntervalX);
		lblXLow.setText(lblLow);
		lblXMed.setText(lblMed);
		lblXHigh.setText(lblHigh);

	}
	
	class UpdateZPosition implements Runnable {
		MainFrame mf;
		public UpdateZPosition(MainFrame mf){
			this.mf = mf;
		}
		
		public void run(){
			while (threadShouldStayRunning) {
				try {
					counter = counter + 1;
					String currZ = String.valueOf(mf.getZStagePosition());
					addPoint(Double.valueOf(currZ));
					panel.repaint();
					Thread.sleep(1000);
				} catch (Exception e) {
					threadShouldStayRunning = false;
					e.printStackTrace();
				}
			}
		}
	}

}
class DrawPanel extends JPanel
{
	int nbrVisiblePoints;
	int nbrPoints;
	int distBetweenPoints;
	int width;
	int height;
	double slope;
	double minValue;
	double maxValue;

	Vector<Double> listPoints = new Vector<Double>();

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(Color.white);	//set color of line
		
		if (nbrPoints>1){
			int xn;
			int xnPlus1;
			int yn;
			int ynPlus1;
			int startIndex = Math.max(0, listPoints.size() - nbrVisiblePoints);	//draw either all points if there are less points than the maximal number of points visible or the last points 
			for (int i=startIndex, j = 0; i<listPoints.size()-1;i++, j++) {
				xn = j * distBetweenPoints;
				xnPlus1 = (j+1)*distBetweenPoints;
				yn = height - (int)Math.ceil((listPoints.get(i)-minValue) * slope);
				ynPlus1 = height - (int)Math.ceil((listPoints.get(i+1)-minValue) * slope);
				g.drawLine(xn,yn,xnPlus1,ynPlus1);
			}
		}
		
		
	}
	public void setValues(int distBetweenPoints2, int height2, int nbrPoints2,
			int nbrVisiblePoints2, double slope2, int width2, double maxValue2,
			double minValue2, Vector<Double> listPoints2) {
		distBetweenPoints = distBetweenPoints2;
		height = height2;
		nbrPoints = nbrPoints2;
		nbrVisiblePoints = nbrVisiblePoints2;
		slope = slope2;
		width = width2;			
		maxValue = maxValue2;
		minValue = minValue2;
		listPoints = listPoints2;
		
	}
	void addPoint(double np) {
		
	}
	void setValues(int nbrPoints_,double maxValue_){
		nbrPoints = nbrPoints_;
		maxValue = maxValue_;
	}
	
	

}
