package microscopeControl;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;


public class OutputPathControl extends JPanel {
	
	MainFrame mf;
	private JTextField path;
	private JTextField measurementTag;
	private JButton btnSelectSavePath;
	
	public OutputPathControl(MainFrame mf, Dimension minSize, Dimension prefSize, Dimension maxSize){
		this.mf = mf;
		setMinimumSize(minSize);
		setPreferredSize(prefSize);
		setMaximumSize(maxSize);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");

		setBorder(new TitledBorder(null, "Output Path", TitledBorder.LEADING, TitledBorder.TOP, mf.getTitelFont(), null));
		setLayout(new GridLayout(2, 1,10,10));
		
		Box horizontalBoxOutputPath = Box.createHorizontalBox();
		path = new JTextField("D:\\Measurements\\"+dateFormat.format(new Date()));
		
		
		btnSelectSavePath = new JButton("Set path");
		btnSelectSavePath.addActionListener(new BtnLoadSavePathActionListener());
		
		horizontalBoxOutputPath.add(path);
		horizontalBoxOutputPath.add(Box.createHorizontalStrut(20));
		horizontalBoxOutputPath.add(btnSelectSavePath);
		
		Box horizontalBoxMeasurementTag = Box.createHorizontalBox();
		
		measurementTag = new JTextField();
		
		
		measurementTag.setText("Measurement01");
		//Utility.setFormatTextFields(measurementTag, 200, 20, 100);
		horizontalBoxMeasurementTag.add(new JLabel("Measurement Tag: "));
		horizontalBoxMeasurementTag.add(Box.createHorizontalStrut(20));
		horizontalBoxMeasurementTag.add(measurementTag);
		
		this.add(horizontalBoxOutputPath);
		this.add(horizontalBoxMeasurementTag);
				
	}
	
	public String getPath(){
		return path.getText();
	}
	public String getMeasurementTag(){
		return measurementTag.getText();
	}

	class BtnLoadSavePathActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("hisafs;djf;sadfj;");
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
			int retVal = fc.showOpenDialog(btnSelectSavePath);
			if (retVal == JFileChooser.APPROVE_OPTION) {
				path.setText(fc.getSelectedFile().getPath());
			}
		}
	}

	public void setMeasurementTag(String text) {this.measurementTag.setText(text);}

	public void setPathOutputFolder(String pathToOutputFolder) {
		path.setText(pathToOutputFolder);
	};
}
