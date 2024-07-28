package space.peetseater.rename.gui;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;

public class PreferredLookAndFeel {
    private final List<String> precedence;

    public PreferredLookAndFeel(List<String> precedence) {
        this.precedence = precedence;
    }

    public void apply() {
        HashMap<String, UIManager.LookAndFeelInfo> foundFeels = new HashMap<>();
        for (UIManager.LookAndFeelInfo lookAndFeelInfo : UIManager.getInstalledLookAndFeels()) {
            foundFeels.put(lookAndFeelInfo.getName(), lookAndFeelInfo);
        }

        for (String name : precedence) {
            if (foundFeels.containsKey(name)) {
                try {
                    UIManager.setLookAndFeel(foundFeels.get(name).getClassName());
                    return;
                } catch (
                    UnsupportedLookAndFeelException |
                    ClassNotFoundException |
                    InstantiationException |
                    IllegalAccessException e
                ) {
                    // It's not THAT important if we can't set a look and feel.
                    e.printStackTrace();
                }
            }
        }
    }
}
