import {PostStatus} from "../generated/graphql-types";

export interface IPost {
    id: string;
    title: string;
    content: string;
    status: PostStatus,
    createdAt: any;
    locale?: any | null
}