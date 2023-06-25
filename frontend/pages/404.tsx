import {serverSideTranslations} from "next-i18next/serverSideTranslations";
import {Alert, Container} from "react-bootstrap";
import Layout from "../components/Layout";
import React from "react";
import {useTranslation} from "next-i18next";

const NotFoundPage = () => {

    const { t } = useTranslation('error')

  return (
      <Layout>
          <Container className="mt-3">
              <h3 className="h3">{t('error.http.404.title')}</h3>
              <Alert className="text-center" variant='dark'>{t('error.http.404.detail')}</Alert>
          </Container>
      </Layout>
  )
}

export const getStaticProps = async ({ locale }: { locale: string }) => ({
    props: {
        ...(await serverSideTranslations(locale, ['error', 'common']))
    }
})

export default NotFoundPage;

