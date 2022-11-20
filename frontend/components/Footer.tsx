import React from "react";
import {Container, Nav, Navbar} from "react-bootstrap";
import logo from '../public/logo.png'
import Image from "next/image";
import {useTranslation} from "next-i18next";

const Footer: React.FC = () => {

    const { t } = useTranslation()

    return (
        <footer>
            <div className="footer-copyright text-center py-3">©2022 Şuayb Şimşek | {t('copyright')}</div>
        </footer>
    );
}

export default Footer;