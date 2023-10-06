export type CoterieType = {
    id: number;
    coterieNum: string;
    userId: number;
    title: string;
    tags: string;
    description: string;
    avatarUrl: string;
    password?: string;
    isEncrypted: number;
    status: number;
    maxNum: number;
    curNum: number;
    createTime: Date;
};