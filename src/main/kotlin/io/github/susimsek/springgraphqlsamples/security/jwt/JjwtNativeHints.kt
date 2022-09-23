package io.github.susimsek.springgraphqlsamples.security.jwt

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.nativex.hint.TypeAccess
import org.springframework.nativex.hint.TypeHint

@TypeHint(
    typeNames = ["io.jsonwebtoken.impl.DefaultJwtParserBuilder", "io.jsonwebtoken.impl.DefaultJwtBuilder"],
    access = [TypeAccess.RESOURCE, TypeAccess.PUBLIC_CLASSES, TypeAccess.DECLARED_CLASSES,
        TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_METHODS,
        TypeAccess.PUBLIC_METHODS, TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS]
)
@Lazy(false)
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = ["org.springframework.nativex.NativeListener"])
class JjwtNativeHints
