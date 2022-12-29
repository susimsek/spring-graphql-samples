import React, {useEffect} from "react";
import {Alert, Container, Spinner} from "react-bootstrap";
import Layout from "../../components/Layout";
import {useTranslation} from "next-i18next";
import {serverSideTranslations} from "next-i18next/serverSideTranslations";
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Card from 'react-bootstrap/Card';
import {SubmitHandler, useForm} from "react-hook-form";
import {useLoginMutation} from "../../generated/graphql-types";
import {useRouter} from "next/router";
import {useAuth} from "../../contexts/AuthProvider";
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from "yup";
import {useGoogleReCaptcha} from "react-google-recaptcha-v3";

type LoginFormData = { username: string; password: string };

const LoginPage = () => {
    const { t } = useTranslation('login')

    const schema = yup.object({
        username: yup.string().required(t("common:validation.required"))
            .min(4, t("common:validation.minlength")).max(50, t("maxlength")),
        password: yup.string().required(t("common:validation.required"))
            .min(4, t("common:validation.minlength")).max(100, t("maxlength"))
    }).required();

    const [, updateIsLoggedIn] = useAuth();

    const router = useRouter();

    const { executeRecaptcha } = useGoogleReCaptcha()

    const redirectUrl = (router.query?.redirectUrl as string) ?? "/";

    const [login, { loading, error }, ] = useLoginMutation({
        errorPolicy: "all"
    });



    const { register, handleSubmit, formState: { errors } } = useForm<LoginFormData>({
        resolver: yupResolver(schema)
    });
    // Creating a state to store the uploaded video

    const handleLogin: SubmitHandler<LoginFormData> = async ({username, password}) => {

        if (!executeRecaptcha) {
            console.log('Execute recaptcha not yet available');
            return;
        }

        const token = await executeRecaptcha('login');


        const result = await login({
            variables: {
                input: {
                    login: username,
                    password
                },
            },
            context: {
                headers: {
                    "recaptcha": token
                }
            }
        });

        if (result.data) {
            updateIsLoggedIn(true)
            await router.push(redirectUrl)
        }
    };

    return (
        <Layout>
            <Container className="mt-3">
                <Card className="col-6 offset-3">
                    <Card.Header className="text-center">{t('login.title')}</Card.Header>
                    <Card.Body>
                        <Form onSubmit={handleSubmit(handleLogin)}>
                            <Form.Group className="mb-3">
                                <Form.Label>{t('login.form.username')}</Form.Label>
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
                                <Form.Label>{t('login.form.password')}</Form.Label>
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
                            <Button className="mb-3" variant="primary" type="submit" disabled={loading}>
                                {loading && <Spinner
                                    as="span"
                                    animation="border"
                                    size="sm"
                                    role="status"
                                    aria-hidden="true"
                                />} {t('login.form.button')}
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
        ...(await serverSideTranslations(locale, ['login', 'common']))
    }
})

export default LoginPage;
