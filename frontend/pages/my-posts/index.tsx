import {useTranslation} from "next-i18next";
import {serverSideTranslations} from "next-i18next/serverSideTranslations";
import {Alert, Container, Spinner} from "react-bootstrap";
import React from "react";
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

    const { data, error } = useOnPostAddedSubscription();

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

    const { t } = useTranslation('post')

    if (error || postsDataError) {
        console.log(error || postsDataError)
    }

    if (data) {
        refetch()
    }

    return (
        <Layout>
            <Container className="mt-3">
                <h3 className="h3">{t('post.new.title')}</h3>
                { data ? data.postAdded && <Post post={data?.postAdded as IPost}/>:
                    <Alert variant='info'>{t('post.new.notFound')}</Alert>}
                <hr/>
                <h3 className="h3">{t('post.title')}</h3>
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
        ...(await serverSideTranslations(locale, ['post', 'common']))
    }
})

export default MyPostPage;