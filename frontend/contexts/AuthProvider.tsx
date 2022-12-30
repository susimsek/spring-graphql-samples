import * as React from "react";

type IAuthContext = {
    isLoggedIn: boolean | false;
    updateIsLoggedIn(isLoggedIn: boolean | false): void;
};

const AuthContext = React.createContext<IAuthContext>({
    isLoggedIn: false,
    updateIsLoggedIn() {},
});

type AuthContextProviderProps = {
    children: React.ReactNode;
};

const AuthProvider: React.FC<AuthContextProviderProps> = ({ children }) => {
    const [isLoggedIn, setIsLoggedIn] = React.useState<boolean | undefined | false>(
        undefined
    );

    React.useEffect(() => {
        setIsLoggedIn(!!localStorage.getItem("isLoggedIn"));
    }, []);

    function updateIsLoggedIn(isLoggedIn: boolean | false) {
        if (!isLoggedIn) {
            localStorage.removeItem("isLoggedIn");
        } else {
            localStorage.setItem("isLoggedIn", String(isLoggedIn));
        }

        setIsLoggedIn(isLoggedIn);
    }

    return isLoggedIn === undefined ? null : (
        <AuthContext.Provider value={{ isLoggedIn, updateIsLoggedIn }}>
            {children}
        </AuthContext.Provider>
    );
}

const useAuth = () => {
    const { isLoggedIn, updateIsLoggedIn } = React.useContext(AuthContext);
    return [isLoggedIn, updateIsLoggedIn] as const;
}

export { useAuth, AuthProvider };