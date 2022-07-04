package ch.clic.newsmaker;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

public class NewsFieldBean {

    private final StringProperty section = new SimpleStringProperty(); //the section where the field is

    private final Map<Format.Language, Map<Format.Tags, StringProperty>> languageVariantPropertiesMap; // all language dependant properties (like description or title) are sorted by language here

    private final Map<Format.Tags, StringProperty> languageInvariantPropertiesMap; // the others properties are stored here

    public String base = Format.DEFAULT_NEWS_TEMPLATE;

    public NewsFieldBean() {

        languageVariantPropertiesMap = new HashMap<>();

        languageInvariantPropertiesMap = new HashMap<>();
        for (Format.Tags tag : Format.Tags.values()) {
            if (!tag.isLanguageVariant) {
                languageInvariantPropertiesMap.put(tag, new SimpleStringProperty());
            }
        }

        // default values
        languageInvariantPropertiesMap.get(Format.Tags.NEWS_DETAILS_URL).setValue("https://clic.epfl.ch");
        languageInvariantPropertiesMap.get(Format.Tags.BACKGROUND_COLOR).setValue("black");
        languageInvariantPropertiesMap.get(Format.Tags.TEXT_COLOR).setValue("white");
        languageInvariantPropertiesMap.get(Format.Tags.NEWS_IMAGE_URL).setValue("");
        languageInvariantPropertiesMap.get(Format.Tags.NEWS_IMAGE).setValue("");

        for (Format.Language language : Format.Language.values()) {
            HashMap<Format.Tags, StringProperty> map = new HashMap<>();
            for (Format.Tags tag : Format.Tags.values()) {
                if (tag.isLanguageVariant)
                    map.put(tag, new SimpleStringProperty());
            }

            // default values
            map.get(Format.Tags.NEWS_DESCRIPTION).setValue("Pas de description");
            map.get(Format.Tags.NEWS_DETAIL_LABEL).setValue("en savoir plus");
            map.get(Format.Tags.NEWS_TITLE).setValue("Titre");
            map.get(Format.Tags.NEWS_DATE).setValue("Aujourd'hui");

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

    public String getPropertyValue(Format.Language language, Format.Tags tag) {
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

    public StringProperty getProperty(Format.Language language, Format.Tags tag) {
        if (!tag.isLanguageVariant)
            return languageInvariantPropertiesMap.get(tag);
        return languageVariantPropertiesMap.get(language).get(tag);
    }

    public void setPropertyValue(Format.Tags tag, String value) {
        if (tag.isLanguageVariant) {
            for (Format.Language language : Format.Language.values()) {
                languageVariantPropertiesMap.get(language).get(tag).setValue(value);
            }
        } else {
            languageInvariantPropertiesMap.get(tag).setValue(value);
        }
    }

    public void setPropertyValue(Format.Language language, Format.Tags tag, String value) {
        if (tag.isLanguageVariant) {
            languageVariantPropertiesMap.get(language).get(tag).setValue(value);
        } else {
            languageInvariantPropertiesMap.get(tag).setValue(value);
        }
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

    /**
     * Build the HTML of the field by replacing all tags in the base template with corresponding value
     *
     * @param language the HTML can be build in any available language
     * @return a String containing the HTML
     */
    public String getHTML(Format.Language language) {

        String formatted = base;

        // replace all tags in the base template with corresponding value
        for (Format.Tags tag : Format.Tags.values()) {
            if (tag == Format.Tags.NEWS_IMAGE) continue;
            formatted = formatted.replace(tag.toString(), getPropertyValue(language, tag));
        }

        return formatted.replace(Format.Tags.NEWS_IMAGE.toString(),
                getPropertyValue(Format.Tags.NEWS_IMAGE_URL).isEmpty()
                        ? ""
                        : Format.IMG.replace(Format.Tags.NEWS_IMAGE_URL.toString(), getPropertyValue(Format.Tags.NEWS_IMAGE_URL)));
    }
}
