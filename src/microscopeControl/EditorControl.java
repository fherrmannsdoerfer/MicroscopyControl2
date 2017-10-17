package microscopeControl;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import utility.Utility;
import dataTypes.ROIParameters;
import editor.MainFrameEditor;
import mmcorej.CMMCore;


public class EditorControl extends JPanel{
	//object used to control all hardware
	CMMCore core;
	
	MainFrame mf;
	
	JTextField experimentPathTextField;
	JProgressBar progressBar;
	
	public EditorControl(MainFrame mf, Dimension minSize, Dimension prefSize, Dimension maxSize){
		this.mf = mf;
		setMinimumSize(minSize);
		setPreferredSize(prefSize);
		setMaximumSize(maxSize);
		setBorder(new TitledBorder(null, "Editor", TitledBorder.LEADING, TitledBorder.TOP, mf.getTitelFont(), null));
		setLayout(new GridLayout(5, 1,10,5));
		
		JPanel upperPart = new JPanel();
		upperPart.setLayout(new GridLayout(1, 2,10,5));
		
		JButton btnOpenEditor = new JButton("Open Editor");
		btnOpenEditor.addActionListener(new btnOpenEditorActionListener());
		upperPart.add(new JLabel(""));
		upperPart.add(btnOpenEditor);
		
		JPanel middlePart = new JPanel();
		middlePart.setLayout(new GridLayout(1, 2,10,5));
		JButton btnImportExperiment = new JButton("Import Experiment");
		btnImportExperiment.addActionListener(new btnImportExperimentActionListener());
		middlePart.add(new JLabel(""));
		middlePart.add(btnImportExperiment);
		
		experimentPathTextField = new JTextField("Place Path To Experiment File here");
		
		progressBar = new JProgressBar();
		
		
		JPanel lowerPart = new JPanel();
		lowerPart.setLayout(new GridLayout(1, 2,10,5));
		
		JButton btnStartExperiment = new JButton("Start Experiment");
		btnStartExperiment.addActionListener(new btnStartExperimentActionListener());
		lowerPart.add(btnStartExperiment);
		JButton btnStopExperiment = new JButton("Stop Experiment");
		btnStopExperiment.addActionListener(new btnStopExperimentActionListener());
		lowerPart.add(btnStopExperiment);
		
		this.add(upperPart);
		this.add(middlePart);
		this.add(experimentPathTextField);
		this.add(progressBar);
		this.add(lowerPart);
		
	}
	
	class btnOpenEditorActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			mf.startEditor();
		}
	}
	
	class btnImportExperimentActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			experimentPathTextField.setText(mf.loadExperiment());
		}
	}

	class btnStartExperimentActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			mf.startExperiment();
		}
	}
	class btnStopExperimentActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			mf.stopExperiment();
		}
	}

}
