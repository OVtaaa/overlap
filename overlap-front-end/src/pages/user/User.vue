<template>
  <br/>
  <template  v-if="curUser!==null">
    <van-nav-bar title="个人中心" />
    <van-cell-group inset>
      <van-cell title="头像" is-link>
        <van-image
            round
            width="48px"
            height="48px"
            :src="curUser.avatarUrl"
        />
      </van-cell>

      <van-cell title="账号" :value="curUser.userAccount"/>
      <van-cell title="密码" value="点击修改" is-link @click="showUpdatePasswordPopup"/>
      <van-cell title="昵称" is-link @click="showPopup('昵称','username',curUser.username)"
                :value="curUser.username"/>
      <van-cell title="性别" is-link @click="showPopup('性别','gender',curUser.gender)"
                :value="curUser.gender"/>
      <van-cell title="年龄" is-link @click="showPopup('年龄','age',curUser.age)"
                :value="curUser.age"/>
      <van-cell title="手机号" is-link @click="showPopup('手机号','phone',curUser.phone)"
                :value="curUser.phone"/>
      <van-cell title="邮箱" is-link @click="showPopup('邮箱','email',curUser.email)"
                :value="curUser.email"/>
      <van-cell :title="`标签（${selectedTags.length} / ${maxTagSize}）`">
        <template #value>
          <div >
            <van-tag v-for="(val,index) in selectedTags"
                     closeable round text-color="#ad0000" color="#ffe1e1" @close="closeTag(index)"
                     style="margin-right: 8px; margin-bottom: 4px; margin-top: 4px">
              {{ val }}
            </van-tag>
            <van-button v-if="selectedTags.length < maxTagSize"
                        icon="plus" type="primary" size="mini" color="#ffe1e1" round @click="dialog.show = true"/>
          </div>
        </template>
      </van-cell>


      <van-cell center title="展示信息" label="开启后可以在首页推荐或是分类中搜索到你">
        <template #right-icon>
          <van-switch v-model="isPost" @click="onSubmitToUpdateIsPost" />
        </template>
      </van-cell>

      <van-field
          v-model="curUser.introduction"
          rows="1"  autosize readonly
          is-link @click="showPopup('简介','introduction',curUser.introduction)"
          label="简介"  type="textarea"
          maxlength="200" show-word-limit
          input-align="right"
          placeholder="描述一下自己吧 ~"
          class="van-field-introduction"
      />
      <van-cell title="创建时间" :value="getTime(curUser.createTime)"/>

      <van-cell>
        <van-button round type="primary" block @click="outLogin">退出登录</van-button>
      </van-cell>

      <van-dialog v-model:show="dialog.show" title="添加标签" show-cancel-button
                  @confirm="addTag" @cancel="dialog.text = ''">
        <van-field v-model="dialog.text" placeholder="长度限制 5"
                   type="text" maxlength="5"/>
      </van-dialog>

      <van-popup v-model:show="show" round position="bottom" closeable>
        <van-nav-bar :title="curPopup.title" />

        <van-form @submit="onSubmit">
          <van-cell-group inset>
            <van-field v-if="curPopup.name !== 'introduction'"
                v-model="curPopup.curFieldValue"
                :name="curPopup.name"
                :placeholder="curPopup.placeholder"
                maxlength="50"
            />
            <van-field v-if="curPopup.name === 'introduction'"
                v-model="curPopup.curFieldValue"
                :name="curPopup.name"
                rows="1"  autosize
                type="textarea" size="large"
                maxlength="200" show-word-limit
                placeholder="描述一下自己吧 ~"
            />
          </van-cell-group>
          <div style="margin: 16px;">
            <van-button round block type="primary" native-type="submit" color="red">
              提交
            </van-button>
          </div>
        </van-form>
      </van-popup>

<!--      修改密码的弹出框-->
      <van-popup v-model:show="showUpdatePassword" round position="bottom" closeable>
        <van-nav-bar title="修改密码" />

        <van-form @submit="onSubmitToUpdatePassword">
          <van-cell-group inset>
            <van-field v-model="curUser.originalPassword"
                       name="originalPassword" type="password"
                       placeholder="原始密码" maxlength="20"
            />
            <van-field v-model="curUser.newPassword"
                       name="newPassword" type="password"
                       placeholder="请输入圈子新密码，6 ~ 20 位" maxlength="20"
            />
            <van-field v-model="curUser.retypePassword"
                       name="retypePassword" type="password"
                       placeholder="重复新密码" maxlength="20"
            />
          </van-cell-group>
          <div style="margin: 16px;">
            <van-button round block type="primary" native-type="submit" color="red">
              提交
            </van-button>
          </div>
        </van-form>
      </van-popup>

    </van-cell-group>
  </template>


