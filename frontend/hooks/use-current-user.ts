import {useMeQuery} from "../generated/graphql-types";
import {IUser} from "../types/user";
import {useLogout} from "./use-logout";
import {useRouter} from "next/router";

export function useCurrentUser() {
    const { loading, error, data } = useMeQuery();

    const {clearStore} = useLogout()

    const router = useRouter()

    if (loading) {
        return null
    } else if (error || !data) {
        clearStore()
        router.push('/')
        return null
    }

    return data.me as IUser;
}
