const prod = process.env.NODE_ENV === 'production'
const GRAPHQL_HTTP_URI = prod ? 'http://api.springgqlmsweb.susimsek.github.io/graphql' : 'http://localhost:9091/graphql'
const GRAPHQL_WS_URL = prod ? 'ws://api.springgqlmsweb.susimsek.github.io/subscriptions' : 'ws://localhost:9091/subscriptions'


const ACCESS_TOKEN_COOKIE_NAME = 'accessToken'

const LANGUAGES = [
    {
        locale: 'en',
        countryCode: 'US'
    },
    {
        locale: 'tr',
        countryCode: 'TR'
    }
];

export {
    GRAPHQL_HTTP_URI,
    GRAPHQL_WS_URL,
    LANGUAGES,
    ACCESS_TOKEN_COOKIE_NAME
}