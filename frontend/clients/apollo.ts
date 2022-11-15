import {ApolloClient as Apollo, createHttpLink, InMemoryCache, split} from "@apollo/client";
import {GRAPHQL_HTTP_URI, GRAPHQL_WS_URL} from "../constants";
import {GraphQLWsLink} from "@apollo/client/link/subscriptions";
import {createClient} from "graphql-ws";
import {getMainDefinition} from "@apollo/client/utilities";
import {setContext} from "@apollo/client/link/context";

const httpLink = createHttpLink({
    uri: GRAPHQL_HTTP_URI,
});

const authLink = setContext((_, { headers }) => {
    return {
        headers: {
            ...headers,
            authorization: 'Bearer 4cF8r+zUivwptsRCt30ENeXPpTXwYtYBb9EQD2ECFl3J1lGe22RXVpl0+pF/z1maZynZ8hRl1dalrn0b9kFQv/PYUwf0ye6SesOTr6x9nu9gWX1dea4y7CcyH8UDBYntsxUw8XuZeEBFoY+gjB1jNYuMPUwPmL5ynfF06QLzXJFoX7V6tf8HCANUH4LIpgM9SW6NUT6iJaFCCdR9kSuLX2RpcPcT5iTzspqedsnonAHWzaKgmP8JlxtIF9A6LAVD/iWIDblYug6cVFGTpIp4sK/ki99H0KLfr4xqnw0kaAHvdcXBEAohRzWrT4ED1KNkuUlOuRncxTj8YpE80Kb2ZmZGbo9kEEWbNIkUSZbCV9DOF8oPF7kX0U5RvkvvpPlW6/m8Uy/enJh30bF6JlY10jsolFZh39dy/9W75q2JOn9InZL9n67CCP4AYcuoHnK11vwtuHP2U56EJRmxIl7vdjGn87YayDK4Rnrc3nHFaGHKpfAfJ+IQQAWOY0qy7vYUxpUB9Hp0xAzNJyN36kx/zn4kx+ukPHxgQ6OhBfvmGiaNjgDVZ7z0zafmT415nya1k1PfVUiLY7VQiozlkGSE1c7VJK7HmNZoxvV6alW1ApSeNw6itvZk6zMTDUsoi6a7T5HAXY3UuuYLMnWnwEogOkpXdmGEUXrBTE03oVcO8zZZ3xMTSchZw4bBpTgr8FGtJjnYOcC6C5XrYD+olD46ZpmpyFU5nAn85XDsh+FNHVI='
        }
    }
});

const wsLink = typeof window !== "undefined"
    ? new GraphQLWsLink(createClient({
        url: GRAPHQL_WS_URL,
        connectionParams: {
            authToken: 'Bearer 4cF8r+zUivwptsRCt30ENeXPpTXwYtYBb9EQD2ECFl3J1lGe22RXVpl0+pF/z1maZynZ8hRl1dalrn0b9kFQv/PYUwf0ye6SesOTr6x9nu9gWX1dea4y7CcyH8UDBYntsxUw8XuZeEBFoY+gjB1jNYuMPUwPmL5ynfF06QLzXJFoX7V6tf8HCANUH4LIpgM9SW6NUT6iJaFCCdR9kSuLX2RpcPcT5iTzspqedsnonAHWzaKgmP8JlxtIF9A6LAVD/iWIDblYug6cVFGTpIp4sK/ki99H0KLfr4xqnw0kaAHvdcXBEAohRzWrT4ED1KNkuUlOuRncxTj8YpE80Kb2ZmZGbo9kEEWbNIkUSZbCV9DOF8oPF7kX0U5RvkvvpPlW6/m8Uy/enJh30bF6JlY10jsolFZh39dy/9W75q2JOn9InZL9n67CCP4AYcuoHnK11vwtuHP2U56EJRmxIl7vdjGn87YayDK4Rnrc3nHFaGHKpfAfJ+IQQAWOY0qy7vYUxpUB9Hp0xAzNJyN36kx/zn4kx+ukPHxgQ6OhBfvmGiaNjgDVZ7z0zafmT415nya1k1PfVUiLY7VQiozlkGSE1c7VJK7HmNZoxvV6alW1ApSeNw6itvZk6zMTDUsoi6a7T5HAXY3UuuYLMnWnwEogOkpXdmGEUXrBTE03oVcO8zZZ3xMTSchZw4bBpTgr8FGtJjnYOcC6C5XrYD+olD46ZpmpyFU5nAn85XDsh+FNHVI=',
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
