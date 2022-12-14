import React from "react";
import {NavDropdown, Navbar} from "react-bootstrap";
import {useRouter} from "next/router";
import {useCookies} from "react-cookie";
import Language from "./Language";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faGlobe} from "@fortawesome/free-solid-svg-icons";
import {useTranslation} from "next-i18next";
import {LANGUAGES} from "../constants";



const LanguageBar: React.FC = () => {

    const setCookie = useCookies(['NEXT_LOCALE'])[1]
    const { i18n } = useTranslation()


    const router = useRouter();

    const changeLanguage = (locale: string) => {
        setCookie("NEXT_LOCALE", locale, {path: "/"})
        router.push(router.asPath, undefined, { locale })
    }

    const currentLanguage = LANGUAGES.find((language) => {
        return language.locale === i18n.language
    })

    const languageIcon = (<Navbar.Text><FontAwesomeIcon icon={faGlobe} /> {currentLanguage?.countryCode}</Navbar.Text>)

    return (
        <NavDropdown align={{ lg: 'end' }} title={languageIcon} menuVariant="dark" id="collasible-nav-dropdown">
            {LANGUAGES.map(language => <Language
                key={language.locale}
                changeLanguage={() => changeLanguage(language.locale)}
                countryCode={language.countryCode}
            />)}
        </NavDropdown>
    );
}

export default LanguageBar;