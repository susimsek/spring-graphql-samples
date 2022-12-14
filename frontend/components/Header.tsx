import React from "react";
import {Container, Nav, Navbar} from "react-bootstrap";
import logo from '../public/logo.png'
import Image from "next/image";
import {useTranslation} from "next-i18next";
import LanguageBar from "./LanguageBar";
import {useAuthToken} from "../contexts/AuthTokenProvider";
import AccountMenu from "./AccountMenu";
import {useRouter} from "next/router";
import Link from "next/link";

const Header: React.FC = () => {
    const { t } = useTranslation()

    const [token] = useAuthToken();

    return (
        <Navbar bg="dark" variant="dark" expand="lg">
            <Container>
                <Navbar.Brand href="/">
                    <Image
                        src={logo}
                        alt="I18N"
                        width="30"
                        height="30"
                        className="d-inline-block align-top"
                    />{' '}I18N</Navbar.Brand>
                <Navbar.Toggle aria-controls="responsive-navbar-nav" />
                <Navbar.Collapse id="responsive-navbar-nav">
                    <Nav className="me-auto">
                        <Nav.Link as={Link} href="/">{t('menu.home')}</Nav.Link>
                        {token && <Nav.Link as={Link} href="/my-posts">{t('menu.myPosts')}</Nav.Link>}
                    </Nav>
                    <Nav>
                        {
                            token ? <AccountMenu/>
                                : <Nav.Link as={Link} href="/login">{t('login')}</Nav.Link>
                        }

                        <LanguageBar/>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default Header;