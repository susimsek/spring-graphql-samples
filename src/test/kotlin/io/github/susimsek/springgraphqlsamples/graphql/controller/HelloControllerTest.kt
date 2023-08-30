package io.github.susimsek.springgraphqlsamples.graphql.controller

import graphql.scalars.country.code.CountryCode
import io.github.susimsek.springgraphqlsamples.graphql.GraphQlUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.test.tester.GraphQlTester
import java.math.BigDecimal
import java.net.URL
import java.util.*

private val DEFAULT_URL = URL("https://www.w3.org/Addressing/URL/url-spec.txt")
private val DEFAULT_CONTENT = mapOf("sivas" to "58", "istanbul" to "43")
private val DEFAULT_PRICE = BigDecimal(30.82)
private val DEFAULT_CURRENCY = Currency.getInstance("USD")
private val DEFAULT_COUNTRY_CODE = CountryCode.TR

@OptIn(ExperimentalCoroutinesApi::class)
@GraphQlUnitTest([HelloController::class])
class HelloControllerTest {

    @Autowired
    private lateinit var graphQlTester: GraphQlTester

    @Test
    fun hello() = runTest {
        graphQlTester
            .documentName("helloQuery")
            .execute()
            .path("data.hello.id").hasValue()
            .path("data.hello.url").entity(URL::class.java).isEqualTo(DEFAULT_URL)
            .path("data.hello.content").entity(Map::class.java).isEqualTo(DEFAULT_CONTENT)
            .path("data.hello.sentDate").hasValue()
            .path("data.hello.price").entity(BigDecimal::class.java).isEqualTo(DEFAULT_PRICE)
            .path("data.hello.currency").entity(Currency::class.java).isEqualTo(DEFAULT_CURRENCY)
            .path("data.hello.currency").entity(Currency::class.java).isEqualTo(DEFAULT_CURRENCY)
            .path("data.hello.countryCode").entity(CountryCode::class.java).isEqualTo(DEFAULT_COUNTRY_CODE)
    }
}
