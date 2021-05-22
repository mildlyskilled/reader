package com.mildlyskilled.parser

import com.mildlyskilled.model.incoming.Atom
import com.mildlyskilled.model.incoming.Opml
import com.mildlyskilled.model.incoming.Rss
import org.http4k.core.Body
import org.http4k.core.Response
import org.http4k.format.JacksonXml.auto

object Parser {
    fun parseOpml(request: Response): Opml {
        val messageLens = Body.auto<Opml>().toLens()
        return messageLens(request)
    }

    fun parseRss(request: Response): Rss {
        val messageLens = Body.auto<Rss>().toLens()
        return messageLens(request)
    }

    fun parseAtom(request: Response): Atom {
        val messageLens = Body.auto<Atom>().toLens()
        return messageLens(request)
    }
}