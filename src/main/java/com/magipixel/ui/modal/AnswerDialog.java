//  AnswerDialog.java

/**
 * Changelog:
 */

package com.magipixel.ui.modal;


import java.util.List;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author alanjohnson
 */
public class AnswerDialog {
  // ===========================  Fields  ==========================
  private static Stage window;
  private static Scene scene;
  private static VBox layout;
  private static Label messageLabel;
  private static TextField answerField;
  private static ToggleGroup toggleGroup;
  private static String answer;
  
  // ============================  Misc  ===========================
  private static final Logger logger =
          Logger.getLogger(AnswerDialog.class.getSimpleName());
  
  public static enum Orientation { LANDSCAPE, PORTRAIT };
  
  // ==========================  Methods  ==========================
  
  /**
   * Static utility.
   */
  private AnswerDialog() {
      
  }
  
  /**
   * <p>Displays a pop-up window to ask the user a question.</p>
   * 
   * @param title             the title of the pop-up window
   * @param message           the message (question) to ask the user
   * @param defaultAnswer     the default answer to provide to the user
   * 
   * @return                  the user's answer to the question
   */
  public static String display(String title, String message, String defaultAnswer) {
    init(title, message);
    double width = 500;
    double height = 520;

    layout.setPadding(new Insets(0, 10, 10, 10));

    // ============================  Body  ===========================
    answerField = new TextField();
    answerField.promptTextProperty().setValue(defaultAnswer);
    answer = null;

    layout.getChildren().addAll(answerField);

    //      =====================  Buttons  =====================
    Button confirmButton = new Button("OK");
    confirmButton.setOnAction(e -> window.close());

    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(e -> {
      answerField.setText(null);
      window.close();
    });

    HBox buttonBox = new HBox(20);
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.getChildren().addAll(confirmButton, cancelButton);
    layout.getChildren().add(buttonBox);

    // ==========================  Sizing  ===========================
    //  Get width based on Orientation parameter and the Golden Ratio:
//    height += 20;
//    width = height / 1.618;
    window.setMinHeight(height);
    window.setMinWidth(width);
//    window.setResizable(false);

    //  The following makes messageLabel grow to its preferred size, and
    //  then increases the window's height by the amount messageLabel itself
    //  increased:
    messageLabel.setMinHeight(Control.USE_PREF_SIZE);

    // =========================  Finalize  ==========================
    window.setOnCloseRequest(e -> {
      answerField.setText(null);
      window.close();
    });
    window.setScene(scene);
    window.initModality(Modality.APPLICATION_MODAL);
    window.showAndWait();

    return answerField.getText();
  }

  /**
   * <p>Displays a pop-up window to ask the user a question with list of
   * options (radio boxes).</p>
   * 
   * @param title
   * @param message
   * @param answers
   * @param orientation
   * @return 
   */
  public static String display (String                 title,
                                  String                 message,
                                  List<String> answers,
                                  Orientation            orientation) {
      init(title, message);

      // ============================  Body  ===========================
      toggleGroup = new ToggleGroup();
      VBox answersBox = new VBox(10);
      layout.getChildren().add(answersBox);
      int rows = 0;
  
      for (String item : answers) {
        RadioButton newButton = new RadioButton(item);
        newButton.setToggleGroup(toggleGroup);
        answersBox.getChildren().add(newButton);
  
        rows++;
      }
  
      int height = 80 + (40 * rows);
      double width;
      //  Get width based on Orientation parameter and the Golden Ratio:
      if (orientation == Orientation.LANDSCAPE) {
        width = height * 1.618;
      } else {
        width = height / 1.618;
      }
  
      window.setMinHeight(height);
      window.setMinWidth(width);
      window.setResizable(false);
  
      //  DEBUG:
      logger.fine(String.format("[AnswerBox.display()]\n"
                              + "height: %s\nwidth: %s\n", height, width));
  
      //  The following makes messageLabel grow to its preferred size, and
      //  then increases the window's height by the amount messageLabel itself
      //  increased:
      messageLabel.setMinHeight(Control.USE_PREF_SIZE);
  
      window.setOnCloseRequest(e -> {
        answer = null;
        window.close();
      });
  
      // ==========================  Buttons  ==========================
      Button confirmButton = new Button("OK");
      confirmButton.setOnAction(e -> {
        RadioButton selectedButton =
            (RadioButton) toggleGroup.getSelectedToggle();
        
        if (selectedButton == null) {
          answer = null;
        } else {
          answer = ((RadioButton) toggleGroup.getSelectedToggle()).getText();
        }
        window.close();
      });
  
      Button cancelButton = new Button("Cancel");
      cancelButton.setOnAction(e -> {
        answer = null;
        window.close();
      });
  
      HBox buttonBox = new HBox(20);
      buttonBox.setAlignment(Pos.CENTER);
      buttonBox.getChildren().addAll(confirmButton, cancelButton);
      layout.getChildren().add(buttonBox);
  
      window.setScene(scene);
      window.initModality(Modality.APPLICATION_MODAL);
      window.showAndWait();
  
      return answer;
  }

  /**
   * <p>Displays a pop-up window to ask the user a question, but with no
   * default answer.</p>
   * 
   * @param title
   * @param message
   * @return 
   */
  public static String display(String title, String message) {
    return display(title, message, "");
  }
    
  /**
   * <p>Initializes the answer box pop-up using the given parameters.</p>
   * 
   * @param title
   * @param message 
   */
  private static void init(String title, String message) {
    window = new Stage();
    window.setTitle(title);
    
    layout = new VBox(20);
    layout.setPadding(new Insets(0, 10, 10, 10));
    scene = new Scene(layout, 200, 100);
    
    // ============================  Body  ===========================
    messageLabel = new Label(message);
    messageLabel.setWrapText(true);
    layout.getChildren().addAll(messageLabel);
      
  }
    
}
