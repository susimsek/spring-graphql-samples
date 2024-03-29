import {useTranslation} from "next-i18next";
import {serverSideTranslations} from "next-i18next/serverSideTranslations";
import {Alert, Col, Container, Form, Row, Spinner} from "react-bootstrap";
import React, {useState} from "react";
import {
    OrderType,
    PostOrderField,
    useGetAllPostsQuery,
    useOnPostAddedSubscription
} from "../../generated/graphql-types";
import Post from "../../components/Post";
import {IPost} from "../../types/post";
import Layout from "../../components/Layout";
import NewPostFormDialog from "../../components/NewPostFormDialog";
import Pagination from "../../components/Pagination";

const MyPostPage = () => {

    const [currentPage, setCurrentPage] = useState<number>(1);
    const [size, setSize] = useState<number>(5);

    const { data, error } = useOnPostAddedSubscription();

    const { data: postsData, loading: postDataLoading, error: postsDataError, refetch} = useGetAllPostsQuery({
        variables: {
            page: (currentPage - 1),
            size: size,
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

    const onChangeSizeSelect = (event: React.ChangeEvent<HTMLSelectElement>) => {
        setCurrentPage(1)
        setSize(+event.target.value)
    }

    const onChangePage = (page: number) => {
        setCurrentPage(page)
    }

    return (
        <Layout>
            <Container className="mt-3">
                <NewPostFormDialog/>
                <h3 className="h3">{t('post.new.title')}</h3>
                { data ? data.postAdded && <Post post={data?.postAdded as IPost}/>:
                    <Alert variant='info'>{t('post.new.notFound')}</Alert>}
                <hr/>
                <h3 className="h3">{t('post.title')}</h3>
                {postDataLoading ?
                    <div className="text-center">
                        <Spinner animation="border" variant="secondary"/>
                    </div>:
                    postsData?.posts.content?.length ? <>{postsData.posts.content.map((post: IPost) => (
                        <Post key={post.id} post={post}/>
                    ))}
                        <Row>
                            <Col className="mb-3">
                                <Form.Select name="size"
                                             value={size}
                                             onChange={onChangeSizeSelect}>
                                    <option>5</option>
                                    <option>10</option>
                                    <option>25</option>
                                    <option>50</option>
                                </Form.Select>
                            </Col>
                            <Col xs={12} md={10} className="d-flex justify-content-md-end">
                                <Pagination
                                    itemsCount={postsData?.posts.pageInfo.totalCount || 0}
                                    itemsPerPage={size}
                                    currentPage={currentPage}
                                    onChange={onChangePage}
                                    alwaysShown={true}/>
                            </Col>
                        </Row>
                      </>:<Alert variant="dark" className="text-center">{t('common:no.records.text')}</Alert>
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