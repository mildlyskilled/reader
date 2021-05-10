package com.mildlyskilled.parser

import com.mildlyskilled.model.incoming.Opml
import com.mildlyskilled.model.incoming.Rss
import org.http4k.core.Body
import org.http4k.core.Request
import org.http4k.format.JacksonXml.auto

object Parser {
    fun parseOpml(request: Request): Opml {
        val messageLens = Body.auto<Opml>().toLens()
        return messageLens(request)
    }

    fun parseRss(request: Request): Rss {
        val messageLens = Body.auto<Rss>().toLens()
        return messageLens(request)
    }
}