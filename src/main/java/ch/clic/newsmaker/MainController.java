package ch.clic.newsmaker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.scene.web.WebView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainController {

    private static final String DEFAULT_SECTION = "NEWS";
    private static final String SECTION_TAG = "section";
    private static final String TEMPLATE_TAG = "template";
    private static final String LANGUAGE_CONSTANT_PROPERTIES_TAG = "language-constant-properties";
    private static final String LANGUAGE_VARIABLE_PROPERTIES_TAG = "language-variable-properties";
    private static final FileChooser.ExtensionFilter HTML_FILTER = new FileChooser.ExtensionFilter("HTML files", "*.html", "*.HTML");
    private static final FileChooser.ExtensionFilter NMKR_FILTER = new FileChooser.ExtensionFilter("NewsMaker files (.nmkr)", "*.nmkr");
    private final HashMap<String, List<NewsFieldBean>> fieldSectionMap = new HashMap<>(); //fields are sorted by sections
    private static Format.Preset defaultPreset;
    private final ObjectProperty<Format> formatProperty = new SimpleObjectProperty<>();

    private final ObjectProperty<File> recentFileProperty = new SimpleObjectProperty<>();
    @FXML
    public WebView preview; // used for previewing the HTML file
    public AnchorPane mainPane;
    public TextArea formatEditor;
    @FXML
    private VBox fields;
    private String previousSavedHTML;

    public MainController() throws IOException {

        formatProperty.setValue(Format.recentFormat());

        defaultPreset = formatProperty.get().presets.get(0);

        fieldSectionMap.put(DEFAULT_SECTION, new ArrayList<>());
        for (Format.Preset preset : formatProperty.get().presets) {
            if (preset.sectionTag() != null) {
                fieldSectionMap.putIfAbsent(preset.sectionTag(), new ArrayList<>());
            }
        }

        previousSavedHTML = buildHTML();
    }


    /**
     * Called right after window creation
     */
    @FXML
    public void initialize() {
        formatEditor.textProperty().bindBidirectional(formatProperty.get().getBaseProperty());
    }


    /**
     * Add a field in the default section
     */
    @FXML
    protected void addFieldButtonClick(){
        NewsFieldBean fieldBean = createNewsFieldBean(DEFAULT_SECTION);
        fieldBean.updateWithPreset(defaultPreset);
    }

    /**
     * Create a new field bean with bindings and corresponding JavaFX nodes
     *
     * @param section the section in which the bean belong
     * @return a new <code>NewsFieldBean</code>
     */
    private NewsFieldBean createNewsFieldBean(String section) {
        NewsFieldBean fieldBean = new NewsFieldBean(defaultPreset, formatProperty.get());
        fieldSectionMap.putIfAbsent(section, new ArrayList<>());
        fieldSectionMap.get(section).add(fieldBean);
        fieldBean.setSection(section);
        fieldBean.formatProperty.bind(formatProperty);
        fields.getChildren().add(createField(fieldBean));
        return fieldBean;
    }

    /**
     * Export the HTML file
     */
    @FXML
    protected void exportFile() {
        FileChooser fileChooser = createFileChooser("Export file", HTML_FILTER);
        File file = fileChooser.showSaveDialog(fields.getScene().getWindow());
        if (file == null) return;
        FileManager.saveInFile(buildHTML(), file);
    }

    /**
     * Open a base file for the HTML to be formatted
     */
    @FXML
    protected void openFormat() {
        FileChooser fileChooser = createFileChooser("Open file",  NMKR_FILTER);
        File file = fileChooser.showOpenDialog(fields.getScene().getWindow());
        if (file == null) return;

        try {
            formatProperty.setValue(Format.fromJSON(file));
        } catch (Exception ignored) {} // the file exist because it is chosen by the user via dialog
    }

    /**
     *  Quick way to save if already saved before
     */
    @FXML
    public void save() throws IOException {
        if (hasNotChanged()) return;

        if (recentFileProperty.isNotNull().get()) {
            saveInFile(recentFileProperty.get());
        } else {
            saveAs();
        }
        formatProperty.get().saveFormat();
    }

    /**
     * The user can choose a file by a dialog and save the current edition of the document
     */
    @FXML
    public void saveAs() {
        FileChooser fileChooser = createFileChooser("Save as", NMKR_FILTER);
        File file = fileChooser.showSaveDialog(fields.getScene().getWindow());
        if (file == null) return;
        recentFileProperty.setValue(file);
        saveInFile(file);
    }

    /**
     * Return true if the content of the HTML changed, else false
     * @return true if the content of the HTML changed, else false
     */
    public boolean hasNotChanged() {
        return buildHTML().equals(this.previousSavedHTML);
    }

    /**
     * Save all <code>NewsFieldBean</code> object in a json file (.nmkr)
     *
     * @param file the file where to save the beans
     */
    private void saveInFile(File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.createObjectNode();

        // save each section
        for (String sectionTag : fieldSectionMap.keySet()) {
            ArrayNode sectionNode = objectMapper.createArrayNode();

            // each field of the section
            for (NewsFieldBean newsFieldBean : fieldSectionMap.get(sectionTag)) {
                ObjectNode newsFieldBeanNode = objectMapper.createObjectNode();
                newsFieldBeanNode.put(SECTION_TAG, newsFieldBean.getSection());
                newsFieldBeanNode.put(TEMPLATE_TAG, newsFieldBean.getTemplate());

                // save language-constant properties
                ObjectNode languageConstantPropertiesNode = objectMapper.createObjectNode();
                newsFieldBean.getLanguageConstantPropertiesMap()
                        .forEach((tag, value) -> languageConstantPropertiesNode.put(tag.name(), value.get()));

                newsFieldBeanNode.set(LANGUAGE_CONSTANT_PROPERTIES_TAG, languageConstantPropertiesNode);

                // save language-variable properties
                ObjectNode languageVariablePropertiesNode = objectMapper.createObjectNode();

                for (String language : formatProperty.get().languages) {
                    ObjectNode languageNode = objectMapper.createObjectNode();
                    newsFieldBean.getLanguageVariablePropertiesMap()
                            .get(language)
                            .forEach((tag, value) -> languageNode.put(tag.name(), value.get()));
                    languageVariablePropertiesNode.set(language, languageNode);
                }

                newsFieldBeanNode.set(LANGUAGE_VARIABLE_PROPERTIES_TAG, languageVariablePropertiesNode);
                sectionNode.add(newsFieldBeanNode);
            }
            rootNode.set(sectionTag, sectionNode);
        }

        FileManager.saveInFile(rootNode.toPrettyString(), file);
        previousSavedHTML = buildHTML();
    }

    /**
     * Open a .nmkr file
     *
     * @throws IOException if the file is corrupted
     */
    @FXML
    public void openFile() throws IOException {
        FileChooser fileChooser = createFileChooser("Open file", NMKR_FILTER);
        File file = fileChooser.showOpenDialog(fields.getScene().getWindow());

        if (file == null) return;

        recentFileProperty.setValue(file);

        fieldSectionMap.forEach((section, list) -> list.clear());      //clear sections
        fields.getChildren().clear();                                  //clear javaFX nodes linked to beans

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(file);

        rootNode.fieldNames().forEachRemaining(sectionTag -> {
            for (JsonNode newsNode : rootNode.get(sectionTag)) {
                NewsFieldBean newsFieldBean = createNewsFieldBean(sectionTag);

                newsFieldBean.setSection(newsNode.get(SECTION_TAG).asText());
                newsFieldBean.setTemplate(newsNode.get(TEMPLATE_TAG).asText());

                // create JSON nodes for language constant properties (like URL, image, ...)
                JsonNode languageConstantNode = newsNode.get(LANGUAGE_CONSTANT_PROPERTIES_TAG);

                languageConstantNode.fieldNames()
                        .forEachRemaining(param -> {
                            Format.Tag tag = new Format.Tag(param, false);
                            newsFieldBean.setPropertyValue(tag, languageConstantNode.get(param).asText());
                        });

                // create JSON for language variable properties (like description, titles, date, ...)
                JsonNode languageVariableNode = newsNode.get(LANGUAGE_VARIABLE_PROPERTIES_TAG);

                languageVariableNode.fieldNames().forEachRemaining(
                        language -> languageVariableNode.get(language).fieldNames().forEachRemaining(
                                param -> {
                                    Format.Tag tag = new Format.Tag(param, true);
                                    newsFieldBean.setPropertyValue(tag, language, languageVariableNode.get(language).get(param).asText());
                                }
                        )
                );
            }
        });

        previousSavedHTML = buildHTML();
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

        String base = formatProperty.get().getBase();

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

        for (String language : this.formatProperty.get().languages) {

            VBox vBox = new VBox();

            for (Map.Entry<Format.Tag, StringProperty> entry : fieldBean.getLanguageVariablePropertiesMap().get(language).entrySet()) {
                if (entry.getKey().isBigText()) {
                    vBox.getChildren().add(createTextWithContent(entry.getKey().presentationName(), entry.getValue()));
                } else {
                    vBox.getChildren().add(createFieldWithLabel(entry.getKey().presentationName(), entry.getValue()));
                }
            }

            Tab tab = new Tab(language.toLowerCase(), vBox);
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
                tabPane);

        for (Map.Entry<Format.Tag, StringProperty> entry : fieldBean.getLanguageConstantPropertiesMap().entrySet()) {
            vb.getChildren().add(createFieldWithLabel(entry.getKey().presentationName(), entry.getValue()));
        }

        vb.getStyleClass().add("field");
        VBox.setMargin(vb, new Insets(10,10,10,10));

        Button rb = new Button("delete");
        rb.setOnAction(event -> {
            fieldSectionMap.get(fieldBean.getSection()).remove(fieldBean);
            fields.getChildren().remove(vb);
        });
        rb.getStyleClass().add("delete-button");

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

    private TextArea createTextWithContent(String text, StringProperty toBind) {
        TextArea ta = new TextArea();
        ta.textProperty().bindBidirectional(toBind);
        ta.textProperty().set(text);
        return ta;
    }
}