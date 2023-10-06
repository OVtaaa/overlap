<template>

  <van-nav-bar title="消息列表">
<!--    <template #right>-->
<!--      <van-icon name="plus" size="20" />-->
<!--    </template>-->
  </van-nav-bar>

<!--  顶部搜索条-->
<!--  <top-search-router-bar :placeholder="'请输入昵称'" :path="''"/>-->

  <van-pull-refresh v-model="loading" @refresh="onRefresh" style="min-height: 70vh;">
    <van-swipe-cell v-if="curUser && chatList" v-for="dialog in chatList">
      <van-cell :value="dialog.latestTime ?? ''" title-style="min-width:65%;"
                @click="toChat(dialog.title,dialog.id,dialog.avatarUrl,dialog.type)">
        <template #title>
          <div style="padding-left: 10px; padding-top: 10px">
            {{dialog.title}}
          </div>
        </template>

        <template #label>
          <div v-if="dialog.latestNews" style="padding-left: 10px;">
            {{dialog.latestNews.length>16
              ? dialog.latestNews.substring(0,16)+"..."
              : dialog.latestNews}}</div>
        </template>


        <div v-if="dialog.latestTime" :style="showLatestTimeStyle(dialog.unRead)">
          {{DateToChatTime(dialog.latestTime)}}
        </div>
        <van-badge v-if="dialog.unRead > 0" :content="dialog.unRead" max="99"
                  style="margin-right: 10px; margin-top: 8px"/>

        <template #icon>
          <van-image round width="48px" height="48px" :src="dialog.avatarUrl" />
        </template>
      </van-cell>

      <template #right>
        <van-button square type="danger" text="删除"
                    style="height: 100%" @click="removeChat(dialog.id,dialog.type)"/>
      </template>
    </van-swipe-cell>
  </van-pull-refresh>


</template>

<script setup>
import {showFailToast, showToast} from "vant";
import TopSearchRouterBar from "../../components/bar/TopSearchRouterBar.vue";
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import {DateToChatTime} from "../../service/userService.ts"
import {getCurrentUser} from "../../service/userService.ts";
import myAxios from "../../plugins/myAxios.js";
import {getChatList, getUnRead, setUnRead} from "../../service/chatService.ts";
import socket from "../../plugins/websocket.ts";
import {websocketStore} from "../../store/websocket.ts";
import {unReadMessageStore} from "../../store/unReadMessage.ts";

const router = useRouter()
const curUser = ref()

// const mockData = [
//   {
//     avatarUrl: 'http://easyimage.ovta.love/i/2023/09/16/iublvh.webp',
//     title: '二进制恋爱',
//     latestNews: '周林林海边唱的歌是十三月里稀薄空气太空飞行留下秘密',
//     latestTime: '15:30',
//     id: '1688490580407070722',
//   }
// ]

const chatList = ref([])
const loading = ref(false);
const webStore = websocketStore()
const unReadStore = unReadMessageStore()

unReadStore.$subscribe((mutation, state) => {
  watchUnRead()
})

webStore.$subscribe((mutation, state) => {
  const data = JSON.parse(state.msg)
  const type = data.chatType, fromId = data.fromId
  let chatId = fromId===curUser.value.id ? data.toId : fromId
  if(type === "1"){
    chatId = data.toId
  }else if(type === "2")
    chatId = data.userId
  //  根据 chatId 更新列表
  let i, ele = null
  for(i = 0; i < chatList.value.length; i++){
    if(Number(chatList.value[i].type) === Number(type) && chatList.value[i].id === chatId){
      ele = chatList.value[i]
      chatList.value.splice(i,1)
      break
    }
  }
  if(ele === null){
    unshiftChatList(chatId, type, 1,data.title, data.avatarUrl,data.content, data.createTime)
  }else {
    ele.latestNews = data.content
    ele.latestTime = data.createTime
    ele.unRead += 1
    chatList.value.unshift(ele)
  }
})

const unshiftChatList = (id, type, unRead, title, avatar, content, latestTime) =>{
  chatList.value.unshift({
    id: id,
    type: type,
    unRead: unRead,
    title: title,
    avatarUrl: avatar,
    latestTime: latestTime,
    latestNews: content,
  })
  // 同时更新数据库中聊天列表

}

const toChat = (title,id,avatarUrl,type) => {
  updateUnReadStore(id,type)

  router.push({
    path: '/chat',
    query: {
      title: title,
      toId: id,
      username: curUser.value.username,
      fromId: curUser.value.id,
      type: type,
      toAvatarUrl: avatarUrl,
      fromAvatarUrl: curUser.value.avatarUrl,
    }
  })
}

const showLatestTimeStyle = (unRead) => {
  if(unRead === 0){
    return "padding-top: 10px; font-size: 12px"
  }
  return "padding-top: 8px; font-size: 12px";
}

onMounted(async () => {
  curUser.value = await getCurrentUser();
  if(!curUser.value){
    await router.push('/user')
    showFailToast('请先登录')
    return
  }

  //  获取聊天列表
  chatList.value = await getChatList()

  // initWebSocket()
  await watchUnRead()
})

const watchUnRead = async () => {
  //  根据 unReadMessageStore 中未读数组消息，更新聊天列表的未读数
  for(let i = 0; i < unReadStore.unReadArr.length; i++){
    let j = 0, k = 0
    for(j = 0; j < chatList.value.length; j++){
      const typeEq = (Number(unReadStore.unReadArr[i].type) === Number(chatList.value[j].type))
      if(typeEq &&
          (unReadStore.unReadArr[i].id === chatList.value[j].id || Number(chatList.value[j].type)===2)) {
        //  更新聊天该行数据
        chatList.value[j].latestTime = unReadStore.unReadArr[i].latestTime
        chatList.value[j].latestNews = unReadStore.unReadArr[i].latestNews
        chatList.value[j].unRead = unReadStore.unReadArr[i].unRead
        break
      }
    }
    if(j === chatList.value.length){
      //  创建新聊天列表
      unshiftChatList(unReadStore.unReadArr[i].id, unReadStore.unReadArr[i].type,
          1,unReadStore.unReadArr[i].title,unReadStore.unReadArr[i].avatarUrl,
          unReadStore.unReadArr[i].latestNews,unReadStore.unReadArr[i].latestTime)
    }
  }
}

const onRefresh = async () => {
  chatList.value = await getChatList()
  await watchUnRead()
  loading.value = false
}

const updateUnReadStore = (id,type) => {
  // 更新 底部 未读总数
  for(let i = 0; i < chatList.value.length; i++){
    if(Number(chatList.value[i].type) === Number(type) && chatList.value[i].id === id){
      unReadStore.size -= chatList.value[i].unRead
      break
    }
  }
  //  若是未读则也删除未读数据
  for(let i = 0; i < unReadStore.unReadArr.length; i++){
    if(Number(unReadStore.unReadArr[i].type) === Number(type) && unReadStore.unReadArr[i].id === id){
      unReadStore.unReadArr.splice(i,1)
      break
    }
  }
}

const removeChat = async (id,type) => {
  updateUnReadStore(id,type)

  await myAxios.delete('/chat/remove?id=' + id + '&type=' + type)
  chatList.value = await getChatList()
}

</script>

<style>
:root:root {
  --van-cell-label-margin-top: 0px
}
</style>