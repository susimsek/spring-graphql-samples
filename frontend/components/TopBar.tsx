import React from "react";
import {Container, Nav, Navbar} from "react-bootstrap";
import logo from '../public/logo.png'
import Image from "next/image";
import {useTranslation} from "next-i18next";
import LanguageBar from "./LanguageBar";
import {useAuth} from "../contexts/AuthProvider";
import AccountMenu from "./AccountMenu";
import Link from "next/link";

const TopBar: React.FC = () => {
    const { t } = useTranslation()

    const [isLoggedIn] = useAuth();

    return (
        <Navbar bg="dark" variant="dark" expand="lg">
            <Container>
                <Navbar.Brand href="/">
                    <Image
                        src={logo}
                        alt="GraphQL"
                        width="30"
                        height="30"
                        className="d-inline-block align-top"
                    />{' '}GraphQL</Navbar.Brand>
                <Navbar.Toggle aria-controls="responsive-navbar-nav" />
                <Navbar.Collapse id="responsive-navbar-nav">
                    <Nav className="me-auto">
                        <Nav.Link as={Link} href="/">{t('menu.home')}</Nav.Link>
                        {isLoggedIn && <Nav.Link as={Link} href="/my-posts">{t('menu.myPosts')}</Nav.Link>}
                    </Nav>
                    <Nav>
                        {
                            isLoggedIn ? <AccountMenu/>
                                : <>
                                    <Nav.Link as={Link} href="/signup">{t('account.register')}</Nav.Link>
                                    <Nav.Link as={Link} href="/login">{t('account.login')}</Nav.Link>
                                </>
                        }

                        <LanguageBar/>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default TopBar;