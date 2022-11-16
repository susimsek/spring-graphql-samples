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

const token = 'dIvXJpYcpMRKITv+kZuVVENE6H/cH05rKrgEfPE2695uXf7H4rd0O0DJkfjVfnYMmaXVLP8j+4DcrkognGRdDxguwzZDisalgwZyMT1DLjMjuKCtMky6+H+jEo4/iU8KoZzZn9Hv6RI7suZOMMqlNeRbvhtt1LB+cL+QPnaGnfZkKDIk2QlHrEC0QKoHmloqrj7JaaSrbbc1iIngRW5j+hCaREnSt8Ls1DQqs/YufzvlW1Ybh+2i0cCK0rMxHw2Ek894RhCm5Sbs8rcgbgw0VChf00UcjYwUlFWUt/rJfD/WR+INy6Te1wbENAizer4M43Shl7MwuA39Ldb4z5G36gx/ame6UUYl9t0JC9E7zf+9V8FTe1/5mmg/3B3p+CU358M45VCulyGD+xU6m1pDc19FJ9IFUReYSSV2hZd0c/bnjYOGfi+n6VWRkFForWAMjfjDTbzjt/+kTz0UK0c00t+WeRXgm64TKRCi7FQwzOVAuMenJzKDHPnKuO4+2HJuDDvhSFa8hPt9i46rtYeq3kYoWUSajaPVwgRYwuXpQgRte5+A46B+jWcFZ7hKndTdMnZOVrNDHoygGrCcVV+ZecxPzynACqaGR8gyvAPE1LbGDQIFD00sqACh2z+h+9L+FLhQ6SndrVdwhVmy6IKdoH9RwdGo2+M7gUEkL2dY2Pdy005SgPizdj2/6HzXgXaqC44bFgo4wE9YlMLe+2P+Qw==';

const authLink = setContext((_, { headers }) => {
    const lng = cookies.get('NEXT_LOCALE')
    return {
        headers: {
            ...headers,
            authorization: `Bearer ${token}`,
            'Accept-Language': lng,
        }
    }
});

const wsLink = typeof window !== "undefined"
    ? new GraphQLWsLink(createClient({
        url: GRAPHQL_WS_URL,
        connectionParams: {
            authToken: `Bearer ${token}`
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
