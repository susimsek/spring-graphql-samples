import React from "react";
import {NavDropdown, Navbar} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faUser} from "@fortawesome/free-solid-svg-icons";
import {Trans, useTranslation} from "next-i18next";
import {useLogout} from "../hooks/use-logout";
import {useCurrentUser} from "../hooks/use-current-user";


const AccountMenu: React.FC = () => {

    const { t } = useTranslation()

    const user = useCurrentUser()

    const handleSignOut = useLogout()

    const profileIcon = (<Navbar.Text><FontAwesomeIcon size="lg" className="text-info rounded-circle" icon={faUser} /></Navbar.Text>)

    return (
        <NavDropdown align={{ lg: 'end' }} title={profileIcon} menuVariant="dark" id="account-menu-dropdown">
            <NavDropdown.Item><Trans
                i18nKey="login.info.text"
                values={{ name: user?.name}}
                components={{ bold: <strong />}}
            /></NavDropdown.Item>
            <NavDropdown.Item onClick={handleSignOut}>{t('logout')}</NavDropdown.Item>
        </NavDropdown>
    );
}

export default AccountMenu;