</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import myAxios from "../../plugins/myAxios.js";
import {useRouter} from "vue-router";
import {DateToDetailTime, getCurrentUser, getTags} from "../../service/userService.ts";
import {showConfirmDialog, showFailToast, showSuccessToast} from "vant";
import {setUnRead} from "../../service/chatService";
import {unReadMessageStore} from "../../store/unReadMessage";

const curUser = ref(null)
const router = useRouter()

const maxTagSize = 6
const show = ref(false)
const isPost = ref(false)
const selectedTags = ref([])
const showUpdatePassword = ref(false)
const unReadStore = unReadMessageStore()
const curPopup = ref({
  show : false,
  title : '',
  curFieldValue : '',
  name : '',
  placeholder : '',
})

const dialog = ref({
  show: false,
  text: ""
})

const showPopup = (editKey,editValue,curFieldValue) => {
  show.value = true;
  curPopup.value.title = "修改"+editKey;
  curPopup.value.curFieldValue = curFieldValue;
  curPopup.value.name = editValue;
  curPopup.value.placeholder = "请输入"+editKey+(editValue==='gender' ? "（男 / 女）" : "");
};

const showUpdatePasswordPopup = () => {
  showUpdatePassword.value = true;
  curUser.value.originalPassword = null
  curUser.value.newPassword = null
  curUser.value.retypePassword = null
}

const outLogin = async () => {
  const res = (await myAxios.delete("/user/outLogin")).data?.data
  if(res){
    //  退出登录成功
    await router.replace("/")
    showSuccessToast("退出登录成功")
    unReadStore.size = 0
  }else {
    showFailToast("退出登录失败")
  }
}

const closeTag = (index) => {
  showConfirmDialog({
    message:
        '确认删除标签：'+selectedTags.value[index],
  })
  .then(() => {
    selectedTags.value.splice(index,1)
    curUser.value.tags = JSON.stringify(selectedTags.value)
    myAxios.post("/user/update",{
      id: curUser.value.id,
      tags: curUser.value.tags
    })
  })
  .catch(() => {
    // on cancel
  });

}

const onSubmit = async () => {
  if(curPopup.value.name==='age'&& isNaN(Number(curPopup.value.curFieldValue))){
    showFailToast("请正确输入年龄格式")
    return;
  }
  const res = (await myAxios.post("/user/update", {
    id: curUser.value.id,
    [curPopup.value.name]: curPopup.value.curFieldValue
  })).data
  if(res.data){
    curUser.value = await getCurrentUser()
    showSuccessToast("修改成功")
    show.value = false
  }else {
    if(res.description)
      showFailToast(res.description)
    else
      showFailToast("修改失败")
  }
};

const onSubmitToUpdateIsPost = async () => {
  // 对 isPost 进行处理
  curUser.value.isPost = isPost.value ? 1 : 0;

  const res = (await myAxios.post("/user/update", {
    id: curUser.value.id,
    isPost: curUser.value.isPost
  })).data
};

const addTag = async () => {
  if(selectedTags.value.length > maxTagSize)
    return;
  //  获取弹出框输入框文本
  const text = dialog.value.text
  if(text.length===0)
    return;
  // 将输入框文本 push 进数组对象中
  selectedTags.value.push(text)
  dialog.value.text = ""
  const res = (await myAxios.post("/user/update",{
    id: curUser.value.id,
    tags: JSON.stringify(selectedTags.value)
  })).data?.data
  if(res)
    curUser.value = await getCurrentUser()
}

const onSubmitToUpdatePassword = async () => {
  const res = (await myAxios.post("/user/update", {
    id: curUser.value.id,
    originalPassword: curUser.value.originalPassword,
    newPassword: curUser.value.newPassword,
    retypePassword: curUser.value.retypePassword
  })).data
  if(res.data){
    curUser.value = await getCurrentUser()
    showSuccessToast("修改成功")
    showUpdatePassword.value = false
  }else {
    if(res.description)
      showFailToast(res.description)
    else
      showFailToast("修改失败")
  }
};

onMounted(async () => {
  const data = await getCurrentUser()
  if(data){
    //  数据不为空，说明已登录
    curUser.value = data
    isPost.value = curUser.value.isPost === 1
    selectedTags.value = getTags(curUser.value.tags)?? ''
  }else {
    //  数据为空，跳转至登录页面
    await router.push("/user/login")
  }
})

const getTime = (date)=> {
  return DateToDetailTime(date)
}
</script>

<style scoped>
.van-field-introduction {
  --van-field-input-text-color: #969696
}

</style>