import React, {useState} from "react";
import {Alert, Col, Container, Row, Spinner} from "react-bootstrap";
import Layout from "../../../components/Layout";
import {Trans, useTranslation} from "next-i18next";
import {serverSideTranslations} from "next-i18next/serverSideTranslations";
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import {SubmitHandler, useForm} from "react-hook-form";
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from "yup";
import {useForgotPasswordMutation} from "../../../generated/graphql-types";
import {useGoogleReCaptcha} from "react-google-recaptcha-v3";

type PasswordResetFormData = {
    email: string;
};

const ForgotPasswordPage = () => {
    const { t } = useTranslation('reset')

    const [visibleSuccessMessage, setVisibleSuccessMessage] = useState<boolean>(false)

    const schema = yup.object({
        email: yup.string().required(t("common:validation.required"))
            .email(t("common:validation.emailInvalid"))
            .min(5, t("common:validation.minlength")).max(254, t("common:validation.maxlength"))
    }).required();

    const { executeRecaptcha } = useGoogleReCaptcha()

    const [forgotPassword, { loading, error }, ] = useForgotPasswordMutation({
        errorPolicy: "all"
    });

    const { register, handleSubmit, formState: { errors } } = useForm<PasswordResetFormData>({
        resolver: yupResolver(schema)
    });
    // Creating a state to store the uploaded video

    const handleResetPassword: SubmitHandler<PasswordResetFormData> = async (
        {
            email
        }) => {

        setVisibleSuccessMessage(false)

        if (!executeRecaptcha) {
            console.log('Execute recaptcha not yet available');
            return;
        }

        const token = await executeRecaptcha('reset_password');

        const result = await forgotPassword({
            variables: {
                email
            },
            context: {
                headers: {
                    "recaptcha": token
                }
            }
        });

        if (result.data?.forgotPassword) {
            setVisibleSuccessMessage(true)
        }
    };

    return (
        <Layout>
            <Container className="mt-3">
                <Row className="d-flex justify-content-center">
                    <Col md={10} xl={6} lg={8}>
                        <Card style={{ borderRadius: "15px" }}>
                            <Card.Header className="text-center">{t('reset.request.title')}</Card.Header>
                            <Card.Body>
                                <Alert variant="warning">
                                    <p>
                                        {t('reset.request.messages.info')}
                                    </p>
                                </Alert>
                                <Form onSubmit={handleSubmit(handleResetPassword)}>
                                    <Form.Group className="mb-3">
                                        <Form.Label>{t('common:form.email.label')}</Form.Label>
                                        <Form.Control
                                            type="email"
                                            {...register('email')}
                                            isInvalid={!!errors.email}
                                            disabled={loading}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.email?.message}
                                        </Form.Control.Feedback>
                                    </Form.Group>

                                    <Button className="mb-3" variant="primary" type="submit" disabled={loading}>
                                        {loading && <Spinner
                                            as="span"
                                            animation="border"
                                            size="sm"
                                            role="status"
                                            aria-hidden="true"
                                        />} {t('reset.request.form.button')}
                                    </Button>
                                    <Alert show={visibleSuccessMessage} variant="success">
                                        {t('reset.request.messages.success')}
                                    </Alert>
                                    {error && error.graphQLErrors.map(({ message }, i) => (
                                        <Alert key={i} variant="danger">
                                            {message}
                                        </Alert>
                                    ))}
                                </Form>
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            </Container>
        </Layout>
    );
}

export const getStaticProps = async ({ locale }: { locale: string }) => ({
    props: {
        ...(await serverSideTranslations(locale, ['reset', 'common']))
    }
})

export default ForgotPasswordPage;
