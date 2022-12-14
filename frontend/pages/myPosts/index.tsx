import {useTranslation} from "next-i18next";
import {serverSideTranslations} from "next-i18next/serverSideTranslations";
import {Alert, Container, Spinner} from "react-bootstrap";
import React from "react";
import {useAuthToken} from "../../contexts/AuthTokenProvider";
import {
    OrderType,
    PostOrderField,
    useGetAllPostsQuery,
    useOnPostAddedSubscription
} from "../../generated/graphql-types";
import Post from "../../components/Post";
import {IPost} from "../../types/post";
import Layout from "../../components/Layout";

const MyPostPage = () => {

    const { data, loading, error } = useOnPostAddedSubscription();

    const [token] = useAuthToken();

    const { data: postsData, loading: postDataLoading, error: postsDataError, refetch} = useGetAllPostsQuery({
        variables: {
            page: 0,
            size: 5,
            orders: {
                field: PostOrderField.CreatedAt,
                order: OrderType.Desc
            }
        }
    })

    const { t } = useTranslation('home')

    if (error || postsDataError) {
        console.log(error || postsDataError)
    }

    if (data) {
        refetch()
    }

    return (
        <Layout>
            <Container className="mt-3">
                <h3>{t('new.post.title')}</h3>
                { data ? data.postAdded && <Post post={data?.postAdded as IPost}/>:
                    <Alert variant='info'>{t('new.post.not.found')}</Alert>}
                <hr/>
                <h3>{t('post.list.title')}</h3>
                {postDataLoading ?
                    <div className="text-center">
                        <Spinner animation="border" variant="secondary"/>
                    </div> :
                    postsData?.posts?.length ? postsData.posts.map((post: IPost) => (
                        <Post key={post.id} post={post}/>
                    )):
                        <Alert variant="dark" className="text-center">{t('common:no.records.text')}</Alert>
                }
            </Container>
        </Layout>
    )
}

export const getStaticProps = async ({ locale }: { locale: string }) => ({
    props: {
        ...(await serverSideTranslations(locale, ['home', 'common']))
    }
})

export default MyPostPage;