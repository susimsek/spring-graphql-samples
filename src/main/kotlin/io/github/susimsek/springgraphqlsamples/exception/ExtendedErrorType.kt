package io.github.susimsek.springgraphqlsamples.exception

import graphql.ErrorClassification

enum class ExtendedErrorType : ErrorClassification {
    THROTTLED
}
