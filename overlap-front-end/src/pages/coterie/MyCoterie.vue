<template>

  <coterie-card-list ref="coterieCardList" v-if="curUser" :path="`/coterie/cur`" :is-my-coterie="true">
    <template v-slot:my-footer-slot="slotProps">
      <van-button v-if="curUser.id === slotProps.coterie.userId"
                  type="primary" size="small" style="margin-right: 8px"
                  @click="updateCoterie(slotProps.coterie)">
        更新
      </van-button>
      <van-button type="danger" size="small"
                  @click="exitCoterie(slotProps.coterie,curUser.id === slotProps.coterie.userId)">
        <span v-if="curUser.id !== slotProps.coterie.userId">
          退出
        </span>
        <span v-if="curUser.id === slotProps.coterie.userId">
          解散
        </span>
      </van-button>
    </template>
  </coterie-card-list>


</template>

<script setup>
import CoterieCardList from "../../components/coterie/CoterieCardList.vue";
import {useRouter} from "vue-router";
import {getCurrentUser} from "../../service/userService.ts";
import {onMounted, ref} from "vue";
import myAxios from "../../plugins/myAxios.js";
import {showConfirmDialog, showFailToast, showSuccessToast} from "vant";
import socket from "../../plugins/websocket.ts";

const router = useRouter()
const curUser = ref(null)
const coterieCardList = ref()

onMounted(async () => {
  curUser.value = await getCurrentUser();
  if(curUser.value === null){
    await router.push("/user/login")
    showFailToast('请先登录')
  }
})

const exitCoterie = async (coterie, isMaster) => {
  //  弹出确认窗口
  showConfirmDialog({
    title: isMaster ? '确认解散' : '确认退出',
    message:
        '解散或退出后将不可逆，三思哦',
  })
  .then(async () => {
    const res = (await myAxios.post("/coterie/exit?coterieId="+coterie.id)).data?.data
    socket.send({
      toId: coterie.userId,
      coterieId: coterie.id,
      userId: curUser.value.id,
      chatType: '2',
      messageType: isMaster ? '4' : '5',
      createTime: new Date(),
      title: "系统消息",
    })
    if (res) {
      showSuccessToast((isMaster ? "解散" : "退出") + "成功")
      coterieCardList.value.refreshing = true
      coterieCardList.value.onRefresh()
    } else {
      showFailToast((isMaster ? "解散" : "退出") + "失败")
    }
  })
  .catch(() => {

  })
}

const updateCoterie = (coterie) => {
  router.push({
    path: "/coterie/update",
    query: {
      title: "更新圈子",
      backEndPath: "/coterie/update",
      curCoterie: JSON.stringify(coterie)
    }
  })
}

</script>

<style scoped>

</style>