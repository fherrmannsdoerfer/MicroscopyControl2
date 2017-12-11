package editor;

public abstract class LoopModules extends EditorModules{
	private int currentIterationStep = 0;
	private int nbrIterations;
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
	abstract public void performIncrementalStep();
	abstract public EditorModules getEndLoopModule(MainFrameEditor mfe);
	abstract public String getLoopTag();
	public void nextStep(){currentIterationStep+=1;}
	public int getCurrentIterationStep(){return currentIterationStep;}
	public int getNbrIterations(){return nbrIterations;}
	public void setNbrIterations(int nbrIterations){this.nbrIterations = nbrIterations;}

}
