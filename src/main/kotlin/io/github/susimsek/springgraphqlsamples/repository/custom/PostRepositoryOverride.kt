package io.github.susimsek.springgraphqlsamples.repository.custom

import io.github.susimsek.springgraphqlsamples.domain.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.ScrollPosition
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Window

interface PostRepositoryOverride {

    suspend fun findAll(pageable: Pageable): Page<Post>
    suspend fun findAllByPosition(limit: Int, scrollPosition: ScrollPosition, sort: Sort): Window<Post>
}
