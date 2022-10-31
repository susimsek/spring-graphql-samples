package io.github.susimsek.springgraphqlsamples.config

import graphql.GraphQL
import graphql.analysis.QueryVisitorFieldArgumentEnvironment
import graphql.analysis.QueryVisitorFieldArgumentInputValue
import graphql.execution.Execution
import graphql.execution.nextgen.result.RootExecutionResultNode
import graphql.language.Argument
import graphql.language.ArrayValue
import graphql.language.BooleanValue
import graphql.language.Directive
import graphql.language.DirectiveDefinition
import graphql.language.DirectiveLocation
import graphql.language.Document
import graphql.language.EnumTypeDefinition
import graphql.language.EnumTypeExtensionDefinition
import graphql.language.EnumValue
import graphql.language.EnumValueDefinition
import graphql.language.Field
import graphql.language.FieldDefinition
import graphql.language.FloatValue
import graphql.language.FragmentDefinition
import graphql.language.FragmentSpread
import graphql.language.ImplementingTypeDefinition
import graphql.language.InlineFragment
import graphql.language.InputObjectTypeDefinition
import graphql.language.InputObjectTypeExtensionDefinition
import graphql.language.InputValueDefinition
import graphql.language.IntValue
import graphql.language.InterfaceTypeDefinition
import graphql.language.InterfaceTypeExtensionDefinition
import graphql.language.ListType
import graphql.language.NonNullType
import graphql.language.NullValue
import graphql.language.ObjectField
import graphql.language.ObjectTypeDefinition
import graphql.language.ObjectTypeExtensionDefinition
import graphql.language.ObjectValue
import graphql.language.OperationDefinition
import graphql.language.OperationTypeDefinition
import graphql.language.ScalarTypeDefinition
import graphql.language.ScalarTypeExtensionDefinition
import graphql.language.SchemaDefinition
import graphql.language.SchemaExtensionDefinition
import graphql.language.SelectionSet
import graphql.language.StringValue
import graphql.language.TypeDefinition
import graphql.language.TypeName
import graphql.language.UnionTypeDefinition
import graphql.language.UnionTypeExtensionDefinition
import graphql.language.VariableDefinition
import graphql.language.VariableReference
import graphql.parser.ParserOptions
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLEnumValueDefinition
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLOutputType
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLSchemaElement
import graphql.schema.GraphQLUnionType
import graphql.schema.validation.SchemaValidationErrorCollector
import graphql.util.NodeAdapter
import graphql.util.NodeZipper
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeReference
import org.springframework.core.io.ClassPathResource
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import java.util.function.Consumer

class GraphqlRuntimeHintsRegistrar : RuntimeHintsRegistrar {
    private val values = MemberCategory.values()
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        mutableSetOf(ClassPathResource("graphiql/index.html"), GraphqlDateTimeConfig.SCHEMA_RESOURCE)
            .forEach(Consumer { s: ClassPathResource -> hints.resources().registerResource(s) })
        mutableSetOf("i18n/Validation.properties", "i18n/Validation", "i18n/Execution.properties", "i18n/General.properties")
            .forEach(Consumer { r: String -> hints.resources().registerResourceBundle(r) })
        mutableSetOf("graphql.analysis.QueryTraversalContext", "graphql.schema.idl.SchemaParseOrder")
            .forEach(
                Consumer { typeName: String ->
                hints.reflection().registerType(TypeReference.of(typeName), *values)
            }
            )
        mutableSetOf(
            ReactiveJwtAuthenticationConverterAdapter::class.java,
            Argument::class.java,
            ArrayValue::class.java,
            Boolean::class.java,
            BooleanValue::class.java,
            DataFetchingEnvironment::class.java,
            Directive::class.java,
            DirectiveDefinition::class.java,
            DirectiveLocation::class.java,
            Document::class.java,
            EnumTypeDefinition::class.java,
            EnumTypeExtensionDefinition::class.java,
            EnumValue::class.java,
            EnumValueDefinition::class.java,
            Execution::class.java,
            Field::class.java,
            FieldDefinition::class.java,
            FloatValue::class.java,
            FragmentDefinition::class.java,
            FragmentSpread::class.java,
            GraphQL::class.java,
            GraphQLArgument::class.java,
            GraphQLCodeRegistry.Builder::class.java,
            GraphQLDirective::class.java,
            GraphQLEnumType::class.java,
            GraphQLEnumValueDefinition::class.java,
            GraphQLFieldDefinition::class.java,
            GraphQLInputObjectField::class.java,
            GraphQLInputObjectType::class.java,
            GraphQLInterfaceType::class.java,
            GraphQLList::class.java,
            GraphQLNamedType::class.java,
            GraphQLNonNull::class.java,
            GraphQLObjectType::class.java,
            GraphQLOutputType::class.java,
            GraphQLScalarType::class.java,
            GraphQLSchema::class.java,
            GraphQLSchemaElement::class.java,
            GraphQLUnionType::class.java,
            ImplementingTypeDefinition::class.java,
            InlineFragment::class.java,
            InputObjectTypeDefinition::class.java,
            InputObjectTypeExtensionDefinition::class.java,
            InputValueDefinition::class.java,
            IntValue::class.java,
            InterfaceTypeDefinition::class.java,
            InterfaceTypeExtensionDefinition::class.java,
            MutableList::class.java,
            ListType::class.java,
            NodeAdapter::class.java,
            NodeZipper::class.java,
            NonNullType::class.java,
            NullValue::class.java,
            ObjectField::class.java,
            ObjectTypeDefinition::class.java,
            ObjectTypeExtensionDefinition::class.java,
            ObjectValue::class.java,
            OperationDefinition::class.java,
            OperationTypeDefinition::class.java,
            ParserOptions::class.java,
            QueryVisitorFieldArgumentEnvironment::class.java,
            QueryVisitorFieldArgumentInputValue::class.java,
            RootExecutionResultNode::class.java,
            ScalarTypeDefinition::class.java,
            ScalarTypeExtensionDefinition::class.java,
            SchemaDefinition::class.java,
            SchemaExtensionDefinition::class.java,
            SchemaValidationErrorCollector::class.java,
            SelectionSet::class.java,
            StringValue::class.java,
            TypeDefinition::class.java,
            TypeName::class.java,
            UnionTypeDefinition::class.java,
            UnionTypeExtensionDefinition::class.java,
            VariableDefinition::class.java,
            VariableReference::class.java
        ) //
            .forEach(Consumer { aClass: Class<out Any> -> hints.reflection().registerType(aClass, *values) })
    }
}
