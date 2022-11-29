package io.github.susimsek.springgraphqlsamples.graphql.directive

import graphql.schema.idl.SchemaDirectiveWiring

data class SchemaDirective(
    var name: String,
    var directive: SchemaDirectiveWiring
)
