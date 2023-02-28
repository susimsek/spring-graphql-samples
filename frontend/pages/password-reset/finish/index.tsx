import React, {useState} from "react";
import {Alert, Col, Container, Row, Spinner} from "react-bootstrap";
import {Trans, useTranslation} from "next-i18next";
import {serverSideTranslations} from "next-i18next/serverSideTranslations";
import {useRouter} from "next/router";
import {useResetPasswordMutation} from "../../../generated/graphql-types";
import Card from "react-bootstrap/Card";
import Form from "react-bootstrap/Form";
import PasswordStrengthBar from "../../../components/PasswordStrengthBar";
import Button from "react-bootstrap/Button";
import * as yup from "yup";
import {SubmitHandler, useForm} from "react-hook-form";
import {yupResolver} from "@hookform/resolvers/yup";
import Layout from "../../../components/Layout";
type ResetPasswordFormData = {
    newPassword: string;
    confirmpassword: string};

const ResetPasswordPage = () => {
    const { t } = useTranslation('reset')

    const [password, setPassword] = useState('')

    const router = useRouter();

    const token = (router.query?.token as string);

    const [visibleSuccessMessage, setVisibleSuccessMessage] = useState<boolean>(false)
    const [visibleErrorMessage, setVisibleErrorMessage] = useState<boolean>(false)

    const schema = yup.object({
        newPassword: yup.string().required(t("common:validation.required"))
            .min(4, t("common:validation.minlength")).max(100, t("common:validation.maxlength")),
        confirmpassword: yup.string().required(t("common:validation.required"))
            .min(4, t("common:validation.minlength")).max(100, t("common:validation.maxlength"))
            .oneOf([yup.ref("newPassword")], t("common:validation.passwordNotMatch"))
    }).required();

    const { register, handleSubmit, formState: { errors } } = useForm<ResetPasswordFormData>({
        resolver: yupResolver(schema)
    });

    const [resetPassword, { loading, error }, ] = useResetPasswordMutation({
        errorPolicy: "all"
    });

    const updatePassword = (event: React.ChangeEvent<HTMLInputElement>) => setPassword(event.target.value)

    const handleResetPassword: SubmitHandler<ResetPasswordFormData> = async (
        {
            newPassword
        }) => {

        setVisibleSuccessMessage(false)
        setVisibleErrorMessage(false)

        const result = await resetPassword({
            variables: {
                input: {
                    token,
                    newPassword
                },
            }
        });

        if (result.data?.resetPassword) {
            setVisibleSuccessMessage(true)
        } else if (!error) {
            setVisibleErrorMessage(true)
        }
    };

    const resetForm = (
        <Form onSubmit={handleSubmit(handleResetPassword)}>
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
                />} {t('reset.finish.form.button')}
            </Button>
            <Alert show={visibleSuccessMessage} variant="success">
                <Trans
                    i18nKey="reset:reset.finish.messages.success"
                    components={{ bold: <strong />}}
                />
            </Alert>
            <Alert show={visibleErrorMessage} variant="danger">
                {t('reset.finish.messages.error')}
            </Alert>
            {error && error.graphQLErrors.map(({ message }, i) => (
                <Alert key={i} variant="danger">
                    {message}
                </Alert>
            ))}
        </Form>
    )

    return (
        <Layout>
            <Container className="mt-3">
                <Row className="d-flex justify-content-center">
                    <Col md={10} xl={6} lg={8}>
                        <Card style={{ borderRadius: "15px" }}>
                            <Card.Header className="text-center">{t('reset.finish.title')}</Card.Header>
                            <Card.Body>
                                <div>{token ? resetForm : null}</div>
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

export default ResetPasswordPage;
