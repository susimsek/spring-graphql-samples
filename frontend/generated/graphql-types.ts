import { gql } from '@apollo/client';
import * as Apollo from '@apollo/client';
export type Maybe<T> = T | null;
export type InputMaybe<T> = Maybe<T>;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
export type MakeOptional<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]?: Maybe<T[SubKey]> };
export type MakeMaybe<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]: Maybe<T[SubKey]> };
const defaultOptions = {} as const;
/** All built-in and custom scalars, mapped to their actual values */
export interface Scalars {
  ID: string;
  String: string;
  Boolean: boolean;
  Int: number;
  Float: number;
  OffsetDateTime: any;
}

export interface AddPostInput {
  content: Scalars['String'];
  title: Scalars['String'];
}

export interface AddUserInput {
  email: Scalars['String'];
  firstName: Scalars['String'];
  lastName: Scalars['String'];
  password: Scalars['String'];
  username: Scalars['String'];
}

export interface LoginInput {
  login: Scalars['String'];
  password: Scalars['String'];
}

export enum OrderType {
  Asc = 'ASC',
  Desc = 'DESC'
}

export interface PostOrder {
  field: PostOrderField;
  order?: InputMaybe<OrderType>;
}

export enum PostOrderField {
  CreatedDate = 'createdDate',
  Id = 'id',
  Title = 'title'
}

export enum PostStatus {
  Draft = 'DRAFT',
  PendingModeration = 'PENDING_MODERATION',
  Published = 'PUBLISHED'
}

export interface UpdatePostInput {
  content: Scalars['String'];
  id: Scalars['ID'];
  title: Scalars['String'];
}

export interface UserFilter {
  email?: InputMaybe<Scalars['String']>;
  firstName?: InputMaybe<Scalars['String']>;
  lastName?: InputMaybe<Scalars['String']>;
  username?: InputMaybe<Scalars['String']>;
}

export interface UserOrder {
  field: UserOrderField;
  order?: InputMaybe<OrderType>;
}

export enum UserOrderField {
  CreatedDate = 'createdDate',
  Email = 'email',
  FirstName = 'firstName',
  Id = 'id',
  LastName = 'lastName',
  Username = 'username'
}

export type OnPostAddedSubscriptionVariables = Exact<{ [key: string]: never; }>;


export type OnPostAddedSubscription = { postAdded: { id: string, title: string, content: string, status: PostStatus, createdDate: any } };


export const OnPostAddedDocument = gql`
    subscription OnPostAdded {
  postAdded {
    id
    title
    content
    status
    createdDate
  }
}
    `;

/**
 * __useOnPostAddedSubscription__
 *
 * To run a query within a React component, call `useOnPostAddedSubscription` and pass it any options that fit your needs.
 * When your component renders, `useOnPostAddedSubscription` returns an object from Apollo Client that contains loading, error, and data properties
 * you can use to render your UI.
 *
 * @param baseOptions options that will be passed into the subscription, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
 *
 * @example
 * const { data, loading, error } = useOnPostAddedSubscription({
 *   variables: {
 *   },
 * });
 */
export function useOnPostAddedSubscription(baseOptions?: Apollo.SubscriptionHookOptions<OnPostAddedSubscription, OnPostAddedSubscriptionVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useSubscription<OnPostAddedSubscription, OnPostAddedSubscriptionVariables>(OnPostAddedDocument, options);
      }
export type OnPostAddedSubscriptionHookResult = ReturnType<typeof useOnPostAddedSubscription>;
export type OnPostAddedSubscriptionResult = Apollo.SubscriptionResult<OnPostAddedSubscription>;