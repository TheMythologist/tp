package greynekos.greybook;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import greynekos.greybook.commons.core.Config;
import greynekos.greybook.commons.core.LogsCenter;
import greynekos.greybook.commons.core.Version;
import greynekos.greybook.commons.exceptions.DataLoadingException;
import greynekos.greybook.commons.util.ConfigUtil;
import greynekos.greybook.commons.util.StringUtil;
import greynekos.greybook.logic.Logic;
import greynekos.greybook.logic.LogicManager;
import greynekos.greybook.model.GreyBook;
import greynekos.greybook.model.History;
import greynekos.greybook.model.Model;
import greynekos.greybook.model.ModelManager;
import greynekos.greybook.model.ReadOnlyGreyBook;
import greynekos.greybook.model.ReadOnlyHistory;
import greynekos.greybook.model.ReadOnlyUserPrefs;
import greynekos.greybook.model.UserPrefs;
import greynekos.greybook.model.util.SampleDataUtil;
import greynekos.greybook.storage.GreyBookStorage;
import greynekos.greybook.storage.HistoryStorage;
import greynekos.greybook.storage.JsonGreyBookStorage;
import greynekos.greybook.storage.JsonHistoryStorage;
import greynekos.greybook.storage.JsonUserPrefsStorage;
import greynekos.greybook.storage.Storage;
import greynekos.greybook.storage.StorageManager;
import greynekos.greybook.storage.UserPrefsStorage;
import greynekos.greybook.ui.Ui;
import greynekos.greybook.ui.UiManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(1, 5, 0, false);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Config config;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing GreyBook ]===========================");
        super.init();

        AppParameters appParameters = AppParameters.parse(getParameters());
        config = initConfig(appParameters.getConfigPath());
        initLogging(config);

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        HistoryStorage historyStorage = new JsonHistoryStorage(config.getHistoryFilePath());
        History history = initHistory(historyStorage);
        GreyBookStorage greyBookStorage = new JsonGreyBookStorage(userPrefs.getGreyBookFilePath());
        storage = new StorageManager(greyBookStorage, userPrefsStorage, historyStorage);

        model = initModelManager(storage, userPrefs, history);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s greybook
     * book and {@code userPrefs}. <br>
     * The data from the sample GreyBook will be used instead if {@code storage}'s
     * GreyBook is not found, or an empty GreyBook will be used instead if errors
     * occur when reading {@code storage}'s GreyBook.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs, ReadOnlyHistory history) {
        logger.info("Using data file : " + storage.getGreyBookFilePath());

        Optional<ReadOnlyGreyBook> greyBookOptional;
        ReadOnlyGreyBook initialData;
        try {
            greyBookOptional = storage.readGreyBook();
            if (!greyBookOptional.isPresent()) {
                logger.info("Creating a new data file " + storage.getGreyBookFilePath()
                        + " populated with a sample GreyBook.");
            }
            initialData = greyBookOptional.orElseGet(SampleDataUtil::getSampleGreyBook);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getGreyBookFilePath() + " could not be loaded."
                    + " Will be starting with an empty GreyBook.");
            initialData = new GreyBook();
        }

        return new ModelManager(initialData, userPrefs, history);
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    /**
     * Returns a {@code Config} using the file at {@code configFilePath}. <br>
     * The default file path {@code Config#DEFAULT_CONFIG_FILE} will be used instead
     * if {@code configFilePath} is null.
     */
    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed;

        configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            if (!configOptional.isPresent()) {
                logger.info("Creating new config file " + configFilePathUsed);
            }
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataLoadingException e) {
            logger.warning("Config file at " + configFilePathUsed + " could not be loaded."
                    + " Using default config properties.");
            initializedConfig = new Config();
        }

        // Update config file in case it was missing to begin with or there are
        // new/unused fields
        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs
     * file path, or a new {@code UserPrefs} with default configuration if errors
     * occur when reading from the file.
     */
    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (!prefsOptional.isPresent()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning(
                    "Preference file at " + prefsFilePath + " could not be loaded." + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        // Update prefs file in case it was missing to begin with or there are
        // new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    protected History initHistory(HistoryStorage storage) {
        Path historyFilePath = storage.getHistoryFilePath();
        logger.info("Using history file : " + historyFilePath);

        History initializedHistory;
        try {
            Optional<History> historyOptional = storage.readHistory();
            if (!historyOptional.isPresent()) {
                logger.info("Creating new history file " + historyFilePath);
            }
            initializedHistory = historyOptional.orElse(new History());
        } catch (DataLoadingException e) {
            logger.warning("History file at " + historyFilePath + " could not be loaded." + " Using default history.");
            initializedHistory = new History();
        }

        // Update history file in case it was missing to begin with or there are
        // new/unused fields
        try {
            storage.saveHistory(initializedHistory);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedHistory;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting GreyBook " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping GreyBook ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
