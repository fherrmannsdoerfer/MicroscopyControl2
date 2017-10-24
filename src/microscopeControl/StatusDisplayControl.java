
package microscopeControl;
import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

import mmcorej.CMMCore;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.border.TitledBorder;

public class StatusDisplayControl extends JPanel {
	MainFrame mf;
	JLabel actionLabel = new JLabel();
	JLabel frameLabel = new JLabel();
	JLabel cameraStatusLabel = new JLabel();
	
	public StatusDisplayControl(MainFrame mf) {
		this.mf = mf;
		setPreferredSize(new Dimension(570,60));
		setMaximumSize(new Dimension(570, 100));
		setMinimumSize(new Dimension(570, 50));

		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		Box verticalBox = Box.createVerticalBox();
		verticalBox.setBorder(new TitledBorder(null, "Status Display", TitledBorder.LEADING, TitledBorder.TOP, mf.getTitelFont(), null));
		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(1,2,228,1));
		JPanel leftBlock = new JPanel();
		leftBlock.setLayout(new GridLayout(2, 2,10,15));
		leftBlock.add(new JLabel("Camera Status: "));
		leftBlock.add(cameraStatusLabel);
		leftBlock.add(new JLabel("Current Action: "));
		leftBlock.add(actionLabel);
		
		JPanel rightBlock = new JPanel();
		rightBlock.setLayout(new GridLayout(2, 2,10,15));
		rightBlock.add(new JLabel());
		rightBlock.add(new JLabel());
		rightBlock.add(new JLabel("Frame: "));
		rightBlock.add(frameLabel);
		
		contentPane.add(leftBlock);
		contentPane.add(rightBlock);
		verticalBox.add(contentPane);
		this.add(verticalBox);
	}
	
	public void setAction(String action){actionLabel.setText(action);}
	public void setFrame(String frame){frameLabel.setText(frame);}
	public void setCameraStatus(String status) {cameraStatusLabel.setText(status);}
}
