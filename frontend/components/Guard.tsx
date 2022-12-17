import { useRouter } from "next/router";
import { useEffect } from "react";
import {useAuthToken} from "../contexts/AuthTokenProvider";
import {useLogout} from "../hooks/use-logout";
import * as React from "react";

interface GuardProps {
    children: React.ReactNode;
    excludedRoutes?: string[];
}

const Guard: React.FC<GuardProps> = ({children, excludedRoutes}) => {
    const [token] = useAuthToken();
    const router = useRouter();

    const handleSignOut = useLogout()

    useEffect(() => {
    }, [router.pathname, excludedRoutes]);

    useEffect(() => {
        if (!token && !excludedRoutes?.includes(router.pathname)) {
            handleSignOut()
            router.push("/login")
        }
    }, [token, router, excludedRoutes, handleSignOut]);

    return (
        <>
            {excludedRoutes?.includes(router.pathname) ? (
                children
            ) : (
                <>{token && children}</>
            )}
        </>
    );
};

export default Guard;