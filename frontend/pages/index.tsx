import styles from '../styles/Home.module.css'
import {OrderType, PostOrderField, useGetAllPostsQuery, useOnPostAddedSubscription} from "../generated/graphql-types";
import {IPost} from "../types/post";
import Post from "../components/Post";

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

  if (error || postsDataError) {
      console.log(error || postsDataError)
  }

  return (
    <div className={styles.container}>
      <h1>New Posts</h1>
        {data?.postAdded && <Post post={data?.postAdded as IPost}/> }
       <hr/>
       <h1>All Posts</h1>
        {postsData?.posts?.map((post: IPost) => (
        <Post key={post.id} post={post}/>
        ))}
    </div>
  )
}

export default Home;

