package eu.kanade.tachiyomi.extension.en.kochab

import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import rx.Observable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RealLifeComics : ParsedHttpSource() {
    override val name = "Kochab Comics"

    override val baseUrl = "https://www.kochab-comic.com"

    override val lang = "en"

    protected open val archive = "/comic/archive"

    protected open val synopsis = "Kochab is a YA wlw fantasy comic about two girls lost in a pile of ruins under the woods, inspired by various myths and fairytales. A lost skier trying to survive a snowy wilderness and find her way back to her village stumbles across and awakens a fire spirit trying to fix the home that she’s let fall apart around her."

    protected open val creator = "Sarah Webb"

    protected open val imageSelector = "#cc-comicbody > img"

    override val supportsLatest = false


    // Popular
    final override fun fetchPopularManga(page: Int) =
        SManga.create().apply {
            title = name
            artist = creator
            author = creator
            description = synopsis
            status = SManga.ONGOING
            thumbnail_url = THUMBNAIL_URL
            setUrlWithoutDomain(archive)
        }.let { Observable.just(MangasPage(listOf(it), false))!! }
    }

    // Search

    override fun fetchSearchManga(page: Int, query: String, filters: FilterList): Observable<MangasPage> =
        fetchPopularManga(1).map { mangaList ->
            mangaList.copy(mangaList.mangas.filter { it.title.contains(query) })
        }

    // Details

    override fun fetchMangaDetails(manga: SManga) = Observable.just(
        manga.apply {
            initialized = true
        },
    )!!

    // Chapters

    override fun chapterListParse(response: Response) =
        response.asJsoup().select(chapterListSelector).map {
            SChapter.create().apply {
                url = it.attr("href")
                val number = url.removeSurrounding("https://www.kochab-comic.com/comic/page-")
                name = it.attr("title"))
                chapter_number = number.toFloat()
                date_upload = it.attr("title").timestamp()
            }
        }


    // Page

    override fun pageListParse(response: Response): List<Page> {
        // if the img tag is empty or has siblings then it is an interactive comic
        val img = response.asJsoup().selectFirst(imageSelector)?.takeIf {
            it.nextElementSibling() == null
        } ?: error(interactiveText)

    }
    // Unsupported

    override fun imageUrlParse(document: Document) = throw Exception("Not used")

    override fun popularMangaSelector() = throw Exception("Not used")

    override fun searchMangaFromElement(element: Element) = throw Exception("Not used")

    override fun searchMangaNextPageSelector() = throw Exception("Not used")

    override fun searchMangaSelector() = throw Exception("Not used")

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList) = throw Exception("Not used")

    override fun popularMangaNextPageSelector() = throw Exception("Not used")

    override fun popularMangaFromElement(element: Element) = throw Exception("Not used")

    override fun mangaDetailsParse(document: Document) = throw Exception("Not used")

    override fun latestUpdatesNextPageSelector() = throw Exception("Not used")

    override fun latestUpdatesFromElement(element: Element) = throw Exception("Not used")

    override fun latestUpdatesRequest(page: Int) = throw Exception("Not used")

    override fun latestUpdatesSelector() = throw Exception("Not used")

    companion object {
        private const val THUMBNAIL_URL =
            "https://fakeimg.pl/550x780/ffffff/6e7b91/?font=museo&text=xkcd"

    }
}