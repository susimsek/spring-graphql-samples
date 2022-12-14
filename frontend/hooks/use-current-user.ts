import {useMeQuery} from "../generated/graphql-types";
import {IUser} from "../types/user";

export function useCurrentUser() {
    const { loading, error, data } = useMeQuery();

    if (loading || error || !data) {
        return null
    }

    return data.me as IUser;
}
