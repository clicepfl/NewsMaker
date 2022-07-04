package ch.clic.newsmaker;

import java.util.HashMap;
import java.util.Map;

public class Format {

    public static final String BASE = """
            <!--
            @author Noé Terrier
            @brief Template of a newsletter for the CLIC and CLIC's commissions
            \s
            @date 02/03/2022
            -->
            \s
            <html lang="fr">
            <head>
                <meta charset="UTF-8">
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                \s
                <title>Newsletter</title>
            </head>
            <body style="font-family:'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; font-size: medium; display: flex; justify-content:center;">
                <div style="padding: 0; width: 100%; display: flex; align-content: center; justify-content:center;">
                \s
                    <div style="margin: auto; display: grid; border-radius: 30px; width: 40rem; width: min(40rem, 100%); padding: 1rem; background: rgb(42, 34, 54); background: linear-gradient(60deg, rgb(233, 30, 30) 0%, rgb(9,9,121) 50%, rgb(233, 30, 30) 100%); justify-content: center; color: lightgray">
                        \s
                        <!-- Pas affiché dans le mail mais dans la preview du mail -->
                        <p style="display: none;"> Quoi de nouveau à la CLIC ? </p>
                        \s
                        <div style="display: grid; padding-top: 2rem; justify-content: center;">
                            <img src="https://clic.epfl.ch/nextcloud/s/W7NfgqXWGNHTSYr/download/logo%20clic.png" alt="CLIC" style="width:10rem; font-size: 3rem; text-align: center; margin:auto" >
                        \s
                            <h1 style=" text-align: center; text-transform: uppercase; margin: 0.5rem 0 2rem 0; color: white;">Save the date !</h1>
                        </div>
                        \s
                        <h2 style="padding-left: 1rem; text-transform: uppercase; font-style: oblique; color: white;">ENGLISH VERSION BELOW</h2>
                        \s
                        
                        <!-- Début des news -->
                        
            @NEWS#FRANCAIS
                        <h2 style="margin: 1rem 0 1rem 3rem; text-transform: uppercase; color: azure;">Commissions</h2>
                        <div style=" color: aliceblue; background:linear-gradient(-60deg, rgb(233, 30, 30) 0%, rgb(12, 12, 54, 0.7) 100%); border-radius: 30px; overflow: hidden;">
                            <div style="grid-template-columns: 1fr;">
            @COMMISSIONS#FRANCAIS
                            </div>
                        </div>
            
                        
                        <!-- Fin commissions -->
            \s
            \s
                        <div style="margin-top: 1rem;">
                            <div style="padding: 1rem 3rem 1rem 3rem; border-radius: 30px; margin-bottom: 1rem; display: grid; background: rgba(0, 0, 0, 0.3); background: linear-gradient(125deg, rgba(0,0,0,0.4) 0%, rgba(0,0,0,0.1)100%);">
                                <h2 style="margin: 3px 0 0 0; color: aliceblue;">Bons plans IC</h2>
                                <p style="color: aliceblue;">Pour vous partager tous les bons plans IC, offres de stages ou bien tarifs préférentiels, la CLIC a créé le channel CLIC Bon Plan !</p>
                                <a href="https://t.me/clic_bonsplans" style="margin-left: auto; margin-right: 0%;"><span  style="color: azure; font-style: oblique;">rejoindre le channel</span></a>
                                
                                <h2 style="margin: 3px 0 0 0; color: aliceblue;">La CLIC ça vous intéresse ?</h2>
                                <p style="color: aliceblue;">Vous pouvez demander pour rejoindre une de nos équipes ou commissions et pourquoi pas postuler en tant que responsable de pôle pour l'année prochaine ! <br><br> Si ça vous intéresse, l'élection du prochain comité se fera lors de notre prochaine Assemblée Générale au mois de mai.</p>
                                <a href="https://clic.epfl.ch/about" style="margin-left: auto; margin-right: 0%;"><span  style="color: azure; font-style: oblique;">en savoir plus</span></a>
                            </div>
                        </div>
            \s
            \s
                        <!-- Réseaux sociaux -->
                        <div>
                            <div style="display: flex; flex-direction: row; justify-content: center; padding: 2rem;">
                                <div style="margin: auto;">
                                    <a href="https://clic.epfl.ch" style="text-decoration: none;"><span style=" padding: 0.5rem; color: white;">Website</span></a>|
                                    <a href="https://go.epfl.ch/clic_telegram" style="text-decoration: none;"><span style="padding: 0.5rem; color: white;">Telegram</span></a>|
                                    <a href="https://go.epfl.ch/clic_twitter" style="text-decoration: none;"><span style="padding: 0.5rem; color: white;">Twitter</span></a>|
                                    <a href="https://go.epfl.ch/clic_insta" style="text-decoration: none;"><span style="padding: 0.5rem; color: white;">Instagram</span></a>
                                </div>
                            </div>
                        </div>
            \s
            \s
            \s
                        <hr style="width: 80%; color: aliceblue; margin: 2rem auto 2rem auto;">
            \s
                        <!-- #################################### -->
                        <!-- ######### VERSION ANGLAISE ######### -->
                        <!-- ##########VVVVVVVVVVVVVVVV########## -->
                        \s
            \s
                        <div style="display: grid; padding-top: 2rem; justify-content: center;">
                            <img style="font-size: 3rem; text-align: center; width:10rem; margin:auto"  src="https://clic.epfl.ch/nextcloud/s/W7NfgqXWGNHTSYr/download/logo%20clic.png" alt="CLIC">
                  \s
                            <h1 style=" text-align: center; text-transform: uppercase; margin: 0.5rem 0 2rem 0; color: white;">Save the date !</h1>
                        </div>
                      \s
                        <h2 style="padding-left: 1rem; text-transform: uppercase; font-style: oblique; color: white;">VERSION FRANÇAISE AU DESSUS</h2>
            \s
                        <!-- Début des news -->
                        
            @NEWS#ENGLISH
                        <h2 style="margin: 1rem 0 1rem 3rem; text-transform: uppercase; color: azure;">Commissions</h2>
                        <div style=" color: aliceblue; background:linear-gradient(-60deg, rgb(233, 30, 30) 0%, rgb(12, 12, 54, 0.7) 100%); border-radius: 30px; overflow: hidden;">
                            <div style="grid-template-columns: 1fr;">
            @COMMISSIONS#ENGLISH
                            </div>
                        </div>
                        
                         <div style="margin-top: 1rem;">
                            <div style="padding: 1rem 3rem 1rem 3rem; border-radius: 30px; margin-bottom: 1rem; display: grid; background: rgba(0, 0, 0, 0.3); background: linear-gradient(125deg, rgba(0,0,0,0.4) 0%, rgba(0,0,0,0.1)100%);">
                                <h2 style="margin: 0; color: aliceblue;">CLIC Bon Plans</h2>
                                <p style="color: aliceblue;">CLIC has created a new channel, CLIC Bon Plans, to share with you exciting opportunities, from open internship positions to discounted tickets, keep an eye out here !</p>
                                <a href="https://t.me/clic_bonsplans" style="margin-left: auto; margin-right: 0%;"><span  style="color: azure; font-style: oblique;">join the channel</span></a>
                                
                                <h2 style="margin: 0; color: aliceblue;">Are you interested in CLIC ?</h2>
                                <p style="color: aliceblue;">You can apply to join one of our teams or committees and why not apply as a head of a unit for next year! <br><br> If you are interested, the election of the next committee will take place at our next General Assembly in May.</p>
                                <a href="https://clic.epfl.ch/about" style="margin-left: auto; margin-right: 0%;"><span  style="color: azure; font-style: oblique;">more information</span></a>
                            </div>
                        </div>
                        
                        <!-- Réseaux sociaux -->
                        <div>
                            <div style="display: flex; flex-direction: row; justify-content: center; padding: 2rem;">
                                <div style="margin: auto;">
                                    <a href="https://clic.epfl.ch" style="text-decoration: none;"><span style=" padding: 0.5rem; color: white;">Website</span></a>|
                                    <a href="https://go.epfl.ch/clic_telegram" style="text-decoration: none;"><span style="padding: 0.5rem; color: white;">Telegram</span></a>|
                                    <a href="https://go.epfl.ch/clic_twitter" style="text-decoration: none;"><span style="padding: 0.5rem; color: white;">Twitter</span></a>|
                                    <a href="https://go.epfl.ch/clic_insta" style="text-decoration: none;"><span style="padding: 0.5rem; color: white;">Instagram</span></a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """;
    public static final String DEFAULT_NEWS_TEMPLATE = """
            <div>
                <div style="padding: 1rem 3rem 1rem 3rem; border-radius: 30px; margin-bottom: 1rem; display: grid; background: rgba(0, 0, 0, 0.3); background: linear-gradient(125deg, rgba(0,0,0,0.4) 0%, rgba(0,0,0,0.1)100%);">
                    @NEWS_IMAGE
                    <h2 style="margin: 0; color: aliceblue;">@NEWS_TITLE</h2>
                    <p style="margin: 0; padding: 0; font-style: oblique;">Date: @NEWS_DATE</p>
                    <p style="margin-top: 1rem;">@NEWS_DESCRIPTION</p>
                    <a href="@NEWS_DETAILS_URL" style="margin-left: auto; margin-right: 0;"><span style="color: azure; font-style: oblique;">@NEWS_DETAIL_LABEL</span></a>
                </div>
            </div>
            """;

