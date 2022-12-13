import * as React from "react";

type IAuthContext = {
    token: string | null;
    updateToken(token: string | null): void;
};

const AuthContext = React.createContext<IAuthContext>({
    token: null,
    updateToken() {},
});

type AuthContextProviderProps = {
    children: React.ReactNode;
};

const AuthTokenProvider: React.FC<AuthContextProviderProps> = ({ children }) => {
    const [token, setToken] = React.useState<string | null | undefined>(
        undefined
    );

    React.useEffect(() => {
        setToken(localStorage.getItem("token") || null);
    }, []);

    function updateToken(newToken: string | null) {
        if (!newToken) {
            localStorage.removeItem("token");
        } else {
            localStorage.setItem("token", newToken);
        }

        setToken(newToken);
    }

    return token === undefined ? null : (
        <AuthContext.Provider value={{ token, updateToken }}>
            {children}
        </AuthContext.Provider>
    );
}

const useAuthToken = () => {
    const { token, updateToken } = React.useContext(AuthContext);
    return [token, updateToken] as const;
}

export { useAuthToken, AuthTokenProvider };