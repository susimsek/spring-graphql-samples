export interface IMessage {
    id: string;
    message: string;
    isChatBoot: boolean;
}

export interface IQuestion {
    id: string;
    question: IMessage;
    answer?: IMessage;
}

export enum Direction {
    LEFT,
    RIGHT
}
