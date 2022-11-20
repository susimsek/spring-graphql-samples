import React from "react";
import {Container, Nav, Navbar} from "react-bootstrap";
import logo from '../public/logo.png'
import Image from "next/image";
import {useTranslation} from "next-i18next";

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
                <Nav className="me-auto">
                    <Nav.Link href="/">{t('home.label')}</Nav.Link>
                </Nav>
            </Container>
        </Navbar>
    );
}

export default Header;