package ch.clic.newsmaker;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.scene.web.WebView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class MainController {

    private static final String CONFIG_FILE_PATH = "/config/config.json";
    private static final String DEFAULT_SECTION = "NEWS";
    private final HashMap<String, ArrayList<NewsFieldBean>> fieldSectionMap = new HashMap<>(); //fields are sorted by sections
    private static Format.Preset defaultPreset;
    private final IntegerProperty numberOfFields = new SimpleIntegerProperty(0);

    private final ObjectProperty<Format> formatProperty = new SimpleObjectProperty<>();
    @FXML
    public WebView preview; // used for previewing the HTML file
    @FXML
    private VBox fields;

    public MainController() throws URISyntaxException, IOException {
        URL configURL = Format.class.getResource(CONFIG_FILE_PATH);

        assert configURL != null;
        formatProperty.setValue(Format.fromJSON(new File(configURL.toURI())));

        defaultPreset = formatProperty.get().presets.get(0);

        fieldSectionMap.put(DEFAULT_SECTION, new ArrayList<>());
        for (Format.Preset preset : formatProperty.get().presets) {
            if (preset.sectionTag() != null) {
                fieldSectionMap.putIfAbsent(preset.sectionTag(), new ArrayList<>());
            }
        }
    }

    /**
     * Add a field in the default section
     */
    @FXML
    protected void addFieldButtonClick(){
        NewsFieldBean fieldBean = new NewsFieldBean();
        fieldBean.formatProperty.bind(formatProperty);
        fieldSectionMap.get(DEFAULT_SECTION).add(fieldBean);
        fieldBean.setSection(DEFAULT_SECTION);
        fieldBean.setTemplate(formatProperty.get().defaultNewsTemplate);

        fieldBean.updateWithPreset(defaultPreset);

        fields.getChildren().add(fields.getChildren().size()-1, createField(fieldBean));
        numberOfFields.setValue(numberOfFields.intValue()+1);
    }

    /**
     * Export the HTML file
     */
    @FXML
    protected void exportFile() {
        FileChooser fileChooser = createFileChooser("Export file", new FileChooser.ExtensionFilter("HTML files", "*.html", "*.HTML"));
        File file = fileChooser.showSaveDialog(fields.getScene().getWindow());
        saveInFile(buildHTML(), file);
    }

    @FXML
    protected void openFile() throws IOException {
        FileChooser fileChooser = createFileChooser("Open file",  new FileChooser.ExtensionFilter("NewsMaker files (.nmkr)", "*.nmkr"));
        File file = fileChooser.showOpenDialog(fields.getScene().getWindow());
        formatProperty.setValue(Format.fromJSON(file));
    }

    @FXML
    public void saveAs() {
        FileChooser fileChooser = createFileChooser("Save as", new FileChooser.ExtensionFilter("NewsMaker files (.nmkr)", "*.nmkr"));
        File file = fileChooser.showSaveDialog(fields.getScene().getWindow());

        String content;
        try {
            content = formatProperty.get().toJSON();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            content = e.getMessage();
        }
        saveInFile(content, file);
    }

    /**
     * Save string content in file on disk
     *
     * @param content the content to write
     * @param file the file where to write
     */
    private void saveInFile(String content, File file) {
        if (Objects.isNull(file)) return;
        try (Writer wr = new FileWriter(file)) {
            wr.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void refreshPreview() {
        preview.getEngine().loadContent(buildHTML());
    }

    /**
     * Build the HTML for a given section in a given language
     * @param section the section
     * @param language the language
     * @return a String containing the HTML of the section
     */
    private String buildSectionHTML(String section, String language) {

        StringBuilder stringBuilder = new StringBuilder();

        for (NewsFieldBean field : fieldSectionMap.get(section)) {
            stringBuilder.append(field.getHTML(language));
        }

        return stringBuilder.toString();

    }

    /**
     * Build all the HTML of the file
     * @return a String containing the HTML of the file
     */
    private String buildHTML() {

        String base = formatProperty.get().base;

        for (String language : formatProperty.get().languages) {
            for (String section : fieldSectionMap.keySet()) {
                String sectionHTML = buildSectionHTML(section, language).indent(12);
                base = base.replace('@'+section.toUpperCase()+'#'+ language.toUpperCase(), sectionHTML);
            }
        }
        return base;
    }

    /**
     * Create a file dialog to choose a file in the filesystem
     * @param title title of the dialog
     * @return the FileChooser
     */
    private FileChooser createFileChooser(String title, FileChooser.ExtensionFilter filter) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(filter);
        fc.setTitle(title);
        return fc;
    }

    /**
     * Create a JavaFX field component used to bring all the information for a cell
     *
     * @param fieldBean the JavaFX bean of a field
     * @return a VBox containing all the JavaFX component with properties bound to corresponding bean's properties
     */
    private VBox createField(NewsFieldBean fieldBean) {

        TabPane tabPane = new TabPane();

        for (String language : formatProperty.get().languages) {
            TextArea ta = new TextArea();
            ta.textProperty().bindBidirectional(fieldBean.getProperty(language, Format.Tags.NEWS_DESCRIPTION));

            Tab tab = new Tab(language.toLowerCase(), new VBox(
                    createFieldWithLabel("Titre: ", fieldBean.getProperty(language, Format.Tags.NEWS_TITLE)),
                    ta,
                    createFieldWithLabel("Date: ", fieldBean.getProperty(language, Format.Tags.NEWS_DATE)),
                    createFieldWithLabel("Détails texte: ", fieldBean.getProperty(language, Format.Tags.NEWS_DETAIL_LABEL))));

            tabPane.getTabs().add(tab);
        }

        tabPane.tabClosingPolicyProperty().setValue(TabPane.TabClosingPolicy.UNAVAILABLE);

        ChoiceBox<String> sectionChoiceBox = new ChoiceBox<>();
        sectionChoiceBox.setItems(FXCollections.observableArrayList(fieldSectionMap.keySet()));
        sectionChoiceBox.setValue(DEFAULT_SECTION);
        sectionChoiceBox.valueProperty().bindBidirectional(fieldBean.sectionProperty());

        fieldBean.sectionProperty().addListener((o, oldValue, newValue) -> {
            fieldSectionMap.get(oldValue).remove(fieldBean);
            fieldSectionMap.get(newValue).add(fieldBean);
        });

        ChoiceBox<Format.Preset> presetChoiceBox = new ChoiceBox<>();
        presetChoiceBox.setItems(FXCollections.observableArrayList(formatProperty.get().presets));
        presetChoiceBox.setValue(defaultPreset);
        presetChoiceBox.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            if (newValue != defaultPreset) {
                fieldBean.updateWithPreset(newValue);
            } else {
                fieldBean.setSection(DEFAULT_SECTION);
                fieldBean.setTemplate(formatProperty.get().defaultNewsTemplate);
            }
        });

        Button upButton = new Button("^");
        upButton.setOnAction(actionEvent -> moveField(fieldBean, -1));
        Button downButton = new Button("^");
        downButton.setOnAction(actionEvent -> moveField(fieldBean, 1));
        downButton.setRotate(180);

        VBox vb = new VBox(
                new HBox(new Label("Section: "), sectionChoiceBox,
                         new Label("Preset: "), presetChoiceBox,
                         new HBox(upButton, downButton)),
                tabPane,
                createFieldWithLabel("Image URL: ", fieldBean.getProperty(Format.Tags.NEWS_IMAGE_URL)),
                createFieldWithLabel("Détails URL: ", fieldBean.getProperty(Format.Tags.NEWS_DETAILS_URL)),
                new Separator(Orientation.HORIZONTAL));
                vb.getStyleClass().add("field");
        VBox.setMargin(vb, new Insets(10,10,10,10));

        Button rb = new Button("delete");
        rb.setOnAction(event -> {
            fieldSectionMap.get(fieldBean.getSection()).remove(fieldBean);
            fields.getChildren().remove(vb);
        });

        vb.getChildren().add(0, rb);
        return vb;
    }


    /**
     * Move the field up or down on the final HTML file
     *
     * @param field the field to move
     * @param delta the delta (essentially -1 or +1)
     */
    private void moveField(NewsFieldBean field, int delta) {
        int start = fieldSectionMap.get(field.getSection()).indexOf(field);
        int index = start + delta;
        index = crop(index, fieldSectionMap.get(field.getSection()).size()-1);

        if (start == index) return;

        fieldSectionMap.get(field.getSection()).remove(field);
        fieldSectionMap.get(field.getSection()).add(index, field);


    }
    private int crop(int n, int max) {
        return n < 0 ? 0 : (Math.min(n, max));
    }

    /**
     * Create a HBox with a Label and a TextField with textProperty bind to a StringProperty
     *
     * @param text the texte of the label
     * @param toBind the StringProperty to bind
     * @return the HBox
     */
    private HBox createFieldWithLabel(String text, StringProperty toBind) {
        TextField tf = new TextField();
        tf.textProperty().bindBidirectional(toBind);
        HBox hb =  new HBox(new Label(text), tf);
        hb.getStyleClass().add("HBox");
        return hb;
    }
}