package com.example.hack;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;

public class ToggleButton extends Button {
    private static PseudoClass pseudoClass = PseudoClass.getPseudoClass("toggled");
    private static ToggleButton last;
    private ToggleButton previous, next;
    public static ToggleButton selected;
    public static Runnable onDeselect = ()->{}, onChange = ()->{};

    BooleanProperty toggled;
    public ToggleButton() {
        toggled = new SimpleBooleanProperty(false);
        toggled.addListener(e -> pseudoClassStateChanged(pseudoClass, toggled.get()));
        getStyleClass().add("toggle-button");
        setOnMousePressed(event -> {
            setToggled(!isToggled());
            if (isToggled()) {
                selected = this;
                if (previous != null) previous.whooosh(false);
                if (next != null) next.whooosh(true);
                onChange.run();
            } else {
                selected = null;
                onDeselect.run();
            }
        });
        previous = last;
        if (last != null) last.next = this;
        last = this;
    }

    public static void setOnDeselect(Runnable runnable) {onDeselect = runnable;}
    public static void setOnChange(Runnable runnable) {
        onChange = runnable;}

    public void setToggled(boolean toggled) {
        this.toggled.set(toggled);
    } public boolean isToggled() {
        return toggled.get();
    }

    public void whooosh(boolean moveRight) {
        setToggled(false);
        if (moveRight && next != null) next.whooosh(moveRight);
        if (!moveRight && previous != null) previous.whooosh(moveRight);
    }
}
