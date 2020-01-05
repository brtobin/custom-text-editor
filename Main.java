import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.awt.event.KeyListener;

public class Main extends Application {
  // store any command-line arguments that were entered.
  // NOTE: this.getParameters().getRaw() will get these also
  private List<String> args;
  private static final int WINDOW_WIDTH = 1000;
  private static final int WINDOW_HEIGHT = 600;
  private static final String APP_TITLE = "TextPad Editor";
  private PriorityQueue pq;
  private ArrayList<FilePair> fileTypes = new ArrayList<FilePair>();
  private ArrayList<FilePair> temp;
  private ArrayList<String> showTypes = new ArrayList<String>();
  private int batch = 0;
  private int c = 0;
  private int java = 0;
  private int javascript = 0;
  private int plain = 0;
  private int make = 0;
  private Random rand = new Random();
  private MenuBar menuBar;
  String fileName = "";
  private boolean saved = true;
  private String title;
  private String starTitle;

  public Main() {
    int size;
    // load in the existing use log of different file types
    try {
      readLog();
    } catch (FileNotFoundException | UnsupportedEncodingException e1) {
    }
    temp = new ArrayList<FilePair>();
    // sort the various file types in order of their # of uses
    addAllFileTypes();
    size = temp.size();
    pq = new PriorityQueue();
    for (int i = 0; i < size; i++) {
      pq.add(temp.get(i));
    }
    for (int i = 0; i < size; i++) {
      showTypes.add(pq.removeMax().getFileType());
    }
  }

  /**
   * Add all of the different file types (w/ their respective number of uses) to a temporary array to
   * eventually be sorted
   */
  private void addAllFileTypes() {
    temp.add(new FilePair(FileLabels.BATCH, batch, FileKeys.BATCH));
    temp.add(new FilePair(FileLabels.C, c, FileKeys.C));
    temp.add(new FilePair(FileLabels.JAVA, java, FileKeys.JAVA));
    temp.add(
        new FilePair(FileLabels.JAVASCRIPT, javascript, FileKeys.JAVASCRIPT));
    temp.add(new FilePair(FileLabels.PLAINTXT, plain, FileKeys.PLAINTXT));
    temp.add(new FilePair(FileLabels.MAKE, make, FileLabels.MAKE));
  }

  /**
   * Interpret the existing log file, loading the current # of uses of each file type
   * 
   * @throws FileNotFoundException        thrown if the log file is not found
   * @throws UnsupportedEncodingException
   */
  public void readLog()
      throws FileNotFoundException, UnsupportedEncodingException {
    ArrayList<String> uses = new ArrayList<String>();
    String[] line;
    Scanner s = new Scanner(new File("log.txt")).useDelimiter("\\n");
    // read log file and assign counts to each respective file type
    while (s.hasNext()) {
      uses.add(s.nextLine());
    }
    for (String use : uses) {
      line = use.split(" ");
      switch (line[0]) {
        case (FileKeys.BATCH):
          batch = Integer.parseInt(line[1].trim());
          break;
        case (FileKeys.C):
          c = Integer.parseInt(line[1].trim());
          break;
        case (FileKeys.JAVA):
          java = Integer.parseInt(line[1].trim());
          break;
        case (FileKeys.PLAINTXT):
          plain = Integer.parseInt(line[1].trim());
          break;
        case (FileKeys.MAKE):
          make = Integer.parseInt(line[1].trim());
          break;
        case (FileKeys.JAVASCRIPT):
          javascript = Integer.parseInt(line[1].trim());
          break;
      }
    }
    s.close();
  }

  /**
   * Save the notespace as a new file of a certain type
   * 
   * @param fileType the file type to be saved as
   * @param fileName the name the file should be saved under
   * @param content  the content of the file
   * @throws FileNotFoundException        thrown if the file is not found
   * @throws UnsupportedEncodingException
   */
  public void saveNew(String fileType, String fileName, String content)
      throws FileNotFoundException, UnsupportedEncodingException {
    PrintWriter writer;
    String[] savedAs = fileType.split(" ");
    // decide which file type was selected and add the approriate suffix
    switch (savedAs[0]) {
      case ("JavaScript"):
        fileName = fileName + ".js";
        javascript++;
        break;
      case ("Plain"):
        plain++;
        fileName = fileName + ".txt";
        break;
      case ("Batch"):
        batch++;
        fileName = fileName + ".bat";
        break;
      case ("C"):
        c++;
        fileName = fileName + ".c";
        break;
      case ("Java"):
        java++;
        fileName = fileName + ".java";
        break;
    }
    // set up to write to the file w/ the newly adjusted name
    writer = new PrintWriter(fileName, "UTF-8");
    // write the content of the notespace to the appropriate file
    if (content == null) {
      content = "";
    }
    writer.write(content);
    saved = true;
    writer.close();
  }

