import React, {useState} from "react";
import {Alert, Col, Container, Row, Spinner} from "react-bootstrap";
import Layout from "../../components/Layout";
import {Trans, useTranslation} from "next-i18next";
import {serverSideTranslations} from "next-i18next/serverSideTranslations";
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import {SubmitHandler, useForm} from "react-hook-form";
import {useChangePasswordMutation} from "../../generated/graphql-types";
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from "yup";
import PasswordStrengthBar from "../../components/PasswordStrengthBar";

type PasswordFormData = {
    currentPassword: string;
    newPassword: string;
    confirmpassword: string};

const PasswordPage = () => {
    const { t } = useTranslation('password')

    const [password, setPassword] = useState('')

    const [visibleSuccessMessage, setVisibleSuccessMessage] = useState<boolean>(false)
    const [visibleErrorMessage, setVisibleErrorMessage] = useState<boolean>(false)

    const schema = yup.object({
        currentPassword: yup.string().required(t("common:validation.required")),
        newPassword: yup.string().required(t("common:validation.required"))
            .min(4, t("common:validation.minlength")).max(100, t("common:validation.maxlength")),
        confirmpassword: yup.string().required(t("common:validation.required"))
            .min(4, t("common:validation.minlength")).max(100, t("common:validation.maxlength"))
            .oneOf([yup.ref("newPassword")], t("common:validation.passwordNotMatch"))
    }).required();

    const [changePassword, { loading, error }, ] = useChangePasswordMutation({
        errorPolicy: "all"
    });

    const { register, handleSubmit, formState: { errors } } = useForm<PasswordFormData>({
        resolver: yupResolver(schema)
    });
    // Creating a state to store the uploaded video

    const updatePassword = (event: React.ChangeEvent<HTMLInputElement>) => setPassword(event.target.value)

    const handleChangePassword: SubmitHandler<PasswordFormData> = async (
        {
            currentPassword,
            newPassword
        }) => {

        setVisibleSuccessMessage(false)
        setVisibleErrorMessage(false)

        const result = await changePassword({
            variables: {
                input: {
                    currentPassword,
                    newPassword
                },
            }
        });

        if (result.data?.changePassword) {
            setVisibleSuccessMessage(true)
        } else if (!error) {
            setVisibleErrorMessage(true)
        }
    };

    return (
        <Layout>
            <Container className="mt-3">
                <Row className="d-flex justify-content-center">
                    <Col md={10} xl={6} lg={8}>
                        <Card style={{ borderRadius: "15px" }}>
                            <Card.Header className="text-center">{t('password.title')}</Card.Header>
                            <Card.Body>
                                <Form onSubmit={handleSubmit(handleChangePassword)}>
                                    <Form.Group className="mb-3">
                                        <Form.Label>{t('common:form.currentpassword.label')}</Form.Label>
                                        <Form.Control
                                            type="password"
                                            {...register('currentPassword')}
                                            isInvalid={!!errors.currentPassword}
                                            disabled={loading}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.currentPassword?.message}
                                        </Form.Control.Feedback>
                                    </Form.Group>
                                    <Form.Group className="mb-3">
                                        <Form.Label>{t('common:form.newpassword.label')}</Form.Label>
                                        <Form.Control
                                            type="password"
                                            {...register('newPassword')}
                                            onChange={updatePassword}
                                            isInvalid={!!errors.newPassword}
                                            disabled={loading}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.newPassword?.message}
                                        </Form.Control.Feedback>
                                        <PasswordStrengthBar password={password} />
                                    </Form.Group>
                                    <Form.Group className="mb-3">
                                        <Form.Label>{t('common:form.confirmpassword.label')}</Form.Label>
                                        <Form.Control
                                            type="password"
                                            {...register('confirmpassword')}
                                            isInvalid={!!errors.confirmpassword}
                                            disabled={loading}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {errors.confirmpassword?.message}
                                        </Form.Control.Feedback>
                                    </Form.Group>
                                    <Button className="mb-3" variant="primary" type="submit" disabled={loading}>
                                        {loading && <Spinner
                                            as="span"
                                            animation="border"
                                            size="sm"
                                            role="status"
                                            aria-hidden="true"
                                        />} {t('password.form.button')}
                                    </Button>
                                    <Alert show={visibleSuccessMessage} variant="success">
                                        <Trans
                                            i18nKey="password:password.messages.success"
                                            components={{ bold: <strong />}}
                                        />
                                    </Alert>
                                    <Alert show={visibleErrorMessage} variant="danger">
                                        <Trans
                                            i18nKey="password:password.messages.error"
                                            components={{ bold: <strong />}}
                                        />
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
        ...(await serverSideTranslations(locale, ['password', 'common']))
    }
})

export default PasswordPage;
