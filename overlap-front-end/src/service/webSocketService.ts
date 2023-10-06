import socket from "../plugins/websocket";

export const initWebPageWebSocket = (id) => {
    id = id.replace(/\"/g, "");
    //  异步执行
    Promise.resolve().then(() => {
        socket.init()
    }).then(() => {
        socket.send({fromId: id, chatType: '-4'})
    })
}