<template>
  <van-sticky>
    <NavBarForReturn :title="title"/>
  </van-sticky>

  <van-list
      v-model:loading="loading"
      :finished="finished"
      @load="onLoad"
      class="list"
      direction="up"
      ref="listRef"
      :immediate-check="false"
      disabled
  >
    <div v-for="item in list" :class="[item.fromId === fromId && commonType.includes(item.type) ?'item-right':'item-left','item']">
      <img :src="item.avatarUrl" class="avatar"/>
      <div :class="[item.fromId === fromId && commonType.includes(item.type) ? 'container-right' : 'container-left', 'chat-container']">
        <div class="chat-messages">
          <div v-if="type === '1' && item.fromId !== fromId" class="sender-nickname">{{item.username}}</div>
          <div :class="[item.fromId === fromId && commonType.includes(item.type) ? 'sent' : 'received','message']">
            <div class="message-content">
              {{ handleContent(item.content,item.type) }}
              <div v-if="item.type===2" style="color: blue; text-decoration: underline" @click="agreeJoin(item.coterieId,item.fromId,item.id,item.content,item.toId)">
                {{joinCoterieMessageHandle(item.content,item.fromId,item.type)}}
              </div>
            </div>
            <div class="message-time">{{DateToChatTime(item.createTime)}}</div>
          </div>
        </div>
      </div>
    </div>
  </van-list>

  <div class="chat-input">
    <input type="text" v-model="text"/>
    <van-button @click="sendMessage">发送</van-button>
  </div>
</template>

<script setup>
import {useRoute, useRouter} from "vue-router";
import NavBarForReturn from "../bar/NavBarForReturn.vue";
import {nextTick, onMounted, ref} from "vue";
import myAxios from "../../plugins/myAxios.js";
import ChatPage from "../../config/chatPage";
import {showFailToast, showSuccessToast} from "vant";
import {DateToChatTime} from "../../service/userService.ts";
import socket from "../../plugins/websocket.ts"
import {websocketStore} from "../../store/websocket.ts";
import websocket from "../../plugins/websocket.ts";

const route = useRoute()
const router = useRouter()

const title = route.query.title
const toId = route.query.toId
const username = route.query.username
const fromId = route.query.fromId
const toAvatarUrl = route.query.toAvatarUrl
const fromAvatarUrl = route.query.fromAvatarUrl
const type = route.query.type
const list = ref([])
const text = ref('')

const loading = ref(true);
const finished = ref(false);
const pageNum = ref(0)

const listRef = ref(null);
const webStore = websocketStore()
const commonType = [0,1,"0","1"]

webStore.$subscribe((mutation, state) => {
  const data = JSON.parse(state.msg)
  //  判断这个状态是否是和当前聊天对象的
  if((data.toId === toId && data.fromId === fromId)
      || (data.fromId === toId && data.toId === fromId)
      || (data.coterieId === toId)
      || (data.chatType === '2' && data.toId === fromId)) {
    const newEle = data
    newEle.type = data.messageType
    list.value?.push(newEle)
    nextTick(() => {
      scrollVisible()
    })
  }
})

const onLoad = async () => {
  pageNum.value++

  const pageData = (await myAxios.post('/chat/query',{
    to: toId,
    type: type,
    pageNum: pageNum.value,
    pageSize: ChatPage.pageSize,
  }))?.data?.data
  if(!pageData){
    showFailToast('加载失败')
    await router.replace('/message')
    return
  }else {
    list.value = pageData.list.concat(list.value)
  }

  loading.value = false;
  //  判断是否加载完毕
  finished.value = (pageData.pageNum*pageData.pageSize) >= pageData.total

};

onMounted( async () => {
  await onLoad()

  //  获取 list 组件 dom 对象
  const listContainer = listRef.value?.$el;
  if (listContainer) {
    //  获取当前界面的可视高度
    const screenHeight = window.innerHeight;
    //  获取 dom 对象的高度
    const containerHeight = listContainer.scrollHeight;
    if (containerHeight >= screenHeight) {
        scrollVisible()
    } else {
      listContainer.scrollTop = 0;
    }
  }


})

