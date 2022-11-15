import {PostStatus} from "../generated/graphql-types";

export interface IPost {
    id: string;
    title: string;
    content: string;
    status: PostStatus,
    createdDate: any;
}