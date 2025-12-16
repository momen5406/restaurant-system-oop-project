package main;

import view.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame login = new LoginFrame();
                login.show();
            }
        });
    }
}
