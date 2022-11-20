import {ApolloClient as Apollo, createHttpLink, InMemoryCache, split} from "@apollo/client";
import {GRAPHQL_HTTP_URI, GRAPHQL_WS_URL} from "../constants";
import {GraphQLWsLink} from "@apollo/client/link/subscriptions";
import {createClient} from "graphql-ws";
import {getMainDefinition} from "@apollo/client/utilities";
import {setContext} from "@apollo/client/link/context";
import {Cookies} from "react-cookie";

const httpLink = createHttpLink({
    uri: GRAPHQL_HTTP_URI,
});

const cookies = new Cookies();

const token = 'S8hr0YGKFV2ODeYB/r0PUVmTlW510Hzf7UehkSW9Jbr4/tdSsPticJtpPw9fInixlLRTPJ9OEh84hOkbj32Ndbfgr00+HlRvwoM7rHeiUkjp8wIaljQJVVjNzcIwulzuKoe1JuBIXr/lzf/J5QuslZzgJQo+lVydo7SkQeC1xEYn5t2cE9QpM46oULyxK/GNMZ4lkwbMmbu/XNrRW8vg1OQz+59VHyYqyrUbxnh2dX1S76q4nugmR1I/YoDFcZ/ib6TG5iJ3upsNyiRWCpkOYhUvcA35njnv4Exof14lmt6OwLmkKKo3ZHi76OMMNFViCvrv1NkuEZZP60vUCpHnOPD0hPoJqThB+y4BefGl2D3Z1I3CcxWc6Q4bTGuJpWSOK6KDqfH7QNo3jFgQNePtgOyOEdtkTZjuhQS4Lw1G8mwJyRs0UMjCT9rQBaVBksmT5aWW1PvuTUK47JpFyfFmmCU3BeN3VvnOOxUcNsRhxI1+vROubMAMCQu2Kg3Cy2ZL90xvMQRTxlaZk6DGejcDZp4zyUJ5KuzgrL4IBT+I0XTeEw9UGAL2r5X6M8bI/k1Kf9OCx2wWJKBYvUBPy5vygOpt5sgT22zPv83q+gNrqd7YYvjUzzQAsd3IFQ0PJair9Tg0nsiDjpX7n5ivbTRfLfZK/5fqRgQToceNVxfv85my/Efj6R1q3NP394vP+MUBA1xHepf+kAAxiA==';

const authLink = setContext((_, { headers }) => {
    return {
        headers: {
            ...headers,
            authorization: `Bearer ${token}`,
            'Accept-Language': cookies.get('NEXT_LOCALE'),
        }
    }
});

const wsLink = typeof window !== "undefined"
    ? new GraphQLWsLink(createClient({
        url: GRAPHQL_WS_URL,
        connectionParams: {
            authorization: `Bearer ${token}`,
        }
    }))
    : null;

const link =
    typeof window !== "undefined" && wsLink != null
        ? split(
            ({ query }) => {
                const def = getMainDefinition(query);
                return (
                    def.kind === "OperationDefinition" &&
                    def.operation === "subscription"
                );
            },
            wsLink,
            authLink.concat(httpLink)
        )
        : authLink.concat(httpLink);


const ApolloClient = new Apollo({
    link: link,
    cache: new InMemoryCache(),
});

export default ApolloClient;