  /**
   * Update the log to account for any additional uses of any file types
   * 
   * @throws FileNotFoundException
   * @throws UnsupportedEncodingException
   */
  public void closeLog()
      throws FileNotFoundException, UnsupportedEncodingException {
    PrintWriter writer = new PrintWriter("log.txt", "UTF-8");
    writer.println("js " + javascript);
    writer.println("plain " + plain);
    writer.println("batch " + batch);
    writer.println("c " + c);
    writer.println("java " + java);
    writer.println("make " + make);
    writer.close();
  }

  /**
   * Set up the top menu bar
   */
  private void setUpMenuBar() {
    menuBar = new MenuBar();
    // set up the main menu options
    Menu menuFile = new Menu("File");
    Menu menuOpen = new Menu("Open");
    Menu menuOther = new Menu("Other");
    // set up the menu sub options
    MenuItem saveAsFile = new MenuItem("Save As..");
    MenuItem saveFile = new MenuItem("Save");
    MenuItem newFile = new MenuItem("Create New File");
    menuFile.getItems().addAll(newFile, saveFile, saveAsFile);
    MenuItem openFile = new MenuItem("Open File..");
    menuOpen.getItems().addAll(openFile);
    // create the finished menu bar
    menuBar.getMenus().addAll(menuFile, menuOpen, menuOther);
  }

  /**
   * Format the file so that if the entered file name contains a prefix already, that it's deleted
   * (and does not create a confusion if a different file type is chosen from the menu)
   * 
   * @param fileName the file name entered in the text field
   * @return the formatted file name w/o a suffix
   */
  private String formatFileName(String fileName) {
    String rslt = "";
    if (fileName.contains(".")) {
      int index = fileName.indexOf(".");
      rslt = fileName.substring(0, index);
    } else {
      // no change is made to the filename is it does not contain a suffix
      rslt = fileName.strip();
    }
    return rslt;
  }

  /**
   * Open a file from local drive
   * @param toOpen the file to open
   * @param choose the dialog to access a file from the local drive
   * @param primaryStage the stage to present the GUI
   * @param sc scanner to read in the file
   * @param note the note space to present the file
   */
  private void openFile(File toOpen, FileChooser choose, Stage primaryStage,
      Scanner sc, TextArea note) {
    toOpen = choose.showOpenDialog(primaryStage);
    if (toOpen != null) {
      try {
        // clear the note space before presenting the selected file
        note.clear();
        fileName = toOpen.getName();
        sc = new Scanner(toOpen);
        while (sc.hasNextLine()) {
          note.appendText(sc.nextLine() + "\n");
        }
        if (fileName.contentEquals("")) {
          primaryStage.setTitle(APP_TITLE);
          title = APP_TITLE;
        } else {
          primaryStage.setTitle(fileName);
          title = fileName;
        }
        saved = true;
      } catch (FileNotFoundException e1) {
        System.out.println("Could not find file");
      } finally {
        if (sc != null)
          sc.close();
      }
    }
  }

