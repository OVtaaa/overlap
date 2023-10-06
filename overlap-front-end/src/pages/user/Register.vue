<template>
  <NavBarForReturn :title="`注册中心`"/>

  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="formAtt.userAccount"
          name="userAccount"
          label="账号"
          placeholder="支持数字、字母、下划线"
          :rules="[{ required: true, message: '请填写账号' }]"
      />
      <van-field
          v-model="formAtt.email"
          name="email"
          label="邮箱"
          placeholder="请输入正确格式的邮箱"
          :rules="[{ required: true, message: '请填写邮箱' }]"
      />
      <van-field
          v-model="formAtt.password"
          type="password"
          name="password"
          label="密码"
          placeholder="密码长度限制 6 ~ 15"
          :rules="[{ required: true, message: '请填写密码' }]"
      />
      <van-field
          v-model="formAtt.checkPassword"
          type="password"
          name="checkPassword"
          label="确认密码"
          placeholder="请再次输入密码"
          :rules="[{ required: true, message: '请确认密码' }]"
      />
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        注册
      </van-button>
    </div>
  </van-form>
</template>

<script setup>
import NavBarForReturn from "../../components/bar/NavBarForReturn.vue";
import {ref} from "vue";
import {showFailToast, showSuccessToast} from "vant";
import myAxios from "../../plugins/myAxios.js";
import {useRouter} from "vue-router";

const formAtt = ref({
  userAccount : "",
  email : "",
  password : "",
  checkPassword : ""
})

const router = useRouter()

const onSubmit = async () => {
  if(formAtt.value.password !== formAtt.value.checkPassword) {
    showFailToast("密码两次不相同")
    return;
  }
  const resp = (await (myAxios.post('/user/register', formAtt.value))).data
  if(resp.data){
    showSuccessToast("注册成功")
    await router.replace('/user')
  }else {
    showSuccessToast(resp.description ?? '注册失败')
  }
}
</script>

<style scoped>

</style>