import React, {useState} from "react";
import {Alert, Container, Spinner} from "react-bootstrap";
import Layout from "../../components/Layout";
import {Trans, useTranslation} from "next-i18next";
import {serverSideTranslations} from "next-i18next/serverSideTranslations";
import {useActivateAccountMutation} from "../../generated/graphql-types";
import {useRouter} from "next/router";
import Link from "next/link";


const successAlert = (
    <Alert variant="success">
        <Trans
            i18nKey="activate:activate.messages.success"
            components={{ bold: <strong />}}
        />
        <Alert.Link as={Link} href="/login" className="alert-link">
            <Trans
                i18nKey="common:messages.info.authenticated.link"
                components={{ bold: <strong />}}
            />
        </Alert.Link>
    </Alert>
);

const failureAlert = (
    <Alert variant="danger">
        <Trans
            i18nKey="activate:activate.messages.error"
            components={{ bold: <strong />}}
        />
    </Alert>
);

const ActivatePage = () => {
    const { t } = useTranslation('activate')

    const router = useRouter();

    const token = (router.query?.token as string);

    const [activateAccount, { loading, error }, ] = useActivateAccountMutation({
        errorPolicy: "all"
    });

    const [activationFailure, setActivationFailure] = useState<boolean>(true);

    React.useEffect(() => {
        if (token) {
            activateAccount({
                variables: {
                    token
                }
            }).then(response => setActivationFailure(!response.data?.activateAccount))
        } else {
            setActivationFailure(true)
        }
    }, [token, activateAccount]);

    if (loading){
        return <Layout>
            <div className="text-center mt-3">
                <Spinner animation="border" variant="secondary"/>
            </div>
        </Layout>
    }

    return (
        <Layout>
            <Container className="mt-3">
                <h1 className="h1">{t('activate.title')}</h1>
                {activationFailure || error ? failureAlert : successAlert}
            </Container>
        </Layout>
    );
}

export const getStaticProps = async ({ locale }: { locale: string }) => ({
    props: {
        ...(await serverSideTranslations(locale, ['activate', 'common']))
    }
})

export default ActivatePage;
