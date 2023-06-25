package io.github.susimsek.springgraphqlsamples.domain.audit

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable

open class AbstractUserAuditingEntity(

    @CreatedBy
    @Indexed
    @Field("created_by")
    var createdBy: String = "",

    @LastModifiedBy
    @Field("last_modified_by")
    var lastModifiedBy: String = ""
) : AbstractAuditingEntity(), Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}
