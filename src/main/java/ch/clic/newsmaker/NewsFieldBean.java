package ch.clic.newsmaker;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

public class NewsFieldBean {

    private final StringProperty section = new SimpleStringProperty(); //the section where the field is

    private final Map<String, Map<Format.Tags, StringProperty>> languageVariantPropertiesMap; // all language dependant properties (like description or title) are sorted by language here

    private final Map<Format.Tags, StringProperty> languageInvariantPropertiesMap; // the others properties are stored here

    private String template = "";

    public final ObjectProperty<Format> formatProperty = new SimpleObjectProperty<>();

    public NewsFieldBean() {
        languageVariantPropertiesMap = new HashMap<>();

        languageInvariantPropertiesMap = new HashMap<>();
        for (Format.Tags tag : Format.Tags.values()) {
            if (!tag.isLanguageVariant) {
                languageInvariantPropertiesMap.put(tag, new SimpleStringProperty());
            }
        }

        this.formatProperty.addListener((o)-> setLanguagesPropertiesMap());
    }


    private void setLanguagesPropertiesMap() {
        for (String language : formatProperty.get().languages) {
            if (languageVariantPropertiesMap.containsKey(language))
                continue;
            HashMap<Format.Tags, StringProperty> map = new HashMap<>();
            for (Format.Tags tag : Format.Tags.values()) {
                if (tag.isLanguageVariant)
                    map.put(tag, new SimpleStringProperty());
            }

            languageVariantPropertiesMap.put(language, map);
        }
    }

    public String getPropertyValue(Format.Tags tag) {
        if (!tag.isLanguageVariant) {
            return languageInvariantPropertiesMap.get(tag).get();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String getPropertyValue(String language, Format.Tags tag) {
        if (!tag.isLanguageVariant)
            return languageInvariantPropertiesMap.get(tag).get();
        return languageVariantPropertiesMap.get(language).get(tag).get();
    }

    public StringProperty getProperty(Format.Tags tag) {
        if (!tag.isLanguageVariant) {
            return languageInvariantPropertiesMap.get(tag);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public StringProperty getProperty(String language, Format.Tags tag) {
        if (!tag.isLanguageVariant)
            return languageInvariantPropertiesMap.get(tag);
        return languageVariantPropertiesMap.get(language).get(tag);
    }

    public void setPropertyValue(Format.Tags tag, String value) {
        if (tag.isLanguageVariant) {
            for (String language : formatProperty.get().languages) {
                languageVariantPropertiesMap.get(language).get(tag).setValue(value);
            }
        } else {
            languageInvariantPropertiesMap.get(tag).setValue(value);
        }
    }

    public void setPropertyValue(String language, Format.Tags tag, String value) {
        if (tag.isLanguageVariant) {
            languageVariantPropertiesMap.putIfAbsent(language, new HashMap<>());
            languageVariantPropertiesMap.get(language).putIfAbsent(tag, new SimpleStringProperty());
            languageVariantPropertiesMap.get(language).get(tag).setValue(value);
        } else {
            languageInvariantPropertiesMap.get(tag).setValue(value);
        }
    }
    public Map<Format.Tags, StringProperty> getLanguageConstantPropertiesMap() {
        return languageInvariantPropertiesMap;
    }
    public Map<String, Map<Format.Tags, StringProperty>> getLanguageVariablePropertiesMap() {
        return languageVariantPropertiesMap;
    }

    public void updateWithPreset(Format.Preset preset) {
        for (Format.Tags param : preset.parameters().keySet()) {
            String value = preset.parameters().get(param);
            setPropertyValue(param, value);
        }
        setSection(preset.sectionTag());
        setTemplate(preset.template());
    }

    public StringProperty sectionProperty() {
        return section;
    }

    public String getSection() {
        return section.get().toUpperCase();
    }

    public void setSection(String value) {
        section.setValue(value);
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String value) {
        this.template = value;
    }

    /**
     * Build the HTML of the field by replacing all tags in the base template with corresponding value
     *
     * @param language the HTML can be build in any available language
     * @return a String containing the HTML
     */
    public String getHTML(String language) {

        String formatted = template;

        // replace all tags in the base template with corresponding value
        for (Format.Tags tag : Format.Tags.values()) {
            if (tag == Format.Tags.NEWS_IMAGE) continue;
            formatted = formatted.replace(tag.toString(), getPropertyValue(language, tag));
        }

        return formatted.replace(Format.Tags.NEWS_IMAGE.toString(),
                getPropertyValue(Format.Tags.NEWS_IMAGE_URL).isEmpty()
                        ? ""
                        : formatProperty.get().img.replace(Format.Tags.NEWS_IMAGE_URL.toString(), getPropertyValue(Format.Tags.NEWS_IMAGE_URL)));
    }
}
