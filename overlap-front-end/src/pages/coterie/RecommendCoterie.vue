<template>

  <coterie-card-list :path="`/coterie/recommend`">
    <template v-slot:my-footer-slot="coterieProps">
      <van-button type="warning" size="small" @click="showDialog(coterieProps.coterie)">加入</van-button>
    </template>
  </coterie-card-list>

  <van-dialog v-model:show="passwordDialogShow" title="请输入入圈密码" show-cancel-button
              @confirm="checkJoinCoterie(curCoterieRef)">
    <van-cell-group inset>
      <!-- 输入密码 -->
      <van-field v-model="dialogAtt.password" type="password" placeholder="密码正确，才可进入小圈哦"/>
    </van-cell-group>
  </van-dialog>

</template>

<script setup>
import CoterieCardList from "../../components/coterie/CoterieCardList.vue";
import {showConfirmDialog, showFailToast, showSuccessToast} from "vant";
import {onMounted, ref} from "vue";
import myAxios from "../../plugins/myAxios.js";
import {getCurrentUser} from "../../service/userService.ts";
import {useRouter} from "vue-router";
import socket from "../../plugins/websocket.ts";

const dialogAtt = ref({
  password: ""
})
const router = useRouter()
const passwordDialogShow = ref(false)
const curCoterieRef = ref()
const curUser = ref(null)

onMounted(async () => {
  curUser.value = await getCurrentUser()
})

const showDialog = (curCoterie) => {
  if(curUser.value=== null){
    router.push("/user/login")
    showFailToast('请先登录')
    return
  }
  dialogAtt.value.password = ""
  curCoterieRef.value = curCoterie
  if(curCoterie.isEncrypted)
    passwordDialogShow.value = true
  else{
    showConfirmDialog({
      title: '确认入圈',
      message:
          '你确定要加入这个神秘的小圈吗？',
    })
    .then(() => {
      checkJoinCoterie(curCoterie)
    })
    .catch(() => {
      // on cancel
    });
  }
}

const checkJoinCoterie = async (curCoterie) => {
  if(curCoterie.isEncrypted && dialogAtt.value.password.length === 0){
    showFailToast("密码不能为空")
    return
  }
  let res = (await myAxios.post("/coterie/join/check",{
      coterieId: curCoterie.id,
      password: dialogAtt.value.password
    })).data
  if(res.data){
    //  若检查基本申请条件通过，则发送 “审核信息” 给圈主
    // await myAxios.post('/chat/send/system/coterie', {
    //   toId: curCoterie.userId,
    //   coterieId: curCoterie.id,
    //   userId: curUser.value.id,
    //   type: 2
    // })
    socket.send({
      toId: curCoterie.userId,
      coterieId: curCoterie.id,
      userId: curUser.value.id,
      chatType: "2",
      messageType: "2",
      createTime: new Date(),
      title: "系统消息",
    })
    showSuccessToast('已发送验证消息')
  }else {
    showFailToast(res.description)
  }
}

</script>

<style scoped>

</style>