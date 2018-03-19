package editor;

import java.awt.dnd.DropTarget;

import javax.swing.JPanel;
//class used for drag and drop functionality
public class RootPanel extends JPanel{
	private final MainFrameEditor mfe;
	RootPanel(MainFrameEditor mfe){
		super();
		this.mfe = mfe;
		this.setTransferHandler(new DragAndDropTransferHandler());
		this.setDropTarget(new DropTarget(RootPanel.this, new PanelDropTargetListener(this)));
	}
	
	public MainFrameEditor getMainFrame() {
        return mfe;
    }
}
