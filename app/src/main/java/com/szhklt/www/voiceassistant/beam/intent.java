package com.szhklt.www.voiceassistant.beam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class intent implements Serializable{

	private boolean save_history;
	private String sid;
	private String text;
	private UsedState used_state;
	private String dialog_stat;
	private Answer answer = null;
	private String service;
	private Data data;
	private ArrayList<Semantic> semantic;
	private String uuid;
	private int rc;
	private String category;
	private int semanticType;
	private String vendor;
	private String version;
	private String intentType;

	// private State state;
	//
	// public State getState() { return this.state; }
	//
	// public void setState(State state) { this.state = state; }

	public boolean isSave_history() {
		return save_history;
	}

	public void setSave_history(boolean save_history) {
		this.save_history = save_history;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public UsedState getUsed_state() {
		return used_state;
	}

	public void setUsed_state(UsedState used_state) {
		this.used_state = used_state;
	}

	public String getDialog_stat() {
		return dialog_stat;
	}

	public void setDialog_stat(String dialog_stat) {
		this.dialog_stat = dialog_stat;
	}

	public Answer getAnswer() {
		return answer;
	}

	public void setAnswer(Answer answer) {
		this.answer = answer;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public ArrayList<Semantic> getSemantic() {
		return semantic;
	}

	public void setSemantic(ArrayList<Semantic> semantic) {
		this.semantic = semantic;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getRc() {
		return rc;
	}

	public void setRc(int rc) {
		this.rc = rc;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getSemanticType() {
		return semanticType;
	}

	public void setSemanticType(int semanticType) {
		this.semanticType = semanticType;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getIntentType() {
		return intentType;
	}

	public void setIntentType(String intentType) {
		this.intentType = intentType;
	}

	/*********************************************************************/

	public static class UsedState implements Serializable{
		private String content;
		private String state;
		private String name;
		private String operation;
		private String state_key;
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getOperation() {
			return operation;
		}
		public void setOperation(String operation) {
			this.operation = operation;
		}
		public String getState_key() {
			return state_key;
		}
		public void setState_key(String state_key) {
			this.state_key = state_key;
		}
		@Override
		public String toString() {
			return "UsedState [content=" + content + ", state=" + state
					+ ", name=" + name + ", operation=" + operation
					+ ", state_key=" + state_key + "]";
		}

	}

	public static class Semantic implements Serializable{
		private String intent;
		private ArrayList<Slot> slots;
		public String getIntent() {
			return intent;
		}
		public void setIntent(String intent) {
			this.intent = intent;
		}
		public ArrayList<Slot> getSlots() {
			return slots;
		}
		public void setSlots(ArrayList<Slot> slots) {
			this.slots = slots;
		}

	}

	public static class Slot implements Serializable{
		private String name;
		private String value;
		private String normValue;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getNormValue() {
			return normValue;
		}
		public void setNormValue(String normValue) {
			this.normValue = normValue;
		}

	}

	public static class Answer implements Serializable{

		private String answ ;
		private String text;

		public Answer(){}

		/**************************************/
		public String getAnsw() {
			return answ;
		}

		public void setAnsw(String answ) {
			this.answ = answ;
		}

		public void setText(String text){
			this.text = text;
		}

		public String getText(){
			return text;
		}

	}
	
	public static class Data implements Serializable{
		public int inherit;
		public List<Result> result;	
		public Data() {}

		public  int getInherit(){
			return inherit;		
		}
		public void setInherit(int inherit){
			this.inherit =inherit;
		}
		
		public List<Result> getResult()   
	    {  
	        return result;  
	    }  
	    public void setResult(List<Result> result)   
	    {  
	        this.result = result;  
	    }  
	}

	//	@Override
	//	public String toString() {
	//		return "intent [save_history=" + save_history + ", sid=" + sid
	//				+ ", text=" + text + ", used_state=" + used_state
	//				+ ", dialog_stat=" + dialog_stat + ", answer=" + answer
	//				+ ", service=" + service + ", data=" + data + ", semantic="
	//				+ semantic + ", uuid=" + uuid + ", rc=" + rc + ", category="
	//				+ category + ", semanticType=" + semanticType + ", vendor="
	//				+ vendor + ", version=" + version + ", intentType="
	//				+ intentType + "]";
	//	}

}
