import * as React from "react";
import Header from "./Header";
import Footer from "./Footer";
import {Container} from "react-bootstrap";

type PageLayoutProps = {
    children?: React.ReactNode;
    narrow?: boolean;
};

const Layout: React.FC<PageLayoutProps> =({
                                       children,
                                       narrow,
                                   }) => {
    return (
        <div>
            <Header/>
            <main>
                    {children}
            </main>
            <Footer />
        </div>
    );
}

export default Layout;