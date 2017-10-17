package editor;

import java.awt.Cursor;
import java.awt.List;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

class PanelDropTargetListener implements DropTargetListener {

    private final RootPanel rootPanel;
    
   
    private static final Cursor droppableCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR),
            notDroppableCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);

    public PanelDropTargetListener(RootPanel sheet) {
        this.rootPanel = sheet;
    }

    public void dragOver(DropTargetDragEvent dtde) {
        if (!this.rootPanel.getCursor().equals(droppableCursor)) {
            this.rootPanel.setCursor(droppableCursor);
        }
    }

    public void dragExit(DropTargetEvent dte) {
        this.rootPanel.setCursor(notDroppableCursor);
    }

    public void drop(DropTargetDropEvent dtde) {
        
        this.rootPanel.setCursor(Cursor.getDefaultCursor());
        
        DataFlavor dragAndDropPanelFlavor = null;
        
        Object transferableObj = null;
        Transferable transferable = null;
        
        try {
            dragAndDropPanelFlavor = MainFrameEditor.getDragAndDropPanelDataFlavor();
            
            transferable = dtde.getTransferable();
            DropTargetContext c = dtde.getDropTargetContext();
            
            if (transferable.isDataFlavorSupported(dragAndDropPanelFlavor)) {
                transferableObj = dtde.getTransferable().getTransferData(dragAndDropPanelFlavor);
            } 
            
        } catch (Exception ex) {}
        
        if (transferableObj == null) {
            return;
        }
        EditorModules panelToDrop = (EditorModules)transferableObj;
        final int dropYLoc = dtde.getLocation().y;
        Map<Integer, EditorModules> mapOfLocY = new HashMap<Integer, EditorModules>();
        mapOfLocY.put(dropYLoc, panelToDrop);

        for (EditorModules nextPanel : rootPanel.getMainFrame().getListProcessingStepPanels()) {
            int y = nextPanel.getY();
            if (!nextPanel.equals(panelToDrop)) {
                mapOfLocY.put(y, nextPanel);
            }
        }

        ArrayList<Integer> sortableYValues = new ArrayList<Integer>();
        sortableYValues.addAll(mapOfLocY.keySet());
        Collections.sort(sortableYValues);

        ArrayList<EditorModules> orderedPanels = new ArrayList<EditorModules>();
        for (Integer i : sortableYValues) {
            orderedPanels.add(mapOfLocY.get(i));
        }
        
        ArrayList<EditorModules> inMemoryPanelList = this.rootPanel.getMainFrame().getListProcessingStepPanels();
        inMemoryPanelList.clear();
        inMemoryPanelList.addAll(orderedPanels);
    
        this.rootPanel.getMainFrame().updatePanels();
    }

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
	}


	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
	}


} 