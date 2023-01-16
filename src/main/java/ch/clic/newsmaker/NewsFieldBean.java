package ch.clic.newsmaker;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NewsFieldBean {

    private final StringProperty section = new SimpleStringProperty(); //the section where the field is

    private final Map<String, Map<Format.Tag, StringProperty>> languageVariantPropertiesMap; // all language dependant properties (like description or title) are sorted by language here

    private final Map<Format.Tag, StringProperty> languageInvariantPropertiesMap; // the others properties are stored here

    private String template = "";

    public final ObjectProperty<Format> formatProperty = new SimpleObjectProperty<>();

    public NewsFieldBean(Format.Preset preset, Format format) {

        formatProperty.setValue(format);

        languageVariantPropertiesMap = new HashMap<>();
        languageInvariantPropertiesMap = new LinkedHashMap<>();

        updateWithPreset(preset);
    }

    public Map<Format.Tag, StringProperty> getLanguageConstantPropertiesMap() {
        return languageInvariantPropertiesMap;
    }
    public Map<String, Map<Format.Tag, StringProperty>> getLanguageVariablePropertiesMap() {
        return languageVariantPropertiesMap;
    }

    /**
     * Given a preset, replace all corresponding values with the values of the preset, it's template and section tag
     * @param preset The preset which will serves as model
     */
    public void updateWithPreset(Format.Preset preset) {
        preset.parameters().forEach(this::setPropertyValue);
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
     * Set value of a language-constant property
     * @param tag the tag which identify the property
     * @param value the new value of the property
     */
    public void  setPropertyValue(Format.Tag tag, String value) {
        if (tag.isLanguageVariant())
            throw new IllegalArgumentException("the tagged property needs values for each language");

        languageInvariantPropertiesMap.putIfAbsent(tag, new SimpleStringProperty());
        languageInvariantPropertiesMap.get(tag).set(value);
    }

    /**
     * Set value of a language-variant property in a given language
     * @param tag the tag which identify the property
     * @param language the language in which the property will be set
     * @param value the new value of the property in a given language
     */
    public void setPropertyValue(Format.Tag tag, String language, String value) {
        if (!tag.isLanguageVariant())
            throw new IllegalArgumentException("the tagged property change value with language");

        languageVariantPropertiesMap.putIfAbsent(language, new LinkedHashMap<>());
        languageVariantPropertiesMap.get(language).putIfAbsent(tag, new SimpleStringProperty());
        languageVariantPropertiesMap.get(language).get(tag).set(value);

    }

    /**
     * Set the value(s) of a property.
     * If the property is language-variant, it must be as many values than there are languages.
     * If the property is language-constant, only one value is expected.
     * @param tag the tag which identify the property
     * @param values the values for update the property
     */
    public void setPropertyValue(Format.Tag tag, List<String> values) {
        if (tag.isLanguageVariant()) {
            if (values.size() != formatProperty.getValue().languages.size())
                throw new IllegalArgumentException("Not enough values for each language");

            int i = 0;
            for (String language : formatProperty.getValue().languages)
                setPropertyValue(tag, language,values.get(i++));

        } else {
            if (values.isEmpty())
                throw new IllegalArgumentException("values is empty");

            setPropertyValue(tag, values.get(0));
        }
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
        for (Map.Entry<Format.Tag, StringProperty> entry : languageInvariantPropertiesMap.entrySet()) {
            formatted = formatted.replace(entry.getKey().toString(), entry.getValue().get());
        }

        for (Map.Entry<Format.Tag, StringProperty> entry : languageVariantPropertiesMap.get(language).entrySet()) {
            formatted = formatted.replace(entry.getKey().toString(), entry.getValue().get());
        }

        return formatted;
    }
}
