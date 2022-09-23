package io.github.susimsek.springgraphqlsamples.service.mapper

interface EntityMapper<E, T> {

    fun toType(entity: E): T

    fun toType(entityList: MutableList<E>): MutableList<T>
}
