import styles from '../styles/Home.module.css'
import {useOnPostAddedSubscription} from "../generated/graphql-types";

const Home = () => {

  const { data, loading, error } = useOnPostAddedSubscription();

  if (error) {
      console.log(error)
  }

  return (
    <div className={styles.container}>
      <h1>New posts</h1>
      <p>{!loading && data?.postAdded.title}</p>
    </div>
  )
}

export default Home;

