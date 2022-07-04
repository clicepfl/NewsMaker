package ch.clic.newsmaker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonEncoder {

    public record Preset(String template, String sectionTag, Map<Tags, String> parameters){}

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


    private String base;
    private String defaultNewsTemplate;
    private String img;
   // private List<Preset> presets;
    private List<String> languages;

    public JsonEncoder(String base, String defaultNewsTemplate, String img, List<Preset> presets, List<String> languages) {
        this.base = base;
        this.defaultNewsTemplate = defaultNewsTemplate;
        this.img = img;
        //this.presets = presets;
        this.languages = languages;
    }

//    static public JsonEncoder fromJSON(FileReader jsonFile) throws IOException, ParseException {
//
//        ObjectMapper
//
//        return new JsonEncoder(base, defaultNewsTemplate, img, presets, languages)
//    }

    public String toJSON() throws JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString(this);
        System.out.println(json);
        return json;
    }


}
