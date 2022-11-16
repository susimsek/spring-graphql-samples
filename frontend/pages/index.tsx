import styles from '../styles/Home.module.css'
import {OrderType, PostOrderField, useGetAllPostsQuery, useOnPostAddedSubscription} from "../generated/graphql-types";
import {IPost} from "../types/post";
import Post from "../components/Post";
import {useTranslation} from "next-i18next";
import {serverSideTranslations} from "next-i18next/serverSideTranslations";
import LanguageBar from "../components/LanguageBar";

const Home = () => {

  const { data, loading, error } = useOnPostAddedSubscription();

    const { data: postsData, loading: postDataLoading, error: postsDataError } = useGetAllPostsQuery({
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

  return (
    <div className={styles.container}>
      <h3>Current Locale is {i18n.language}</h3>
        <LanguageBar/>
      <h3>{t('new.post.title')}</h3>
        {data?.postAdded && <Post post={data?.postAdded as IPost}/> }
       <hr/>
       <h3>{t('post.list.title')}</h3>
        {postsData?.posts?.map((post: IPost) => (
        <Post key={post.id} post={post}/>
        ))}
    </div>
  )
}

export const getStaticProps = async ({ locale }: { locale: string }) => ({
    props: {
        ...(await serverSideTranslations(locale, ['home']))
    }
})

export default Home;

