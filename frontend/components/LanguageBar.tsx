import React from "react";
import {IPost} from "../types/post";
import {Button, Card} from "react-bootstrap";
import {useRouter} from "next/router";
import Link from "next/link";



const LanguageBar: React.FC = () => {
    const currentPath = useRouter().asPath;

    return (
        <section className="mt-3 mb-3">
            <Link href={currentPath} locale="en" className="me-2">
                <Button variant="outline-primary" size="sm">English</Button>
            </Link>
            <Link href={currentPath} locale="tr">
                <Button variant="outline-primary" size="sm">Turkish</Button>
            </Link>
        </section>
    );
}

export default LanguageBar;