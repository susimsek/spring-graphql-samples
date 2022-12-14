import {ApolloClient as Apollo, createHttpLink, InMemoryCache, split} from "@apollo/client";
import {GRAPHQL_HTTP_URI, GRAPHQL_WS_URL} from "../constants";
import {GraphQLWsLink} from "@apollo/client/link/subscriptions";
import {createClient} from "graphql-ws";
import {getMainDefinition} from "@apollo/client/utilities";
import {setContext} from "@apollo/client/link/context";
import {Cookies} from "react-cookie";

const httpLink = createHttpLink({
    uri: GRAPHQL_HTTP_URI,
    credentials: 'include'
});

const cookies = new Cookies();
const authLink = setContext((_, { headers }) => {
    // const token = localStorage.getItem("token")
    const lang = cookies.get('NEXT_LOCALE')
    return {
        headers: {
            ...headers,
            // authorization: token ? `Bearer ${token}` : "",
            'Accept-Language':   lang ? lang : "en",
        }
    }
});

const wsLink = typeof window !== "undefined"
    ? new GraphQLWsLink(
        createClient({
        url: GRAPHQL_WS_URL
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
