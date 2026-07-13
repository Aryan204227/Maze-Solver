import controller.AppController;
import view.AppWindow;
import view.SplashScreen;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SplashScreen splash = new SplashScreen();
                splash.showSplash(2000, new Runnable() {
                    @Override
                    public void run() {
                        AppController controller = new AppController();
                        AppWindow window = new AppWindow(controller);
                        controller.setAppWindow(window);
                        window.setVisible(true);
                    }
                });
            }
        });
    }
}
