package com.lxkj.common.util.collection;


import com.lxkj.common.util.Strings;

public class Option extends Pair<String, String> {

  private static final long serialVersionUID = -1003279673635690656L;

  private boolean selected;

  public Option(String value, String label) {
    super(value, label);
  }

  public Option(String value, String label, boolean selected) {
    this(value, label);
    this.selected = selected;
  }

  public static Option of(String value, String label) {
    return new Option(value, label);
  }

  public static Option of(String value, String label, boolean selected) {
    return new Option(value, label, selected);
  }

  @Override
  public String getValue() {
    return super.getKey();
  }

  public String getLabel() {
    return super.getValue();
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  @Override
  public String toString() {
    String selected = this.selected ? "," + "selected" : Strings.EMPTY;
    return super.toString() + selected;
  }

}