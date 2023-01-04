import React, {useState} from "react";
import {Alert, Card, Col, Container, Modal, Row, Spinner} from "react-bootstrap";
import {useTranslation} from "next-i18next";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlusSquare, faSave} from "@fortawesome/free-solid-svg-icons";
import Form from "react-bootstrap/Form";
import {useCreatePostMutation} from "../generated/graphql-types";
import {Controller, SubmitHandler, useForm} from "react-hook-form";
import * as yup from "yup";
import {yupResolver} from "@hookform/resolvers/yup";
import QuillNoSSRWrapper, {formats, modules} from "./QuillNoSSRWrapper";
import dynamic from "next/dynamic";
import {ref} from "yup";

type NewPostFormData = { title: string; content: string };
const NewPostFormDialog: React.FC = () => {
    const { t } = useTranslation('post')

    const [show, setShow] = useState<boolean>(false);

    const [visibleCreatedAlert, setVisibleCreatedAlert] = useState<boolean>(false);

    const schema = yup.object({
        title: yup.string().required(t("common:validation.required"))
            .min(3, t("common:validation.minlength")).max(40, t("common:validation.maxlength")),
        content: yup.string().required(t("common:validation.required"))
            .min(12, t("post:post.validation.contentMinlength")).max(1000 , t("common:validation.maxlength"))
    }).required();

    const [createPost, { loading, error }, ] = useCreatePostMutation({
        errorPolicy: "all"
    });

    const { register, handleSubmit, control, formState: { errors } } = useForm<NewPostFormData>({
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
            setVisibleCreatedAlert(true)
            setTimeout(() => {
                setVisibleCreatedAlert(false)
                handleClose()
            }, 2000);
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
                   size="lg"
                   onHide={handleClose}>
                <Form onSubmit={handleSubmit(handleLogin)}>
                <Modal.Header closeButton>
                    <Modal.Title>{t('post.createTitle')}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Container>
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
                            <Controller
                                control={control}
                                name="content"
                                render={({ field: { onChange, value}}) => (
                                    <QuillNoSSRWrapper
                                        theme="snow"
                                        modules={modules}
                                        formats={formats}
                                        onChange={onChange}
                                        value={value}
                                    />
                                )}
                            />
                            <p className="error">
                                {errors.content?.message}
                            </p>
                        </Card.Body>
                    </Card>
                </Container>
                </Modal.Body>
                <Modal.Footer>
                    <Container>
                        <Row>
                            <Col>
                                <Alert show={visibleCreatedAlert} variant="success">
                                    {t('post.created')}
                                </Alert>
                            </Col>
                        </Row>
                        {error && error.graphQLErrors.map(({ message }, i) => (
                            <Row key={i}>
                                <Col>
                                    <Alert variant="danger">
                                        {message}
                                    </Alert>
                                </Col>
                            </Row>
                        ))}
                        <Row>
                            <Col>
                                <Button variant="secondary" className="me-2" onClick={handleClose} disabled={loading}>
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
                            </Col>
                        </Row>
                    </Container>
                </Modal.Footer>
                </Form>
            </Modal>
        </>
    );
}

export default NewPostFormDialog;