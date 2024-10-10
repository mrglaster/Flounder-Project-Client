import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.Serializable
import java.io.StringReader

data class Gsmf(
    val author: String,
    val title: String,
    val wlang: String,
    val date: String,
    val trfillmode: String,
    val cover: String,
    val words: List<String>,
    val translations: Map<String, List<String>>,
    val audios: List<String>,
    val definitions: List<List<String>>,
    val examples: List<List<String>>
):Serializable


fun parseGsmf(xml: String): Gsmf {
    val factory = XmlPullParserFactory.newInstance()
    val parser = factory.newPullParser()
    parser.setInput(StringReader(xml))

    var eventType = parser.eventType
    var currentTag: String? = null

    var author = ""
    var title = ""
    var wlang = ""
    var date = ""
    var trfillmode = ""
    var cover = ""
    val words = mutableListOf<String>()
    val translations = mutableMapOf<String, MutableList<String>>()
    val audios = mutableListOf<String>()
    val definitions = mutableListOf<MutableList<String>>()
    val examples = mutableListOf<MutableList<String>>()

    var currentLang: String? = null
    var currentWordDefs = mutableListOf<String>()
    var currentExampleDefs = mutableListOf<String>()

    while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
            XmlPullParser.START_TAG -> {
                currentTag = parser.name
                when (currentTag) {
                    "ru", "de" -> currentLang = currentTag
                    "wdblock" -> currentWordDefs = mutableListOf()
                    "exblock" -> currentExampleDefs = mutableListOf()
                }
            }

            XmlPullParser.TEXT -> {
                val text = parser.text.trim()
                if (text.isNotEmpty()) {
                    when (currentTag) {
                        "author" -> author = text
                        "title" -> title = text
                        "wlang" -> wlang = text
                        "date" -> date = text
                        "trfillmode" -> trfillmode = text
                        "cover" -> cover = text
                        "word" -> words.add(text)
                        "tr" -> currentLang?.let {
                            translations.getOrPut(it) { mutableListOf() }.add(text)
                        }
                        "spk" -> audios.add(text)
                        "df" -> currentWordDefs.add(text)
                        "ex" -> currentExampleDefs.add(text)
                    }
                }
            }

            XmlPullParser.END_TAG -> {
                when (parser.name) {
                    "wdblock" -> definitions.add(currentWordDefs)
                    "exblock" -> examples.add(currentExampleDefs)
                    "ru", "de" -> currentLang = null
                }
            }
        }
        eventType = parser.next()
    }

    return Gsmf(
        author = author,
        title = title,
        wlang = wlang,
        date = date,
        trfillmode = trfillmode,
        cover = cover,
        words = words,
        translations = translations,
        audios = audios,
        definitions = definitions,
        examples = examples
    )
}
