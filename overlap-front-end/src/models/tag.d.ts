export type TagType = {
    id: number;
    tagName: string;
    subTag: TagType[];
    userId: number;
    parentId: number;
    classifyId: number;
    isParent: number;
};