package greynekos.greybook.ui;

import java.util.logging.Logger;

import greynekos.greybook.commons.core.GuiSettings;
import greynekos.greybook.commons.core.LogsCenter;
import greynekos.greybook.logic.Logic;
import greynekos.greybook.logic.commands.CommandResult;
import greynekos.greybook.logic.commands.exceptions.CommandException;
import greynekos.greybook.logic.parser.exceptions.ParseException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * The Main Window. Provides the basic application layout containing a menu bar
 * and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private PersonTablePanel personListPanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane personListPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane resultDisplaySpacer;

    @FXML
    private StackPane statusbarPlaceholder;

    @FXML
    private SplitPane splitPane;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage} and {@code Logic}.
     */
    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        helpWindow = new HelpWindow();
    }

    /**
     * Runs when this component is initialized. Used to set the splitPane divider
     * positions once the screen size is known.
     */
    @FXML
    public void initialize() {
        Platform.runLater(() -> splitPane.setDividerPositions(0.7));

        splitPane.getDividers().get(0).positionProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() < 0.3) {
                splitPane.setDividerPositions(0.3);
            }
        });

        // Hide result display initially, show spacer
        toggleResultDisplay(false);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     *
     * @param keyCombination
     *            the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666 is fixed in later version of
         * SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will not
         * work when the focus is in them because the key event is consumed by the
         * TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is in
         * CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        personListPanel = new PersonTablePanel(logic.getFilteredPersonList());
        personListPanelPlaceholder.getChildren().add(personListPanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(logic.getGreyBookFilePath());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand, logic.getHistory().getCommandHistory());
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());
    }

    /**
     * Checks if the settings defined in {@code GuiSettings} is out of the all
     * screens.
     *
     * @param guiSettings
     *            GUI position and width settings.
     * @return True if the screen is out of all screens, false otherwise.
     */
    private boolean isWindowIsOutOfBounds(GuiSettings guiSettings) {
        for (Screen screen : Screen.getScreens()) {
            Rectangle2D bounds = screen.getVisualBounds();
            if (bounds.getMinX() <= guiSettings.getWindowX() && guiSettings.getWindowX() < bounds.getMaxX()
                    && bounds.getMinY() <= guiSettings.getWindowY() && guiSettings.getWindowY() < bounds.getMaxY()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Resets the {@code primaryStage} to the primary screen.
     *
     * @param isMaximized
     *            Whether to maximize the screen.
     */
    private void moveToPrimaryScreen(boolean isMaximized) {
        // Reset all fields, except if it's maximized
        primaryStage.setHeight(GuiSettings.DEFAULT_HEIGHT);
        primaryStage.setWidth(GuiSettings.DEFAULT_WIDTH);
        primaryStage.setX(GuiSettings.DEFAULT_X);
        primaryStage.setY(GuiSettings.DEFAULT_Y);
        primaryStage.setMaximized(isMaximized);
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        if (isWindowIsOutOfBounds(guiSettings)) {
            moveToPrimaryScreen(guiSettings.getIsMaximized());
        } else {
            primaryStage.setHeight(guiSettings.getWindowHeight());
            primaryStage.setWidth(guiSettings.getWindowWidth());
            primaryStage.setX(guiSettings.getWindowX());
            primaryStage.setY(guiSettings.getWindowY());
            primaryStage.setMaximized(guiSettings.getIsMaximized());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                primaryStage.getX(), primaryStage.getY(), primaryStage.maximizedProperty().get());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    public PersonTablePanel getPersonListPanel() {
        return personListPanel;
    }

    /**
     * Toggles the visibility of the result display and spacer.
     *
     * @param shouldDisplayResult
     *            true to show the result display and hide the spacer, false to hide
     *            the result display and show the spacer.
     */
    private void toggleResultDisplay(boolean shouldDisplayResult) {
        resultDisplayPlaceholder.setVisible(shouldDisplayResult);
        resultDisplaySpacer.setVisible(!shouldDisplayResult);
    }

    /**
     * Executes the command and returns the result.
     *
     * @see greynekos.greybook.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());

            toggleResultDisplay(true);

            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);

            toggleResultDisplay(true);

            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }
}
