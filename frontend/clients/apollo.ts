import {ApolloClient as Apollo, createHttpLink, InMemoryCache, split} from "@apollo/client";
import {GRAPHQL_HTTP_URI, GRAPHQL_WS_URL} from "../constants";
import {GraphQLWsLink} from "@apollo/client/link/subscriptions";
import {createClient} from "graphql-ws";
import {getMainDefinition} from "@apollo/client/utilities";

const httpLink = createHttpLink({
    uri: GRAPHQL_HTTP_URI,
});

const wsLink = typeof window !== "undefined"
    ? new GraphQLWsLink(createClient({
        url: GRAPHQL_WS_URL,
        connectionParams: {
            authToken: 'Bearer CMYyRbUVfx38p12Llw12cXYQseSASiUgkwudqBf7JNmDOt+g6LaknXcqlFOluWavMq7Jxt7KUoOtSUwuYQeSJktGUz//9klMnX16XhwrnpFJ1B/LGVda0xDlZjVSxODybNEETlqWzddx0P0UJM0QjZpXtHz3uUnsJ3tGWOB61OH3wIE2REgtzSV16+pJIa5/4Vv+BoqAZmzGEZN43/bNnzp10DRX5UkWmAiugMqXO/awJ78c98gkCDpC21NajoBuJBLXv6aYCoxBx9qDIhUZTGluUh4ZwhXmyryVbrRt0FNdE3w/CuIMyuNpjvk4brAj41nYGEMYZ4ZZ5K2HerBYlU+lXE/8Ug2YUfqVmGvnHA90PuOxwl/4jestL7DCBiAqAXmjtKZ/jG4sZvNNuRGCx8iocZozf84Dx6rnWqCESbBXSILvopH9NzkK12scMUQuiB10h1DjNRZ9qcIz4Y5F9cCgfAzxK5x8rhqOw1EwXynImkIh/bp8x5Wu+wZlMi4W8bgtJt4xIJeA/hIgH7j2XprIybWIJ74hy9qPn/WGcv943AEeIcMCTdFIN2UhMUFQXZWBdA5nZZIHWzTRDKCuD6okAg5/n1tffIBrr0KpWRXfvpNSAweqPRC3gO2USbDPun8CtaQPgzgueq++mbmJ+R25mCJSj7jPo9x0iGkoG3sgRxX3j7/H9M73apom0QLirE/mp/8n6+NIlwhw2Q+pGEYr0HDkDpwRi9bHFk7Rx0E=',
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
            httpLink
        )
        : httpLink;


const ApolloClient = new Apollo({
    link: link,
    cache: new InMemoryCache(),
});

export default ApolloClient;
