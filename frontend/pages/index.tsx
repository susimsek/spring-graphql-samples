import {OrderType, PostOrderField, useGetAllPostsQuery, useOnPostAddedSubscription} from "../generated/graphql-types";
import {IPost} from "../types/post";
import Post from "../components/Post";
import {useTranslation} from "next-i18next";
import {serverSideTranslations} from "next-i18next/serverSideTranslations";
import {Alert, Container, Spinner} from "react-bootstrap";
import {useAuthToken} from "../contexts/AuthTokenProvider";
import Layout from "../components/Layout";
import React from "react";

const Home = () => {

  return (
      <Layout>
          <Container className="mt-3">
              <h3>test</h3>
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

