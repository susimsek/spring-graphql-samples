import {ApolloClient as Apollo, createHttpLink, InMemoryCache, split} from "@apollo/client";
import {GRAPHQL_HTTP_URI} from "../constants";
import {GraphQLWsLink} from "@apollo/client/link/subscriptions";
import {createClient} from "graphql-ws";
import {getMainDefinition} from "@apollo/client/utilities";

const httpLink = createHttpLink({
    uri: GRAPHQL_HTTP_URI,
});

const wsLink = typeof window !== "undefined"
    ? new GraphQLWsLink(createClient({
        url: 'ws://localhost:4000/subscriptions',
        connectionParams: {
            authToken: 'PADIKXL0YscBUXmIm3iW/i37Mwp1MEtWbr7dHGGtQSp1IsIpKvcZlwPXgLa+8iVyin48DCMq5GrnJfrhMILTIQQcbaoW3ERMsjOEuXmgF4WLu2lh3ljVk4m2/ouZEYYR6JmJa8ZpIenUdlcy1mbWr6X1VJL4YBO2ManrjjR22j1+I66CjAVxiV7etUtDZY2mWJqv2fdsiy+RHIlAA1LXC3QC3zAq+FeEkU2QVPj7YDach7bRzt0eJfOJLsGoNgvJ580pS88I7qi7j+duksEjTVDtdCpRyR3CKL8RkiA1oApCRuYEZDYmVFs7CwKGbhNRj7bBboLKE/zyZh6Fe3f1xvzx3DvA2sRF/RXu9oOKdMJM6Rf2/bEyMkgpWcB/hB9tn/ZLreHB14gHEjlskfAx6NyCJw78HnRz+ud8HxiAir6Z+WSLDBJYsvBYKBD2OZA1O986ACxewbrlxfenDLzt0zTcX7vPuBSaKxqYB+gLVK+VjkaukwBE5PmzAa49754nLv9qfl9kVhqxqtfM8QnX9yCEC1Qk53+VKyfpGwdorAcdyLu1xOne0GGbCY76RTdIt5MVMlRXf4csFXHHZJ9KMvDMmPH9Z/ge0eERCMZhwND7UMA2Xeg3HFCDcP0hM98VNTX4JwesRZF6UHYjbnCdoLQE1ZwjlZ5EuvRMEyj63IwtLnss3MFOkYY0Yamp750drLQmjGhTms2JMEQfxm+ZPw==',
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
