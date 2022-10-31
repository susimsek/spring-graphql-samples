package io.github.susimsek.springgraphqlsamples.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("security.authentication.token")
data class TokenProperties(
    var tokenValidityInSeconds: Long = 1800L,
    var publicKey: String =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu17slRZjJj/K+u0+1tlG3udhc20KD" +
                "ZI+hAjrfsXKOatO1yUxDteTcZubZdsLL5I/0bA569pbMPm5P0VwTfUBpMEpWIQac4Za/" +
                "hmpTMqA1SQSnoGRnqb0Mt84qC9aMyweoC+sJME96E0vyD1nOPjqHksQV3gL7Y7IaJGYHhOfJ" +
                "nNJgNQKEx9it+7ZtjmRjQpnnCLxGZB3+iUH+svhJW4Qkb9nwZ9JOVErk0jjuQZrXACQhFimA" +
                "WuY4a7dW6KS2i7o1qylEDyeUKFxkGapfoZveddb3WzMmEVB3fJGdGgOQby8eYkbQTGQcgZ+0" +
                "/eb49XfZnLAO/akjhEN2LMMmaqZtQIDAQAB",
    var privateKey: String =
        "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC7XuyVFmMmP8r67T7W2Ube52FzbQoNk" +
                "j6ECOt+xco5q07XJTEO15Nxm5tl2wsvkj/RsDnr2lsw+bk/RXBN9QGkwSlYhBpzhlr+GalMyoDVJ" +
                "BKegZGepvQy3zioL1ozLB6gL6wkwT3oTS/IPWc4+OoeSxBXeAvtjshokZgeE58mc0mA1AoTH2K37tm2" +
                "OZGNCmecIvEZkHf6JQf6y+ElbhCRv2fBn0k5USuTSOO5BmtcAJCEWKYBa5jhrt1bopLaLujWrKUQPJ5Qo" +
                "XGQZql+hm9511vdbMyYRUHd8kZ0aA5BvLx5iRtBMZByBn7T95vj1d9mcsA79qSOEQ3YswyZqpm1AgMBAAE" +
                "CggEAB+nqdCGN7n+jQxXiIZDrvV5ob0ebtRcILOtzhtczw1vI6K1ZdRcF0zaPSa4GuuuiZmjbCWsfjuH9" +
                "QP7dz20ZtKuz41kxBNAX58ONswCakGYFo0w1qk3C4qMq8W+Oj7q9UGazDppa0wTvj7WINo/M05q4Bfm8BzyQm" +
                "loL4rlRb348aH8LseRajDUSEyV1nYHVE2TLnRfMmJXJ4iL1LEyEoWl+910PbFRf5jNiOMJ368Pu/r8vw2T1/c0zp5" +
                "W3qEt9ErT9PydT12M9RKHC6aAP3E/nhgIvXGyQMGvZ7Z4Dt9umUzINPrmo6JqL0qIT3Ya+AQsjz4TS4lPlYjnu1A7SA" +
                "QKBgQD3q9/RGbuOBW3n2jnxxQa/HdUNFKVphZn4YVIMqCC93RU+mkq8XrOxynkSsR3nz2zKEQlzOkIs7WWs5r9tcxc8" +
                "9msUInRh0vLd1Ag2awwXShQ0CGsG4Rr731qRVZMt38ZUa3c/7BQLrk1q3GteNaxO7CeNewljcK/bHRycwANifQKBgQDB" +
                "q/DgGfC8+BaPb0dSjxgi1LKbVf1lud8t8RMMyhf3Jse4nJl4v6DItsA9aSOkRHO0cf9tuuMBJ8hQkZ2qxAblwP2ARKpdKi" +
                "j3odKnS28b2fUc7UPdhTrdNbXTUeOM+PUYQRZBXVr2f1WGK4iuLevT07bFvqYA0sOhJjG3EJ9BmQKBgQCIs6BE3Mwt1odUOpp" +
                "S/On9YOzwXF30gV57vtO30rDHuxh3xlfL4wWR82yiSYp1IYMtgPvTUuulup6tMWulcKn55xUxNtdAC1wr3lVuG+W/kQ7XrXHNiUA" +
                "Il754i2BCKMpiXDKk4Jwr2lg2zFSi2kMyCJzINYn4DgGykj3xsvXrTQKBgQChq5qkOINR+/c5PTdYn8MSMGP4b8vcA1Fe1IOGhZ" +
                "2D34eRgIhM2AqsAoJvwTjTg6Di3NbRDtU4vbDmMOhhCMqJOJVlYeylYPBZ52gl5z6VDVkkEJw6a2E8D/38rbs6jwhqesrNPdUVO" +
                "C54lha+7+6RIYbNg9yni0lrWKrfmqyW4QKBgQCLondh3JS9AZgQITl3x8UFTMNMtDQJeQfABvqHYpc83AfKpxUgxyQrFkZS0lki" +
                "srvvvXXYvETcnttyAFqh/um2tPrEn4z6cAsQStX5p3yO/DAfd6I59YOL69OHxSUBkh6DCyh6BXUfUhQCdydkdOSs9pUKR26VxjkL" +
                "Jco+icLpBQ=="
)
