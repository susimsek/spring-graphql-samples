import React from "react";
import {Button} from "react-bootstrap";
import {useRouter} from "next/router";
import {useCookies} from "react-cookie";


const LanguageBar: React.FC = () => {

    const setCookie = useCookies(['NEXT_LOCALE'])[1]


    const router = useRouter();

    const changeLanguage = (locale: string) => (event: any) => {
        setCookie("NEXT_LOCALE", locale, {path: "/"})
        router.push(router.asPath, undefined, { locale })
    }

    return (
        <section className="mt-3 mb-3">
            <Button variant="outline-primary" size="sm" className="me-2" onClick={changeLanguage('en')}>English</Button>
            <Button variant="outline-primary" size="sm" onClick={changeLanguage('tr')}>Turkish</Button>
        </section>
    );
}

export default LanguageBar;