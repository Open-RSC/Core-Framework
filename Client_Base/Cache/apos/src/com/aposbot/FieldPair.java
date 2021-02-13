package com.aposbot;

import java.awt.*;

public final class FieldPair extends Panel {

    private static final long serialVersionUID = -7845847154396333166L;
    private final TextField field;

    public FieldPair(String text, String value, boolean enabled) {
        final GridLayout layout = new GridLayout(1, 2);
        setLayout(layout);
        field = new TextField(value);
        field.setEnabled(enabled);
        add(new Label(text));
        add(field);
        setVisible(true);
    }

    @Override
    public boolean isEnabled() {
        return field.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        field.setEnabled(enabled);
    }

    public String getValue() {
        return field.getText();
    }

    public void setValue(String str) {
        field.setText(str);
    }
}