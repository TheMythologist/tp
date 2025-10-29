package greynekos.greybook.ui;

import java.util.logging.Logger;

import greynekos.greybook.commons.core.LogsCenter;
import greynekos.greybook.model.person.AttendanceStatus;
import greynekos.greybook.model.person.Person;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * Panel containing the list of persons.
 */
public class PersonTablePanel extends UiPart<Region> {
    private static final String FXML = "PersonTablePanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonTablePanel.class);

    @FXML
    private TableView<Person> personTableView;

    @FXML
    private TableColumn<Person, String> indexColumn;

    @FXML
    private TableColumn<Person, String> nameColumn;

    @FXML
    private TableColumn<Person, String> studentIdColumn;

    @FXML
    private TableColumn<Person, String> emailColumn;

    @FXML
    private TableColumn<Person, String> phoneColumn;

    @FXML
    private TableColumn<Person, String> tagsColumn;

    @FXML
    private TableColumn<Person, String> attendanceStatusColumn;

    public class PersonTableCellWithTooltip extends TableCell<Person, String> {
        private final Tooltip tooltip = new Tooltip();

        public PersonTableCellWithTooltip() {
            super();
            tooltip.setShowDelay(Duration.millis(500));
            setTooltip(tooltip);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null || item.isEmpty()) {
                setText(null);
                tooltip.setText(null);
            } else {
                setText(item);
                tooltip.setText(item);
            }
        }
    }

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonTablePanel(ObservableList<Person> personList) {
        super(FXML);
        indexColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            public void updateIndex(int index) {
                super.updateIndex(index);
                if (isEmpty() || index < 0) {
                    setText(null);
                } else {
                    setText(Integer.toString(index + 1)); // +1 to make it 1-based index
                }
            }
        });

        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName().toString()));
        nameColumn.setCellFactory(col -> new PersonTableCellWithTooltip());

        studentIdColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getStudentID().toString()));
        studentIdColumn.setCellFactory(col -> new PersonTableCellWithTooltip());

        emailColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail().toString()));
        emailColumn.setCellFactory(col -> new PersonTableCellWithTooltip());

        phoneColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone().toString()));
        phoneColumn.setCellFactory(col -> new PersonTableCellWithTooltip());

        /*
         * This cellValueFactory is required, as the string is passed to the item
         * argument in the updateItem function for the cellFactory below. Otherwise the
         * tag chips will not get added.
         */
        tagsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTags().stream()
                .map(tag -> tag.tagName).reduce((x, y) -> x + "," + y).orElse("")));

        // Custom cell factory for tags to display them as styled chips
        tagsColumn.setCellFactory(col -> new TableCell<Person, String>() {
            private final FlowPane flowPane = new FlowPane();

            {
                flowPane.setId("tags");
                flowPane.setHgap(4);
                flowPane.setVgap(4);
                flowPane.setAlignment(Pos.CENTER_LEFT);
                setGraphic(flowPane);

                widthProperty().addListener((obs, oldW, newW) -> flowPane.setPrefWrapLength(newW.doubleValue()));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.isEmpty()) {
                    flowPane.getChildren().clear();
                    setGraphic(null);
                } else {
                    // Reuse existing labels if possible
                    String[] tags = item.split(",");
                    int existingCount = flowPane.getChildren().size();

                    // Adjust number of labels to match tag count
                    if (existingCount > tags.length) {
                        flowPane.getChildren().remove(tags.length, existingCount);
                    } else {
                        for (int i = existingCount; i < tags.length; i++) {
                            Label label = new Label();
                            Tooltip tooltip = new Tooltip();
                            tooltip.setShowDelay(Duration.millis(500));
                            label.setTooltip(tooltip);
                            flowPane.getChildren().add(label);
                        }
                    }

                    // Update label texts & tooltips
                    for (int i = 0; i < tags.length; i++) {
                        String tagName = tags[i].trim();
                        Label label = (Label) flowPane.getChildren().get(i);
                        label.setText(tagName);
                        label.getTooltip().setText(tagName);
                    }
                    flowPane.setPrefWrapLength(getWidth()); // Wrap based on cell width
                    setGraphic(flowPane);
                }
            }
        });

        attendanceStatusColumn.setCellValueFactory(cellData -> {
            AttendanceStatus status = cellData.getValue().getAttendance();
            String display = (status.value == AttendanceStatus.Status.NONE) ? "" : status.toString();
            return new SimpleStringProperty(display);
        });
        attendanceStatusColumn.setCellFactory(col -> new PersonTableCellWithTooltip());

        personTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        personTableView.setItems(personList);
    }
}
