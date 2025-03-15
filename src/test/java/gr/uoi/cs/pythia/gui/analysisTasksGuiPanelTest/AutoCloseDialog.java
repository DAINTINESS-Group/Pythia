package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;


public class AutoCloseDialog{

    public static void closeErrorDialog(String windowTitle) {
        new Thread(() -> {
            try {
                Robot robot = new Robot();
                while (true) {
                    SwingUtilities.invokeAndWait(() -> {
                        Window[] windows = Window.getWindows();
                        for (Window window : windows) {
                            if (window instanceof JDialog) {
                                JDialog dialog = (JDialog) window;
                                if (dialog.getTitle() != null && dialog.getTitle().equals(windowTitle)) {
                                    if (dialog.getTitle().equals("Select catalog for save scema")) {
                                        dialog.toFront();
                                        robot.keyPress(KeyEvent.VK_ESCAPE);
                                        robot.keyRelease(KeyEvent.VK_ESCAPE);
                                        break;
                                    } else {
                                        dialog.toFront();
                                        robot.keyPress(KeyEvent.VK_ENTER);
                                        robot.keyRelease(KeyEvent.VK_ENTER);
                                        break;
                                    }
                                }

                            }
                        }
                    });
                    //Thread.sleep(10);
                }
            } catch (InterruptedException | AWTException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }).start();
    }
}