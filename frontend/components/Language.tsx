import React, {MouseEventHandler} from "react";
import {NavDropdown} from 'react-bootstrap';
import ReactCountryFlag from "react-country-flag"

interface LanguageProps {
    countryCode: string;
    changeLanguage?: MouseEventHandler;
}

const Language: React.FC<LanguageProps> = ({countryCode,changeLanguage}) => {

    return (
        <NavDropdown.Item onClick={changeLanguage}>
            <ReactCountryFlag countryCode={countryCode} style={{
                fontSize: '42px',
                lineHeight: '30px',
            }}></ReactCountryFlag><span className="ms-3"> {countryCode}</span></NavDropdown.Item>
    );
}

export default Language;