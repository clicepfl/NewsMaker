package ch.clic.newsmaker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  The Format class represent all the default (or not) parameters that the document will need, as languages, presets,
 *  the HTML base template and all the assets needed to construct the final html file
 */
public class Format {

    private final File baseFile;

    /**
     * A Preset for the field with preconfigured parameters like background color, image url or description
     *
     * @param name the name of the preset
     * @param template the template associated with the preset
     * @param sectionTag the section in which the preset belong
     * @param parameters all the preconfigured parameters of the preset (a value for each tag of the <code>Tags</code> enum)
     */
    public record Preset(String name, String template, String sectionTag, Map<Tags, String> parameters){

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * The tags identify a part of the template to remplace with a text value
     * <p>
     * For exemple, the <code>@NEWS_TITLE</code> tag in the html base template will be replaced by the corresponding title of a news
     */
    public enum Tags {
        BACKGROUND_COLOR,
        TEXT_COLOR,
        NEWS_TITLE(true),
        NEWS_IMAGE_URL,
        NEWS_DESCRIPTION(true),
        NEWS_DATE(true),
        NEWS_DETAIL_LABEL(true),
        NEWS_DETAILS_URL,
        NEWS_IMAGE;

        // this boolean identify if the tag will have different value according to all the possible language defined
        public final boolean isLanguageVariant;

        Tags() {
            isLanguageVariant = false;
        }

        Tags(boolean isLanguageVariant) {
            this.isLanguageVariant = isLanguageVariant;
        }

        @Override
        public String toString() {
            return "@" + name();
        }
    }


    public final StringProperty baseProperty = new SimpleStringProperty(); // the first html template in which elements will be inserted
    public String defaultNewsTemplate; // the template of a default div
    public String img; // html for how to display an img div by default
    public List<Preset> presets; // list of all preconfigured presets

    public ObservableList<String> languages; // set of all languages in which the document will be redacted

    /**
     * Constructor of a <code>Format</code> object
     *
     * @param baseFile the first html template in which elements will be inserted
     * @param defaultNewsTemplate the template of a default div
     * @param img html for how to display an img div by default
     * @param presets list of all preconfigured presets
     * @param languages set of all languages in which the document will be redacted
     */
    public Format(File baseFile, String defaultNewsTemplate, String img, List<Preset> presets, List<String> languages) throws IOException {
        this.baseFile = baseFile;
        this.baseProperty.set(FileManager.readContentOfFile(baseFile));
        this.defaultNewsTemplate = defaultNewsTemplate;
        this.img = img;
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
    static public Format fromJSON(String path) throws IOException, URISyntaxException {

        String json = FileManager.readContentOfFile(path);

        ObjectMapper om = new ObjectMapper();
        JsonNode node = om.readTree(json);

        String defaultNewsTemplate;

        File baseFile = FileManager.openFile(node.get("baseFilePath").asText());

        defaultNewsTemplate = FileManager.readContentOfFile(node.get("defaultNewsTemplateFilePath").asText());
        String img = FileManager.readContentOfFile(node.get("imgFilePath").asText());

        List<Preset> presets = extractPresets(node);

        List<String> languages = new ArrayList<>();
        for (JsonNode jsonNode : node.get("languages")) {
            languages.add(jsonNode.asText());
        }

        return new Format(baseFile, defaultNewsTemplate, img, presets, languages);
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

        for (JsonNode nodePreset : jsonNode) {

            String name = nodePreset.get("name").asText();
            String sectionTag = nodePreset.get("sectionTag").asText();
            String templateFile = nodePreset.get("templateFile").asText();

            String template = FileManager.readContentOfFile(templateFile);

            HashMap<Tags, String> parameters = new HashMap<>();
            JsonNode nodeParams = nodePreset.get("parameters");

            Iterator<String> fieldsIt = nodeParams.fieldNames();
            while (fieldsIt.hasNext()) {
                String fieldName = fieldsIt.next();
                parameters.put(Tags.valueOf(fieldName), nodeParams.get(fieldName).asText());
            }

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

    public void saveFormat() {
        FileManager.saveInFile(baseProperty.get(), baseFile);
    }
}
