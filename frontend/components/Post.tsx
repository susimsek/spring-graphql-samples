import React from "react";
import {IPost} from "../types/post";
import {Card} from "react-bootstrap";
import {useTranslation} from "next-i18next";

interface PostProps {
    post: IPost;
}

const Post: React.FC<PostProps> = ({ post }) => {
    const { t } = useTranslation('home')

    return (
        <Card
            bg='Light'
            className="mb-3"
        >
            <Card.Header>{post.title}</Card.Header>
            <Card.Body>
                {post.locale && <Card.Title>{t('language.label') + ': ' + post.locale}</Card.Title>}
                <Card.Text>
                    {post.content}
                </Card.Text>
            </Card.Body>
        </Card>
    );
}

export default Post;