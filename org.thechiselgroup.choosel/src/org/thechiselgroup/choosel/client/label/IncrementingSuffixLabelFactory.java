package org.thechiselgroup.choosel.client.label;

public class IncrementingSuffixLabelFactory implements LabelProvider {

    private final String prefix;

    private int currentIndex = 1;

    public IncrementingSuffixLabelFactory(String prefix) {
        assert prefix != null;
        this.prefix = prefix;
    }

    @Override
    public String nextLabel() {
        return prefix + Integer.toString(currentIndex++);
    }

}