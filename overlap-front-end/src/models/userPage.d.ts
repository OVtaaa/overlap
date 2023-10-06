import {UserType} from "./user";

export type UserPage = {
    list: UserType[];
    total: number;
    curPageNum: number;
    pageSize: number;
};