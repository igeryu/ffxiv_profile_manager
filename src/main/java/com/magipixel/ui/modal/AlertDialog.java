//  AlertBox.java

/**
 * Changelog:
 * 2016-02-25 : Created file
 * 
 * 2016-02-29 : Removed unused imports
 * 2016-02-29 : Added Javadoc for display()
 * 
 * 2016-03-24 : Formatted to match Google Java Style
 */

package com.magipixel.ui.modal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author alanjohnson
 */
public class AlertDialog {
    
  /**
   * <p>Displays a window to let the user know some piece of information,
   * without asking for a hard confirmation.</p>
   * 
   * @param title       title of the pop-up window
   * @param message     message to be relayed to the user
   */
  public static void display(String title, String message) {
    VBox layout = new VBox(10);
    layout.setPadding(new Insets(10, 5, 10, 5));
    
    Label messageLabel = new Label(message);
    messageLabel.setWrapText(true);
    layout.getChildren().add(messageLabel);
    
    Stage window = new Stage();
    window.setTitle(title);
    Scene scene = new Scene(layout, 200, 100);
    window.setScene(scene);

    // ==========================  Buttons  ==========================
    Button confirmButton = new Button("OK");
    confirmButton.setOnAction(e -> {
      window.close();
    });

    HBox buttonBox = new HBox(20);
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.getChildren().add(confirmButton);
    layout.getChildren().add(buttonBox);

    // ==========================  Sizing  ===========================
    //  The following makes messageLabel grow to its preferred size, and
    //  then increases the window's height by the amount messageLabel itself
    //  increased:
//    messageLabel.setMinHeight(Control.USE_PREF_SIZE);
    
//    double height = Math.max(500, window.getHeight());
//    double labelHeight = messageLabel.getHeight();
//    labelHeight = messageLabel.get
//    double height = Math.max(200, 5.5 * labelHeight);
    //  Get width based on the Golden Ratio:
//    double width = height * 1.618;

    
//    window.setMinHeight(height);
//    window.setHeight(height);
//    window.setMinWidth(width);
    window.sizeToScene();
//    window.setResizable(false);


    // =========================  Finalize  ==========================
    window.initModality(Modality.APPLICATION_MODAL);
    window.showAndWait();
  }
  
}