const agreeJoin = async (coterieId,userId,id,content,toId) => {
  // const userId = content.match(/userId=([^&]+)/)[1]
  // const coterieId = content.match(/coterieId=([^&]+)/)[1]
  //  若 发送者 跟 登录者一个人，则不能同意加入（自己不能同意自己加入）
  const handle = content.match(/handle=([^&]+)/)[1]
  if(userId === fromId || handle !== '未处理')
    return
  const res = (await myAxios.post('/chat/join/coterie', {
        userId: userId,
        coterieId: coterieId,
        messageId: id,
      })).data
  if(res.data){
    showSuccessToast('成功加入')
0    //  发送 websocket 请求（加入圈子成功）
    socket.send({
      toId: toId,
      coterieId: coterieId,
      userId: userId,
      chatType: "2",
      messageType: "3",
      createTime: new Date()
    })
  }else {
    showFailToast(res.description)
  }
}

const handleContent = (content, type) => {
  if(commonType.includes(type))
    return content
  const index = content.indexOf("?")
  return index === -1 ?  content : content.substring(0,index)
}

//  未处理
const joinCoterieMessageHandle = (content, userId, type) => {
  if(type === 2){
    const aft = content.match(/handle=([^&]+)/)[1]
    if(aft === "未处理") {
      if(userId === fromId)
        return '待验证'
      return '同意'
    }
  }
  return '已同意'
}

const sendMessage = () => {
  if(text.value === '')
    return

  // myAxios.post('/chat/send/common',{
  //   toId: toId,
  //   content: text.value,
  //   avatarUrl: fromAvatarUrl,
  //   type: type,
  // })

  const message = {
    toId: toId,
    fromId: fromId,
    username: username,
    coterieId: Number(type)===1 ? toId : '0',
    content: text.value,
    avatarUrl: fromAvatarUrl,
    chatType: type,
    messageType: type,
    createTime: new Date(),
    title: title,
  }
  socket.send(message)

  text.value = ''
}

// 显示最后一条数据
const scrollVisible = () => {
  listRef.value?.$el?.lastElementChild?.scrollIntoView();
}

// const initWebSocket = () => {
//   //  异步执行
//
//   Promise.resolve().then(() => {
//     socket.init()
//   }).then(() => {
//     if(type === '0') {
//       //  单聊
//       socket.send({fromId: fromId, type: '-1'})
//     }else if(type === '1'){
//       //  群聊
//       socket.send({fromId: fromId, type: '-2', toId: toId})
//     }
//   })
// }

</script>

<style scoped>
.list{
  padding: 10px 10px 10px;
  display: flex;
  flex-direction: column;
}
.item{
  display: flex;
  padding-bottom: 10px;
}
.item-left{
  flex-direction: row;
}

.item-right{
  flex-direction: row-reverse;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.container-right {
  padding-left: 50px;
  padding-right: 10px;
}

.container-left {
  padding-left: 10px;
  padding-right: 50px;
}

.sender-nickname {
  color: #333;
  font-size: 4px;
}

.chat-messages {
  flex-grow: 1;
  overflow-y: auto;
}

.message {
  display: flex;
  flex-direction: row;
  align-items: flex-start; /* 调整头像和内容垂直对齐 */
  border-radius: 6px;
}

.avatar {
  width: 40px;
  height: 40px;
  background-color: #f4f4f4;
  border-radius: 6px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  object-fit: cover;
}

.sent {
  align-self: flex-end;
  background-color: #dcf8c6;
}

.received {
  align-self: flex-start;
  background-color: #f4f4f4;
}

.message-content {
  padding: 10px;
}

.message-time {
  align-self: flex-end;
  font-size: 12px;
  color: #888;
}

.chat-input {
  display: flex;
  align-items: center;
  background-color: #f4f4f4;

  height: 60px; /* 底部组件的高度 */

  width: 100%;
  position: fixed;
  bottom: 0;
  z-index: 100;
  padding-bottom: env(safe-area-inset-bottom);
}

.chat-input input[type="text"] {
  flex-grow: 1;
  padding: 8px;
  border: none;
  border-radius: 5px;
  background-color: #fff;
  margin-right: 10px;
  margin-left: 10px;
}

.chat-input button {
  padding: 8px 15px;
  border: none;
  border-radius: 5px;
  background-color: #4caf50;
  color: #fff;
  cursor: pointer;
  font-weight: bold;
}
</style>