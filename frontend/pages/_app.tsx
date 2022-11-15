import '../styles/globals.css'
import type {AppProps} from 'next/app'
import ApolloClient from "../clients/apollo";
import {ApolloProvider} from "@apollo/client";
import React from "react";
import 'bootstrap/dist/css/bootstrap.min.css';

const MyApp: React.FC<AppProps> = ({ Component, pageProps }) => {
    return (
        <ApolloProvider client={ApolloClient}>
            <Component {...pageProps} />
        </ApolloProvider>
    )
}

export default MyApp;