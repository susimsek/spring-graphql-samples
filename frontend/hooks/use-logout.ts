import { useApolloClient } from "@apollo/client";
import {useAuth} from "../contexts/AuthProvider";
import {useLogoutMutation} from "../generated/graphql-types";
import {useRouter} from "next/router";

export function useLogout() {
    const client = useApolloClient();
    const [, updateIsLoggedIn] = useAuth();

    const [signOut] = useLogoutMutation();

    const router = useRouter();

    async function logout() {
        await signOut()
        await clearStore()
    }

    async function clearStore() {
        updateIsLoggedIn(false);
        await client.clearStore();
    };

    return { logout, clearStore }
}