import React, {useEffect, useRef, useState} from "react";
import {useTranslation} from "next-i18next";
import {Card, Col, Container, Form, Row, Spinner} from "react-bootstrap";
import {useTextCompletionMutation} from "../generated/graphql-types";
import {SubmitHandler, useForm} from "react-hook-form";
import * as yup from "yup";
import {yupResolver} from "@hookform/resolvers/yup";
import {faPaperPlane, faUser} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import Button from "react-bootstrap/Button";

import chatBotPic from '../public/assets/chatbot.png'
import Image from "next/image";
import {IChat} from "../types/chat";

type ChatFormData = { question: string };
const ChatBox: React.FC = () => {

    const bottomRef = useRef<HTMLDivElement>(null)

    const { t } = useTranslation('chat')

    const [chats, setChats] = useState<IChat[]>([]);

    const schema = yup.object({
        question: yup.string().required(t("common:validation.required"))
            .min(3, t("common:validation.minlength"))
    }).required();

    const [textCompletion, { data, loading, error }, ] = useTextCompletionMutation({
        errorPolicy: "all"
    });

    const { register, handleSubmit, formState: { errors } } = useForm<ChatFormData>({
        resolver: yupResolver(schema)
    });

    const handleQuestion: SubmitHandler<ChatFormData> = async ({question}) => {
        chats.push({
            question,
            answer: undefined
        })
        setChats([...chats])
        const response = await textCompletion({
            variables: {
                input: {
                    prompt: question
                },
            }
        });

        if (response.data?.textCompletion?.choices?.length) {
            chats[chats.length - 1] = {
                question,
                answer: response.data.textCompletion.choices[0]?.text
            }
            setChats([...chats])
        }
    };

    useEffect(() => {
        // 👇️ scroll to bottom every time messages change
        bottomRef.current?.scrollIntoView({behavior: 'smooth'});
    }, [chats]);

    return (
            <Row className="d-flex justify-content-center">
                <Col md={10} xl={6} lg={8}>
                    <Card id="chat2" style={{ borderRadius: "15px" }}>
                        <Card.Header className="d-flex justify-content-between align-items-center p-3">
                            <h5 className="mb-0">{t('chat.title')}</h5>
                        </Card.Header>
                            <Card.Body style={{ position: "relative", height: "400px", overflowY: "scroll" }}>
                                <div className="d-flex flex-row justify-content-start">
                                    <Image src={chatBotPic}
                                           style={{ width: "45px", height: "100%" }}
                                           alt="avatar 1"
                                           className='img-thumbnail'
                                          />
                                    <div>
                                        <p
                                            className="small p-2 ms-3 mb-1 rounded-3"
                                            style={{ backgroundColor: "#f5f6f7" }}
                                        >
                                            {t('chat.chatbot.greeting')}
                                        </p>
                                        <p
                                            className="small p-2 ms-3 mb-1 rounded-3"
                                            style={{ backgroundColor: "#f5f6f7" }}
                                        >
                                            {t('chat.chatbot.helpQuestion')}
                                        </p>
                                    </div>
                                </div>

                                {chats.map((chat: IChat) => (
                                        <>
                                            {chat.question &&  <div key={chat.question} className="d-flex flex-row justify-content-end mb-4 pt-1">
                                                <div>
                                                    <p className="small p-2 me-3 mb-1 text-white rounded-3 bg-primary">
                                                        {chat.question}
                                                    </p>
                                                </div>
                                                <FontAwesomeIcon className="text-primary rounded-circle" icon={faUser}  style={{ width: "30px", height: "100%" }}/>
                                            </div>}


                                            {
                                                (chat.answer || (!chat.answer && loading)) &&   <div key={chat.answer} className="d-flex flex-row justify-content-start">
                                                <Image src={chatBotPic}
                                                       style={{ width: "45px", height: "100%" }}
                                                       alt="avatar 1"
                                                       className='img-thumbnail'
                                                />
                                                <div>
                                                    <p
                                                        className="small p-2 ms-3 mb-1 rounded-3"
                                                        style={{ backgroundColor: "#f5f6f7" }}
                                                    >
                                                        {(!chat.answer && loading) ? <span><Spinner size="sm"
                                                                                                    variant="secondary"
                                                                                                    className="me-1"
                                                                                                    animation="grow"/>{t('chat.chatbot.answerLoadingText')}</span> : chat.answer}
                                                    </p>
                                                </div>
                                            </div>}
                                        </>
                                    ))
                                }
                                <div ref={bottomRef} />
                            </Card.Body>
                        <Card.Footer className="text-muted justify-content-start align-items-center p-3">
                            <Form onSubmit={handleSubmit(handleQuestion)}>
                            <Form.Group className="mb-3">
                                <Form.Control
                                    as="textarea"
                                    rows={3}
                                    placeholder={t('chat.form.buttonPlaceholder')}
                                    {...register('question')}
                                    isInvalid={!!errors.question}
                                    disabled={loading}
                                />
                                <Form.Control.Feedback type="invalid">
                                    {errors.question?.message}
                                </Form.Control.Feedback>
                            </Form.Group>
                                <Button className="mb-3 float-end" variant="info" type="submit" disabled={loading}>
                                    {loading && <Spinner
                                        as="span"
                                        animation="border"
                                        size="sm"
                                        role="status"
                                        aria-hidden="true"
                                    />} <FontAwesomeIcon size="lg"
                                                         className="text-white rounded-circle"
                                                         icon={faPaperPlane}
                                />
                                </Button>
                            </Form>
                        </Card.Footer>
                    </Card>
                </Col>
            </Row>
    );
}

export default ChatBox;