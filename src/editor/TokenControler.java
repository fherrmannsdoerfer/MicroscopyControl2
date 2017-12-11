package editor;

import java.util.ArrayList;

public class TokenControler {
	private ArrayList<Token> knownTokens = new ArrayList<Token>();
	public TokenControler(){}
	public void addToken(String parameterTag, ArrayList<String> parameters){
		knownTokens.add(new Token(parameterTag, parameters));
	}
	
	public String getParameter(String parameterTag, int index){
		
		for (Token token : knownTokens){
			if (token.getParameterTag().equals(parameterTag)){
				if (token.getNumberParameters()<= index){
					System.err.println("For the token with parrameter tag "+parameterTag+" there are only "+token.getNumberParameters()+" parameters but a higher index was requested ("+index+")");
					System.err.println("Parameter from the last index is used instead.");
					return token.getParameter(token.getNumberParameters()-1);
				}
				else{
					return token.getParameter(index);
				}
			}
		}
		System.err.println("No parameter found with parameter tag: "+parameterTag);
		return null;
	}
	
	class Token{
		private ArrayList<String> parameters;
		private String parameterTag;
		public Token(String parameterTag, ArrayList<String> parameters){
			this.parameterTag = parameterTag;
			this.parameters = parameters;
		}
		public String getParameterTag(){return this.parameterTag;}
		public int getNumberParameters(){return this.parameters.size();}
		public String getParameter(int index){return parameters.get(index);}
	}
}
