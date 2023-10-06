import {defineStore} from "pinia";

export const websocketStore = defineStore("websocket",{
    state: () => {
        return{
            msg: ""
        }
    }
})