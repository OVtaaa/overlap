<template>

  <van-nav-bar title="登录中心"/>


  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="userAccount"
          name="userAccount"
          label="账号"
          placeholder="支持数字、字母、下划线"
          :rules="[{ required: true, message: '请填写账号' }]"
      />
      <van-field
          v-model="password"
          type="password"
          name="密码"
          label="密码"
          placeholder="密码长度限制 6 ~ 15"
          :rules="[{ required: true, message: '请填写密码' }]"
      />
      <div style="color: blue; float: right" @click="forgotPassword">
        忘记密码
      </div>
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        登录
      </van-button>
    </div>
    <div style="margin: 16px;">
      <van-button round block type="primary" @click="router.push('/user/register')">
        注册
      </van-button>
    </div>
  </van-form>
</template>

<script setup>
import {ref} from "vue";
import myAxios from "../../plugins/myAxios.js";
import {useRouter} from "vue-router";
import {showFailToast, showSuccessToast} from "vant";
import {initWebPageWebSocket} from "../../service/webSocketService.ts";

const router = useRouter()
const userAccount = ref('');
const password = ref('');

const forgotPassword = () => {

}

const onSubmit = (values) => {
  myAxios.post("/user/login",{
    userAccount: userAccount.value,
    password: password.value
  }).then(async function (response) {
    const data = response.data?.data
    if(data){
      showSuccessToast('登录成功');
      router.back()
      //  建立 websocket 连接
      initWebPageWebSocket(data.id)
    }else {
      showFailToast(response.data.description);
    }
  })
};


</script>

<style scoped>

</style>