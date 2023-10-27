package io.github.susimsek.springgraphqlsamples.rest.payload

import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    enumAsRef = true,
    title = "Gender",
    example = "MALE",
    description = "Description: Gender\n"
            + "* `NOT_SPECIFIED` - Not Specified\n"
            + "* `MALE` - Male\n"
            + "* `FEMALE` - Female"
)
enum class Gender {
    NOT_SPECIFIED,
    MALE,
    FEMALE
}