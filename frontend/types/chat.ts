export interface IChat {
    id?: string | null | undefined;
    question: string;
    answer?: string | null | undefined;
}

export enum Direction {
    LEFT,
    RIGHT
}
