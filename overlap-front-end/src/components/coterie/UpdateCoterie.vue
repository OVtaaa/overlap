<template>

  <NavBarForReturn :title="title"/>

  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="coterie.title"
          name="title"
          label="圈名"
          placeholder="请输入圈名"
          :rules="[{ required: true, message: '请填写圈名' }]"
      />
      <van-field
          v-model="coterie.description"
          rows="2"
          autosize
          label="描述"
          type="textarea"
          maxlength="200"
          placeholder="对圈子来个简短的介绍吧 ~"
          show-word-limit
      />

      <van-field :label="`标签（${selectedTag.length}/5）`" >
        <template #input>
          <div style="display: flex; flex-wrap: wrap;">
            <van-tag v-for="(val,index) in selectedTag"
                     closeable round text-color="#ad0000" color="#ffe1e1" @close="closeTag(index)"
                     style="margin-right: 8px; margin-bottom: 4px; margin-top: 4px">
              {{ val }}
            </van-tag>
            <van-button v-if="selectedTag.length < 5"
                        icon="plus" type="primary" size="mini" color="#ffe1e1" round @click="showDialog"/>
          </div>
        </template>
      </van-field>

      <van-field label="最大人数">
        <template #input>
          <van-stepper v-model="coterie.maxNum" :min="coterie.curNum" max="50" integer />
        </template>
      </van-field>

      <van-field label="圈子状态">
        <template #input>
          <van-radio-group v-model="coterie.status" direction="horizontal">
            <van-radio :name=0>公开</van-radio>
            <van-radio :name=1>私密</van-radio>
          </van-radio-group>
        </template>
      </van-field>

      <van-field label="是否加密">
        <template #input>
          <van-switch v-model="isEncrypted" @change="changEncrypted"/>
        </template>
      </van-field>

      <van-field v-if="coterie.isEncrypted"
          v-model="coterie.password"
          type="password"
          name="password"
          label="密码"
          placeholder="请输入圈子密码，6 ~ 20 位"
          :rules="[{ required: true, message: '请填写密码' }]"
      />
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        提交
      </van-button>
    </div>
  </van-form>

  <van-dialog v-model:show="dialog.show" title="添加标签" show-cancel-button
              @confirm="addTag" @cancel="cancelDialog">
    <van-field v-model="dialog.text" placeholder="长度限制 5"
               type="text" maxlength="5"/>
  </van-dialog>

</template>

<script setup lang="ts">
import NavBarForReturn from "../bar/NavBarForReturn.vue";
import {onMounted, ref} from "vue";
import myAxios from "../../plugins/myAxios.js";
import {showFailToast, showSuccessToast} from "vant";
import {useRoute, useRouter} from "vue-router";

const router = useRouter()
const route = useRoute()

const title = route.query.title
const dialog = ref({
  show: false,
  text: ""
})
const coterie = ref(JSON.parse(route.query.curCoterie as string))
const isEncrypted = ref(coterie.value.isEncrypted===1)

const selectedTag = ref([])

const changEncrypted = () => {
  coterie.value.isEncrypted = !!isEncrypted.value;
  coterie.value.password = ""
}

const onSubmit = async () => {
  // 将选中的标签转为 json 赋值给 tags 属性
  coterie.value.tags = JSON.stringify(selectedTag.value)
  coterie.value.isEncrypted = coterie.value.isEncrypted ? 1 : 0;

  const path = route.query.backEndPath
  if(path === '/coterie/add') {
    const res = (await myAxios.post(path, coterie.value)).data
    if (res.data) {
      showSuccessToast("创建成功")
      await router.replace("/coterie")
    } else {
      showFailToast(res.description)
    }
  }else if(path === '/coterie/update'){
    const res = (await myAxios.post(path, coterie.value)).data
    if (res.data) {
      showSuccessToast("更新成功")
      await router.replace("/coterie")
    } else {
      showFailToast(res.description)
    }
  }
};

onMounted(() => {
  let tagsArr = JSON.parse(coterie.value.tags)
  for(let i = 0; i < tagsArr.length; i++)
    selectedTag.value.push(tagsArr[i])
})

const showDialog = () => {
  dialog.value.show = true
}

const addTag = () => {
  if(selectedTag.value.length > 5)
    return;
  //  获取弹出框输入框文本
  const text = dialog.value.text
  if(text.length===0)
    return;
  // 将输入框文本 push 进数组对象中
  selectedTag.value.push(text)
  dialog.value.text = ""
}

const closeTag = (index) => {
  selectedTag.value.splice(index,1)
}

const cancelDialog = () => {
  dialog.value.text = ""
}

</script>

<style scoped>

</style>