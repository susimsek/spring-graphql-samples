import {serverSideTranslations} from "next-i18next/serverSideTranslations";
import {Container} from "react-bootstrap";
import Layout from "../components/Layout";
import React from "react";
import Image from 'next/image';
import welcomePic from '../public/assets/introduction.jpeg'
import {useTranslation} from "next-i18next";

const Home = () => {

    const { t } = useTranslation('home')

  return (
      <Layout>
          <Container className="mt-3">
              <h2 className="h2">{t('home.title')}</h2>
              <Image src={welcomePic}
                     className='img-thumbnail'
                     alt="introduction"/>
          </Container>
      </Layout>
  )
}

export const getStaticProps = async ({ locale }: { locale: string }) => ({
    props: {
        ...(await serverSideTranslations(locale, ['home', 'common']))
    }
})

export default Home;

