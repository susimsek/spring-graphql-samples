import React from "react";
import {IPost} from "../types/post";
import {Card} from "react-bootstrap";
import {useTranslation} from "next-i18next";
import QuillNoSSRWrapper, {modules} from "./QuillNoSSRWrapper";

interface PostProps {
    post: IPost;
}

const Post: React.FC<PostProps> = ({ post }) => {
    const { t } = useTranslation('post')

    return (
        <Card
            bg='Light'
            className="mb-3"
        >
            <Card.Header>{post.title}</Card.Header>
            <Card.Body>
                {post.locale && <Card.Title>{t('common:language.label') + ': ' + post.locale}</Card.Title>}
                <QuillNoSSRWrapper
                    theme="bubble"
                    value={post.content}
                    readOnly={true}/>
            </Card.Body>
        </Card>
    );
}

export default Post;