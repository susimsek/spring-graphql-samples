import * as React from "react";
import TopBar from "./TopBar";
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
            <TopBar/>
            <main>
                    {children}
            </main>
            <Footer />
        </div>
    );
}

export default Layout;