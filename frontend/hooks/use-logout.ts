import { useApolloClient } from "@apollo/client";
import {useAuth} from "../contexts/AuthProvider";
import {useLogoutMutation} from "../generated/graphql-types";

export function useLogout() {
    const client = useApolloClient();
    const [, updateIsLoggedIn] = useAuth();

    const [signOut] = useLogoutMutation();

    return async function logout() {
        await signOut()
        updateIsLoggedIn(false);
        await client.clearStore();
    };
}