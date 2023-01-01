import React, {useState} from "react";
import {Alert, Card, Modal, Spinner} from "react-bootstrap";
import {useTranslation} from "next-i18next";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusSquare, faSave} from "@fortawesome/free-solid-svg-icons";
import Form from "react-bootstrap/Form";
import {useCreatePostMutation, useLoginMutation} from "../generated/graphql-types";
import {SubmitHandler, useForm} from "react-hook-form";
import * as yup from "yup";
import {yupResolver} from "@hookform/resolvers/yup";


type NewPostFormData = { title: string; content: string };
const NewPostFormDialog: React.FC = () => {
    const { t } = useTranslation('post')

    const [show, setShow] = useState<boolean>(false);

    const schema = yup.object({
        title: yup.string().required(t("common:validation.required"))
            .min(5, t("common:validation.minlength")).max(100, t("maxlength")),
        content: yup.string().required(t("common:validation.required"))
            .min(5, t("common:validation.minlength")).max(1000, t("maxlength"))
    }).required();

    const [createPost, { loading, error }, ] = useCreatePostMutation({
        errorPolicy: "all"
    });

    const { register, handleSubmit, formState: { errors } } = useForm<NewPostFormData>({
        resolver: yupResolver(schema)
    });

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    const handleLogin: SubmitHandler<NewPostFormData> = async ({title, content}) => {

        const result = await createPost({
            variables: {
                input: {
                   title,
                    content
                },
            }
        });

        if (result.data) {
            handleClose()
        }
    };

    return (
        <>
            <Button variant="primary"
                    className="mb-3"
                    onClick={handleShow}>
                <FontAwesomeIcon icon={faPlusSquare} /> {t('post.createLabel')}
            </Button>
            <Modal show={show}
                   onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>{t('post.createTitle')}</Modal.Title>
                </Modal.Header>
                <Form onSubmit={handleSubmit(handleLogin)}>
                <Modal.Body>
                    <Card>
                        <Card.Body>
                                <Form.Group className="mb-3">
                                    <Form.Label>{t('post.form.title')}</Form.Label>
                                    <Form.Control
                                        type="text"
                                        {...register('title')}
                                        isInvalid={!!errors.title}
                                        disabled={loading}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {errors.title?.message}
                                    </Form.Control.Feedback>
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>{t('post.form.content')}</Form.Label>
                                    <Form.Control
                                        type="text"
                                        as="textarea"
                                        rows={3}
                                        {...register('content')}
                                        isInvalid={!!errors.content}
                                        disabled={loading}
                                    />
                                    <Form.Control.Feedback type="invalid">
                                        {errors.content?.message}
                                    </Form.Control.Feedback>
                                </Form.Group>
                        </Card.Body>
                    </Card>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleClose}>
                        {t('common:entity.action.cancel')}
                    </Button>
                    <Button variant="primary" type="submit" disabled={loading}>
                        {loading && <Spinner
                            as="span"
                            animation="border"
                            size="sm"
                            role="status"
                            aria-hidden="true"
                        />} <FontAwesomeIcon icon={faSave} /> {t('common:entity.action.save')}
                    </Button>
                    {error && error.graphQLErrors.map(({ message }, i) => (
                        <Alert key={i} variant="danger">
                            {message}
                        </Alert>
                    ))}
                </Modal.Footer>
                </Form>
            </Modal>
        </>
    );
}

export default NewPostFormDialog;