  /**
   * Create the menu window for saving a new file
   * 
   * @param note
   */
  private void createSaveAsPopUp(TextArea note) {
    VBox inputs = new VBox();
    HBox nameField = new HBox();
    HBox fileField = new HBox();
    TextField fileNameInput;
    Label enterName;
    Label enterFile;
    ComboBox fileMenu;
    Button saveButton;
    // initialize the pop-up window
    Stage popUp = new Stage();
    popUp.initModality(Modality.APPLICATION_MODAL);
    popUp.setTitle("Save File As..");
    // create and format the components related to requesting the file name to save as
    fileNameInput = new TextField();
    enterName = new Label("Enter file name: ");
    enterName.setPadding(new Insets(2, 15, 15, 15));
    // create and format the components related to requesting the file type to save as
    // create the menu of file types as an ordered list based on their # of uses
    ObservableList<String> formats = FXCollections.observableArrayList();
    for (String type : showTypes) {
      formats.add(type);
    }
    fileMenu = new ComboBox<String>(formats);
    enterFile = new Label("Select file type: ");
    // create, format, and create functionality for the save button
    saveButton = new Button("Save");
    saveButton.setTranslateX(200);
    saveButton.setTranslateY(2);
    saveButton.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent e) {
        try {
          if (fileMenu.getValue() != null) {
            try {
              fileName = formatFileName(fileNameInput.getText());
              System.out.println("The file name is: " + fileName);
              try {
                if (fileName.contentEquals("")) {
                  throw new NoNameException();
                } else {
                  saveNew(fileMenu.getValue().toString(), fileName,
                      note.getText());
                  popUp.close();
                }
              } catch (NoNameException e1) {
                Alert noName = new Alert(AlertType.WARNING);
                noName.setTitle("No file name entered");
                noName.setHeaderText(null);
                noName.setContentText(e1.getMessage());
                noName.showAndWait();
              }
            } catch (FileNotFoundException e1) {
              e1.printStackTrace();
            } catch (UnsupportedEncodingException e1) {
              e1.printStackTrace();
            }
          } else {
            throw new NoFileTypeException();
          }
        } catch (NoFileTypeException e1) {
          Alert noType = new Alert(AlertType.WARNING);
          noType.setTitle("No file type entered");
          noType.setHeaderText(null);
          noType.setContentText(e1.getMessage());
          noType.showAndWait();
        }
      }
    });
    // group elements and format group w/in the popup window
    fileField.getChildren().addAll(enterFile, fileMenu);
    nameField.getChildren().addAll(enterName, fileNameInput);
    nameField.setTranslateX(-20);
    nameField.setPadding(new Insets(20, 20, 2, 20));
    enterFile.setPadding(new Insets(2, 15, 15, 15));
    inputs.getChildren().addAll(nameField, fileField, saveButton);
    // Display the pop-up window
    Scene pop = new Scene(inputs, 400, 130);
    popUp.setScene(pop);
    popUp.sizeToScene();
    popUp.showAndWait();
  }

  /**
   * Update/save an existing file
   * 
   * @param note the current note area
   */
  private void saveExisting(TextArea note) {
    PrintWriter writer;
    try {
      String content = note.getText();
      writer = new PrintWriter(fileName, "UTF-8");
      if (content == null) {
        content = "";
      }
      writer.write(content);
      saved = true;
      writer.close();
    } catch (FileNotFoundException e) { // if the file is not existing, save as a new file
      createSaveAsPopUp(note);
    } catch (UnsupportedEncodingException e) {
    }
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    BorderPane root = new BorderPane();
    TextArea note = new TextArea();
    setUpMenuBar();
    MenuItem open = menuBar.getMenus().get(1);
    MenuItem saveAs = menuBar.getMenus().get(0).getItems().get(2);
    MenuItem save = menuBar.getMenus().get(0).getItems().get(1);
    MenuItem newFile = menuBar.getMenus().get(0).getItems().get(0);
    // Create functionality to open file
    open.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent e) {
        FileChooser choose = new FileChooser();
        File toOpen = null;
        Scanner sc = null;
        if (saved == false) {
          Alert warning = new Alert(AlertType.CONFIRMATION);
          warning.setTitle("Unsaved File");
          warning.setHeaderText("The file you are about to close is not saved");
          warning.setContentText("Would you still like" + "to continue?");
          ButtonType cont = new ButtonType("Continue");
          ButtonType canc = new ButtonType("Cancel");
          warning.getButtonTypes().setAll(cont, canc);
          Optional<ButtonType> result = warning.showAndWait();
          if (result.get() == cont) {
            openFile(toOpen, choose, primaryStage, sc, note);
          }
        } else {
          openFile(toOpen, choose, primaryStage, sc, note);
        }      
      }
    });
    // Create functionality to newly save a file
    saveAs.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent e) {
        createSaveAsPopUp(note);
        primaryStage.setTitle(title);
      }
    });
    // Create functionality to save an existing file if requested
    save.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent e) {
        saveExisting(note);
        primaryStage.setTitle(title);
      }
    });
    // Create functionality to start editing a new file
    newFile.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent e) {
        if (saved == false) {
          Alert warning = new Alert(AlertType.CONFIRMATION);
          warning.setTitle("Unsaved File");
          warning.setHeaderText("The file you are about to close is not saved");
          warning.setContentText("Would you still like" + "to continue?");
          ButtonType cont = new ButtonType("Continue");
          ButtonType canc = new ButtonType("Cancel");
          warning.getButtonTypes().setAll(cont, canc);
          Optional<ButtonType> result = warning.showAndWait();
          if (result.get() == cont) {
            fileName = "";
            note.clear();
            primaryStage.setTitle(APP_TITLE);
            title = APP_TITLE;
          } 
        } else {
          fileName = "";
          note.clear();
          primaryStage.setTitle(APP_TITLE);
          title = APP_TITLE;
        }
      }
    });
    note.addEventFilter(KeyEvent.ANY, event -> {
      saved = false;
      starTitle = title + "*";
      primaryStage.setTitle(starTitle);
    });
    // Set up the application window
    root.setTop(menuBar);
    root.setCenter(note);
    Scene mainScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
    primaryStage.setTitle(APP_TITLE);
    title = APP_TITLE;
    primaryStage.setScene(mainScene);
    primaryStage.show();
    primaryStage.setOnCloseRequest(event -> {
      try {
        closeLog();
      } catch (FileNotFoundException | UnsupportedEncodingException e1) {
        e1.printStackTrace();
      }
    });
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    launch(args);
  }
}
