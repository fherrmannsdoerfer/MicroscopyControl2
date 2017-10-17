package editor;

public abstract class LoopModules extends EditorModules{
	
	public LoopModules(MainFrameEditor mfe){
		super(mfe);
	}
	
	public LoopModules(){
		super();
	}

	abstract public EditorModules getFunction(MainFrameEditor mfe);

	abstract public String[] getSettings();

	abstract public void setSettings(String[] tempString);

	abstract public EditorModules getEditorModulesObject(
			EditorModules processingStepsPanelObject, MainFrameEditor mfe);

	abstract public String getFunctionName();

	abstract public void perform();
	abstract public EditorModules getEndLoopModule(MainFrameEditor mfe);

}
