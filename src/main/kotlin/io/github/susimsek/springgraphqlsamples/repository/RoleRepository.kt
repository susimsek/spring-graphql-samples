package io.github.susimsek.springgraphqlsamples.repository

import io.github.susimsek.springgraphqlsamples.domain.Role
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.RoleName
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.graphql.data.GraphQlRepository

@GraphQlRepository
interface RoleRepository :
    CoroutineCrudRepository<Role, String>,
    CoroutineSortingRepository<Role, String>
{
    suspend fun findByName(name: RoleName): Role?
}