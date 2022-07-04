package ch.clic.newsmaker;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

public class MainController {

    private static final String DEFAULT_SECTION = "NEWS";
    private final HashMap<String, ArrayList<NewsFieldBean>> fieldSectionMap = new HashMap<>(); //fields are sorted by sections

    private final IntegerProperty numberOfFields = new SimpleIntegerProperty(0);
    @FXML
    public WebView preview; // used for previewing the HTML file
    @FXML
    private VBox fields;


    public MainController() {
        fieldSectionMap.put(DEFAULT_SECTION, new ArrayList<>());
        for (Format.Preset preset : Format.Preset.values()) {
            if (preset.sectionTag != null) {
                fieldSectionMap.putIfAbsent(preset.sectionTag, new ArrayList<>());
            }
        }
    }

    /**
     * Add a field in the default section
     */
    @FXML
    protected void addFieldButtonClick(){
        NewsFieldBean nfb = new NewsFieldBean();
        fieldSectionMap.get(DEFAULT_SECTION).add(nfb);
        nfb.setSection(DEFAULT_SECTION);
        fields.getChildren().add(fields.getChildren().size()-1, createField(nfb));
        numberOfFields.setValue(numberOfFields.intValue()+1);
    }

    /**
     * Export the HTML file
     */
    @FXML
    protected void exportFile() {
        FileChooser fileChooser = createFileChooser("Export file");
        File file = fileChooser.showSaveDialog(fields.getScene().getWindow());
        try (Writer wr = new FileWriter(file)){
            wr.write(buildHTML());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void openFile(){
        FileChooser fileChooser = createFileChooser("Open file");
        File file = fileChooser.showOpenDialog(fields.getScene().getWindow());
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
    private String buildSectionHTML(String section, Format.Language language) {

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

        String base = Format.BASE;

        for (Format.Language language : Format.Language.values()) {
            for (String section : fieldSectionMap.keySet()) {
                String sectionHTML = buildSectionHTML(section, language).indent(12);
                base = base.replace('@'+section.toUpperCase()+'#'+language.toString().toUpperCase(), sectionHTML);
            }
        }
        return base;
    }

    /**
     * Create a file dialog to choose a file in the filesystem
     * @param title title of the dialog
     * @return the FileChooser
     */
    private FileChooser createFileChooser(String title) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("HTML files (.html)","*.html"));
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

        for (Format.Language language : Format.Language.values()) {
            TextArea ta = new TextArea();
            ta.textProperty().bindBidirectional(fieldBean.getProperty(language, Format.Tags.NEWS_DESCRIPTION));

            Tab tab = new Tab(language.toString(), new VBox(
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
        presetChoiceBox.setItems(FXCollections.observableArrayList(Format.Preset.values()));
        presetChoiceBox.setValue(Format.Preset.NONE);
        presetChoiceBox.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            if (!newValue.equals(Format.Preset.NONE)) {
                for (Format.Tags param : newValue.parameters.keySet()) {
                    String value = newValue.parameters.get(param);
                    fieldBean.setPropertyValue(param, value);
                }
                fieldBean.setSection(newValue.sectionTag);
                fieldBean.base = newValue.template;
            } else {
                fieldBean.setSection(DEFAULT_SECTION);
                fieldBean.base = Format.DEFAULT_NEWS_TEMPLATE;
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