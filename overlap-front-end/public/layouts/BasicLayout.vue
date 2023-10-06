<template>

  <div id="content">
    <router-view/>
  </div>

  <van-tabbar route v-if="$route.meta.showTabbar?? true">
    <van-tabbar-item replace to="/" icon="home-o">首页</van-tabbar-item>
    <van-tabbar-item replace to="/coterie" icon="fire-o">小圈</van-tabbar-item>
    <van-tabbar-item replace to="/message" icon="chat-o"
                     :badge="unReadStore.size > 0 ? unReadStore.size : undefined">消息</van-tabbar-item>
    <van-tabbar-item replace to="/user" icon="manager-o">我的</van-tabbar-item>
  </van-tabbar>

</template>

<script setup>
import {getUnRead, setUnRead} from "../../src/service/chatService.ts";
import {websocketStore} from "../../src/store/websocket.ts";
import {onMounted, ref} from "vue";
import {getCurrentUser} from "../../src/service/userService.ts";
import {initWebPageWebSocket} from "../../src/service/webSocketService";
import {unReadMessageStore} from "../../src/store/unReadMessage.ts";

const webStore = websocketStore()
const unReadStore = unReadMessageStore()
const curUser = ref()

webStore.$subscribe(async (mutation, state) => {
  const data = JSON.parse(state.msg)
  //  若发送方为自己则忽略
  if(curUser.value === null)
    curUser.value = await getCurrentUser()
  if(data.fromId !== null && data.fromId === curUser.value.id)
    return
  unReadStore.size++
  let i = 0
  const dataType = Number(data.chatType)

//  遍历未读数组，id 相同则 size++
  for (i = 0; i < unReadStore.unReadArr.length; i++) {
    if(dataType===0 && data.fromId === unReadStore.unReadArr[i].id){
      //  单聊
      updateUnReadItem(unReadStore,i,data.content,data.createTime);
      break
    }else if(dataType===1 && data.toId === unReadStore.unReadArr[i].id){
      //  群聊
      updateUnReadItem(unReadStore,i,data.content,data.createTime);
      break
    }else if(dataType===2 && data.userId === unReadStore.unReadArr[i].id) {
      updateUnReadItem(unReadStore,i,data.content,data.createTime);
      break
    }
  }
  //  新聊天列表消息
  if (i === unReadStore.unReadArr.length) {
    let id = dataType===0 ? data.fromId : data.toId
    id = dataType === 2 ? data.userId : id
    unReadStore.unReadArr.push({id: id, type: dataType, unRead: 1, avatarUrl: data.avatarUrl,
          latestNews: data.content, latestTime: data.createTime, title: data.title})
  }
})

const updateUnReadItem = (unReadStore, i, content, createTime) => {
  unReadStore.unReadArr[i].unRead++
  unReadStore.unReadArr[i].latestNews = content
  unReadStore.unReadArr[i].latestTime = createTime
}

onMounted(async () => {
  curUser.value = await getCurrentUser()
  if(curUser.value !== null){
    //  建立网页 websocket 连接
    initWebPageWebSocket(curUser.value.id)
  }
})

</script>

<style scoped>
#content {
  padding-bottom: 50px;
}
</style>