    public static final String IMG = """
            <div style="width: 100%; display: flex; justify-content: center;">
                <img src="@NEWS_IMAGE_URL"; alt="event image" style="font-size: 2rem; height: 7rem;">
            </div>
            """;

    private static final String COMMISSION_TEMPLATE = """
            <div style="padding: 1rem; background-color: @BACKGROUND_COLOR;">
                <div style="display: grid; padding: 1rem;">
                    <div>
                        <img src="@NEWS_IMAGE_URL" style="font-size: 2rem; width: 7rem;" alt="commission image">
                    </div>
                    <h2 style="margin: 1rem 0 0 0; color: @TEXT_COLOR;">@NEWS_TITLE</h2>
                    <p style="color: @TEXT_COLOR; margin: 0; padding: 0; font-style: oblique;">Date: @NEWS_DATE</p>
                    <p style="color: @TEXT_COLOR;">@NEWS_DESCRIPTION</p>
                    <a href="@NEWS_DETAILS_URL" style="margin-left: auto; margin-right: 0%;"><span style="color: @TEXT_COLOR; font-style: oblique;">@NEWS_DETAIL_LABEL</span></a>
                </div>
            </div>
            """;

    public enum Preset {
        NONE(null, null, null),

        CEVE("COMMISSIONS", COMMISSION_TEMPLATE, new HashMap<>() {{
            put(Tags.NEWS_IMAGE_URL, "https://clic.epfl.ch/nextcloud/s/MmpHMAWN5kmXnbo/download/CEVE.png");
            put(Tags.BACKGROUND_COLOR, "red");
            put(Tags.TEXT_COLOR, "white");
            put(Tags.NEWS_TITLE, "CEVE");
        }}),
        GAME_STAR("COMMISSIONS", COMMISSION_TEMPLATE, new HashMap<>() {{
            put(Tags.NEWS_IMAGE_URL, "https://clic.epfl.ch/nextcloud/s/LXWiks46dWH3ZE3/download/game%20star%20logo.png");
            put(Tags.BACKGROUND_COLOR, "grey");
            put(Tags.TEXT_COLOR, "white");
            put(Tags.NEWS_TITLE, "Game*");
        }}),
        ORBITAL_GAME_JAM("COMMISSIONS", COMMISSION_TEMPLATE, new HashMap<>() {{
            put(Tags.NEWS_IMAGE_URL, "https://clic.epfl.ch/nextcloud/s/42ATMkKr3JSJtFJ/download/logo%20ogj.png");
            put(Tags.BACKGROUND_COLOR, "blue");
            put(Tags.TEXT_COLOR, "black");
            put(Tags.NEWS_TITLE, "Orbital Game Jam");
        }}),
        POLYGLOT("COMMISSIONS", COMMISSION_TEMPLATE, new HashMap<>() {{
            put(Tags.NEWS_IMAGE_URL, "red");
            put(Tags.BACKGROUND_COLOR, "red");
            put(Tags.TEXT_COLOR, "white");
            put(Tags.NEWS_TITLE, "Polyglot");
        }}),
        IC_TRAVEL("COMMISSIONS", COMMISSION_TEMPLATE, new HashMap<>() {{
            put(Tags.NEWS_IMAGE_URL, "https://clic.epfl.ch/nextcloud/s/xa64WXekgoHmYSA/download/logo%20ic%20travel.png");
            put(Tags.BACKGROUND_COLOR, "yellow");
            put(Tags.TEXT_COLOR, "white");
            put(Tags.NEWS_TITLE, "IC Travel");
        }});

        public final String template;
        public final String sectionTag;
        public final Map<Tags, String> parameters;

        Preset(String sectionTag, String template, Map<Tags, String> parameters) {
            this.sectionTag = sectionTag;
            this.template = template;
            this.parameters = parameters;
        }
    }

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

    public enum Language {
        FRANCAIS,
        ENGLISH;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

}

