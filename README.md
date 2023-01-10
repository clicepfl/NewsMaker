
## Compilation:

The easy way to compile the project is to use Maven with jdk 18 (you can also use the Maven integration in IntellIJ).

The command for compiling the project is:<br>
`mvn compile`

and for creating the .jar (in the target directory):<br>
`mvn package`

ensure that the .jar named with *-shaded* at the end is executable, and then it is ready to be used !


## How it works:

Given an HTML base template (/assets/base.html), the software will replace all occurrence of @SECTION#LANGUAGE by all HTML formatted nodes of the given section "SECTION" "in the language "LANGUAGE".

## Presets:

You can use presets to specify default values of you recurrent news fields as the name of the event, the image URL or the date. By changing the preset selection of a news field from `default` to another preset, the specified information will override the default's ones.

Presets can be added by modifying the config.json file.
In order to add a preset to the list of presets you just need to add to the json array `presets` the following element, by replacing all your parameters information, text, and URL.

    {
      "sectionTag": "SECTIONNAME",
      "name": "Preset Name",
      "templateFile": "/assets/template_of_the_preset.html",
      "parameters": {
        "NEWS_DETAILS_URL": "https://url.of.website/page",
        "BACKGROUND_COLOR": "black",
        "TEXT_COLOR": "white",
        "NEWS_IMAGE_URL": "https://url/of/image",
        "NEWS_DESCRIPTION": "The description of the news",
        "NEWS_DETAIL_LABEL": "more information",
        "NEWS_TITLE": "Title of your news",
        ...
      }
    }


The template file is an HTML file with the default HTML code for a news div. Part to be replaced in the HTML have to be indicated by a tag @TAG_NAME and the tag will be replaced by the corresponding text content of the tag.  

In parameters section, can add all tags available in this list:

NEWS_DETAILS_URL, BACKGROUND_COLOR, TEXT_COLOR, NEWS_IMAGE_URL, NEWS_DESCRIPTION, NEWS_DETAIL_LABEL, NEWS_TITLE, NEWS_DATE

And you can also add your personal tags like SUBTITLE for exemple. In the HTML template you can identify the tags to be replaced by @YOUR_TAG_NAME (@SUBTITLE for exemple). 

Don't remove the first preset `default` but you can modify its parameters and its template file.
The parameters of the `default` preset are used to create a new news field, but will not override the field by changing the news field preset selection to `default`.

## Languages:

The config.json file can be edited to change the number of languages available by adding one to the list of language.
