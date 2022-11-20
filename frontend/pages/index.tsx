import {OrderType, PostOrderField, useGetAllPostsQuery, useOnPostAddedSubscription} from "../generated/graphql-types";
import {IPost} from "../types/post";
import Post from "../components/Post";
import {useTranslation} from "next-i18next";
import {serverSideTranslations} from "next-i18next/serverSideTranslations";
import LanguageBar from "../components/LanguageBar";
import {Container, Spinner} from "react-bootstrap";
import Header from "../components/Header";
import Footer from "../components/Footer";

const Home = () => {

  const { data, loading, error } = useOnPostAddedSubscription();

    const { data: postsData, loading: postDataLoading, error: postsDataError, refetch} = useGetAllPostsQuery({
        variables: {
        page: 0,
        size: 5,
        orders: {
            field: PostOrderField.CreatedDate,
            order: OrderType.Desc
        }
    }
    })

    const { t, i18n } = useTranslation('home')

  if (error || postsDataError) {
      console.log(error || postsDataError)
  }

    if (data) {
        refetch()
    }

  return (
      <>
          <main>
              <Header/>
              <Container className="mt-3">
                  <h3>Current Locale is {i18n.language}</h3>
                  <LanguageBar/>
                  <h3>{t('new.post.title')}</h3>
                  {loading ?
                      <div className="text-center">
                          <Spinner animation="border" variant="secondary"/>
                      </div> :
                      data?.postAdded && <Post post={data?.postAdded as IPost}/>}
                  <hr/>
                  <h3>{t('post.list.title')}</h3>
                  {postDataLoading ?
                      <div className="text-center">
                          <Spinner animation="border" variant="secondary"/>
                      </div> :
                      postsData?.posts?.map((post: IPost) => (
                          <Post key={post.id} post={post}/>
                      ))}
              </Container>
          </main>
          <Footer />
      </>
  )
}

export const getStaticProps = async ({ locale }: { locale: string }) => ({
    props: {
        ...(await serverSideTranslations(locale, ['home', 'common']))
    }
})

export default Home;

