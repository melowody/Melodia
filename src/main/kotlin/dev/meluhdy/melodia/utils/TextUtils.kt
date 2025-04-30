package dev.meluhdy.melodia.utils

import dev.meluhdy.melodia.MelodiaPlugin
import dev.meluhdy.melodia.utils.FileUtils.getYMLConfig
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import java.io.File
import java.text.MessageFormat

/**
 * A collection of functions to deal with text and chat messages
 */
object TextUtils {

    @Suppress("unused")
    enum class Language {
        AAR,	/* Afar */
        ABK,	/* Abkhazian */
        ACE,	/* Achinese */
        ACH,	/* Acoli */
        ADA,	/* Adangme */
        ADY,	/* Adyghe */
        AFA,	/* Afro-Asiatic languages */
        AFH,	/* Afrihili */
        AFR,	/* Afrikaans */
        AIN,	/* Ainu */
        AKA,	/* Akan */
        AKK,	/* Akkadian */
        ALE,	/* Aleut */
        ALG,	/* Algonquian languages */
        ALT,	/* Southern Altai */
        AMH,	/* Amharic */
        ANG,	/* English, Old (ca.450-1100) */
        ANP,	/* Angika */
        APA,	/* Apache languages */
        ARA,	/* Arabic */
        ARC,	/* Official Aramaic (700-300 BCE) */
        ARG,	/* Aragonese */
        ARN,	/* Mapudungun */
        ARP,	/* Arapaho */
        ART,	/* Artificial languages */
        ARW,	/* Arawak */
        ASM,	/* Assamese */
        AST,	/* Asturian */
        ATH,	/* Athapascan languages */
        AUS,	/* Australian languages */
        AVA,	/* Avaric */
        AVE,	/* Avestan */
        AWA,	/* Awadhi */
        AYM,	/* Aymara */
        AZE,	/* Azerbaijani */
        BAD,	/* Banda languages */
        BAI,	/* Bamileke languages */
        BAK,	/* Bashkir */
        BAL,	/* Baluchi */
        BAM,	/* Bambara */
        BAN,	/* Balinese */
        BAS,	/* Basa */
        BAT,	/* Baltic languages */
        BEJ,	/* Beja */
        BEL,	/* Belarusian */
        BEM,	/* Bemba */
        BEN,	/* Bengali */
        BER,	/* Berber languages */
        BHO,	/* Bhojpuri */
        BIH,	/* Bihari languages */
        BIK,	/* Bikol */
        BIN,	/* Bini */
        BIS,	/* Bislama */
        BLA,	/* Siksika */
        BNT,	/* Bantu languages */
        BOD,	/* Tibetan */
        BOS,	/* Bosnian */
        BRA,	/* Braj */
        BRE,	/* Breton */
        BTK,	/* Batak languages */
        BUA,	/* Buriat */
        BUG,	/* Buginese */
        BUL,	/* Bulgarian */
        BYN,	/* Blin */
        CAD,	/* Caddo */
        CAI,	/* Central American Indian languages */
        CAR,	/* Galibi Carib */
        CAT,	/* Catalan */
        CAU,	/* Caucasian languages */
        CEB,	/* Cebuano */
        CEL,	/* Celtic languages */
        CES,	/* Czech */
        CHA,	/* Chamorro */
        CHB,	/* Chibcha */
        CHE,	/* Chechen */
        CHG,	/* Chagatai */
        CHK,	/* Chuukese */
        CHM,	/* Mari */
        CHN,	/* Chinook jargon */
        CHO,	/* Choctaw */
        CHP,	/* Chipewyan */
        CHR,	/* Cherokee */
        CHU,	/* Church Slavic */
        CHV,	/* Chuvash */
        CHY,	/* Cheyenne */
        CMC,	/* Chamic languages */
        CNR,	/* Montenegrin */
        COP,	/* Coptic */
        COR,	/* Cornish */
        COS,	/* Corsican */
        CPE,	/* Creoles and pidgins, English based */
        CPF,	/* Creoles and pidgins, French-based */
        CPP,	/* Creoles and pidgins, Portuguese-based */
        CRE,	/* Cree */
        CRH,	/* Crimean Tatar */
        CRP,	/* Creoles and pidgins */
        CSB,	/* Kashubian */
        CUS,	/* Cushitic languages */
        CYM,	/* Welsh */
        DAK,	/* Dakota */
        DAN,	/* Danish */
        DAR,	/* Dargwa */
        DAY,	/* Land Dayak languages */
        DEL,	/* Delaware */
        DEN,	/* Slave (Athapascan) */
        DEU,	/* German */
        DGR,	/* Tlicho */
        DIN,	/* Dinka */
        DIV,	/* Divehi */
        DOI,	/* Dogri (macrolanguage) */
        DRA,	/* Dravidian languages */
        DSB,	/* Lower Sorbian */
        DUA,	/* Duala */
        DUM,	/* Dutch, Middle (ca.1050-1350) */
        DYU,	/* Dyula */
        DZO,	/* Dzongkha */
        EFI,	/* Efik */
        EGY,	/* Egyptian (Ancient) */
        EKA,	/* Ekajuk */
        ELL,	/* Greek, Modern (1453-) */
        ELX,	/* Elamite */
        ENG,	/* English */
        ENM,	/* English, Middle (1100-1500) */
        EPO,	/* Esperanto */
        EST,	/* Estonian */
        EUS,	/* Basque */
        EWE,	/* Ewe */
        EWO,	/* Ewondo */
        FAN,	/* Fang */
        FAO,	/* Faroese */
        FAS,	/* Persian */
        FAT,	/* Fanti */
        FIJ,	/* Fijian */
        FIL,	/* Filipino */
        FIN,	/* Finnish */
        FIU,	/* Finno-Ugrian languages */
        FON,	/* Fon */
        FRA,	/* French */
        FRM,	/* French, Middle (ca.1400-1600) */
        FRO,	/* French, Old (842-ca.1400) */
        FRR,	/* Northern Frisian */
        FRS,	/* Eastern Frisian */
        FRY,	/* Western Frisian */
        FUL,	/* Fulah */
        FUR,	/* Friulian */
        GAA,	/* Ga */
        GAY,	/* Gayo */
        GBA,	/* Gbaya */
        GEM,	/* Germanic languages */
        GEZ,	/* Geez */
        GIL,	/* Gilbertese */
        GLA,	/* Gaelic */
        GLE,	/* Irish */
        GLG,	/* Galician */
        GLV,	/* Manx */
        GMH,	/* German, Middle High (ca.1050-1500) */
        GOH,	/* German, Old High (ca.750-1050) */
        GON,	/* Gondi */
        GOR,	/* Gorontalo */
        GOT,	/* Gothic */
        GRB,	/* Grebo */
        GRC,	/* Greek, Ancient (to 1453) */
        GRN,	/* Guarani */
        GSW,	/* Swiss German */
        GUJ,	/* Gujarati */
        GWI,	/* Gwich'in */
        HAI,	/* Haida */
        HAT,	/* Haitian */
        HAU,	/* Hausa */
        HAW,	/* Hawaiian */
        HEB,	/* Hebrew */
        HER,	/* Herero */
        HIL,	/* Hiligaynon */
        HIM,	/* Himachali languages */
        HIN,	/* Hindi */
        HIT,	/* Hittite */
        HMN,	/* Hmong */
        HMO,	/* Hiri Motu */
        HRV,	/* Croatian */
        HSB,	/* Upper Sorbian */
        HUN,	/* Hungarian */
        HUP,	/* Hupa */
        HYE,	/* Armenian */
        IBA,	/* Iban */
        IBO,	/* Igbo */
        IDO,	/* Ido */
        III,	/* Sichuan Yi */
        IJO,	/* Ijo languages */
        IKU,	/* Inuktitut */
        ILE,	/* Interlingue */
        ILO,	/* Iloko */
        INA,	/* Interlingua (International Auxiliary Language Association) */
        INC,	/* Indic languages */
        IND,	/* Indonesian */
        INE,	/* Indo-European languages */
        INH,	/* Ingush */
        IPK,	/* Inupiaq */
        IRA,	/* Iranian languages */
        IRO,	/* Iroquoian languages */
        ISL,	/* Icelandic */
        ITA,	/* Italian */
        JAV,	/* Javanese */
        JBO,	/* Lojban */
        JPN,	/* Japanese */
        JPR,	/* Judeo-Persian */
        JRB,	/* Judeo-Arabic */
        KAA,	/* Kara-Kalpak */
        KAB,	/* Kabyle */
        KAC,	/* Kachin */
        KAL,	/* Kalaallisut */
        KAM,	/* Kamba */
        KAN,	/* Kannada */
        KAR,	/* Karen languages */
        KAS,	/* Kashmiri */
        KAT,	/* Georgian */
        KAU,	/* Kanuri */
        KAW,	/* Kawi */
        KAZ,	/* Kazakh */
        KBD,	/* Kabardian */
        KHA,	/* Khasi */
        KHI,	/* Khoisan languages */
        KHM,	/* Central Khmer */
        KHO,	/* Khotanese */
        KIK,	/* Kikuyu */
        KIN,	/* Kinyarwanda */
        KIR,	/* Kirghiz */
        KMB,	/* Kimbundu */
        KOK,	/* Konkani (macrolanguage) */
        KOM,	/* Komi */
        KON,	/* Kongo */
        KOR,	/* Korean */
        KOS,	/* Kosraean */
        KPE,	/* Kpelle */
        KRC,	/* Karachay-Balkar */
        KRL,	/* Karelian */
        KRO,	/* Kru languages */
        KRU,	/* Kurukh */
        KUA,	/* Kuanyama */
        KUM,	/* Kumyk */
        KUR,	/* Kurdish */
        KUT,	/* Kutenai */
        LAD,	/* Ladino */
        LAH,	/* Lahnda */
        LAM,	/* Lamba */
        LAO,	/* Lao */
        LAT,	/* Latin */
        LAV,	/* Latvian */
        LEZ,	/* Lezghian */
        LIM,	/* Limburgan */
        LIN,	/* Lingala */
        LIT,	/* Lithuanian */
        LOL,	/* Mongo */
        LOZ,	/* Lozi */
        LTZ,	/* Luxembourgish */
        LUA,	/* Luba-Lulua */
        LUB,	/* Luba-Katanga */
        LUG,	/* Ganda */
        LUI,	/* Luiseno */
        LUN,	/* Lunda */
        LUO,	/* Luo (Kenya and Tanzania) */
        LUS,	/* Lushai */
        MAD,	/* Madurese */
        MAG,	/* Magahi */
        MAH,	/* Marshallese */
        MAI,	/* Maithili */
        MAK,	/* Makasar */
        MAL,	/* Malayalam */
        MAN,	/* Mandingo */
        MAP,	/* Austronesian languages */
        MAR,	/* Marathi */
        MAS,	/* Masai */
        MDF,	/* Moksha */
        MDR,	/* Mandar */
        MEN,	/* Mende */
        MGA,	/* Irish, Middle (900-1200) */
        MIC,	/* Mi'kmaq */
        MIN,	/* Minangkabau */
        MIS,	/* Uncoded languages */
        MKD,	/* Macedonian */
        MKH,	/* Mon-Khmer languages */
        MLG,	/* Malagasy */
        MLT,	/* Maltese */
        MNC,	/* Manchu */
        MNI,	/* Manipuri */
        MNO,	/* Manobo languages */
        MOH,	/* Mohawk */
        MON,	/* Mongolian */
        MOS,	/* Mossi */
        MRI,	/* Maori */
        MSA,	/* Malay (macrolanguage) */
        MUL,	/* Multiple languages */
        MUN,	/* Munda languages */
        MUS,	/* Creek */
        MWL,	/* Mirandese */
        MWR,	/* Marwari */
        MYA,	/* Burmese */
        MYN,	/* Mayan languages */
        MYV,	/* Erzya */
        NAH,	/* Nahuatl languages */
        NAI,	/* North American Indian languages */
        NAP,	/* Neapolitan */
        NAU,	/* Nauru */
        NAV,	/* Navajo */
        NBL,	/* Ndebele, South */
        NDE,	/* Ndebele, North */
        NDO,	/* Ndonga */
        NDS,	/* Low German */
        NEP,	/* Nepali (macrolanguage) */
        NEW,	/* Nepal Bhasa */
        NIA,	/* Nias */
        NIC,	/* Niger-Kordofanian languages */
        NIU,	/* Niuean */
        NLD,	/* Dutch */
        NNO,	/* Norwegian Nynorsk */
        NOB,	/* Bokmål, Norwegian */
        NOG,	/* Nogai */
        NON,	/* Norse, Old */
        NOR,	/* Norwegian */
        NQO,	/* N'Ko */
        NSO,	/* Pedi */
        NUB,	/* Nubian languages */
        NWC,	/* Classical Newari */
        NYA,	/* Chichewa */
        NYM,	/* Nyamwezi */
        NYN,	/* Nyankole */
        NYO,	/* Nyoro */
        NZI,	/* Nzima */
        OCI,	/* Occitan (post 1500) */
        OJI,	/* Ojibwa */
        ORI,	/* Oriya (macrolanguage) */
        ORM,	/* Oromo */
        OSA,	/* Osage */
        OSS,	/* Ossetian */
        OTA,	/* Turkish, Ottoman (1500-1928) */
        OTO,	/* Otomian languages */
        PAA,	/* Papuan languages */
        PAG,	/* Pangasinan */
        PAL,	/* Pahlavi */
        PAM,	/* Pampanga */
        PAN,	/* Panjabi */
        PAP,	/* Papiamento */
        PAU,	/* Palauan */
        PEO,	/* Persian, Old (ca.600-400 B.C.) */
        PHI,	/* Philippine languages */
        PHN,	/* Phoenician */
        PLI,	/* Pali */
        POL,	/* Polish */
        PON,	/* Pohnpeian */
        POR,	/* Portuguese */
        PRA,	/* Prakrit languages */
        PRO,	/* Provençal, Old (to 1500) */
        PUS,	/* Pushto */
        QAA,	/* Reserved for local use */
        QUE,	/* Quechua */
        RAJ,	/* Rajasthani */
        RAP,	/* Rapanui */
        RAR,	/* Rarotongan */
        ROA,	/* Romance languages */
        ROH,	/* Romansh */
        ROM,	/* Romany */
        RON,	/* Romanian */
        RUN,	/* Rundi */
        RUP,	/* Aromanian */
        RUS,	/* Russian */
        SAD,	/* Sandawe */
        SAG,	/* Sango */
        SAH,	/* Yakut */
        SAI,	/* South American Indian languages */
        SAL,	/* Salishan languages */
        SAM,	/* Samaritan Aramaic */
        SAN,	/* Sanskrit */
        SAS,	/* Sasak */
        SAT,	/* Santali */
        SCN,	/* Sicilian */
        SCO,	/* Scots */
        SEL,	/* Selkup */
        SEM,	/* Semitic languages */
        SGA,	/* Irish, Old (to 900) */
        SGN,	/* Sign Languages */
        SHN,	/* Shan */
        SID,	/* Sidamo */
        SIN,	/* Sinhala */
        SIO,	/* Siouan languages */
        SIT,	/* Sino-Tibetan languages */
        SLA,	/* Slavic languages */
        SLK,	/* Slovak */
        SLV,	/* Slovenian */
        SMA,	/* Southern Sami */
        SME,	/* Northern Sami */
        SMI,	/* Sami languages */
        SMJ,	/* Lule Sami */
        SMN,	/* Inari Sami */
        SMO,	/* Samoan */
        SMS,	/* Skolt Sami */
        SNA,	/* Shona */
        SND,	/* Sindhi */
        SNK,	/* Soninke */
        SOG,	/* Sogdian */
        SOM,	/* Somali */
        SON,	/* Songhai languages */
        SOT,	/* Sotho, Southern */
        SPA,	/* Spanish */
        SQI,	/* Albanian */
        SRD,	/* Sardinian */
        SRN,	/* Sranan Tongo */
        SRP,	/* Serbian */
        SRR,	/* Serer */
        SSA,	/* Nilo-Saharan languages */
        SSW,	/* Swati */
        SUK,	/* Sukuma */
        SUN,	/* Sundanese */
        SUS,	/* Susu */
        SUX,	/* Sumerian */
        SWA,	/* Swahili (macrolanguage) */
        SWE,	/* Swedish */
        SYC,	/* Classical Syriac */
        SYR,	/* Syriac */
        TAH,	/* Tahitian */
        TAI,	/* Tai languages */
        TAM,	/* Tamil */
        TAT,	/* Tatar */
        TEL,	/* Telugu */
        TEM,	/* Timne */
        TER,	/* Tereno */
        TET,	/* Tetum */
        TGK,	/* Tajik */
        TGL,	/* Tagalog */
        THA,	/* Thai */
        TIG,	/* Tigre */
        TIR,	/* Tigrinya */
        TIV,	/* Tiv */
        TKL,	/* Tokelau */
        TLH,	/* Klingon */
        TLI,	/* Tlingit */
        TMH,	/* Tamashek */
        TOG,	/* Tonga (Nyasa) */
        TON,	/* Tonga (Tonga Islands) */
        TPI,	/* Tok Pisin */
        TSI,	/* Tsimshian */
        TSN,	/* Tswana */
        TSO,	/* Tsonga */
        TUK,	/* Turkmen */
        TUM,	/* Tumbuka */
        TUP,	/* Tupi languages */
        TUR,	/* Turkish */
        TUT,	/* Altaic languages */
        TVL,	/* Tuvalu */
        TWI,	/* Twi */
        TYV,	/* Tuvinian */
        UDM,	/* Udmurt */
        UGA,	/* Ugaritic */
        UIG,	/* Uighur */
        UKR,	/* Ukrainian */
        UMB,	/* Umbundu */
        UND,	/* Undetermined */
        URD,	/* Urdu */
        UZB,	/* Uzbek */
        VAI,	/* Vai */
        VEN,	/* Venda */
        VIE,	/* Vietnamese */
        VOL,	/* Volapük */
        VOT,	/* Votic */
        WAK,	/* Wakashan languages */
        WAL,	/* Wolaitta */
        WAR,	/* Waray */
        WAS,	/* Washo */
        WEN,	/* Sorbian languages */
        WLN,	/* Walloon */
        WOL,	/* Wolof */
        XAL,	/* Kalmyk */
        XHO,	/* Xhosa */
        YAO,	/* Yao */
        YAP,	/* Yapese */
        YID,	/* Yiddish */
        YOR,	/* Yoruba */
        YPK,	/* Yupik languages */
        ZAP,	/* Zapotec */
        ZBL,	/* Blissymbols */
        ZEN,	/* Zenaga */
        ZGH,	/* Standard Moroccan Tamazight */
        ZHA,	/* Zhuang */
        ZHO,	/* Chinese */
        ZND,	/* Zande languages */
        ZUL,	/* Zulu */
        ZUN,	/* Zuni */
        ZXX,	/* No linguistic content */
        ZZA		/* Zaza */
    }

