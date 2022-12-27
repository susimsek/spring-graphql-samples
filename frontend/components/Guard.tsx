import { useRouter } from "next/router";
import { useEffect } from "react";
import {useAuth} from "../contexts/AuthProvider";
import {useLogout} from "../hooks/use-logout";
import * as React from "react";

interface GuardProps {
    children: React.ReactNode;
    excludedRoutes?: string[];
}

const Guard: React.FC<GuardProps> = ({children, excludedRoutes}) => {
    const [isLoggedIn] = useAuth();
    const router = useRouter();

    const handleSignOut = useLogout()

    useEffect(() => {
        if (!isLoggedIn && !excludedRoutes?.includes(router.pathname)) {
            router.push({
                pathname: '/login',
                query: { returnUrl: router.asPath }
            });
        }
    }, [isLoggedIn, router, excludedRoutes, handleSignOut]);

    return (
        <>
            {excludedRoutes?.includes(router.pathname) ? (
                children
            ) : (
                <>{isLoggedIn && children}</>
            )}
        </>
    );
};

export default Guard;