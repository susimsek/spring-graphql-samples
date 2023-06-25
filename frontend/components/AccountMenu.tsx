import React from "react";
import {NavDropdown, Navbar} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faLock, faRightFromBracket, faUser} from "@fortawesome/free-solid-svg-icons";
import {Trans, useTranslation} from "next-i18next";
import {useLogout} from "../hooks/use-logout";
import {useCurrentUser} from "../hooks/use-current-user";
import {useRouter} from "next/router";
import Link from "next/link";


const AccountMenu: React.FC = () => {

    const { t } = useTranslation()

    const user = useCurrentUser()

    const {logout} = useLogout()

    const router = useRouter()

    const handleLogout = async () => {

        await logout()

        await router.push('/')
    };

    const profileIcon = (<Navbar.Text><FontAwesomeIcon size="lg" className="text-white rounded-circle" icon={faUser} /></Navbar.Text>)

    return (
        <NavDropdown align={{ lg: 'end' }} title={profileIcon} menuVariant="dark" id="account-menu-dropdown">
            <NavDropdown.Item><Trans
                i18nKey="login.info.text"
                values={{ name: user?.name}}
                components={{ bold: <strong />}}
            /></NavDropdown.Item>
            <NavDropdown.Item as={Link} href="/password"><FontAwesomeIcon icon={faLock}/>{' '}{t("account.password")}</NavDropdown.Item>
            <NavDropdown.Item onClick={handleLogout}><FontAwesomeIcon icon={faRightFromBracket}/>{' '}{t('account.logout')}</NavDropdown.Item>
        </NavDropdown>
    );
}

export default AccountMenu;