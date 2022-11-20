import '../styles/globals.css'
import type {AppProps} from 'next/app'
import ApolloClient from "../clients/apollo";
import {ApolloProvider} from "@apollo/client";
import React from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import {appWithTranslation} from "next-i18next";
import { CookiesProvider } from 'react-cookie';

import { config } from '@fortawesome/fontawesome-svg-core'
import '@fortawesome/fontawesome-svg-core/styles.css' // Import the CSS
config.autoAddCss = false

const MyApp: React.FC<AppProps> = ({ Component, pageProps }) => {
    return (
        <CookiesProvider>
            <ApolloProvider client={ApolloClient}>
                <Component {...pageProps} />
            </ApolloProvider>
        </CookiesProvider>
    )
}

export default appWithTranslation(MyApp);