package ch.clic.newsmaker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  The Format class represent all the default (or not) parameters that the document will need, as languages, presets,
 *  the HTML base template and all the assets needed to construct the final html file
 */
public class Format {

    /**
     * The tags identify a part of the template to remplace with a text value
     * <p>
     * For exemple, the <code>@NEWS_TITLE</code> tag in the html base template will be replaced by the corresponding title of a news
     */
    public record Tag(String name, Boolean isLanguageVariant) {
        @Override
        public String toString() {
            return '@' + name();
        }

        public String presentationName() {
            return name().toLowerCase().replaceAll("[_#@]", " ");
        }

        public boolean isBigText() {
            return name.contains("DESCRIPTION");
        }
    }

    /**
     * A Preset for the field with preconfigured parameters like background color, image url or description
     *
     * @param name the name of the preset
     * @param template the template associated with the preset
     * @param sectionTag the section in which the preset belong
     * @param parameters all the preconfigured parameters of the preset (a value for each tag of the <code>Tags</code> enum)
     */
    public record Preset(String name, String template, String sectionTag, Map<Tag, List<String>> parameters) {

        @Override
        public String toString() {
            return name;
        }
    }

    private static final Path LAST_USED_FOLDER_PATH = Paths.get("./NewsMakerFormat");
    private static final String DEFAULT_BASE_FILE_NAME = "base.html";
    private static final String DEFAULT_NEWS_TEMPLATE_FILE_NAME = "default_news_template.html";
    private static final String DEFAULT_IMG_FILE_NAME = "image.html";
    private final File baseFile, imgFile, newsTemplateFile;
    public final StringProperty baseProperty = new SimpleStringProperty(); // the first html template in which elements will be inserted
    public String defaultNewsTemplate; // the template of a default div
    public String img; // html for how to display an img div by default
    public List<Preset> presets; // list of all preconfigured presets

    public ObservableList<String> languages; // set of all languages in which the document will be redacted

    /**
     * Constructor of a <code>Format</code> object
     *
     * @param presets list of all preconfigured presets
     * @param languages set of all languages in which the document will be redacted
     */
    public Format(String defaultFolderPath, List<Preset> presets, List<String> languages) throws IOException {

        this.baseFile = new File(LAST_USED_FOLDER_PATH.resolve(DEFAULT_BASE_FILE_NAME).toString());
        this.imgFile = new File(LAST_USED_FOLDER_PATH.resolve(DEFAULT_IMG_FILE_NAME).toString());
        this.newsTemplateFile = new File(LAST_USED_FOLDER_PATH.resolve(DEFAULT_NEWS_TEMPLATE_FILE_NAME).toString());

        // try open the folder with the last saved format. Open default files if it fails
        try {
            this.baseProperty.set(FileManager.readContentOfFile(baseFile));
            this.img = FileManager.readContentOfFile(imgFile);
            this.defaultNewsTemplate = FileManager.readContentOfFile(newsTemplateFile);
        } catch (Exception e) {
            Path defaultFolder = Paths.get(defaultFolderPath);
            this.baseProperty.set(FileManager.readContentOfResource(defaultFolder.resolve(DEFAULT_BASE_FILE_NAME).toString()));
            this.img = FileManager.readContentOfResource(defaultFolder.resolve(DEFAULT_IMG_FILE_NAME).toString());
            this.defaultNewsTemplate = FileManager.readContentOfResource(defaultFolder.resolve(DEFAULT_NEWS_TEMPLATE_FILE_NAME).toString());
            saveFormat();
        }

        this.presets = presets;
        this.languages = FXCollections.observableList(languages);
    }


    /**
     * Construct a <code>Format</code> object from a json file (config.json by default)
     *
     * @param path the path of the file in which the json has to be read
     * @return a <code>Format</code> object
     * @throws IOException throws <code>IOException</code> in case of an input-output exception (the file doesn't exist)
     */
    static public Format fromJSON(String path) throws IOException {

        String json = FileManager.readContentOfResource(path);

        ObjectMapper om = new ObjectMapper();
        JsonNode node = om.readTree(json);

        List<Preset> presets = extractPresets(node);

        String defaultFolderPath = node.get("defaultFolderPath").asText();

        List<String> languages = new ArrayList<>();
        for (JsonNode jsonNode : node.get("languages")) {
            languages.add(jsonNode.asText());
        }

        return new Format(defaultFolderPath, presets, languages);
    }

    /**
     * Extract presets from a json object
     *
     * @param node the json object containing the presets configurations
     * @return the list of presets extracted
     * @throws IOException throws <code>IOException</code> in case of an input-output exception
     */
    static private List<Preset> extractPresets(JsonNode node) throws IOException {
        List<Preset> presets = new ArrayList<>();
        JsonNode jsonNode = node.get("presets");

        // iter on all preset node
        for (JsonNode nodePreset : jsonNode) {

            String name = nodePreset.get("name").asText();
            String sectionTag = nodePreset.get("sectionTag").asText();
            String templateFile = nodePreset.get("templateFile").asText();
            String template = FileManager.readContentOfResource(templateFile);

            // extract all parameters from the node
            Map<Tag, List<String>> parameters = new LinkedHashMap<>();
            JsonNode nodeParams = nodePreset.get("parameters");

            nodeParams.fieldNames().forEachRemaining(fieldName -> {
                JsonNode field = nodeParams.get(fieldName);
                Tag tag = new Tag(fieldName, field.isArray());
                List<String> array = new ArrayList<>();
                if (field.isArray()) {
                    field.forEach(n -> array.add(n.asText()));
                } else {
                    array.add(field.asText());
                }
                parameters.put(tag, array);
            });

            presets.add(new Format.Preset(name, template, sectionTag, parameters));

        }
        return presets;
    }

    public StringProperty getBaseProperty() {
        return baseProperty;
    }

    public String getBase() {
        return baseProperty.get();
    }

    /**
     * Save the format in a "last used folder".
     *
     * @throws IOException if an I/O error occurs
     */
    public void saveFormat() throws IOException {
        Files.createDirectories(LAST_USED_FOLDER_PATH);
        FileManager.saveInFile(baseProperty.get(),  baseFile);
        FileManager.saveInFile(img,                 imgFile);
        FileManager.saveInFile(defaultNewsTemplate, newsTemplateFile);
    }
}
