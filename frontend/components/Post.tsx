import React from "react";
import {IPost} from "../types/post";
import {Card} from "react-bootstrap";

interface PostProps {
    post: IPost;
}

const Post: React.FC<PostProps> = ({ post }) => {
    return (
        <Card
            bg='Light'
            className="mb-3"
        >
            <Card.Header>{post.title}</Card.Header>
            <Card.Body>
                <Card.Text>
                    {post.content}
                </Card.Text>
            </Card.Body>
        </Card>
    );
}

export default Post;