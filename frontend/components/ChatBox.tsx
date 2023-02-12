import React, {useEffect, useRef, useState} from "react";
import {useTranslation} from "next-i18next";
import {Card, Col, Form, Row, Spinner} from "react-bootstrap";
import {useTextCompletionMutation} from "../generated/graphql-types";
import {SubmitHandler, useForm} from "react-hook-form";
import * as yup from "yup";
import {yupResolver} from "@hookform/resolvers/yup";
import {faPaperPlane, faUser} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import Button from "react-bootstrap/Button";

import chatBotPic from '../public/assets/chatbot.png'
import Image from "next/image";
import {Direction, IMessage, IQuestion} from "../types/chat";
import Message from "./Message";
import {v4 as uuidv4} from 'uuid';

type ChatFormData = { question: string };
const ChatBox: React.FC = () => {

    const bottomRef = useRef<HTMLDivElement>(null)

    const { t } = useTranslation('chat')

    const [messages, setMessages] = useState<IQuestion[]>([]);

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
        messages.push({
            id: uuidv4(),
            question: {
                id: uuidv4(),
                message: question,
                isChatBoot: false
            },
            answer: undefined
        })
        setMessages([...messages])
        const response = await textCompletion({
            variables: {
                input: {
                    prompt: question
                },
            }
        });

        if (response.data?.textCompletion?.choices?.length) {
            messages[messages.length - 1] = {
                ... messages[messages.length - 1],
                answer: {
                    id: uuidv4(),
                    message:  response.data.textCompletion.choices[0]?.text || "",
                    isChatBoot: true
            }
        }
            setMessages([...messages])
        }
    };

    useEffect(() => {
        // 👇️ scroll to bottom every time messages change
        bottomRef.current?.scrollIntoView({behavior: 'smooth'});
    }, [messages]);

    return (
            <Row className="d-flex justify-content-center">
                <Col md={10} xl={6} lg={8}>
                    <Card id="chat2" style={{ borderRadius: "15px" }}>
                        <Card.Header className="d-flex justify-content-between align-items-center p-3">
                            <h5 className="mb-0">{t('chat.title')}</h5>
                        </Card.Header>
                            <Card.Body style={{ position: "relative", height: "400px", overflowY: "scroll" }}>
                                <Message icon={ <Image src={chatBotPic}
                                                       style={{ width: "45px", height: "100%" }}
                                                       alt="avatar 1"
                                                       className='img-thumbnail'
                                         />}
                                         direction={Direction.LEFT}
                                         messages={[
                                             {
                                                 id: uuidv4(),
                                                 message: t('chat.chatbot.greeting'),
                                                 isChatBoot: true
                                             },
                                             {
                                                 id: uuidv4(),
                                                 message: t('chat.chatbot.helpQuestion'),
                                                 isChatBoot: true
                                             }
                                         ]}
                                        multiRowEnabled={true}/>

                                {messages.map((question: IQuestion) => (
                                    <>
                                        <Message key={question.question.id}
                                                 icon={<FontAwesomeIcon key={question.question.id} className="text-primary rounded-circle" icon={faUser}  style={{ width: "30px", height: "100%" }}/>}
                                                 direction={Direction.RIGHT}
                                                 message={question.question}/>
                                        <Message key={question.answer?.id}
                                                 icon={ <Image
                                                     key={question.question.id}
                                                     src={chatBotPic}
                                                     style={{ width: "45px", height: "100%" }}
                                                     alt="avatar 1"
                                                     className='img-thumbnail'
                                                 />}
                                                 direction={Direction.LEFT}
                                                 message={question.answer}
                                                 loading={!question.answer && loading}
                                                 spinnerText={t('chat.chatbot.answerLoadingText')}/>
                                    </>))}
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