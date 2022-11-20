import React from "react";
import {Container, Nav, Navbar} from "react-bootstrap";
import logo from '../public/logo.png'
import Image from "next/image";
import {useTranslation} from "next-i18next";
import LanguageBar from "./LanguageBar";

const Header: React.FC = () => {
    const { t } = useTranslation()

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
                        <Nav.Link href="/">{t('home.label')}</Nav.Link>
                    </Nav>
                    <Nav>
                        <LanguageBar/>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default Header;