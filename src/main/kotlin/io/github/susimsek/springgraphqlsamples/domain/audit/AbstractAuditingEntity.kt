package io.github.susimsek.springgraphqlsamples.domain.audit

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Clock
import java.time.OffsetDateTime

open class AbstractAuditingEntity(

    @CreatedDate
    @Field("created_at")
    var createdAt: OffsetDateTime = OffsetDateTime.now(Clock.systemDefaultZone()),

    @LastModifiedDate
    @Field("last_modified_at")
    var updatedAt: OffsetDateTime = OffsetDateTime.now(Clock.systemDefaultZone()),

    @Version
    @Field("version")
    var version: Long = 0
)
