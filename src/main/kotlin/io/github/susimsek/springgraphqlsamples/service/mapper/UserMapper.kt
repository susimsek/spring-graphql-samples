package io.github.susimsek.springgraphqlsamples.service.mapper

import io.github.susimsek.springgraphqlsamples.domain.User
import io.github.susimsek.springgraphqlsamples.graphql.input.AddUserInput
import io.github.susimsek.springgraphqlsamples.graphql.type.UserPayload
import org.mapstruct.BeanMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Named
import org.mapstruct.NullValuePropertyMappingStrategy
import org.mapstruct.ReportingPolicy
import java.util.Locale


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface UserMapper : EntityMapper<User, UserPayload> {

    @Mapping(target = "lang", defaultExpression = "java(Locale.ENGLISH)")
    fun toEntity(input: AddUserInput): User

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    fun partialUpdate(@MappingTarget entity: User, input: AddUserInput)
}
