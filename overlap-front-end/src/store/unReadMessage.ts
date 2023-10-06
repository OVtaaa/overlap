import {defineStore} from "pinia";

export const unReadMessageStore = defineStore("unReadMessage",{
    state: () => {
        return{
            unReadArr: [],
            size: 0,
        }
    }
})