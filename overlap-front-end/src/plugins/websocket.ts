import {websocketStore} from "../store/websocket";

const isDev = process.env.NODE_ENV === 'development'
let url = isDev ? 'ws://localhost:9501/ws' : 'ws://overlap-backend.ovta.love:9501/ws'

interface webSocket {
    websocket: any,
    init:()=>any,
    send:(e)=>void
}

const socket: webSocket = {
    websocket: null,
    init: () => {
        socket.websocket = new WebSocket(url)
        socket.websocket.onmessage = (e) => {
            const socketStore = websocketStore()
            socketStore.msg = e.data
        }
        socket.websocket.onclose = (e) => {
            console.log(e)
        }
    },
    send: (e) => {
        if(socket.websocket.readyState === 1) {
            socket.websocket.send(JSON.stringify(e))
        }else {
            setTimeout(() => {
                if(socket.websocket.readyState === 1) {
                    socket.websocket.send(JSON.stringify(e))
                }
            },1000)
        }
    }
}

export default socket