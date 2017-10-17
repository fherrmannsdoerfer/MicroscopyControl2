package editor;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceMotionListener;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

class DragAndDropTransferHandler extends TransferHandler implements DragSourceMotionListener {

    public DragAndDropTransferHandler() {
        super();
    }

    @Override()
    public Transferable createTransferable(JComponent c) {
        if (c instanceof EditorModules) {
            Transferable trans = (EditorModules) c;
            return trans;
        }

        return null;
    }

    public void dragMouseMoved(DragSourceDragEvent dsde) {
  
    }

    @Override()
    public int getSourceActions(JComponent c) {
            
        if (c instanceof EditorModules) {
            return TransferHandler.COPY;
        }
        
        return TransferHandler.NONE;
    }
} 