    /**
     * Colors a string using the legacy color codes.
     *
     * @param message The message to colorize.
     * @param identifier The identifier for the color codes. Default: &
     *
     * @return A TextComponent colored using the inputted color codes.
     */
    fun colorize(message: String, identifier: Char = '&'): TextComponent {
        return LegacyComponentSerializer.legacy(identifier).deserialize(message)
    }

    /**
     * Localizes a string for the given plugin
     *
     * @param stringId The ID of the string to localize
     * @param language The language to localize to
     * @param plugin The plugin to localize for
     * @param args Varargs for the arguments to replace in the formatted string
     */
    @Throws(IllegalArgumentException::class)
    fun localize(stringId: String, language: Language, plugin: MelodiaPlugin, vararg args: Any): TextComponent {

        if (!plugin.languageEnum.keys.contains(language)) throw IllegalArgumentException("Language $language is not supported by this plugin!")

        val config = getYMLConfig(plugin, plugin.languageEnum.get(language)!!)
        if (!config.contains(stringId)) throw IllegalArgumentException("String $stringId not found!")
        val string = config.getString(stringId)!!

        val fmt = MessageFormat(string)
        return colorize(fmt.format(args))

    }

    /**
     * Localizes a string list for the given plugin
     * Returns an empty array if the listId doesn't correspond to a list
     *
     * @param listId The ID of the string to localize
     * @param language The language to localize to
     * @param plugin The plugin to localize for
     * @param args Varargs for the arguments to replace in the formatted string
     */
    @Throws(IllegalArgumentException::class)
    fun localizeArray(listId: String, language: Language, plugin: MelodiaPlugin, vararg args: Any): Array<TextComponent> {

        if (!plugin.languageEnum.keys.contains(language)) throw IllegalArgumentException("Language $language is not supported by this plugin!")

        val config = getYMLConfig(plugin, plugin.languageEnum.get(language)!!)
        if (!config.contains(listId)) throw IllegalArgumentException("String List $listId not found!")
        val list = config.getStringList(listId)

        return list.map { colorize(MessageFormat(it).format(args)) }.toTypedArray()

    }

}