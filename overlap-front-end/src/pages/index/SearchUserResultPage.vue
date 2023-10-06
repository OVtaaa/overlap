<template>
  <van-sticky>
    <!--  顶部搜索条-->
    <van-search
        v-model="value"
        show-action
        shape="round"
        placeholder="请输入用户名 / 手机号 / 邮箱 / 标签"
        @search="onSearch"
        maxlength="50"
    >
      <template v-slot:left>
        <van-icon name="arrow-left" @click="onBack"/>
      </template>
      <template #action>
        <div @click="onClickButton" style="color: #7a8ca2">搜索</div>
      </template>
    </van-search>
  </van-sticky>

  <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
    <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
        class="finished-text-with-line-break"
    >
      <user-card-list :result-user="resultUser"/>
    </van-list>
  </van-pull-refresh>

</template>

<script setup>
import {ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import myAxios from "../../plugins/myAxios.js";
import userPage from "../../config/userPage.ts";
import UserCardList from "../../components/user/UserCardList.vue";

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
let curPageNum = 0
const pageSize = userPage.pageSize
const resultUser = ref([])
const value = ref(route.query.plan)

const onSearch = (val) => {
  if(val !== null && val.length !== 0){
    resultUser.value = []
    curPageNum = 0
    finished.value = false
  }
}
const onClickButton = () => {
  onSearch(value.value)
};

const onLoad = () => {
  // 若 load 时刷新属性为 true，将列表置空
  if (refreshing.value) {
    resultUser.value = [];
    curPageNum = 0;
    refreshing.value = false;
  }

  curPageNum++
  // console.log("加载中")
  // console.log("pageNum: "+curPageNum+" pageSize:"+pageSize)
  // 更新数据
  myAxios.get("/user/search/multi", {
    params:{
      text: value.value,
      pageNum: curPageNum,
      pageSize: pageSize
    }
  })
  .then(function (response) {
    const searchUser = response.data?.data
    if(searchUser.list){
      searchUser.list.forEach(user => {
        if(user.introduction === null)
          user.introduction = "暂无"
      })
      for(let i = 0; i < searchUser.list.length; i++)
        resultUser.value.push( searchUser.list[i] )
    }

    // 加载状态结束
    loading.value = false;
    // 数据全部加载完成
    finished.value = (searchUser.curPageNum * searchUser.pageSize) >= searchUser.total;

  })
  .catch(function (error) {
    console.log(error);
  });
};

const onRefresh = () => {
  finished.value = false;
  // 重新加载数据
  // 将 loading 设置为 true，表示处于加载状态
  loading.value = true;
  onLoad();
};

const onBack = ()=>{
  router.back()
}

</script>

<style scoped>
.finished-text-with-line-break {
  white-space: pre-line;
}
</style>