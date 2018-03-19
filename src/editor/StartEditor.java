package editor;

import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import microscopeControl.MainFrame;
//class to start the editor for test purposes, microscope control is started as well
//but no functionality that needs hardware components like the ROILoop work

public class StartEditor {
	static ControlerEditor controler;
	static MainFrameEditor mfe;
	
	public static void main(String[] args) {
		controler = new ControlerEditor();
		try {
	        // Set System L&F
	    UIManager.setLookAndFeel(
	        UIManager.getSystemLookAndFeelClassName());
	    }
	    catch (UnsupportedLookAndFeelException e) {
	       // handle exception
	    }
	    catch (ClassNotFoundException e) {
	       // handle exception
	    }
	    catch (InstantiationException e) {
	       // handle exception
	    }
	    catch (IllegalAccessException e) {
	       // handle exception
	    }
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame mf = new MainFrame(null,null);
					mfe = new MainFrameEditor(controler,mf);
					controler.setMainFrameReference(mfe);
					mfe.setVisible(true);
					mfe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
}
