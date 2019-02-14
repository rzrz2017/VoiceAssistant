package com.szhklt.www.voiceassistant.beam;

import java.util.ArrayList;


public class AlarmRootObject
{
  private int rc;

  public int getRc() { return this.rc; }

  public void setRc(int rc) { this.rc = rc; }

  private ArrayList<Semantic> semantic;

  public ArrayList<Semantic> getSemantic() { return this.semantic; }

  public void setSemantic(ArrayList<Semantic> semantic) { this.semantic = semantic; }

  private String service;

  public String getService() { return this.service; }

  public void setService(String service) { this.service = service; }

  private String uuid;

  public String getUuid() { return this.uuid; }

  public void setUuid(String uuid) { this.uuid = uuid; }

  private String text;

  public String getText() { return this.text; }

  public void setText(String text) { this.text = text; }


  private UsedState used_state;

  public UsedState getUsedState() { return this.used_state; }

  public void setUsedState(UsedState used_state) { this.used_state = used_state; }

  private Answer answer;

  public Answer getAnswer() { return this.answer; }

  public void setAnswer(Answer answer) { this.answer = answer; }

  private String dialog_stat;

  public String getDialogStat() { return this.dialog_stat; }

  public void setDialogStat(String dialog_stat) { this.dialog_stat = dialog_stat; }

  private boolean save_history;

  public boolean getSaveHistory() { return this.save_history; }

  public void setSaveHistory(boolean save_history) { this.save_history = save_history; }

  private String sid;

  public String getSid() { return this.sid; }

  public void setSid(String sid) { this.sid = sid; }
  
  
  public class Slot
  {
    private String name;

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    private String value;

    public String getValue() { return this.value; }

    public void setValue(String value) { this.value = value; }

    private String normValue;

    public String getNormValue() { return this.normValue; }

    public void setNormValue(String normValue) { this.normValue = normValue; }
  }

  public class Semantic
  {
    private String intent;

    public String getIntent() { return this.intent; }

    public void setIntent(String intent) { this.intent = intent; }

    private ArrayList<Slot> slots;

    public ArrayList<Slot> getSlots() { return this.slots; }

    public void setSlots(ArrayList<Slot> slots) { this.slots = slots; }
  }



  public class UsedState
  {
    private String content;

    public String getContent() { return this.content; }

    public void setContent(String content) { this.content = content; }

    private String name;

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    private String operation;

    public String getOperation() { return this.operation; }

    public void setOperation(String operation) { this.operation = operation; }


    private String state;

    public String getState() { return this.state; }

    public void setState(String state) { this.state = state; }

    private String state_key;

    public String getStateKey() { return this.state_key; }

    public void setStateKey(String state_key) { this.state_key = state_key; }
  }

  public class Answer
  {
    private String text;

    public String getText() { return this.text; }

    public void setText(String text) { this.text = text; }
  }
  
}
