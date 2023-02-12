import React from "react";
import {Container} from "react-bootstrap";
import Layout from "../../components/Layout";
import {serverSideTranslations} from "next-i18next/serverSideTranslations";
import ChatBox from "../../components/ChatBox";


const ChatPage = () => {

    // Creating a state to store the uploaded video

    return (
        <Layout>
            <Container className="mt-3">
                <ChatBox/>
            </Container>
        </Layout>
    );
}

export const getStaticProps = async ({ locale }: { locale: string }) => ({
    props: {
        ...(await serverSideTranslations(locale, ['common', 'chat']))
    }
})

export default ChatPage;
