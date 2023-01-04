import React, {useEffect} from "react";
import {Alert, Container, Spinner} from "react-bootstrap";
import Layout from "../../components/Layout";
import {useTranslation} from "next-i18next";
import {serverSideTranslations} from "next-i18next/serverSideTranslations";
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import {SubmitHandler, useForm} from "react-hook-form";
import {useCreateUserMutation, useLoginMutation} from "../../generated/graphql-types";
import {useRouter} from "next/router";
import {useAuth} from "../../contexts/AuthProvider";
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from "yup";
import {useGoogleReCaptcha} from "react-google-recaptcha-v3";

type SignupFormData = {
    username: string;
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    confirmpassword: string};

const SignupPage = () => {
    const { t } = useTranslation('register')

    const { i18n } = useTranslation()

    const schema = yup.object({
        username: yup.string().required(t("common:validation.required"))
            .min(4, t("common:validation.minlength")).max(50, t("common:validation.maxlength")),
        firstName: yup.string().required(t("common:validation.required"))
            .min(1, t("common:validation.minlength")).max(50, t("common:validation.maxlength")),
        lastName: yup.string().required(t("common:validation.required"))
            .min(1, t("common:validation.minlength")).max(50, t("common:validation.maxlength")),
        email: yup.string().required(t("common:validation.required"))
            .min(5, t("common:validation.minlength")).max(254, t("common:validation.maxlength")),
        password: yup.string().required(t("common:validation.required"))
            .min(4, t("common:validation.minlength")).max(100, t("common:validation.maxlength")),
        confirmpassword: yup.string().required(t("common:validation.required"))
            .min(4, t("common:validation.minlength")).max(100, t("common:validation.maxlength"))
    }).required();

    const router = useRouter();

    const { executeRecaptcha } = useGoogleReCaptcha()

    const [createUser, { loading, error }, ] = useCreateUserMutation({
        errorPolicy: "all"
    });

    const { register, handleSubmit, formState: { errors } } = useForm<SignupFormData>({
        resolver: yupResolver(schema)
    });
    // Creating a state to store the uploaded video

    const handleSignup: SubmitHandler<SignupFormData> = async (
        {
            username,
            firstName,
            lastName,
            email,
            password
        }) => {

        if (!executeRecaptcha) {
            console.log('Execute recaptcha not yet available');
            return;
        }

        const token = await executeRecaptcha('signup');

        const result = await createUser({
            variables: {
                input: {
                    username,
                    firstName,
                    lastName,
                    email,
                    password,
                    lang: i18n.language
                },
            },
            context: {
                headers: {
                    "recaptcha": token
                }
            }
        });

        if (result.data) {
            await router.push('/')
        }
    };

    return (
        <Layout>
            <Container className="mt-3">
                <Card className="col-6 offset-3">
                    <Card.Header className="text-center">{t('register.title')}</Card.Header>
                    <Card.Body>
                        <Form onSubmit={handleSubmit(handleSignup)}>
                            <Form.Group className="mb-3">
                                <Form.Label>{t('common:form.username.label')}</Form.Label>
                                <Form.Control
                                    type="text"
                                    {...register('username')}
                                    isInvalid={!!errors.username}
                                    disabled={loading}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {errors.username?.message}
                                </Form.Control.Feedback>
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>{t('common:form.firstName.label')}</Form.Label>
                                <Form.Control
                                    type="text"
                                    {...register('firstName')}
                                    isInvalid={!!errors.firstName}
                                    disabled={loading}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {errors.firstName?.message}
                                </Form.Control.Feedback>
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>{t('common:form.lastName.label')}</Form.Label>
                                <Form.Control
                                    type="text"
                                    {...register('lastName')}
                                    isInvalid={!!errors.lastName}
                                    disabled={loading}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {errors.lastName?.message}
                                </Form.Control.Feedback>
                            </Form.Group>
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
                            <Form.Group className="mb-3">
                                <Form.Label>{t('common:form.newpassword.label')}</Form.Label>
                                <Form.Control
                                    type="password"
                                    {...register('password')}
                                    isInvalid={!!errors.password}
                                    disabled={loading}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {errors.password?.message}
                                </Form.Control.Feedback>
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
                                />} {t('register.form.button')}
                            </Button>
                            {error && error.graphQLErrors.map(({ message }, i) => (
                                <Alert key={i} variant="danger">
                                    {message}
                                </Alert>
                            ))}
                        </Form>
                    </Card.Body>
                </Card>
            </Container>
        </Layout>
    );
}

export const getStaticProps = async ({ locale }: { locale: string }) => ({
    props: {
        ...(await serverSideTranslations(locale, ['register', 'common']))
    }
})

export default SignupPage;
