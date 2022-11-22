package io.github.susimsek.springgraphqlsamples.domain.audit

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.mongodb.core.mapping.Field

open class AbstractUserAuditingEntity(

    @CreatedBy
    @Field("created_by")
    var createdBy: String = "",

    @LastModifiedBy
    @Field("last_modified_by")
    var lastModifiedBy: String = ""
) : AbstractAuditingEntity()
