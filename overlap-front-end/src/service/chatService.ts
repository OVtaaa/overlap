import myAxios from "../plugins/myAxios.js";

export const getChatList = async ()=>{
    return (await myAxios.get('/chat/list')).data?.data
}

// export const getAllUnRead = async (id) => {
//     return (await myAxios.get('/chat/all/unRead?id='+id)).data?.data
// }

export const getUnRead = () => {
    return Number(localStorage.getItem("unRead") ?? 0)
}

export const setUnRead = (val) => {
    localStorage.setItem("unRead",JSON.stringify(val))
}